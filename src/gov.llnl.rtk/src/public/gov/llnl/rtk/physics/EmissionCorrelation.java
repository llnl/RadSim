// --- file: gov/llnl/rtk/physics/EmissionCorrelation.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Specify that two emissions are linked.
 *
 * @author nelson85
 */
public interface EmissionCorrelation
{

  /**
   * Get the first emission in the sequence.
   *
   * @return
   */
  Emission getPrimary();

  /**
   * Get the second emission in the sequence.
   *
   * @return
   */
  Emission getSecondary();

  /**
   * Get the conditionally probability that the first will be followed by the
   * second.
   *
   * @return
   */
  double getProbability();

  /**
   * Get the expected time between the first and second emission.
   *
   * @return the time delay in seconds.
   */
  default double getDelay()
  {
    return 0;
  }
}
