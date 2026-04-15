// --- file: gov/llnl/rtk/response/SplineUtilities.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

/**
 *
 * @author nelson85
 */
public class SplineUtilities
{
  /**
   * Create references mesh for the continuum.
   *
   * The meaning of the reference energies depends on the peak energy.
   *
   * Below a certain energy they are based on the max of the backscatter and the
   * Compton edge.
   *
   * When the backscatter is equal to 1/2 of the Compton edge, the lower
   * reference is the backscatter and the upper is the Compton edge.
   *
   * @param x is the target array of energies to fill.
   * @param ref are the reference energies for the continuum.
   * @param meshNodes are the number of points in each continuum.
   */
  public static void createPoints(double[] x,
          double[] ref, int[] meshNodes)
  {
    int j = 0;
    double step;
    double f;
    double r0 = ref[0];
    x[j++] = r0;
    for (int i = 0; i < meshNodes.length; ++i)
    {
      double r = ref[i + 1];
      int s = meshNodes[i];

      if (s > 0)
      {
        step = 1.0 / s;
        f = 0;
        for (int k = 0; k < s; ++k)
        {
          //
          f += step;
          double f2 = (-2 * f * f + 3 * f) * f;
          x[j++] = r0 + f2 * (r - r0);
        }
      }

      // update the end point for the next segment
      r0 = ref[i + 1];
    }
  }
}
