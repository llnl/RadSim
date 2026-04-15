// --- file: gov/llnl/rtk/physics/SphericalEscapeCalculator.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Photon escape probability calculator for layered spherical models.
 * <p>
 * This class provides methods to compute the probability that photons, emitted
 * within a multi-layered spherical source model, referenceEscape the object
 * without interaction or after a single low-angle scatter. It supports detailed
 * material composition and energy-dependent cross sections.
 * </p>
 *
 * <h3>Thread Safety and Reentrancy</h3>
 * <ul>
 * <li><b>Not Thread-Safe:</b> This class is <b>not</b> thread-safe. It
 * maintains mutable internal state, including a reusable
 * {@link SphericalRayTrace} instance and shared workspace objects. Do not share
 * a single instance across threads without external synchronization.</li>
 * <li><b>Not Reentrant:</b> Like many "evaluator" or "calculator" classes in
 * this codebase,
 * <b>methods may overwrite or invalidate previous results</b> if the same
 * instance is reused. Do not invoke methods concurrently or interleave calls on
 * the same instance.</li>
 * <li>To avoid race conditions or data corruption, use separate instances per
 * thread or per calculation.</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <ul>
 * <li>Set the cross section library with
 * {@link #setLibrary(PhotonCrossSectionLibrary)} before use.</li>
 * <li>Configure energy units as needed (default is keV).</li>
 * <li>Call referenceEscape methods to compute referenceEscape probabilities for
 * a given model and energy grid.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li>Assumes spherical symmetry for ray tracing and integration.</li>
 * <li>Currently models only uncollided and single-scatter referenceEscape
 * probabilities.</li>
 * <li>Internal state (e.g., ray trace and cross section tables) is reused and
 * may be invalidated by subsequent calls.</li>
 * </ul>
 *
 * @author nelson85
 */
public class SphericalEscapeCalculator
{
  // 60 for 3 sigfig XCOM accuracy, 120 for 4 sigfig XCOM accuracy.
  static final public int N = 120;
  public int M = 30;
  public int KD = 6;

  // Default is very tight, such that "low angle" means photons are emitted
  // nearly in the same direction as the original photon. This ensures that
  // the single-scatter analytic approximation remains accurate. For larger
  // tolerances, the probability of multiple low-angle scatters increases
  // rapidly, and the analytic approach will underestimate the true escape
  // probability (see setLowAngleTolerance() for details).
  double lowAngleTolerance = 0.005 * Math.PI;

  KleinNishinaDistribution kn = new KleinNishinaDistribution();
  SphericalRayTrace rayTracer = new SphericalRayTrace();
  PhotonCrossSectionLibrary library = null;
  public Units energyUnits = Units.get("keV");
  public HashMap<Layer, Workspace> map = new HashMap<>();

  /**
   * Set the library for material cross sections.
   *
   * @param library
   */
  public void setLibrary(PhotonCrossSectionLibrary library)
  {
    this.library = library;
  }

  /**
   * Set the units used for energy.
   *
   * @param units are a unit of energy such as J, keV, MeV.
   */
  public void setEnergyUnits(Units units)
  {
    units.require(PhysicalProperty.ENERGY);
    energyUnits = units;
  }

  /**
   * Set the angular tolerance (in radians) for what is considered "low-angle"
   * scattering.
   *
   * This angle determines the maximum deviation from the original photon
   * direction that is counted as "low-angle" (i.e., included in the step or
   * Compton edge region).
   *
   * <b>Accuracy Note:</b>
   * <ul>
   * <li>For tolerances below about 0.01*PI (~1.8 degrees), the analytic
   * single-scatter model is highly accurate.</li>
   * <li>For tolerances up to 0.05*PI (~9 degrees), the model remains tolerable
   * for many applications, but small inaccuracies due to neglected
   * double-scatter events may be noticeable.</li>
   * <li>For tolerances above 0.05*PI, the probability of multiple low-angle
   * scatters becomes significant, and <b>correlations between scattering events
   * and path length (i.e., the total attenuation distance)</b> become
   * important. The analytic model does not account for these correlations, and
   * errors in the estimated path length appear in the exponent of the
   * attenuation factor, causing the model to increasingly underestimate the
   * true escape probability. The fudge factor correction can partially
   * compensate, but is only a rough approximation.</li>
   * <li><b>Recommendation:</b> Tolerances greater than 0.05*PI are not
   * recommended unless approximate results are acceptable. For larger
   * tolerances, consider using a full numerical or Monte Carlo approach.</li>
   * </ul>
   *
   * @param toleranceRadians The maximum angle (in radians) for low-angle
   * classification.
   */
  public void setLowAngleTolerance(double toleranceRadians)
  {
    if (toleranceRadians <= 0)
      throw new IllegalArgumentException("Tolerane must be positive");
    this.lowAngleTolerance = toleranceRadians;
  }

  /**
   * Computes the photon escape probability for a layered spherical model using
   * a surface-based coordinate system and recursive attenuation.
   * <p>
   * Supports both uncollided and single low-angle scattered escape
   * probabilities. For analytic details and derivation, see Appendix C.
   * </p>
   *
   * @param model the spherical source model to transport
   * @param emitting the layer which is emitting photons
   * @param energy array of photon energies (in units compatible with
   * {@code energyUnits})
   * @return an {@link SphericalEscapeResult} containing escape probabilities
   * for each energy
   * @throws IllegalArgumentException if the geometry is not spherical
   * @throws IllegalStateException if the cross-section library is not set
   */
  public SphericalEscapeResult computeEscape(SourceModel model, Layer emitting, double... energy)
  {
    requireSpherical(model);

    // convert energies into a cross_section table
    computeCrossSections(model, energy);

    // Get the layers from the model
    List<? extends Layer> layers = model.getLayers();

    // Check for bad inputs
    if (layers.isEmpty() || emitting == null)
    {
      double[] uncollided = new double[energy.length];
      double[] scattered = new double[energy.length];
      Arrays.fill(uncollided, 1.0);
      return new SphericalEscapeResult(energy, this.energyUnits, uncollided, scattered, this.lowAngleTolerance);
    }

    // Define the limits of integration
    double emittingInnerRadius = emitting.getInnerRadius().get();
    double emittingOuterRadius = emitting.getOuterRadius().get();
    double modelOuterRadius = layers.get(layers.size() - 1).getOuterRadius().get();
    double phiMin = 0;
    double phiMax = Math.asin(emittingOuterRadius / modelOuterRadius);
    double deltaPhi = (phiMax - phiMin) / (N + 1);
    double volume = (4.0 / 3.0) * Math.PI * (cube(emittingOuterRadius) - cube(emittingInnerRadius));

    QuantityImpl surfaceRadius = new QuantityImpl(-modelOuterRadius, PhysicalProperty.LENGTH, 0, true);
    int n = energy.length;

    // Set up attenuation to surface and emitted photons
    double[] markovUncollided = new double[n];
    double[] markovLowAngle = new double[n];
    double[] probUncollided = new double[n];
    double[] probLowAngle = new double[n];

    // Compute the scaling constant
    double integrationConstant = 2 * Math.PI * modelOuterRadius * modelOuterRadius * deltaPhi / volume;

    // Integrate through the angles
    for (int i0 = 0; i0 < N; i0++)
    {
      double phi = (i0 + 0.5) * deltaPhi;

      // Compute the path from the surface back into the object
      rayTracer.trace(model, surfaceRadius, phi);

      // Set the chord importance and scaling factor
      double sliceFactor = integrationConstant * Math.sin(phi) * Math.cos(phi);
      Arrays.fill(markovUncollided, 1);
      Arrays.fill(markovLowAngle, 0);

      // For each segment in the ray trace
      for (RayTraceSegment segment : rayTracer)
      {
        // Skip any invalid layer
        if (segment.layer == null)
          continue;

        // Attenuation constant times the path length
        double pathLength = segment.length.get();
        double[] attenuationCoeff = map.get(segment.layer).attenuation;
        double[] lowAngleScatterCoeff = map.get(segment.layer).lowAngle; // low-angle cross section

        // Compute photon referenceEscape probabilities for a uniformly emitting slab segment.
        // For each group (i1), calculate:
        //   - uncollided0: Fraction of photons escaping without interaction.
        //     Analytic: (1 - exp(-a * r)) / a
        //   - scattered0: Fraction escaping after a single low-angle scatter.
        //     Analytic: (f / a) * [1 - exp(-a * r) * (1 + a * r)]
        // Where:
        //   a = macroscopic attenuation coefficient (alphaRho[i1])
        //   r = slab thickness (pathLength)
        //   f = fraction of scatter events leading to low-angle referenceEscape (sigmaLow[i1] / a)
        //
        // Note: The use of Math.expm1(u) improves numerical stability for small arguments.
        //       The sign convention (u = -a * r) ensures correct exponential decay.
        //       Units: sigmaLow[i1] and a must both be macroscopic (1/length) for f to be dimensionless.
        if (segment.layer == emitting)
        {
          for (int i1 = 0; i1 < energy.length; ++i1)
          {
            if (attenuationCoeff[i1] <= 0)
              continue;

            double r = pathLength;
            double a = attenuationCoeff[i1];
            double f = lowAngleScatterCoeff[i1] / a; // We need to verify the units as we are mixing microscopd and macroscopic here.

            // First compute for this body
            //   loss to surface times the build up over path length 
            // \int_{0}^{L} exp(-a p l) dl 
            double u = -a * r;
            double expm1u = Math.expm1(u);
            double uncollided0 = sliceFactor * -expm1u / a;
            double scattered0 = sliceFactor * f / a * (1 + (u - 1) * Math.exp(u));

            // We now have a source so we call apply the loss coefficients we have built up.
            probUncollided[i1] += markovUncollided[i1] * uncollided0;
            probLowAngle[i1] += markovUncollided[i1] * scattered0 + markovLowAngle[i1] * uncollided0;
          }
        }

        // ------------------------------------------------------------------------
        // Recursively propagate uncollided (P) and single-scattered (Q) photon fluxes
        // through each layer, working from the outermost layer inward.
        // 
        // This is the reverse of the more common "inner-to-outer" (source-to-surface)
        // recursion. Here, we start at the surface and propagate inward, updating
        // the probabilities for each energy.
        // 
        // For each energy bin:
        //   - P[i1]: Probability that a photon has traversed all outer layers uncollided.
        //   - Q[i1]: Probability that a photon has scattered (low-angle) once in any
        //            inner layer and then escaped all subsequent outer layers uncollided.
        // 
        // For each segment (layer):
        //   - Pn:   Attenuation factor for uncollided photons in this layer.
        //   - Qn:   Probability that a photon will scatter within this layer and then
        //           referenceEscape the rest of the way out uncollided.
        // 
        // Update rules:
        //   - P[i1] *= Pn;
        //     // Multiply by the attenuation for this layer (uninterrupted path).
        // 
        //   - Q[i1] *= Qn * P1 + Q1 * Pn;
        //     // Cross-multiplies the two possible sources of scattered photons:
        //     //   (1) Photons that scatter in this layer (Qn), then referenceEscape outer layers (P1).
        //     //   (2) Photons that scattered in an inner layer (Q1), then referenceEscape this layer (Pn).
        // 
        // Note: This "outer-to-inner" recursion is mathematically equivalent to the
        // more intuitive "inner-to-outer" approach, but is structured this way to
        // match the ray tracing logic, which traces rays from the surface inward.
        // ------------------------------------------------------------------------ 
        for (int i1 = 0; i1 < energy.length; ++i1)
        {
          double l = pathLength;
          double a = attenuationCoeff[i1];
          double f = lowAngleScatterCoeff[i1] / a;
          // We need to verify the units as we are mixing microscopd and macroscopic here.

          // Use the prior layer as a starting point
          double P1 = markovUncollided[i1];
          double Q1 = markovLowAngle[i1];

          // Compute in layer contributes to attenuation and scattering
          double Pn = Math.exp(-a * l);
          double Qn = f * a * l * Pn;

          // Attenuation multiplies inner time outer
          markovUncollided[i1] *= Pn;

          // Scattering does a cross as we have two sources
          //   Scatter in inner layer time loss to outside plus
          //   loss from inner layer times scattering in outer
          markovLowAngle[i1] = Qn * P1 + Q1 * Pn;
        }
      }
    }

    return new SphericalEscapeResult(energy, this.energyUnits, probUncollided, probLowAngle, this.lowAngleTolerance);
  }

  /**
   * Solid ball analytic solution for photon escape.
   *
   * This requires the model have exactly one homogeneous layer.
   *
   * <p>
   * Computes both:
   * <ul>
   * <li>Uncollided escape probability (photons escaping without any
   * interaction)</li>
   * <li>Single-scatter, low-angle escape probability (photons that scatter once
   * at low angle and escape)</li>
   * </ul>
   * Both are computed using closed-form analytic expressions.
   * </p>
   *
   * @param model the spherical model with one layer
   * @param energy the energies (in specified energy units)
   * @return the SphericalEscapeResult containing arrays of probabilities for
   * each energy. Multiply by activity per volume to get number escaping.
   */
  public SphericalEscapeResult computeSolid(SourceModel model, double... energy)
  {
    requireSpherical(model);
    if (model.getLayers().size() != 1)
      throw new IllegalArgumentException("Must have only one layer");

    computeCrossSections(model, energy);
    Layer layer = model.getLayers().get(0);
    double R = layer.getThickness().get();
    double[] alpha = map.get(layer).attenuation; // macroscopic attenuation
    double[] sigmaLow = map.get(layer).lowAngle; // macroscopic low-angle scatter
    double[] uncollided = new double[energy.length];
    double[] lowAngle = new double[energy.length];
    double V = 4.0 / 3.0 * Math.PI * cube(R);
    double constant1 = Math.PI / 2 / V;

    for (int i = 0; i < energy.length; ++i)
    {
      double a = alpha[i];
      if (a == 0)
      {
        uncollided[i] = 1;
        lowAngle[i] = 0;
        continue;
      }

      double aR = a * R;
      double aR2 = aR * aR;
      double E = Math.exp(-2 * aR);
      double a3 = cube(a);

      // Uncollided analytic solution (unchanged)
      uncollided[i] = constant1 / a3 * (-1 + 2 * aR2 + E * (1 + 2 * aR));

      // Low-angle, single-scatter analytic solution (optimized for numerical stability)
      double f = sigmaLow[i] / a;
      lowAngle[i] = constant1 / a3 * f * 3 * (-1 + 2 * aR2 / 3 + E * (1 + 2 * aR + 4 * aR2 / 3));
    }
    return new SphericalEscapeResult(energy, this.energyUnits, uncollided, lowAngle, this.lowAngleTolerance);
  }

  //<editor-fold desc="internal" default-state="collapsed">
  final static double cube(double x)
  {
    return x * x * x;
  }

  final void requireSpherical(SourceModel model)
  {
    // Geometry check: Only allow spherical models
    if (model.getGeometry().getType() != Geometry.Type.SPHERICAL)
      throw new IllegalArgumentException(
              "SphericalEscapeCalculator only supports spherical geometry. "
              + "Provided geometry: " + model.getGeometry().getType()
      );
  }

  final void requireLibrary()
  {
    if (library == null)
      throw new IllegalStateException("Library must be set");
  }

  /**
   * Get the total cross section by layer.
   *
   * (We will need the same table for low angle scattering)
   *
   * @param model
   * @param energy
   */
  public void computeCrossSections(SourceModel model, double[] energy)
  {
    requireLibrary();
    this.map.clear();

    var layers = model.getLayers();
    for (int i = 0; i < layers.size(); ++i)
    {
      Layer layer = layers.get(i);
      Workspace ws = new Workspace(layer, energy);
      map.put(ws.layer, ws);
    }
  }

  /**
   * Converts a microscopic electron cross section (per electron, SI units) to a
   * macroscopic cross section (per kg of material) for a given material.
   * <p>
   * This method computes the total number of electrons per kilogram of material
   * by summing over all components, and then multiplies by the provided
   * microscopic cross section.
   *
   * @param material The material whose composition is used for the calculation.
   * @param sigma_si The microscopic cross section per electron (in m²).
   * @return The macroscopic cross section per kg (in m²/kg).
   */
  private static double convertElectronMicroscopicToMacroscopic(
          Material material,
          double sigma_si)
  {
    double electronsPerKg = 0.0;
    for (MaterialComponent c : material)
    {
      Nuclide nuclide = c.getNuclide();
      double Z = nuclide.getAtomicNumber();
      double massFraction = c.getMassFraction();
      double atomicMass_kg = nuclide.getAtomicMass() / 1000.0; // g/mol -> kg/mol
      if (atomicMass_kg <= 0.0)
        continue; // Defensive: skip invalid atomic mass

      // Number of moles per kg for this component
      double molesPerKg = massFraction / atomicMass_kg;
      // Number of electrons per kg for this component
      double electrons = molesPerKg * Z * Nuclide.AVAGADROS_CONSTANT;
      electronsPerKg += electrons;
    }
    return electronsPerKg * sigma_si;

  }

  /**
   * Per-evaluation scratch workspace for photon transport calculations.
   * <p>
   * This class is intended for temporary, per-calculation use only and should
   * not be serialized, cached, or stored beyond the scope of a single
   * evaluation. It holds precomputed, layer-specific cross-section data arrays
   * needed for efficient photon referenceEscape and scattering probability
   * calculations.
   * <p>
   * Not thread-safe and not suitable for persistent storage.
   */
  public class Workspace
  {
    public final Layer layer;
    public double density; // k/m^3
    public final double[] energy;
    public double[] attenuation; // m^2/kg
    public double[] lowAngle;

    /**
     * Workspace caches per-layer, per-energy-group cross-section data for
     * photon transport.
     * <p>
     * For each energy in the provided array, this computes and stores:
     * <ul>
     * <li>Macroscopic attenuation coefficient (attenuation) in this layer's
     * material.</li>
     * <li>Macroscopic low-angle scattering cross section (lowAngle) for the
     * same energies.</li>
     * </ul>
     * This enables efficient repeated calculations during photon
     * referenceEscape probability simulations.
     *
     * @param layer The physical layer for which cross sections are computed.
     * @param energy Array of photon energies (in units compatible with
     * {@code energyUnits}).
     */
    public Workspace(Layer layer, double[] energy)
    {
      this.energy = energy;
      this.layer = layer;
      Material material = layer.getMaterial();
      // Extract the density of the material (SI units, e.g., kg/m^3)
      this.density = material.getDensity().get();

      // Allocate arrays to hold macroscopic cross sections for each energy group.
      this.attenuation = new double[energy.length];
      this.lowAngle = new double[energy.length];

      // Obtain photon cross-section data for this material from the library.
      PhotonCrossSections xcom = library.get(material);
      // Set the units for energy input and cross-section output.
   
      // Create an evaluator for interpolating cross sections at arbitrary energies.
      PhotonCrossSectionsEvaluator eval = xcom.newEvaluator();
      eval.setInputUnits(energyUnits);
      eval.setOutputUnits(PhysicalProperty.CROSS_SECTION);

      // Loop over all requested energies to compute cross sections.
      QuantityVectorIterator energyIter = new QuantityVectorIterator(energy, energyUnits);
      int j = 0;
      while (energyIter.hasNext())
      {
        Quantity e = energyIter.next();
        eval.seek(e);
        double total = eval.getTotal();
        this.attenuation[j] = this.density * total;
        double lowangleCs_KN = convertElectronMicroscopicToMacroscopic(material, kn.getAngularCrossSection(e, 0, lowAngleTolerance));
        double KN_total = convertElectronMicroscopicToMacroscopic(material, kn.getTotalCrossSection(e));
        double XCOM_incoh = eval.getIncoherent();

        // Apply bound-electron correction to low-angle KN cross section
        double ratio = (KN_total > 0) ? (XCOM_incoh / KN_total) : 1.0;
        double lowangleCs_corrected = lowangleCs_KN * ratio;
        this.lowAngle[j] = this.density * lowangleCs_corrected;
        ++j;
      }

    }
  }
//</editor-fold>
}

/*

==============================================================================
Appendix A: Conversion from Interior to Surface Integrals and the Markov
            Property in Photon Escape
==============================================================================

1. Introduction
------------------------------------------------------------------------------
In photon transport through a sphere (or slab), the escape probability is
often written as a volume-based (interior) integral over all emission points
and directions. However, by exploiting the Markov (memoryless) property of
exponential attenuation, we can transform the problem into a surface-based
(exterior) integral. This transformation enables efficient, recursive, and
separable computation of escape probabilities.

2. Standard Interior (Volume-Based) Integral
------------------------------------------------------------------------------
Let photons be emitted isotropically and uniformly throughout a sphere of
radius R, with attenuation coefficient alpha (per unit length).

The probability that a photon escapes without interaction:

  P_esc = (1/V) ∫_V ∫_Ω exp(-∫₀^L alpha(s) ds) (dΩ/4π) d^3x

  where:
    V   = volume of the sphere
    x   = emission point
    Ω   = emission direction
    L   = distance to surface along Ω
    alpha(s) = attenuation along the path

3. The Markov (Memoryless) Property
------------------------------------------------------------------------------
The exponential attenuation law:

  P_survive(l) = exp(-∫₀^l alpha(s) ds)

is Markovian: survival to distance (l1 + l2) is the product of surviving to
l1 and then from l1 to (l1 + l2):

  P_survive(l1 + l2) = P_survive(l1) * P_survive(l2)

This means attenuation is memoryless, and the total survival probability
across layers is the product of survival in each layer.

4. Transformation to Exterior (Surface-Based) Integral
------------------------------------------------------------------------------
Instead of integrating over all emission points, we can integrate over all
surface exit points and angles. For each surface point and exit angle phi,
the possible emission points lie along the chord inside the sphere in the
direction opposite to escape.

The Jacobian introduces a cos(phi) term (projection onto the surface normal).
The limits for path length l are from 0 (surface) to L(phi) (deepest point).

The surface-based integral becomes:

  P_esc = 2π R^2 ∫₀^{π/2} sin(phi) cos(phi) ∫₀^{L(phi)} exp(-alpha l) dl dphi

  where:
    L(phi) = 2R cos(phi)

5. Example: Analytic Solution for Uncollided Escape
------------------------------------------------------------------------------
For uniform alpha, the inner integral evaluates to:

  ∫₀^{L(phi)} exp(-alpha l) dl = [1 - exp(-alpha L(phi))] / alpha

Substitute back:

  P_esc = 2π R^2 ∫₀^{π/2} sin(phi) cos(phi)
          [1 - exp(-alpha L(phi))] dphi

6. Example: Single Low-Angle Scatter (P_low)
------------------------------------------------------------------------------
Let f be the fraction of scatter events leading to low-angle escape.
The probability for a photon to escape after a single low-angle scatter:

  P_low = 2π R^2 ∫₀^{π/2} sin(phi) cos(phi)
          alpha f exp(-alpha L(phi)) L(phi) dphi

Recall L(phi) = 2R cos(phi):

  P_low = 2π R^2 ∫₀^{π/2} sin(phi) cos(phi) alpha f
          exp(-2 alpha R cos(phi)) [2R cos(phi)] dphi

        = 4π alpha f R^3 ∫₀^{π/2} sin(phi) cos^2(phi)
          exp(-2 alpha R cos(phi)) dphi

------------------------------------------------------------------------------

7. Recursion and Separability (Markov/Layered Media)
------------------------------------------------------------------------------
For a layered sphere, the survival probability is the product of exponentials
for each segment (layer) traversed:

  P_total = Π_i exp(-alpha_i * l_i)
          = exp(-Σ_i alpha_i * l_i)

This factorization allows the escape probability to be computed recursively,
layer by layer, with each step depending only on the current state.

8. References
------------------------------------------------------------------------------
- Chandrasekhar, S. (1960). Radiative Transfer. Dover Publications.
- Pomraning, G. C. (1973). The Equations of Radiation Hydrodynamics.
- Modest, M. F. (2013). Radiative Heat Transfer (3rd ed.). Academic Press.
- Spanier, J., & Gelbard, E. M. (1969). Monte Carlo Principles and Neutron
  Transport Problems. Addison-Wesley.

==============================================================================
Appendix B: Analytic Solution for Single Low-Angle Scatter Escape in a Sphere
==============================================================================

Overview:
------------------------------------------------------------------------------
Derives the analytic probability that a photon, emitted uniformly in a sphere,
escapes after a single low-angle scatter. Applies to a sphere of radius R,
attenuation coefficient a, and low-angle scatter fraction f.

Assumptions:
------------------------------------------------------------------------------
| Assumption | Description                                                    |
|------------|----------------------------------------------------------------|
| Geometry   | Sphere of radius R, homogeneous and isotropic.                 |
| Emission   | Photons emitted isotropically and uniformly.                   |
| Attenuation| Uniform attenuation coefficient a (1/length).                  |
| Scattering | Only single low-angle scatter events (fraction f) counted.     |
| Surface    | Only photons escaping after one scatter are included.          |
| Symmetry   | Azimuthal symmetry; only polar angle needed.                   |

Equations:
------------------------------------------------------------------------------
Surface-based integral for single low-angle scatter:
P_low = (2π R² / V) f ∫₀^{π/2} sinφ cosφ [ ∫₀^{L(φ)} a e^{-a l} (L(φ)-l) dl ] dφ

Where:
  P_low : Probability per photon to escape after one low-angle scatter
  f     : Fraction of scatters that are low-angle
  a     : Macroscopic attenuation coefficient (1/length)
  R     : Sphere radius
  V     : Sphere volume = (4/3)π R³
  φ     : Angle from surface normal
  l     : Distance from emission to scatter point
  L(φ)  : Chord length at angle φ = 2R cosφ

Closed-form analytic solution (numerically stable):
P_low = (f π / (2 a³ V)) [ e^{-2aR}(3 + 6aR + 4 a²R²) - 3 + 2 a²R²]

Summary Table:
------------------------------------------------------------------------------
| Variable | Range        | Description                       |
|----------|-------------|-----------------------------------|
| φ        | 0 → π/2     | Exit angle at surface             |
| l        | 0 → 2Rcosφ  | Path length inside sphere for φ   |
| x        | 1 → 0       | cosφ substitution for integration |

References:
------------------------------------------------------------------------------
- Chandrasekhar, S. (1960). Radiative Transfer. Dover Publications.
- Case, K. M., & Zweifel, P. F. (1967). Linear Transport Theory. Addison-Wesley.
- Mathematica symbolic integration for analytic result.

Significance:
------------------------------------------------------------------------------
Provides a closed-form analytic probability for a photon, emitted uniformly in
a homogeneous sphere, to escape after a single low-angle scatter. Essential for
accurate modeling of the "step" in gamma spectra and efficient simulation.


---

==============================================================================
Appendix C: Analytic Expressions and Recursion in SphericalEscapeCalculator
==============================================================================

Overview:
------------------------------------------------------------------------------
This appendix details the explicit analytic expressions and recursion
relationships implemented in the `computeEscape` method of
SphericalEscapeCalculator. These expressions are used to efficiently compute
photon escape probabilities for both uncollided and single low-angle scattered
photons in layered spherical geometries.

Assumptions:
------------------------------------------------------------------------------
| Assumption | Description                                                    |
|------------|----------------------------------------------------------------|
| Geometry   | Spherical, possibly layered, with uniform or piecewise-uniform |
|            | attenuation and scattering coefficients in each layer.         |
| Emission   | Photons are emitted isotropically and uniformly within the     |
|            | emitting shell or layer.                                       |
| Attenuation| Each layer has macroscopic attenuation coefficient α (1/length)|
| Low-angle  | A fraction f of all scatters are considered "low-angle" based  |
| scattering | on the angular tolerance specified.                            |

1. Analytic Expression: Uncollided Escape from Emitting Volume
------------------------------------------------------------------------------
For a shell with inner radius r₀ and outer radius r₁, and macroscopic
attenuation coefficient α, the probability that a photon escapes the shell
without interaction is:

  P_uncollided = (2π R² / V_emit) ∫_{φ_min}^{φ_max} sinφ cosφ
                 [1 - exp(-α L(φ))]/α dφ

where:
  - R is the outer radius of the shell
  - V_emit = (4/3)π (r₁³ - r₀³) is the emitting volume
  - φ is the angle from the surface normal
  - L(φ) is the chord length through the emitting shell at angle φ
  - The limits φ_min, φ_max depend on the emitting shell's position

2. Analytic Expression: Low-Angle Single-Scatter Escape
------------------------------------------------------------------------------
The probability that a photon, emitted uniformly in the shell, escapes after a
single low-angle scatter (fraction f of all scatters), is:

  P_low = (2π R² / V_emit) ∫_{φ_min}^{φ_max} sinφ cosφ
          [f/α] * [1 - exp(-α L(φ)) * (1 + α L(φ))] dφ

where:
  - f is the fraction of scatter events that are low-angle (as determined by
    the angular tolerance)
  - Other terms as above

3. Analytic Expressions: Transport in Non-Emitting Layers
------------------------------------------------------------------------------
For a non-emitting layer of thickness d and attenuation coefficient α:

- Uncollided photons are attenuated by:

    P'_uncollided = P_uncollided * exp(-α d)

- Low-angle scattered photons are updated by:

    P'_low = P_uncollided * f * (1 - exp(-α d)) + P_low * exp(-α d)

where:
  - f is the low-angle scatter fraction in this layer

4. Recursion Relationships (Layer-by-Layer Transport)
------------------------------------------------------------------------------
The code implements a recursion for each energy group as rays traverse each
layer. For each segment (layer) of path length l, attenuation α, and low-angle
fraction f:

Let:
  - P = current uncollided escape probability
  - Q = current low-angle escape probability

Update rules:

  P_out = P_in * exp(-α l)

  Q_out = P_in * f * (1 - exp(-α l)) + Q_in * exp(-α l)

These relationships are applied sequentially for each segment along the ray
from the emitting region to the surface.

5. Summary Table
------------------------------------------------------------------------------
| Quantity                      | Expression                                  |
|-------------------------------|---------------------------------------------|
| Uncollided escape (emitting)  | (2π R² / V_emit) ∫ sinφ cosφ                |
|                               |   [1 - exp(-α L(φ))]/α dφ                   |
| Low-angle escape (emitting)   | (2π R² / V_emit) ∫ sinφ cosφ                |
|                               |   [f/α][1 - exp(-α L(φ))(1 + α L(φ))] dφ    |
| Uncollided transport          | P_out = P_in * exp(-α l)                    |
| Low-angle transport           | Q_out = P_in * f * (1 - exp(-α l)) +        |
|                               |         Q_in * exp(-α l)                    |

6. Physical Meaning
------------------------------------------------------------------------------
- Uncollided escape: Probability a photon escapes the sphere without any
  interaction.
- Low-angle escape: Probability a photon undergoes exactly one low-angle
  scatter and then escapes without further interaction.
- Transport: Each non-emitting layer attenuates both uncollided and low-angle
  populations, and can contribute additional low-angle scattered photons.

7. References
------------------------------------------------------------------------------
- Chandrasekhar, S. (1960). Radiative Transfer. Dover Publications.
- Modest, M. F. (2013). Radiative Heat Transfer (3rd ed.). Academic Press.
- Spanier, J., & Gelbard, E. M. (1969). Monte Carlo Principles and Neutron
  Transport Problems. Addison-Wesley.

==============================================================================
```
 */
