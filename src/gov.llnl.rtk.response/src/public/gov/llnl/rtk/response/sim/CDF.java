// --- file: gov/llnl/rtk/response/sim/CDF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

/**
 *
 * @author nelson85
 */
public interface CDF
{
    /**
   * Evaluates the cumulative distribution function (CDF) for the chord length
   * distribution of a shape at the specified chord length {@code x}.
   * <p>
   * This method returns the probability that a randomly chosen chord through
   * the shape has length less than or equal to {@code x}.
   *
   * @param x the chord length at which to evaluate the cumulative probability
   * @return the value of the CDF at chord length {@code x}, in the range [0, 1]
   */
  public double eval(double x);
}
