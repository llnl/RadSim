// --- file: private/gov/llnl/rtk/response/support/CubicSplineBoundary.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

/**
 * Plugin to specify the desired boundary condition for a spline.
 *
 * Boundary condition must set the values for the tridiagonal while eliminating
 * the lower element and normalizing the diagonal to zero.
 *
 * @author nelson85
 */
public interface CubicSplineBoundary
{
  public final static CubicSplineBoundary NATURAL
          = new CubicSplineBoundary()
  {
    @Override
    public void first(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n)
    {
      double diag = 2 / dx0;
      slope[n] = (3 * dy0 / dx0 / dx0) / diag;
      upper[n] = 1 / dx0 / diag;
    }

    @Override
    public void last(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n)
    {
      double lower = 1 / dx1;
      double diag = 2 / dx1 - lower * upper[n - 1];
      slope[n] = (3 * dy1 / dx1 / dx1 - lower * slope[n - 1]) / diag;
    }
  };

  public final static CubicSplineBoundary NOTAKNOT
          = new CubicSplineBoundary()
  {
    @Override
    public void first(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n)
    {
      // Jerk at first knot is continuous.
      //   The condition for jerk makes the formulation non-tridiagonal but we can add
      //   1/h1 times the second row to the first to eliminate the extra term.
      // Thus the formulation is an ordinary tridiagonal solver.
      double diag = 1 / dx0 * (1 / dx0 + 1 / dx1);
      slope[n] = (dy1 / dx1 / dx1 / dx1 + dy0 / dx0 / dx0 * (3 / dx1 + 2 / dx0)) / diag;
      upper[n] = (1 / dx0 / dx0 + 1 / dx1 / dx1 + 2 / dx1 / dx0) / diag;
    }

    @Override
    public void last(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n)
    {
      // Jerk at last knot is continuous.
      //   Same trick as with the first row.  We elimiminate the extra element using the 
      //   lower from the previous row.
      double lower = 1 / dx1 / dx1 + 1 / dx0 / dx0 + 2 / dx1 / dx0;
      double diag = 1 / dx1 * (1 / dx1 + 1 / dx0) - lower * upper[n - 1];
      slope[n] = (dy0 / dx0 / dx0 / dx0 + dy1 / dx1 / dx1 * (3 / dx0 + 2 / dx1) - lower * slope[n - 1]) / diag;
    }
  };

  /**
   * Create the row for the start of the spline.
   *
   * The diagonal of the last row is always 1.
   *
   * @param dx0
   * @param dy0
   * @param dx1
   * @param dy1
   * @param upper
   * @param slope
   * @param n is the index of the row to create.
   */
  void first(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n);

  /**
   * Create the row for the end of the spline.
   *
   * The diagonal of the last row is always 1.
   *
   * @param dx0
   * @param dy0
   * @param dx1
   * @param dy1
   * @param upper
   * @param slope
   * @param n is the index of the row to create.
   */
  void last(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n);

//<editor-fold desc="fixed">
  public static CubicSplineBoundary slope(double value)
  {
    return new CubicSplineBoundary()
    {
      @Override
      public void first(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n)
      {
        slope[n] = value;
        upper[n] = 0;
      }

      @Override
      public void last(double h0, double g0, double h1, double g1, double[] upper, double[] slope, int n)
      {
        slope[n] = value;
      }
    };
  }

  public static CubicSplineBoundary accel(double value)
  {
    return new CubicSplineBoundary()
    {
      @Override
      public void first(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n)
      {
        double diag = 2 / dx0;
        slope[n] = (3 * dy0 / dx0 / dx0 - value / 2) / diag;
        upper[n] = 1 / dx0 / diag;
      }

      @Override
      public void last(double dx0, double dy0, double dx1, double dy1, double[] upper, double[] slope, int n)
      {
        double lower = 1 / dx1;
        double diag = 2 / dx1 - lower * upper[n - 1];
        slope[n] = (3 * dy1 / dx1 / dx1 - lower * slope[n - 1] + value / 2) / diag;
      }
    };
  }
//</editor-fold>
}
