/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.distribution;

import gov.llnl.math.SpecialFunctions;
import gov.llnl.utility.UUIDUtilities;

/**
 *
 * @author nelson85
 */
public class ChiSquaredDistribution implements Distribution
{
  private static final long serialVersionUID = UUIDUtilities.createLong("ChiSquaredDistribution");
  double k; // degree of freedom

  public ChiSquaredDistribution(double d)
  {
    k = d;
  }

  @Override
  public String toString()
  {
    return "ChiSquared Distribution df=" + k;
  }

  @Override
  public double pdf(double x)
  {
    if (x < 0)
      return 0;

    if (x == 0)
    {
      if (k < 2)
        return Double.POSITIVE_INFINITY; // Limit approaches infinity
      if (k == 2)
        return 0.5;                      // f(0; 2) = 0.5 * e^0
      return 0;                          // For k > 2
    }

    // Standard calculation for x > 0
    return Math.exp(
            -Math.log(2) * k / 2
            - SpecialFunctions.gammaln(k / 2)
            + (k / 2 - 1) * Math.log(x)
            - x / 2);
  }

  @Override
  public double cdf(double x)
  {
    return SpecialFunctions.gammaP(k / 2, x / 2);
  }

  @Override
  public double ccdf(double x)
  {
    return SpecialFunctions.gammaQ(k / 2, x / 2);
  }

  @Override
  public double logccdf(double x)
  {
    return Math.log(ccdf(x));
  }

  @Override
  public double cdfinv(double x)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
