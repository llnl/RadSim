/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.Arrays;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class SphericalEscapeCalculatorNGTest
{

  public SphericalEscapeCalculatorNGTest()
  {
  }

  /**
   * Test of setLibrary method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testSetLibrary()
  {
    PhotonCrossSectionLibrary library = new MockPhotonCrossSectionLibrary(new MockPhotonCrossSections(0, 0));
    SphericalEscapeCalculator instance = new SphericalEscapeCalculator();
    instance.setLibrary(library);
  }

  /**
   * Test of setEnergyUnits method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testSetEnergyUnits()
  {
    Units units = Units.get("keV");
    SphericalEscapeCalculator instance = new SphericalEscapeCalculator();
    instance.setEnergyUnits(units);
  }

  /**
   * Test of setLowAngleTolerance method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testSetLowAngleTolerance()
  {
    double toleranceRadians = 0.01 * Math.PI;
    SphericalEscapeCalculator instance = new SphericalEscapeCalculator();
    instance.setLowAngleTolerance(toleranceRadians);
  }

  /**
   * Test of computeEscape method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testReferenceSolid()
  {
    SphericalModelBuilder smb = new SphericalModelBuilder();
    smb.units(UnitSystem.CGS);
    LayerBuilder layer = smb.layer().thickness(10);
    layer.material().label("Pb").add(Elements.get("Pb"), 100).density(11.2).build();
    layer.build();
    SourceModel model = smb.build();

    Layer emitting = model.getLayers().get(0);
    Reference instance = new Reference();
    // Use Lead 511 cross sections
    PhotonCrossSectionLibrary library = new MockPhotonCrossSectionLibrary(new MockPhotonCrossSections(0.014548686021874549, 0.006675971173284972));
    instance.setLibrary(library);
    instance.setEnergyUnits(Units.get("keV"));

    SphericalEscapeResult er1 = instance.computeSolid(model, 511);
    SphericalEscapeResult er2 = instance.computeEscape(model, emitting, 511);
    SphericalEscapeResult er3 = instance.referenceSolid(model, 511);
    SphericalEscapeResult er4 = instance.referenceSolid2(model, 511);
    SphericalEscapeResult er5 = instance.referenceEscape(model, emitting, 511);

    assertEquals(er1.getUncollided()[0], er2.getUncollided()[0], 1e-6);
    assertEquals(er1.getLowAngle()[0], er2.getLowAngle()[0], 2e-7);
    assertEquals(er1.getStepRatio()[0], er2.getStepRatio()[0], 2e-5);

    assertEquals(er1.getUncollided()[0], er3.getUncollided()[0], 1e-4);
    assertEquals(er1.getUncollided()[0], er4.getUncollided()[0], 1e-6);
    assertEquals(er1.getUncollided()[0], er5.getUncollided()[0], 5e-5);
    assertEquals(er1.getLowAngle()[0], er3.getLowAngle()[0], 1e-5);
    assertEquals(er1.getLowAngle()[0], er4.getLowAngle()[0], 1e-7);
    assertEquals(er1.getLowAngle()[0], er5.getLowAngle()[0], 5e-6);

    assertEquals(er2.getUncollided()[0], er3.getUncollided()[0], 1e-4);
    assertEquals(er2.getUncollided()[0], er4.getUncollided()[0], 1e-6);
    assertEquals(er2.getUncollided()[0], er5.getUncollided()[0], 5e-5);
    assertEquals(er2.getLowAngle()[0], er3.getLowAngle()[0], 1e-5);
    assertEquals(er2.getLowAngle()[0], er4.getLowAngle()[0], 1e-7);
    assertEquals(er2.getLowAngle()[0], er5.getLowAngle()[0], 5e-6);
  }

  private SourceModel buildThreeLayerModel(double thickness)
  {
    SphericalModelBuilder smb = new SphericalModelBuilder();
    smb.units(UnitSystem.CGS);
    LayerBuilder layer = smb.layer().thickness(0.1).label("core");
    layer.material().add(Elements.get("Pb"), 100).density(11.2).build();
    layer.build();
    layer = smb.layer().thickness(5).label("void");
    layer.material().add(Elements.get("Pb"), 100).density(0.0001).build();
    layer.build();
    layer = smb.layer().thickness(thickness).label("shield");
    layer.material().add(Elements.get("Pb"), 100).density(2).build();
    layer.build();
    return smb.build();
  }

  /**
   * Test of computeEscape method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testReferenceShells()
  {
    SourceModel model = buildThreeLayerModel(10);

    Layer emitting = model.getLayers().get(0);
    Reference instance = new Reference();
    // Use Lead 511 cross sections
    PhotonCrossSectionLibrary library = new MockPhotonCrossSectionLibrary(new MockPhotonCrossSections(0.014548686021874549, 0.006675971173284972));
    instance.setLibrary(library);
    instance.setEnergyUnits(Units.get("keV"));

    SphericalEscapeResult er1 = instance.computeEscape(model, emitting, 511);
    SphericalEscapeResult er2 = instance.referenceEscape(model, emitting, 511);

    assertEquals(er1.getUncollided()[0], er2.getUncollided()[0], 1e-3);
    assertEquals(er1.getLowAngle()[0], er2.getLowAngle()[0], 2e-3);
  }

  @Test
  public void testReferenceAngles()
  {
//    SourceModel model = buildThreeLayerModel(10);
//
//    Layer emitting = model.getLayers().get(0);
//    Reference instance = new Reference();
//    // Use Lead 511 cross sections
//    PhotonCrossSectionLibrary library = new MockPhotonCrossSectionLibrary(new MockPhotonCrossSections(0.014548686021874549, 0.006675971173284972));
//    instance.setLibrary(library);
//    instance.setEnergyUnits(Units.get("keV"));
//
//    SphericalEscapeResult er1 = instance.computeEscape(model, emitting, 511);
//    instance.setLowAngleTolerance(0.05 * Math.PI);
//    SphericalEscapeResult er2 = instance.computeEscape(model, emitting, 511);
//    assertEquals(er1.getStepRatio()[0], er2.getStepRatio()[0], 0.5e-4);
  }

  /**
   * Test of computeSolid method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testComputeSolid()
  {
//    System.out.println("computeSolid");
//    SourceModel model = null;
//    double[] energy = null;
//    SphericalEscapeCalculator instance = new SphericalEscapeCalculator();
//    SphericalEscapeResult expResult = null;
//    SphericalEscapeResult result = instance.computeSolid(model, energy);
//    assertEquals(result, expResult);
//    // TODO review the generated test code and remove the default call to fail.
//    fail("The test case is a prototype.");
  }

  /**
   * Test of cube method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testCube()
  {
    double x = 2.0;
    double expResult = 8.0;
    double result = SphericalEscapeCalculator.cube(x);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of requireSpherical method, of class SphericalEscapeCalculator.
   */
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testRequireSpherical()
  {
    SourceModelImpl model = new SourceModelImpl();
    model.setGeometry(Geometry.of(Geometry.Type.CONE, Quantity.UNSPECIFIED, Quantity.UNSPECIFIED));
    SphericalEscapeCalculator instance = new SphericalEscapeCalculator();
    instance.requireSpherical(model);
  }

  /**
   * Test of requireLibrary method, of class SphericalEscapeCalculator.
   */
  @Test(expectedExceptions = IllegalStateException.class)
  public void testRequireLibrary()
  {
    SphericalEscapeCalculator instance = new SphericalEscapeCalculator();
    instance.requireLibrary();
  }

  /**
   * Test of computeCrossSections method, of class SphericalEscapeCalculator.
   */
  @Test
  public void testComputeCrossSections()
  {
    PhotonCrossSectionLibrary library = new MockPhotonCrossSectionLibrary(new MockPhotonCrossSections(0.014548686021874549, 0.006675971173284972));
    SphericalModelBuilder smb = new SphericalModelBuilder();
    smb.units(UnitSystem.CGS);
    LayerBuilder layer = smb.layer().thickness(10);
    layer.material().label("Pb").add(Elements.get("Pb"), 100).density(11.2).build();
    layer.build();
    SourceModel model = smb.build();
    double[] energy = new double[]
    {
      511
    };
    SphericalEscapeCalculator instance = new SphericalEscapeCalculator();
    instance.setLibrary(library);
    instance.computeCrossSections(model, energy);
  }

  class MockPhotonCrossSectionLibrary implements PhotonCrossSectionLibrary
  {
    private final PhotonCrossSections sections;

    public MockPhotonCrossSectionLibrary(PhotonCrossSections sections)
    {
      this.sections = sections;
    }

    @Override
    public PhotonCrossSections get(Element element)
    {
      return sections;
    }

    @Override
    public PhotonCrossSections get(Material material)
    {
      return sections;
    }
  }

  class MockPhotonCrossSections implements PhotonCrossSections
  {
    private final double attenuation;
    private final double incoherent;

    public MockPhotonCrossSections(double attenuation, double incoherent)
    {
      this.attenuation = attenuation;
      this.incoherent = incoherent;
    }

    @Override
    public Material getMaterial()
    {
      return null;
    }

    @Override
    public Units getInputUnits()
    {
      return Units.get("keV");
    }

    @Override
    public Units getOutputUnits()
    {
      return PhysicalProperty.CROSS_SECTION;
    }

    @Override
    public void setInputUnits(Units units)
    {
    }

    @Override
    public void setOutputUnits(Units units)
    {
    }

    @Override
    public PhotonCrossSectionsEvaluator newEvaluator()
    {
      return new PhotonCrossSectionsEvaluator()
      {
        @Override public Units getInputUnits()
        {
          return Units.get("keV");
        }

        @Override public void setInputUnits(Units unit)
        {
        }

        @Override public Units getOutputUnits()
        {
          return PhysicalProperty.CROSS_SECTION;
        }

        @Override public void setOutputUnits(Units unit)
        {
        }

        @Override public double getIncoherent()
        {
          return incoherent;
        }

        @Override public double getPairElectron()
        {
          return 0;
        }

        @Override public double getPairNuclear()
        {
          return 0;
        }

        @Override public double getPhotoelectric()
        {
          return 0;
        }

        @Override public double getTotal()
        {
          return attenuation;
        }

        @Override
        public PhotonCrossSectionsEvaluator seek(double energy)
        {
          return this;
        }

        @Override
        public PhotonCrossSectionsEvaluator seek(Quantity energy)
        {
          return this;
        }
      };
    }
  }

  static class Reference extends SphericalEscapeCalculator
  {
    // Intentionally shadow for control
    public int N = 120;
    public int M = 30;
    public int KD = 6;

    /**
     * Numerical method used to verify analytic.
     *
     * Will be used as a reference in the test code. This is using spherical
     * coordinates.
     *
     * @param model
     * @param energy
     * @return
     */
    public SphericalEscapeResult referenceSolid(SourceModel model, double... energy)
    {
      requireSpherical(model);
      if (model.getLayers().size() != 1)
        throw new IllegalArgumentException("Must have only one layer");

      computeCrossSections(model, energy);

      Layer layer = model.getLayers().get(0);
      double R = layer.getThickness().get();
      double[] coefAttenuation = map.get(layer).attenuation;
      double[] coefLowAngle = map.get(layer).lowAngle;
      double[] totalUncollided = new double[energy.length];
      double[] totalScattered = new double[energy.length];

      // Integration parameters
      int Nr = 500;     // Number of radial steps
      int Ntheta = 100; // Number of angle steps
      double dr = R / Nr;
      double dtheta = Math.PI / Ntheta;
      double volume = 4.0 / 3.0 * Math.PI * cube(R);
      for (int j = 0; j < coefAttenuation.length; ++j)
      {
        double alpha = coefAttenuation[j];
        double probScatter = coefLowAngle[j] / alpha;
        double uncollided = 0.0;
        double scattered = 0.0;
        for (int ir = 0; ir < Nr; ir++)
        {
          // Midpoint integration for r
          double r = (ir + 0.5) * dr;
          double r2 = r * r;
          for (int itheta = 0; itheta < Ntheta; itheta++)
          {
            // Midpoint integration for theta
            double theta = (itheta + 0.5) * dtheta;
            double sintheta = Math.sin(theta);
            // Path length to surface
            double underSqrt = R * R - r2 * sintheta * sintheta;
            if (underSqrt < 0)
              continue; // Outside sphere, skip
            double pathLength = -r * Math.cos(theta) + Math.sqrt(underSqrt);
            double weight = r2 * sintheta * dr * dtheta;
            uncollided += weight * Math.exp(-alpha * pathLength);
            scattered += weight * (alpha * probScatter * pathLength * Math.exp(-alpha * pathLength));
          }
        }
        totalUncollided[j] = 2 * Math.PI * uncollided / volume;
        totalScattered[j] = 2 * Math.PI * scattered / volume;
      }
      return new SphericalEscapeResult(energy, this.energyUnits, totalUncollided, totalScattered, this.lowAngleTolerance);
    }

    /**
     * Numerical method used to verify analytic.
     *
     * Will be used as a reference in the test code. This is using surface
     * coordinates.
     *
     * @param model
     * @param energy
     * @return
     */
    public SphericalEscapeResult referenceSolid2(SourceModel model, double... energy)
    {
      requireSpherical(model);
      if (model.getLayers().size() != 1)
        throw new IllegalArgumentException("Must have only one layer");

      // convert energies into a cross_section table
      computeCrossSections(model, energy);

      // Get the layers from the model
      List<? extends Layer> layers = model.getLayers();

      // Define the limits of integration
      Layer layer = layers.get(0);
      double emittingInnerRadius = layer.getInnerRadius().get();
      double emittingOuterRadius = layer.getOuterRadius().get();
      double modelOuterRadius = layers.get(layers.size() - 1).getOuterRadius().get();
      double phiMin = 0;
      double phiMax = Math.asin(emittingOuterRadius / modelOuterRadius);
      double deltaPhi = (phiMax - phiMin) / (N + 1);
      double volume = (4.0 / 3.0) * Math.PI * (cube(emittingOuterRadius) - cube(emittingInnerRadius));
      int n = energy.length;

      // Set up attenuation to surface and emitted photons
      double markovUncollided = 1;
      double markovLowAngle = 0;
      double[] probUncollided = new double[n];
      double[] probLowAngle = new double[n];

      // Compute the scaling constant
      double integrationConstant = 2 * Math.PI * modelOuterRadius * modelOuterRadius * deltaPhi / volume;
      double R = layer.getOuterRadius().get();
      double[] attenuationCoeff = map.get(layer).attenuation;
      double[] lowAngleScatterCoeff = map.get(layer).lowAngle; // low-angle cross section

      // Integrate through the angles
      for (int i0 = 0; i0 < N; i0++)
      {
        double phi = (i0 + 0.5) * deltaPhi;
        double pathLength = 2 * R * Math.cos(phi);

        // Set the chord importance and scaling factor
        double sliceFactor = integrationConstant * Math.sin(phi) * Math.cos(phi);

        // Attenuation constant times the path length
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
          double scattered0 = sliceFactor * f / a * (-u - (1 + u) * expm1u);

          // We now have a source so we call apply the loss coefficients we have built up.
          probUncollided[i1] += markovUncollided * uncollided0;
          // \int_{0}^{L} a p l exp(-a p l) dl = 
          probLowAngle[i1] += markovUncollided * scattered0 + markovLowAngle * uncollided0;
        }
      }
      return new SphericalEscapeResult(energy, this.energyUnits, probUncollided, probLowAngle, this.lowAngleTolerance);
    }

    /**
     * Compute the escape probability for a series of energies.
     *
     * This is the original (to be replaced).
     *
     * @param model is the spherical model to transport.
     * @param emitting is the layer which is emitting photons.
     * @param energy in specified energy unit.
     * @return the probability for a photon to referenceEscape for the volume.
     * Multiply by activity per volume to get number escaping.
     */
    public SphericalEscapeResult referenceEscape(SourceModel model, Layer emitting, double... energy)
    {
      requireSpherical(model);

      // convert energies into a cross_section table
      computeCrossSections(model, energy);

      double emittingInnerRadius = emitting.getInnerRadius().get();
      double emittingOuterRadius = emitting.getOuterRadius().get();
      double[] totalUncollided = new double[energy.length];
      double[] totalScattered = new double[energy.length];
      double[] escape = new double[energy.length];
      double[] scatter = new double[energy.length];

      // Integration parameters
      int Nr = 500;     // Number of radial steps
      int Ntheta = 100; // Number of angle steps
      double dr = (emittingOuterRadius - emittingInnerRadius) / Nr;
      double dtheta = Math.PI / Ntheta;
      double volume = 4.0 / 3.0 * Math.PI * (cube(emittingOuterRadius) - cube(emittingInnerRadius));

      QuantityImpl workingRadius = new QuantityImpl(0, PhysicalProperty.LENGTH, 0, true);
      for (int ir = 0; ir < Nr; ir++)
      {
        // Midpoint integration for r
        double r = emittingInnerRadius + (ir + 0.5) * dr;
        workingRadius.set(r);
        for (int itheta = 0; itheta < Ntheta; itheta++)
        {
          // Midpoint integration for theta
          double theta = (itheta + 0.5) * dtheta;

          // Compute the path from the surface back into the object
          rayTracer.trace(model, workingRadius, theta);
          Arrays.fill(escape, 1);
          Arrays.fill(scatter, 0);

          for (RayTraceSegment segment : rayTracer)
          {
            // Skip any invalid layer
            if (segment.layer == null)
              continue;

            double[] coefAttenuation = map.get(segment.layer).attenuation;
            double[] coefLowAngle = map.get(segment.layer).lowAngle;
            double segmentLength = segment.length.get();

            for (int j = 0; j < coefAttenuation.length; ++j)
            {
              double alpha = coefAttenuation[j];
              double probScatter = coefLowAngle[j] / alpha;

              double oldEscape = escape[j];
              double oldScatter = scatter[j];

              double lossSegment = Math.exp(-alpha * segmentLength);
              double newScatter = oldEscape * probScatter * alpha * segmentLength * lossSegment;
              escape[j] *= lossSegment;
              scatter[j] = oldScatter * lossSegment + newScatter;
            }
          } // end segments

          double weight = 2 * Math.PI * r * r * Math.sin(theta) * dr * dtheta / volume;
          for (int j = 0; j < energy.length; ++j)
          {
            totalUncollided[j] += weight * escape[j];
            totalScattered[j] += weight * scatter[j];
          }
        }
      }
      return new SphericalEscapeResult(energy, this.energyUnits, totalUncollided, totalScattered, this.lowAngleTolerance);
    }
  }

}
