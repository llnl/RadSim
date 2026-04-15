// --- file: gov/llnl/rtk/response/LLDFunction.java ---
/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.interp.SingleInterpolator;

/**
 *
 * @author nelson85
 */
public class LLDFunction
{
  double[] energy;
  double[] attenuation;

  SingleInterpolator function;

  void initialize()
  {
    this.function = SingleInterpolator.createLinear(energy, attenuation);
  }

  /**
   * @return the energy in keV
   */
  public double[] getEnergy()
  {
    return energy;
  }

  /**
   * @return the attenuation factors for the energy points
   */
  public double[] getAttenuation()
  {
    return attenuation;
  }

}
