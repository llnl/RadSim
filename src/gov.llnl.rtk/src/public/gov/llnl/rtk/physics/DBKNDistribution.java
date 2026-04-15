// --- file: gov/llnl/rtk/physics/DBKNDistribution.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import static gov.llnl.math.SpecialFunctions.erf;
import java.util.List;

/**
 * <b>Doppler-Broadened Klein-Nishina (DBKN) Distribution.</b>
 *
 * <p>
 * Implements a modified Klein-Nishina formula for Compton scattering that
 * includes Doppler broadening effects due to the momentum distribution of bound
 * electrons in atomic shells.
 * </p>
 *
 * <h3>Purpose and Context</h3>
 * <ul>
 * <li>This model is specifically designed to accurately reproduce the
 * <b>Compton edge</b> and the <b>shape into the Compton hole</b> in photon
 * energy spectra, as observed in detectors.</li>
 * <li>It achieves this by applying an ad hoc broadening to the Klein-Nishina
 * cross section, with empirical parameters per electron shell.</li>
 * <li>Computational efficiency is prioritized, making this suitable for
 * detector response simulations where the Compton edge region is critical.</li>
 * </ul>
 *
 * <h3>Validation</h3>
 * <ul>
 * <li>The model was empirically tuned and benchmarked to match the output of
 * GEANT4 for the Compton edge and hole regions.</li>
 * <li>For most practical detector simulation purposes, this approximation is
 * sufficient where the output matches GEANT4 within acceptable tolerances.</li>
 * </ul>
 *
 * <h3>Physical Scope</h3>
 * <ul>
 * <li>Models the <b>energy distribution</b> of Compton-scattered photons,
 * including Doppler broadening from atomic electron shells.</li>
 * <li><b>Not intended for angular (directional) calculations</b>: provides the
 * probability density for a photon of incident energy {@code energyIncident} to
 * be scattered to energy {@code energyEmitted} (integrated over all
 * angles).</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li>Does <b>not capture the full angular response</b> of Compton scattering.
 * For angular-dependent calculations, use the standard Klein-Nishina formula or
 * more advanced models.</li>
 * <li>Broadening parameters are empirical and may not be universally accurate
 * for all elements or detector geometries.</li>
 * <li>This is not a direct implementation of the full Compton profile
 * convolution as in GEANT4.</li>
 * </ul>
 *
 * <h3>Usage Example</h3>
 * <pre>
 * // Construct a list of electron shells (with count and binding energy)
 * List&lt;DBKNDistribution.ElectronShell&gt; shells = ...;
 * DBKNDistribution dbkn = new DBKNDistribution(shells);
 * double cs = dbkn.getCrossSection(incidentEnergy, emittedEnergy);
 * </pre>
 *
 * <h3>References</h3>
 * <ul>
 * <li>GEANT4 Physics Reference Manual, Section on Compton Scattering</li>
 * <li>J. H. Hubbell, "Electron Binding Effects in the Compton Scattering of
 * Gamma Rays," Int. J. Appl. Radiat. Isot. 33, 1269 (1982)</li>
 * </ul>
 *
 * <b>Note:</b>
 * This code is not intended as a general-purpose Compton scattering model, but
 * is optimized for detector simulation scenarios where the Compton edge and
 * hole are the primary features of interest.
 *
 * For practical detector response modeling, we approximate the
 * Doppler-broadened Compton edge using a sum of error functions, justified by
 * the near-Gaussian nature of the dominant shell momentum distributions and
 * validated empirically against reference Monte Carlo. This approach provides
 * high accuracy in the region of the Compton edge, which is most relevant for
 * detector calibration and analysis.
 *
 * <h3>FIXME</h3>
 * <ul>
 * <li>Consider connecting X-ray data directly for more accurate shell
 * structure.</li>
 * </ul>
 *
 * @author nelson85
 */
public class DBKNDistribution implements ScatteringDistribution
{
  final static double RE2 = Constants.RADIUS_E.get() * Constants.RADIUS_E.get(); // [m²]
  final static double KCS = 2 * Math.PI * RE2 * Constants.MEC2.get() * Constants.MEC2.get(); // [m² * (energy)²]

  /**
   * Empirical width scaling parameter for Doppler broadening (tunable)
   */
  final static double WIDTH = 0.017 * Math.sqrt(1000); // Unitless
  final static double MEC2 = Constants.MEC2.get();

  /**
   * Fractional occupancy of each electron shell (normalized)
   */
  final double[] fraction;

  /**
   * Effective Doppler width for each shell (in sqrt[J])
   */
  final double[] width;

  /**
   * Constructs a DBKNDistribution for a given set of electron shells.
   *
   * @param electrons List of electron shells, each with count and binding
   * energy. The fractions are normalized to sum to 1.
   */
  public DBKNDistribution(List<ElectronShell> electrons)
  {
    int n = electrons.size();
    fraction = new double[n];
    width = new double[n];
    double totalCount = electrons.stream().mapToInt(p -> p.count).sum();
    int i = 0;
    for (ElectronShell e : electrons)
    {
      fraction[i] = e.count / totalCount;
      // Doppler width scales with sqrt(binding energy)
      width[i] = WIDTH * Math.sqrt(e.energy.get());
      i++;
    }
  }

  /**
   * Computes the Doppler-broadened Klein-Nishina cross section for Compton
   * scattering, given incident and emitted photon energies.
   *
   * @param energyIncident Energy of the incident photon (e.g., in keV)
   * @param energyEmitted Energy of the emitted (scattered) photon (same units
   * as incident)
   * @return The cross section in m^2
   */
  @Override
  public double getCrossSection(Quantity energyIncident, Quantity energyEmitted)
  {
    energyIncident.require(PhysicalProperty.ENERGY);
    energyEmitted.require(PhysicalProperty.ENERGY);
    double ei = energyIncident.get();
    double ep = energyEmitted.get();
    return _getCrossSection(ei, ep);
  }

