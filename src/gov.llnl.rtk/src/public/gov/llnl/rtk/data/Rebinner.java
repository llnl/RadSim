// --- file: gov/llnl/rtk/data/Rebinner.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

/**
 *
 * @author nelson85
 */
public interface Rebinner
{
  public EnergyScale getInputBins();

  public double[] rebinArray(double[] input);

  public int[] rebinArray(int[] input);
}
