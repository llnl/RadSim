// --- file: gov/llnl/rtk/response/deposition/IsotropicChordQF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import java.util.function.DoubleUnaryOperator;

/**
 * Implementation of the inverse CDF using recursive subdivision and quadratic
 * interpolation.
 * <p>
 * <b>Assumptions:</b>
 * <ul>
 * <li>The IsotropicChordCDF is strictly increasing and continuous.</li>
 * <li>The shape is convex and physically realizable.</li>
 * </ul>
 * <p>
 The inverse is constructed by sampling the IsotropicChordCDF at chord lengths,
 recursively subdividing, and refining with quadratic interpolation. The
 result is a monotonic mapping from probability to chord length.
 */
public class IsotropicChordQF implements DoubleUnaryOperator
{
  static final double TOLERANCE = 1e-6;
  static final int MAX_ITER = 20;
  final int N;
  final double[] chordValues; // Corresponding chord lengths
  final double[] actualValues; // Actual IsotropicChordCDF at chordValues
  private final IsotropicChordCDF cdf;

  /**
   * Construct an inverse IsotropicChordCDF mapping for the specified IsotropicChordCDF.
   *
   * @param cdf the IsotropicChordCDF to invert
   * @param N the number of sample points for the inverse mapping
   * @param maxChord the maximum chord length for the shape
   */
  public IsotropicChordQF(IsotropicChordCDF cdf, int N, double maxChord)
  {
    this.cdf = cdf;
    this.N = N;
    this.chordValues = new double[N];
    this.actualValues = new double[N];
    // Initialize endpoints
    chordValues[0] = 0;
    chordValues[N - 1] = maxChord;
    actualValues[0] = cdf.eval(0);
    actualValues[N - 1] = cdf.eval(maxChord);
    // Recursive fill (analogous to Python's fill)
    fill(0, N - 1);
    // Refine points for accuracy
    for (int k = 1; k < N - 1; ++k)
    {
      if (Math.abs((double) k / (N - 1) - actualValues[k]) < TOLERANCE)
        continue;
      refine(k, TOLERANCE, MAX_ITER);
    }
  }

  /**
   * Apply the inverse CDF mapping to a cumulative probability value.
   * <p>
   * Input is clamped to [0, 1]. Output is a chord length corresponding to the
   * input probability.
   *
   * @param q quantile value in [0, 1]
   * @return corresponding chord length
   */
  @Override
  public double applyAsDouble(double q)
  {
    double p = Math.max(0.0, Math.min(1.0, q));
    int k = (int) Math.round(p * (N - 1));
    k = Math.max(0, Math.min(N - 2, k)); // Ensure k+1 is in bounds
    // Avoid division by zero if actualValues[k+1] == actualValues[k]
    double denom = actualValues[k + 1] - actualValues[k];
    if (denom == 0)
      return chordValues[k];
    double f = (p - actualValues[k]) / denom;
    return chordValues[k] * (1 - f) + chordValues[k + 1] * f;
  }
  
  public double getChord(double q)
  {
    return this.applyAsDouble(q);
  }

  /**
   * Recursively fill the sample arrays by subdividing intervals and evaluating
 the IsotropicChordCDF.
   *
   * @param i lower index of interval
   * @param j upper index of interval
   */
  private void fill(int i, int j)
  {
    if ((j - i) <= 1)
      return;
    double lower = chordValues[i];
    double upper = chordValues[j];
    double newChord = 0.5 * (lower + upper);
    double cdfVal = cdf.eval(newChord);
    int k = (int) Math.round(cdfVal * (N - 1));
    k = Math.max(0, Math.min(N - 1, k));
    actualValues[k] = cdfVal;
    chordValues[k] = newChord;
    fill(i, k);
    fill(k, j);
  }

  /**
   * Quadratic interpolation helper for inverse mapping refinement.
   *
   * @param x0 first x value (IsotropicChordCDF)
   * @param x1 second x value (IsotropicChordCDF)
   * @param x2 third x value (IsotropicChordCDF)
   * @param y0 chord length at x0
   * @param y1 chord length at x1
   * @param y2 chord length at x2
   * @param xTarget target IsotropicChordCDF value for interpolation
   * @return interpolated chord length at xTarget
   */
  public static double quadraticInterpolate(double x0, double x1, double x2, double y0, double y1, double y2, double xTarget)
  {
    double denom0 = (x0 - x1) * (x0 - x2);
    double denom1 = (x1 - x0) * (x1 - x2);
    double denom2 = (x2 - x0) * (x2 - x1);
    double term0 = y0 * (xTarget - x1) * (xTarget - x2) / denom0;
    double term1 = y1 * (xTarget - x0) * (xTarget - x2) / denom1;
    double term2 = y2 * (xTarget - x0) * (xTarget - x1) / denom2;
    return term0 + term1 + term2;
  }

  /**
   * Refine the inverse mapping at index {@code k} using quadratic interpolation
   * and iterative evaluation for increased accuracy.
   *
   * @param k index to refine
   * @param tolerance error tolerance for refinement
   * @param maxIter maximum number of refinement iterations
   */
  private void refine(int k, double tolerance, int maxIter)
  {
    // always called when k between 1 and N-2
    double target = (double) k / (N - 1);
    double x0 = actualValues[k - 1];
    double x1 = actualValues[k];
    double x2 = actualValues[k + 1];
    double y0 = chordValues[k - 1];
    double y1 = chordValues[k];
    double y2 = chordValues[k + 1];
    for (int i = 0; i < maxIter; ++i)
    {
      if (x0 > target || x2 < target)
        return; // Out of bounds
      double chordGuess = quadraticInterpolate(x0, x1, x2, y0, y1, y2, target);
      if (chordGuess > y0 && chordGuess < y2)
      {
        double cdfActual = cdf.eval(chordGuess);
        double error = target - cdfActual;
        if (Math.abs(error) < Math.abs(target - actualValues[k]))
        {
          chordValues[k] = chordGuess;
          actualValues[k] = cdfActual;
          x1 = cdfActual;
          y1 = chordGuess;
        }
        if (Math.abs(error) < tolerance)
          return;
      }
      // Tighten bounds
      if (x1 < target)
      {
        x0 = x1;
        y0 = y1;
        y1 = (y0 + y2) / 2;
        x1 = cdf.eval(y1);
      }
      else
      {
        x2 = x1;
        y2 = y1;
        y1 = (y0 + y2) / 2;
        x1 = cdf.eval(y1);
      }
    }
  }

}
