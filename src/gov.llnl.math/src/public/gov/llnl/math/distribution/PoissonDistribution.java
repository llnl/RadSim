/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.distribution;

import static gov.llnl.math.SpecialFunctions.gammaP;
import static gov.llnl.math.SpecialFunctions.gammaQ;
import static gov.llnl.math.SpecialFunctions.gammaln;
import gov.llnl.utility.UUIDUtilities;
import static java.lang.Math.floor;

/**
 *
 * @author seilhan3
 */
public class PoissonDistribution implements Distribution
{
  private static final long serialVersionUID = UUIDUtilities.createLong("PoissonDistribution");
  final double lambda;

  public PoissonDistribution(double lambda)
  {
    this.lambda = lambda;
  }

  @Override
  public double pdf(double x)
  {
    if (floor(x) != x)
      return 0;
    //return Math.exp(-lambda)*Math.pow(lambda, c)
    return Math.exp(-lambda + x * Math.log(lambda) - gammaln(1 + x));
  }

  @Override
  public double cdf(double x)
  {
    return gammaQ(Math.floor(x + 1), lambda);
  }

  @Override
  public double ccdf(double x)
  {
    return gammaP(Math.floor(x + 1), lambda);
  }
  
  @Override
  public double logccdf(double x)
  {
    return Math.log(ccdf(x));
  }

  @Override
  public double cdfinv(double x)
  {
    throw new UnsupportedOperationException();
  }

}
