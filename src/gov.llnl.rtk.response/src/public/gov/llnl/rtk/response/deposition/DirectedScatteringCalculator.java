/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.Cursor;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxEvaluator;
import gov.llnl.rtk.flux.FluxGroup;
import gov.llnl.rtk.flux.FluxItem;
import gov.llnl.rtk.flux.FluxLine;
import gov.llnl.rtk.flux.FluxSpectrum;
import gov.llnl.rtk.physics.PhotonCrossSections;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.ScatteringDistribution;

/**
 *
 * @author nelson85
 */
public class DirectedScatteringCalculator extends ScatteringCalculator
{

  double density = 1.0;  //used if there is a thickness specified
  double thickness = Double.POSITIVE_INFINITY;
  ReflectionCalculator reflectionCalculator = new ReflectionCalculator();
  private BeamProfile beamProfile;

  public DirectedScatteringCalculator(ScatteringDistribution dist)
  {
    this.scatteringDistribution = dist;
    this.scatteringEvaluator = dist.newEvaluator();
    this.scatteringEvaluator.setInputUnits(energyUnits);
  }

  public void defineReflector(Vector3 center,
          double length, double width, double thickness,
          PhotonCrossSections cs,
          Quantity density,
          Vector3 x,
          Vector3 y
  )
  {
    this.density = density.get(); // kg/m^3
    this.materialCrossSections = cs;
    reflectionCalculator.defineReflector(center, length, width, x, y);
  }

  public void setBeamProfile(BeamProfile beam)
  {
    this.beamProfile = beam;
  }

  public void setSourcePosition(Vector3 c)
  {
    reflectionCalculator.setSourcePosition(c);
  }

  public void setDetectorPosition(Vector3 c)
  {
    reflectionCalculator.setDetectorPosition(c);
  }

  public Vector3 getSourcePosition()
  {
    return reflectionCalculator.getSourcePosition();
  }

  public Vector3 getDetectorPosition()
  {
    return reflectionCalculator.getDetectorPosition();
  }

  public ReflectionCalculator getReflectionCalculator()
  {
    return this.reflectionCalculator;
  }

  /**
   * Apply to a photon flux.
   *
   * @param flux Flux (always in keV)
   * @return
   */
  public Flux apply(Flux flux)
  {
    if (flux == null)
      return null;
    if (this.energyScale == null)
      throw new RuntimeException("EnergyScale is not set");

    if (this.materialCrossSections != null)
    {
      this.materialEval1 = this.materialCrossSections.newEvaluator();
      this.materialEval2 = this.materialCrossSections.newEvaluator();
      this.materialEval1.setInputUnits(energyUnits);
      this.materialEval2.setInputUnits(energyUnits);
    }

    // Compute the cosine ratio for the centeral integration point so
    // that we can get the incident to emitted angle ratio for 
    // attenuation calculations.
    ReflectionCalculator rc = this.reflectionCalculator;
    Vector3 rn = rc.reflectorZ;
    Vector3 sp = rc.sourcePosition;
    Vector3 dp = rc.detectorPosition;
    Vector3 rp = rc.reflectorPosition;
    double cosI = ReflectionCalculator.dot(
            sp.getX() - rp.getX(), sp.getY() - rp.getY(), sp.getZ() - rp.getZ(),
            rn.getX(), rn.getY(), rn.getZ())
            / ReflectionCalculator.norm(
                    sp.getX() - rp.getX(), sp.getY() - rp.getY(), sp.getZ() - rp.getZ());
    double cosE = ReflectionCalculator.dot(
            dp.getX() - rp.getX(), dp.getY() - rp.getY(), dp.getZ() - rp.getZ(),
            rn.getX(), rn.getY(), rn.getZ())
            / ReflectionCalculator.norm(
                    dp.getX() - rp.getX(), dp.getY() - rp.getY(), dp.getZ() - rp.getZ());

    // Backscatter only currently (no scatter though materials allowed
    if (cosE <= 0 || cosI <= 0)
      return null;

    //The ratio of angles between the incident and emitted.  This
    // is important if the energy of the emitted is low enoguh that it can't 
    // escape.   For xample a perpendicular entry and nearly parallel escape
    // angle.
    this.scatterRatio = cosI / cosE;

    // Get the edges for the result
    Cursor energyCursor = new Cursor(this.energyScale.getEdges());

    // Allocate memory
    double[] result = new double[this.energyScale.getChannels()];

    // Compute the scatteringDistribution of angles representing the surface
    rc.setBeamProfile(beamProfile);
    double[] angularWeights = reflectionCalculator.compute();

    // Special handling is needed for spectrum
    if (!(flux instanceof FluxSpectrum))
    {
      // Tally groups first
      for (FluxGroup group : flux.getPhotonGroups())
      {
        double e0 = group.getEnergyLower();
        double e1 = group.getEnergyUpper();
        double d = group.getCounts();
        renderGroup(result, energyCursor, e0, e1, d, angularWeights);
      }

      // Tally lines on groups
      //   FIXME we may be able to reduce computations by merging weak lines into their
      //   parent group.
      for (FluxLine line : flux.getPhotonLines())
      {
        renderLine(result, energyCursor, line.getEnergy(), line.getIntensity(), angularWeights);
      }
    }
    else
    {
      // FluxSpectrum are better evaluated using group structure.
      FluxEvaluator eval = flux.newPhotonEvaluator();
      double[] edges = this.energyScale.getEdges();
      for (int i = 0; i < edges.length - 1; ++i)
      {
        double d = eval.getIntegral(edges[i], edges[i + 1], FluxItem.ALL);
        renderGroup(result, energyCursor, edges[i], edges[i + 1], d, angularWeights);
      }
    }

    return new FluxSpectrum(this.energyScale, result, null, null);
  }

  @Override
  protected double getThicknessCorrection(double energy, double ep, double cosTheta)
  {
    // 1. Sanity Checks
    if (thickness == Double.POSITIVE_INFINITY || this.materialCrossSections == null)
      return 1.0;

    // 2. Align the Evaluators to the Incident and Emitted energies
    this.materialEval1.seek(energy);
    this.materialEval2.seek(ep);

    // 3. Calculate the Linear Attenuation Coefficients (1/cm)
    double muIn = this.materialEval1.getTotal() * density;
    double muOut = this.materialEval2.getTotal() * density;

    // 4. Calculate the 'Effective Path' coefficient using your tracked scatterRatio
    // This assumes the entry/exit paths scale linearly with the ratio.
    double effectiveMu = muIn + (muOut * this.scatterRatio);

    // 5. Compute the Survival Fraction (1 - exp(-mu_eff * d))
    // We use -Math.expm1(-x) to calculate (1 - exp(-x)) with high precision 
    // near zero, which is critical for thin 'Spruce Triangle' features.
    double arg = effectiveMu * thickness;
    return -Math.expm1(-arg);
  }

}
