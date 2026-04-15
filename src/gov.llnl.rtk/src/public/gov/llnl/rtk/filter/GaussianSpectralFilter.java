// --- file: gov/llnl/rtk/filter/GaussianSpectralFilter.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.filter;

import static gov.llnl.math.DoubleUtilities.sqr;
import gov.llnl.math.MathConstants;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnTable;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.math.matrix.MatrixViews;
import java.util.LinkedList;

/**
 *
 * @author nelson85
 */
public class GaussianSpectralFilter implements SpectralFilter
{
  double[] centers;
  Matrix post;
  Matrix filter;

  public void createFilter(int channels, double factor, double lambda)
  {
    try
    {
      centers = inumerateCenters(4, channels, factor);
      Matrix m = new MatrixColumnTable(channels, centers.length);
      for (int i0 = 0; i0 < centers.length; i0++)
      {
        double x0 = centers[i0];
        double sigma2 = factor * x0;
        double k = 1 / Math.sqrt(2 * Math.PI * sigma2);
        double k2 = 1 / 2.0 / sigma2;
        for (int i1 = 0; i1 < channels; i1++)
        {
          m.set(i1, i0, k * Math.exp(-k2 * sqr(i1 - x0)));
        }
      }

      // T=m*inv(m'*m+lambda*eye(length(y)))*m';
      Matrix r = MatrixOps.multiply(m.transpose(), m);
      MatrixOps.addAssign(MatrixViews.diagonal(r), lambda);
      Matrix q = m.transpose().copyOf();
      q = MatrixOps.divideLeft(r, q);
      filter = q;
      post = m;
    }
    catch (MathExceptions.SizeException ex)
    {
      throw new RuntimeException(ex); // does not occur
    }
  }

  static public double[] inumerateCenters(double initial, double limit, double factor)
  {
    LinkedList<Double> out = new LinkedList<>();
    while (true)
    {
      out.add(initial);
      double step = Math.sqrt(initial * factor) * MathConstants.GAUSSIAN_FWHM / 2;
      initial += step;
      if (initial > limit)
        break;
    }
    double out2[] = new double[out.size()];
    for (int i = 0; i < out2.length; ++i)
    {
      out2[i] = out.get(i);
    }
    return out2;
  }

  @Override
  public Matrix apply(Matrix in) throws MathExceptions.SizeException
  {
    return MatrixOps.multiply(post, MatrixOps.multiply(filter, in));
  }

  @Override
  public double[] apply(double[] in) throws MathExceptions.SizeException
  {
    return MatrixOps.multiply(post, MatrixOps.multiply(filter, in));
  }

}
