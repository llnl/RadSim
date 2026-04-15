// --- file: gov/llnl/rtk/pileup/TriggerModel.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.pileup;

import gov.llnl.math.random.RandomGenerator;

/**
 *
 * @author nelson85
 */
public interface TriggerModel
{
  /**
   * Get the time constant for the trigger.
   *
   * @return
   */
  public double getTau();

  /**
   * Draw the counts for a pulse count sensor.
   *
   * @param generator 
   * @param rate
   * @param time
   * @return
   */
  int drawCounts(RandomGenerator generator, double rate, double time);

  /**
   * Draw the counts by interaction number for a spectral sensor.
   *
   * @param output is the location to store the
   * @param generator
   * @param rate
   * @param time
   */
  void drawDistribution(int[] output, RandomGenerator generator, double rate, double time);
}
