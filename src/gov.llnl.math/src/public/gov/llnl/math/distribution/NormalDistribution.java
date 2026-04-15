/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.distribution;

import gov.llnl.math.MathConstants;
import gov.llnl.math.SpecialFunctions;
import gov.llnl.utility.UUIDUtilities;

/**
 *
 * @author nelson85
 */
public class NormalDistribution implements Distribution
{
  private static final long serialVersionUID = UUIDUtilities.createLong("NormalDistribution");
  final double mean;
  final double sigma;

  public NormalDistribution()
  {
    this.mean = 0;
    this.sigma = 1;
  }

  public NormalDistribution(double mean, double std)
  {
    this.mean = mean;
    this.sigma = std;
  }

  @Override
  public String toString()
  {
    return "Normal Distribution(mean=" + mean + ", var=" + sigma + ")";
  }

  @Override
  public double cdf(double x)
  {
    return 0.5 * SpecialFunctions.erfc(-(x - mean) / sigma / MathConstants.SQRT_2);
  }

  @Override
  public double cdfinv(double x)
  {
    if (x < 0 || x > 1)
      throw new RuntimeException("input argument=" + x + ", expected value between 0 and 1");
    // Accuracy is really limited on this one.  Fails around 8 sigma.
    return -sigma * MathConstants.SQRT_2 * SpecialFunctions.erfcinv(2 * x) + mean;
  }

  @Override
  public double ccdf(double x)
  {
    return 0.5 * SpecialFunctions.erfc((x - mean) / sigma / MathConstants.SQRT_2);
  }

  public double ccdfinv(double x)
  {
    if (x < 0 || x > 1)
      throw new RuntimeException("input argument=" + x + ", expected value between 0 and 1");
    // Accuracy is really limited on this one.  Fails around 8 sigma.
    return -sigma * MathConstants.SQRT_2 * SpecialFunctions.erfcinv(2 * (1 - x)) + mean;
  }

  @Override
  public double logccdf(double x)
  {
    return -MathConstants.LOG_2 + SpecialFunctions.logerfc((x - mean) / sigma / MathConstants.SQRT_2);
  }

  @Override
  public double pdf(double x)
  {
    double d = sigma * MathConstants.SQRT_2PI;
    double z = (x - mean) * (x - mean) / sigma / sigma / 2;
    return Math.exp(-z) / d;
  }

  public double[] cdfinv(double[] x)
  {
    double[] out = new double[x.length];
    for (int i = 0; i < x.length; ++i)
      out[i] = cdfinv(x[i]);
    return out;
  }
}
