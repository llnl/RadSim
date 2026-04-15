// --- file: private/gov/llnl/rtk/response/support/CubicSplineBuilder.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

/**
 *
 * @author nelson85
 */
public class CubicSplineBuilder
{
  final double[] x;
  final double[] y;
  int n = -1;
  CubicSplineBoundary startBoundary = CubicSplineBoundary.NATURAL;
  CubicSplineBoundary endBoundary = CubicSplineBoundary.NATURAL;
  CubicSplineExtrapolation startExtrapolation = CubicSplineExtrapolation.LINEAR;
  CubicSplineExtrapolation endExtrapolation = CubicSplineExtrapolation.LINEAR;

  public CubicSplineBuilder()
  {
    this.x = null;
    this.y = null;
  }

  public CubicSplineBuilder(double[] x, double[] y)
  {
    this.x = x;
    this.y = y;
  }

  /**
   * Set all boundaries to specified boundary condition.
   *
   * @param boundary
   * @return this factory for chaining.
   */
  public CubicSplineBuilder boundary(CubicSplineBoundary boundary)
  {
    this.startBoundary = boundary;
    this.endBoundary = boundary;
    return this;
  }

  /**
   * Set the start boundary condition.
   *
   * @param boundary
   * @return
   */
  public CubicSplineBuilder start(CubicSplineBoundary boundary)
  {
    this.startBoundary = boundary;
    return this;
  }

  public CubicSplineBuilder end(CubicSplineBoundary boundary)
  {
    this.endBoundary = boundary;
    return this;
  }

  /**
   * Set the extrapolation for both ends.
   *
   * @param extrapolation
   * @return
   */
  public CubicSplineBuilder extrapolation(CubicSplineExtrapolation extrapolation)
  {
    this.startExtrapolation = extrapolation;
    this.endExtrapolation = extrapolation;
    return this;
  }

  public CubicSplineBuilder start(CubicSplineExtrapolation extrapolation)
  {
    this.startExtrapolation = extrapolation;
    return this;
  }

  public CubicSplineBuilder end(CubicSplineExtrapolation extrapolation)
  {
    this.endExtrapolation = extrapolation;
    return this;
  }

  public CubicSpline create()
  {
    // Allocate a new spline
    CubicSpline cs = new CubicSpline();

    // Set up conditions
    cs.startBoundary = this.startBoundary;
    cs.endBoundary = this.endBoundary;
    cs.startExtrapolation = startExtrapolation;
    cs.endExtrapolation = endExtrapolation;

    // Construct spline if x is specified
    if (x != null)
    {
      if (n == -1)
        n = x.length;
      if (x.length < n)
        throw new IllegalStateException("x size mismatch");
      if (y.length < n)
        throw new IllegalStateException("y size mismatch");
      cs.update(x, y, n);
    }
    return cs;
  }

}
