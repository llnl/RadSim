// --- file: gov/llnl/rtk/gm/GeigerMuellerModel.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.gm;

import gov.llnl.math.spline.CubicHermiteSplineFactory;
import gov.llnl.math.spline.Spline;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxGroup;
import gov.llnl.rtk.flux.FluxLine;

/**
 * GeigerMueller tube detector response.
 *
 * This is an empirical model used to estimate the response to an incoming
 * gamma ray flux.  The actual physics needs to be determined using a physics
 * code such as MCNP, GEANT or by measurements.  Thus this simply applies the
 * efficiency function for the incoming flux.   This currently does not include
 * angular response.
 *
 * G-M tubes have a large dead time and is insensitive for even longer.
 * Without active quenching the count rate is limited to 1000 counts per second.
 * With active quenching a count rate of 10,000 can be achieved by lowering
 * the drive voltage during the recovery period so that charges as swept away.
 *
 * @author nelson85
 */
public class GeigerMuellerModel
{
  // Initial parameters
  double[] energies;
  double[] efficiency;

  double length; // m
  double diameter; // m
  double doseFactor; // Sv/hr per count
  Spline efficiencyFunction;

  double recoveryTime;

  public void initialize()
  {
     if (diameter == 0)
      throw new IllegalStateException("diameter must be set");
     if (length == 0)
      throw new IllegalStateException("length must be set");
    if (recoveryTime == 0)
      throw new IllegalStateException("recoveryTime must be set");
    if (energies == null)
      throw new NullPointerException("Energiet must not be null");
    if (efficiency == null)
      throw new NullPointerException("Efficiency must not be null");
    efficiencyFunction = CubicHermiteSplineFactory.createNatural(energies, efficiency);
  }

  /**
   * Compute the rate observed if there were no recovery time.
   *
   * @param flux
   * @return
   */
  public double computeGammaIdealRate(Flux flux, double distance)
  {
    if (efficiencyFunction==null)
      initialize();
    // area of the detector in sr
    double area = diameter*diameter/4*Math.PI/distance/distance;
    double counts = 0;
    for ( FluxLine line:flux.getPhotonLines())
    {
      counts += area*line.getIntensity()*this.efficiencyFunction.applyAsDouble(line.getEnergy());
    }
    for ( FluxGroup group:flux.getPhotonGroups())
    {
      counts += area*group.getCounts()*this.efficiencyFunction.applyAsDouble(group.getEnergyAverage());
    }
    return counts;
  }

  public double computeGammaExpectedRate(Flux flux, double distance)
  {
    double rate = computeGammaIdealRate(flux, distance);
    return rate/(1+recoveryTime*rate);
  }

}
