// --- file: gov/llnl/rtk/response/DoseEvaluatorICRP119.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.interp.MultiInterpolator;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxGroup;
import gov.llnl.rtk.flux.FluxLine;
import java.time.Duration;
import java.util.function.DoubleUnaryOperator;

/**
 * ICRP-119 Dose evaluator.
 *
 * This class converts a flux into a dose. The conditions for the conversion can
 * be adjusted using the
 *
 * @author nelson85
 */
public class DoseEvaluatorICRP119 implements DoseEvaluator
{

  DoseResponseFunctionICRP119 drf;

  private Duration duration = Duration.ofHours(1);
  private double durationFactor = 3600.0;  // Cache for speed
  private double distance = 1.0; // units are meters
  private double[] view = new double[5]; // view factors for exposure.
  private final MultiInterpolator.Evaluator evaluator;
  private DoubleUnaryOperator dfi;

  DoseEvaluatorICRP119(DoseResponseFunctionICRP119 drf)
  {
    this.drf = drf;
    this.evaluator = drf.tables.get();
    this.dfi = this.evaluator.get(0);
  }

  @Override
  public DoseResponseFunction getResponseFunction()
  {
    return this.drf;
  }

//<editor-fold desc="conditions" defaultstate="collapsed">
  /**
   * Set the distance for the dose calculation.
   *
   * @param distance in meters.
   */
  @Override
  public void setDistance(double distance)
  {
    this.distance = distance;
  }

  /**
   * Set the duration for the dose.
   *
   * Typically this is set to hours so the doses are reported in per hour
   * quantities.
   *
   * @param duration the duration to set
   */
  @Override
  public void setDuration(Duration duration)
  {
    this.duration = duration;
    this.durationFactor = (duration.toSeconds() + duration.getNano() * 1e-9);
  }

  /**
   * Define the view angle for absorbed dose.
   *
   * @param ap is fraction of dose exposed from the front.
   * @param pa is fraction of dose exposed from the back.
   * @param llat is the fraction of dose exposed from the left.
   * @param rlat is the fraction of dose exposed from the right.
   * @param iso is the fraction of dose exposed isotropic.
   */
  public void setView(double iso, double ap, double pa, double llat, double rlat)
  {
    this.view[0] = iso;
    this.view[1] = ap;
    this.view[2] = pa;
    this.view[3] = llat;
    this.view[4] = rlat;
  }
//</editor-fold>
//<editor-fold desc="calculators" defaultstate="collapsed">

  /**
   * Compute the absorbed dose.
   *
   * @param flux is the flux to be absorbed.
   * @return is dose in Gy per duration.
   */
  @Override
  public double getAbsorbedDose(Flux flux)
  {
    double dose = 0;
    for (FluxLine line : flux.getPhotonLines())
    {
      double factor = dfi.applyAsDouble(line.getEnergy() / 1000);
      dose += factor * line.getIntensity();
    }

    for (FluxGroup group : flux.getPhotonGroups())
    {
      double counts = group.getCounts();
      double e0 = group.getEnergyLower();
      double e1 = group.getEnergyUpper();
      if (e1 < 10.0)
        continue;
      if (counts <= 0)
        continue;
      double factor = (dfi.applyAsDouble(e0 / 1000)
              + dfi.applyAsDouble(e1 / 1000)) / 2;
      dose += group.getCounts() * factor;
    }
    double area = 4 * Math.PI * distance * distance * 10000;
    // pGy cm^2/photon * 3600 s/hr * 1e-12 p = (Gy/hr)/(photons/cm^2 s)
    return dose * durationFactor * 1e-12 / area;
  }

  /**
   * Get the equivalent dose.
   *
   * This is used to get the biological equivalent dose for gamma rays. Before
   * this issued, the view must be set to define the orientation of target
   * relative to the incoming flux.
   *
   * @param flux is the flux to be absorbed.
   * @return the equivalent dose in Sv per duration.
   */
  @Override
  public double getEquivalentDose(Flux flux)
  {
    double dose = 0;
    for (FluxLine line : flux.getPhotonLines())
    {

      double e = line.getEnergy() / 1000;
      double intensity = line.getIntensity();

      // Skip lines less than 10 keV and with no contribution.
      if (e < 0.01 || intensity <= 0)
        continue;

      // Compute the equivalence factor based on the view
      double equivalence = 0;
      evaluator.seek(e);
      for (int i = 0; i < view.length; ++i)
      {
        if (view[i] >= 0)
          equivalence += view[i] * evaluator.evaluate(i + 1);;
      }

      dose += equivalence * evaluator.evaluate(0) * intensity;
    }

    for (FluxGroup group : flux.getPhotonGroups())
    {
      double e0 = group.getEnergyLower() / 1000;
      double e1 = group.getEnergyUpper() / 1000;
      double ec = (e0 + e1) / 2;
      double counts = group.getCounts();

      // Skip groups less than 10 keV as the table do not go that low
      // and skip groups with no counts as it would be wasted calculations.
      if (e1 < 0.01 || counts <= 0)
        continue;

      // Reposition the evaluator
      evaluator.seek(ec);

      // Collect the view efficiencies
      double equivalence = 0;
      for (int i = 0; i < view.length; ++i)
      {
        // Only compute views with contributions
        if (view[i] >= 0)
          equivalence += view[i] * evaluator.evaluate(i + 1);
      }

      // Dose is the sum of the absorbed dose times the equivalence factor.
      double absorption = (dfi.applyAsDouble(e0)
              + dfi.applyAsDouble(e1)) / 2;
      dose += equivalence * absorption * counts;
    }

    // pGy cm^2/photon * 3600 s/hr * 1e-12 p = (Gy/hr)/(photons/cm^2 s)
    double area = 4 * Math.PI * distance * distance * 10000;
    return dose * durationFactor * 1e-12 / area;
  }
//</editor-fold>
//<editor-fold desc="getters" defaultstate="collapsed">

  /**
   * @return the duration
   */
  @Override
  public Duration getDuration()
  {
    return duration;
  }

  /**
   * @return the distance
   */
  @Override
  public double getDistance()
  {
    return distance;
  }

  /**
   * Get the current view.
   *
   * Order is defined by the dose table.
   *
   * @return the view
   */
  public double[] getView()
  {
    return view;
  }

  /**
   * Set the current view.
   *
   * The order of parameters is defined by dose table.
   *
   * @param view the view to set
   */
  public void setView(double[] view)
  {
    if (view.length > 5)
      throw new IllegalArgumentException();
    this.view = view;
  }
//</editor-fold> 

}
