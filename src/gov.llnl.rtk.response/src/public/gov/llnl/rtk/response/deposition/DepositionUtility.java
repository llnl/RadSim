// --- file: gov/llnl/rtk/response/deposition/DepositionUtility.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.ml.EncoderPredictor;
import gov.llnl.rtk.ml.ModuleState;

/**
 *
 * @author nelson85
 */
public class DepositionUtility
{

  /**
   * Integrate exp(-sigma * x) over quantiles using Simpson's rule.
   *
   * @param chords array of chord lengths (must be odd length, evenly spaced
   * quantiles)
   * @param sigma cross-section (attenuation coefficient)
   * @return Simpson's rule approximation of the integral
   */
  public static double simpsonEscapeProbability(double[] chords, double sigma)
  {
    int n = chords.length;
    if (n < 3 || (n % 2) == 0)
      throw new IllegalArgumentException("Simpson's rule requires odd number of points >= 3");

    double h = 1.0 / (n - 1);
    double sum = Math.exp(-sigma * chords[0]) + Math.exp(-sigma * chords[n - 1]);
    for (int j = 1; j < n - 1; ++j)
    {
      int weight = 2 + 2 * (j % 2); // 4 if odd, 2 if even
      sum += weight * Math.exp(-sigma * chords[j]);
    }
    return (h / 3.0) * sum;
  }

  /**
   * Numerically integrates exp(-sigma * x(q)) over q in [0,1], assuming x(q) is
   * piecewise linear between the provided chord samples (quantile grid).
   *
   * @param chords array of chord lengths at uniform quantiles (length >= 2)
   * @param sigma attenuation coefficient
   * @return integral of exp(-sigma * x(q)) dq over q in [0,1]
   */
  public static double piecewiseExponentialIntegral(double[] chords, double sigma)
  {
    int n = chords.length;
    if (n < 2)
      throw new IllegalArgumentException("Need at least two points for piecewise integration.");
    double dq = 1.0 / (n - 1);
    double sum = 0.0;
    for (int j = 0; j < n - 1; ++j)
    {
      double x0 = chords[j];
      double x1 = chords[j + 1];
      if (x1 != x0)
      {
        double denom = sigma * (x1 - x0);
        sum += (dq / -denom) * (Math.exp(-sigma * x1) - Math.exp(-sigma * x0));
      }
      else
      {
        // If x is constant, just use rectangle rule
        sum += dq * Math.exp(-sigma * x0);
      }
    }
    return sum;
  }

  /**
   * Adds the scaled convolution of A and B to R in-place. For all i in
   * 0..lenA-1 and j in 0..lenB-1: R[i + j] += A[i] * B[j] * scale
   *
   * @param R Destination array (accumulator), length at least lenA + lenB - 1
   * @param A First input array, length lenA
   * @param B Second input array, length lenB
   * @param lenA Number of elements to use from A
   * @param lenB Number of elements to use from B
   * @param scale Scaling factor to apply to each product
   */
  public static double[] addAssignConvolution(double[] R, double[] A, int lenA, double[] B, int lenB, double scale)
  {
    for (int i = 0; i < lenA; i++)
      DoubleArray.addAssignScaled(R, i, B, 0, lenB, A[i] * scale);
    return R;
  }

  public static void computeChordDistribution(double[] chord, IsotropicChordQF quantile)
  {
    // Precompute the chord distibution as a function of cdf
    int nChord = chord.length;
 
    // Compute chord length by quantile function
    for (int i = 0; i < nChord; i++)
      chord[i] = quantile.applyAsDouble(((double) i) / (nChord - 1));
  }
  
  
    static double flow(EncoderPredictor model, ModuleState state, double[] xp, double initial, double dt, int steps)
  {
    xp[0] = initial;
    // ODE solver
    for (int step = 0; step < steps; ++step)
    {
      // Predict velocity (vector field) at this point
      double[] v_t = model.predict(state, xp);

      // Euler update
      xp[1] += v_t[0] * dt; // update x
      xp[0] += dt; // update t

      // Clamp at limits
      if (xp[1] < 0)
        xp[1] = 0;
      if (xp[1] > 1)
        xp[1] = 1;
    }

    if (xp[1] < 0)
      xp[1] = 0;
    return xp[1];
  }

}
