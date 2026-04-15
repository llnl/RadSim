// --- file: private/gov/llnl/rtk/response/support/CubicSpline.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import gov.llnl.math.interp.SingleInterpolator;
import gov.llnl.math.Cursor;

/**
 * This is a specialized cubic spline calculator using in place operations. We
 * will be calling cubic spline a lot, and my standard implementation which used
 * an object for each control point is way too bloated for high speed operation.
 * This code is better suited for repeated spline calls with in order evaluate
 * operations.
 *
 * @author nelson85
 */
public final class CubicSpline implements SingleInterpolator
{
  double[] x;
  double[] y;
  double[] m; // array of slopes
  double[] upper;
  int size;
  CubicSplineExtrapolation startExtrapolation = CubicSplineExtrapolation.LINEAR;
  CubicSplineExtrapolation endExtrapolation = CubicSplineExtrapolation.LINEAR;
  CubicSplineBoundary startBoundary = CubicSplineBoundary.NATURAL;
  CubicSplineBoundary endBoundary = CubicSplineBoundary.NATURAL;

  public CubicSpline()
  {
  }

  /**
   * Solve cubic spline with specified boundary conditions.
   *
   * @param x is the array of x values.
   * @param y is the array of y values.
   * @param n is the length of segment to use for interpolation.
   * @return the array of slopes for plotting.
   */
  public double[] update(double[] x, double[] y, int n)
  {
    // Replace the x and y
    this.x = x;
    this.y = y;
    this.size = n;

    // Make sure we have enough working space
    if (this.m == null || this.m.length < n)
    {
      this.m = new double[n];
      this.upper = new double[n];
    }

    double dx0 = x[1] - x[0];
    double dx1 = x[2] - x[1];
    double dy0 = y[1] - y[0];
    double dy1 = y[2] - y[1];

    // Apply the boundary condition to the first row
    startBoundary.first(dx0, dy0, dx1, dy1, upper, m, 0);
    for (int i = 1; i < n - 1; ++i)
    {
      // Update to current row
      dx0 = x[i] - x[i - 1];
      dx1 = x[i + 1] - x[i];
      dy0 = y[i] - y[i - 1];
      dy1 = y[i + 1] - y[i];

      // Compute the lower
      double lower = 1 / dx0;
      // diagonal is the diag minus the term to cancel the lower
      double diag = 2 * (1 / dx0 + 1 / dx1) - lower * upper[i - 1];

      // rhs constrains accel i == accel i-1 with cancel and normalization by diag
      m[i] = (3 * (dy0 / dx0 / dx0 + dy1 / dx1 / dx1) - lower * m[i - 1]) / diag;

      // upper normalized by diag
      upper[i] = 1 / dx1 / diag;

      // diag is now 1
    }
    // Apply the boundary condition to the last row
    endBoundary.last(dx0, dy0, dx1, dy1, upper, m, n - 1);

    // backsubstitute
    int i = n - 2;
    while (i >= 0)
    {
      m[i] -= upper[i] * m[i + 1];
      i--;
    }
    return m;
  }

  /**
   * Prevent ripples for sections of zeros when we have a positive density
   * function such as a spectrum.
   */
  public void flatten()
  {
    boolean last = false;
    for (int i = 0; i < x.length; i++)
    {
      if (y[i] == 0 && last)
      {
        m[i - 1] = 0;
        m[i] = 0;
      }
      last = y[i] == 0;
    }
  }

  @Override
  public SingleInterpolator.Evaluator get()
  {
    return new Evaluator();
  }

  public class Evaluator implements SingleInterpolator.Evaluator
  {
    private final Cursor cursor;

    Evaluator()
    {
      this.cursor = new Cursor(getX(), 0, size);
    }

    @Override
    public void seek(double x)
    {
      this.cursor.seek(x);
    }

    @Override
    public double evaluate()
    {
      int i = this.cursor.getIndex();
      double t = this.cursor.getFraction();

      // Outside of limits
      if (t < 0)
        return startExtrapolation.apply(t, getX()[i], getY()[i], m[i], getX()[i + 1], getY()[i + 1], m[i + 1]);
      if (t > 1)
        return endExtrapolation.apply(t, getX()[i], getY()[i], m[i], getX()[i + 1], getY()[i + 1], m[i + 1]);

      // Hermite Cubic spline equations
      double h = getX()[i + 1] - getX()[i];
      double t2 = t * t;
      double h00 = (2 * t - 3) * t2 + 1;
      double h10 = ((t - 2) * t + 1) * t;
      double h01 = (3 - 2 * t) * t2;
      double h11 = (t - 1) * t2;
      return h00 * getY()[i] + h10 * m[i] * h + h01 * getY()[i + 1] + h11 * m[i + 1] * h;
    }
  }

  /**
   * @return the x
   */
  public double[] getX()
  {
    return x;
  }

  /**
   * @return the y
   */
  public double[] getY()
  {
    return y;
  }

}
