// --- file: gov/llnl/rtk/response/deposition/Deposition.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.DoubleArray;

/**
 *
 * @author nelson85
 */
public class Deposition
{
  public double energy;
  public double totalPhoto;
  public double totalPair;

  // Pair depositions
  public double singleEnergy;
  public double singleEscape;
  public double doubleEnergy;
  public double doubleEscape;

  public double[] scattered; // all partial collection
  public double[] initial;

  /**
   * Used to apply the intensity scaler
   */
  void rescale(double intensity)
  {
    // Short cut
    if (intensity == 1)
      return;
    this.totalPhoto *= intensity;
    this.totalPair *= intensity;
    this.singleEscape *= intensity;
    this.doubleEscape *= intensity;
    if (this.scattered != null)
      DoubleArray.multiplyAssign(this.scattered, intensity);
    if (this.initial != null)
      DoubleArray.multiplyAssign(this.initial, intensity);
  }

}
