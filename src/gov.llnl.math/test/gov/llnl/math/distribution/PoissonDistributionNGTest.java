/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.distribution;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for PoissonDistribution.
 */
public class PoissonDistributionNGTest
{

  public PoissonDistributionNGTest()
  {
  }

  /**
   * Test of pdf method, of class PoissonDistribution.
   */
  @Test
  public void testPdf()
  {
    // Test Poisson distribution with lambda = 1
    PoissonDistribution instance = new PoissonDistribution(1.0);

    // Test at k=0 (should be e^-1)
    assertEquals(instance.pdf(0.0), Math.exp(-1.0), 1e-12);

    // Test at k=1 (should be e^-1)
    assertEquals(instance.pdf(1.0), Math.exp(-1.0), 1e-12);

    // Test at k=2 (should be e^-1 / 2)
    assertEquals(instance.pdf(2.0), Math.exp(-1.0) / 2.0, 1e-12);

    // Test at non-integer value (should be 0)
    assertEquals(instance.pdf(1.5), 0.0, 1e-12);

    // Test with different lambda
    PoissonDistribution instance2 = new PoissonDistribution(5.0);
    assertEquals(instance2.pdf(5.0), 0.1754, 1e-4);
  }

  /**
   * Test of cdf method, of class PoissonDistribution.
   */
  @Test
  public void testCdf()
  {
    // Test Poisson distribution with lambda = 1
    PoissonDistribution instance = new PoissonDistribution(1.0);

    // Test at k=0
    assertEquals(instance.cdf(0.0), Math.exp(-1.0), 1e-12);

    // Test at k=1
    assertEquals(instance.cdf(1.0), 0.7357588823428846, 1e-12);

    // Test with lambda = 5
    PoissonDistribution instance2 = new PoissonDistribution(5.0);

    // Verify cdf is increasing
    assertTrue(instance2.cdf(0.0) < instance2.cdf(1.0));
    assertTrue(instance2.cdf(4.0) < instance2.cdf(5.0));
    assertTrue(instance2.cdf(9.0) < instance2.cdf(10.0));

    // Verify cdf approaches 1 for large values
    assertEquals(instance2.cdf(20.0), 0.999999918907495400, 1e-8);
  }

  /**
   * Test of ccdf method, of class PoissonDistribution.
   */
  @Test
  public void testCcdf()
  {
    // Test Poisson distribution with lambda = 1
    PoissonDistribution instance = new PoissonDistribution(1.0);

    // Test at k=0
    assertEquals(instance.ccdf(0.0), 1.0 - Math.exp(-1.0), 1e-12);

    // Verify ccdf + cdf = 1
    double k = 2.0;
    assertEquals(instance.ccdf(k) + instance.cdf(k), 1.0, 1e-12);

    // Test with lambda = 5
    PoissonDistribution instance2 = new PoissonDistribution(5.0);

    // Verify ccdf is decreasing
    assertTrue(instance2.ccdf(0.0) > instance2.ccdf(1.0));
    assertTrue(instance2.ccdf(4.0) > instance2.ccdf(5.0));
    assertTrue(instance2.ccdf(9.0) > instance2.ccdf(10.0));

    // Verify ccdf approaches 0 for large values
    assertEquals(instance2.ccdf(20.0), 0.000000081092504599, 1e-8);
  }

  /**
   * Test of logccdf method, of class PoissonDistribution.
   */
  @Test
  public void testLogccdf()
  {
    // Test Poisson distribution with lambda = 1
    PoissonDistribution instance = new PoissonDistribution(1.0);

    // Test at various points, comparing to log(ccdf(x))
    double k = 1.0;
    assertEquals(instance.logccdf(k), Math.log(instance.ccdf(k)), 1e-12);

    k = 2.0;
    assertEquals(instance.logccdf(k), Math.log(instance.ccdf(k)), 1e-12);

    // Test with lambda = 5
    PoissonDistribution instance2 = new PoissonDistribution(5.0);
    k = 3.0;
    assertEquals(instance2.logccdf(k), Math.log(instance2.ccdf(k)), 1e-12);
  }

  /**
   * Test of cdfinv method, of class PoissonDistribution. This method throws
   * UnsupportedOperationException.
   */
  @Test(expectedExceptions = UnsupportedOperationException.class)
  public void testCdfinv()
  {
    double p = 0.5;
    PoissonDistribution instance = new PoissonDistribution(1.0);
    instance.cdfinv(p);
  }
}