  // Top-level call
  public double integralOverEnergy(Quantity energyIncident, Quantity epMin, Quantity epMax, double tol)
  {
    double ei = energyIncident.get();
    double a = epMin.get();
    double b = epMax.get();
    double fa = _getCrossSection(ei, a);
    double fb = _getCrossSection(ei, b);
    double mid = 0.5 * (a + b);
    double fm = _getCrossSection(ei, mid);
    return adaptiveSimpson(ei, a, b, fa, fb, fm, tol, 0);
  }

  private double adaptiveSimpson(
          double ei, double a, double b,
          double fa, double fb, double fm,
          double tol, int depth
  )
  {
    double mid = 0.5 * (a + b);

    // Quarter points
    double leftMid = 0.5 * (a + mid);
    double rightMid = 0.5 * (mid + b);

    double fleftMid = _getCrossSection(ei, leftMid);
    double frightMid = _getCrossSection(ei, rightMid);

    // Simpson's estimates
    double S = (b - a) / 6.0 * (fa + 4 * fm + fb);
    double Sleft = (mid - a) / 6.0 * (fa + 4 * fleftMid + fm);
    double Sright = (b - mid) / 6.0 * (fm + 4 * frightMid + fb);
    double error = Math.abs(S - (Sleft + Sright));

    if (error < 15 * tol || depth > 12)
    {
      return Sleft + Sright;
    }
    else
    {
      // Recurse, passing down already-computed values
      return adaptiveSimpson(ei, a, mid, fa, fm, fleftMid, tol / 2, depth + 1)
              + adaptiveSimpson(ei, mid, b, fm, fb, frightMid, tol / 2, depth + 1);
    }
  }

  double _getCrossSection(double ei, double ep)
  {

    // Minimum scattered energy for backscatter (Compton edge)
    double em = ei / (1 + 2.0 * ei / MEC2);
    double w = Math.sqrt(ei);

    // There is no crosssection for negative energy
    if (ei < 0 || ep < 0)
      return 0;

    double sinT2 = 0;
    double u;
    if (ep < em)
    {
      // Below Compton edge: use ratio for u
      u = em / ei;
    }
    else
    {
      // Above Compton edge: infer angle from energy
      double cosT = 1 + MEC2 * (1 / ei - 1 / ep);
      sinT2 = 1 - cosT * cosT;
      u = ep / ei;
    }

    // Klein-Nishina kernel (energy redistribution)
    double k = 0.5 * (u + 1 / u - sinT2);

    // Doppler broadening: sum over electron shells
    double s = 1;
    for (int i = 0; i < this.fraction.length; ++i)
    {
      // Error function models the probability of energy shift due to electron motion
      s += fraction[i] * erf((ep - em) / w / width[i]);
    }

    // Divide by ei^2 to get the right units (see Klein-Nishina)
    return KCS * k * s / (ei * ei); // [m²/keV]
  }

  /**
   * Compute the cosine of the angle for the resulting photons.
   *
   * This is necessary for compute the expected path length for escaping
   * photons. We will be using the reflection approximation for Doppler
   * broadened photons.
   *
   * @param energyIncident
   * @param energyEmitted
   * @return
   */
  @Override
  public double getCosAngle(Quantity energyIncident, Quantity energyEmitted)
  {
    energyIncident.require(PhysicalProperty.ENERGY);
    energyEmitted.require(PhysicalProperty.ENERGY);
    double ei = energyIncident.get();
    double ep = energyEmitted.get();

    // Minimum scattered energy for backscatter (Compton edge)
    double em = ei / (1 + 2.0 * ei / MEC2);

    if (ei < 0 || ep < 0)
      return 0;

    // Reflection for forbidden region
    if (ep < em)
      ep = 2 * em - ep;

    // Standard Compton angle formula
    return 1 + MEC2 * (1 / ei - 1 / ep);
  }

  /**
   * Returns the cosine of the Compton scattering angle for given incident (ei)
   * and emitted (ep) photon energies. For DBKN: negative energies return
   * forward (cosθ=1), and forbidden regions are reflected to preserve symmetry.
   */
  double _getCosAngle(double ei, double ep)
  {
    // Minimum scattered energy for backscatter (Compton edge)
    double em = ei / (1 + 2.0 * ei / MEC2);
    double ev = ei / (1 + ei / MEC2);

    if (ei < 0 || ep < 0)
      return 1.0; // Forward direction for unphysical negative energy

    // Reflection for forbidden region (DBKN symmetry)
    if (ep < em)
    {
      ep = 2 * em - ep;
      if (ep>ev)
        ep = ev;
    }

    // Standard Compton angle formula
    return 1 + MEC2 * (1 / ei - 1 / ep);
  }

  @Override
  public Evaluator newEvaluator()
  {
    return new EvaluatorImpl();
  }

  /**
   * Fast evaluator for vector operations.
   */
  class EvaluatorImpl implements Evaluator
  {
    private Units units = PhysicalProperty.ENERGY;
    private double k = 1;

    @Override
    public void setInputUnits(Units energy)
    {
      energy.require(PhysicalProperty.ENERGY);
      this.units = energy;
      this.k = units.getValue();
    }

    @Override
    public Units getInputUnits()
    {
      return this.units;
    }

    @Override
    public double getCrossSection(double ei, double ep)
    {
      return _getCrossSection(ei * k, ep * k);
    }

    @Override
    public double getCosAngle(double ei, double ep)
    {
      return _getCosAngle(ei * k, ep * k);
    }

  }
}
