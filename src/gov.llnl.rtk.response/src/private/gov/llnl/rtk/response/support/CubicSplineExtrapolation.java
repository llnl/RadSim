// --- file: private/gov/llnl/rtk/response/support/CubicSplineExtrapolation.java ---
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
public interface CubicSplineExtrapolation
{
  double apply(double f,
          double x0, double y0, double m0,
          double x1, double y1, double m1);

//  double integrate(double f0, double f1,
//          double x0, double y0, double m0,
//          double x1, double y1, double m1);
  static CubicSplineExtrapolation CLAMP = new CubicSplineExtrapolation()
  {
    @Override
    public double apply(double f, double x0, double y0, double m0, double x1, double y1, double m1)
    {
      if (f <= 0)
        return y0;
      if (f >= 1)
        return y1;
      throw new RuntimeException();
    }

//    @Override
//    public double integrate(double f0, double f1,
//            double x0, double y0, double m0,
//            double x1, double y1, double m1)
//    {
//      double h = x1 - x0;
//      if (f1 <= 0)
//        return y0 * (f1 - f0) * h;
//      if (f0 >= 1)
//        return y1 * (f1 - f0) * h;
//      throw new RuntimeException();
//    }
  };

  static CubicSplineExtrapolation CLAMP0 = new CubicSplineExtrapolation()
  {
    @Override
    public double apply(double f, double x0, double y0, double m0, double x1, double y1, double m1)
    {
      return 0;
    }

//    @Override
//    public double integrate(double f0, double f1,
//            double x0, double y0, double m0,
//            double x1, double y1, double m1)
//    {
//      return 0;
//    }
  };

  static CubicSplineExtrapolation LINEAR = new CubicSplineExtrapolation()
  {
    @Override
    public double apply(double f, double x0, double y0, double m0, double x1, double y1, double m1)
    {
      if (f <= 0)
        return y0 + m0 * (x1 - x0) * f;
      if (f >= 1)
        return y1 + m1 * (x1 - x0) * (f - 1);
      throw new RuntimeException();
    }

//    @Override
//    public double integrate(double f0, double f1, double x0, double y0, double m0, double x1, double y1, double m1)
//    {
//      double h = x1 - x0;
//      double b = h * (f1 - f0);
//      if (f1 <= 0)
//        return m0 * h * b * b / 2;
//      if (f0 >= 1)
//        return m1 * h * b * b / 2;
//      throw new RuntimeException();
//    }
  };

  static CubicSplineExtrapolation QUADRATIC = new CubicSplineExtrapolation()
  {
    @Override
    public double apply(double f, double x0, double y0, double m0, double x1, double y1, double m1)
    {
      double h = x1 - x0;
      double a = (m1 - m0) / h;
      if (f < 0)
      {
        double x = h * f;
        return 0.5 * a * x * x + m0 * x + y0;
      }
      else
      {
        double x = h * (f - 1);
        return 0.5 * a * x * x + m1 * x + y1;
      }
    }

//    @Override
//    public double integrate(double f0, double f1, double x0, double y0, double m0, double x1, double y1, double m1)
//    {
//      double h = x1 - x0;
//      double a = (m1 - m0) / h;
//
//      if (f1 <= 0)
//      {
//        double xr0 = h * f0;
//        double xr1 = h * f1;
//        return (0.5 / 3 * a * xr1 * xr1 * xr1 + 0.5 * m0 * xr1 * xr1)
//                - (0.5 / 3 * a * xr0 * xr0 * xr0 + 0.5 * m0 * xr0 * xr0);
//      }
//      if (f0 >= 1)
//      {
//        double xr0 = h * (f0 - 1);
//        double xr1 = h * (f1 - 1);
//        return (0.5 / 3 * a * xr1 * xr1 * xr1 + 0.5 * m1 * xr1 * xr1)
//                - (0.5 / 3 * a * xr0 * xr0 * xr0 + 0.5 * m1 * xr0 * xr0);
//      }
//      throw new RuntimeException();
//    }
  };

}
