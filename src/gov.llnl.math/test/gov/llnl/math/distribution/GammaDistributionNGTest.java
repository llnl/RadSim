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
 * Test code for GammaDistribution.
 */
public class GammaDistributionNGTest
{

  public GammaDistributionNGTest()
  {
  }

  /**
   * Test of pdf method, of class GammaDistribution.
   */
  @Test
  public void testPdf()
  {
    // Test Gamma distribution with shape=2, scale=2
    GammaDistribution instance = new GammaDistribution(2.0, 2.0);

    // Test at x=0 (boundary case)
    assertEquals(instance.pdf(0.0), 0.0, 1e-12);

    // Test at x=2 (should be 0.25*e^-1)
    assertEquals(instance.pdf(2.0), 0.5 * Math.exp(-1.0), 1e-12);

    // Test at x=4 (should be 0.25*e^-2)
    assertEquals(instance.pdf(4.0), 1.0 * Math.exp(-2.0), 1e-12);

    // Test negative value (should be 0)
    assertEquals(instance.pdf(-1.0), 0.0, 1e-12);

    // Test with different shape and scale
    GammaDistribution instance2 = new GammaDistribution(1.0, 1.0); // Exponential distribution
    assertEquals(instance2.pdf(1.0), Math.exp(-1.0), 1e-12);
    assertEquals(instance2.pdf(2.0), Math.exp(-2.0), 1e-12);
  }

  /**
   * Test of cdf method, of class GammaDistribution.
   */
  @Test
  public void testCdf()
  {
    // Test Gamma distribution with shape=1, scale=1 (exponential distribution)
    GammaDistribution instance = new GammaDistribution(1.0, 1.0);

    // Test at x=0 (boundary case)
    assertEquals(instance.cdf(0.0), 0.0, 1e-12);

    // Test at x=1 (should be 1-e^-1)
    assertEquals(instance.cdf(1.0), 1 - Math.exp(-1.0), 1e-10);

    // Test at x=2 (should be 1-e^-2)
    assertEquals(instance.cdf(2.0), 1 - Math.exp(-2.0), 1e-12);

    // Test with shape=2, scale=2
    GammaDistribution instance2 = new GammaDistribution(2.0, 2.0);
    assertEquals(instance2.cdf(2.0), 0.264241117657115, 1e-12);
    assertEquals(instance2.cdf(4.0), 0.5939941502901619, 1e-12);

    // Test that CDF is increasing
    assertTrue(instance2.cdf(1.0) < instance2.cdf(2.0));
    assertTrue(instance2.cdf(2.0) < instance2.cdf(3.0));
    assertTrue(instance2.cdf(3.0) < instance2.cdf(4.0));
  }

  /**
   * Test of ccdf method, of class GammaDistribution.
   */
  @Test
  public void testCcdf()
  {
    // Test Gamma distribution with shape=1, scale=1 (exponential distribution)
    GammaDistribution instance = new GammaDistribution(1.0, 1.0);

    // Test at x=0 (boundary case)
    assertEquals(instance.ccdf(0.0), 1.0, 1e-12);

    // Test at x=1 (should be e^-1)
    assertEquals(instance.ccdf(1.0), Math.exp(-1.0), 1e-10);

    // Test at x=2 (should be e^-2)
    assertEquals(instance.ccdf(2.0), Math.exp(-2.0), 1e-12);

    // Verify ccdf + cdf = 1
    double x = 2.5;
    assertEquals(instance.ccdf(x) + instance.cdf(x), 1.0, 1e-12);

    // Test with shape=2, scale=2
    GammaDistribution instance2 = new GammaDistribution(2.0, 2.0);
    assertEquals(instance2.ccdf(2.0), 1 - 0.264241117657115, 1e-12);
    assertEquals(instance2.ccdf(4.0), 0.40600584970983805, 1e-9);

    // Test that CCDF is decreasing
    assertTrue(instance2.ccdf(1.0) > instance2.ccdf(2.0));
    assertTrue(instance2.ccdf(2.0) > instance2.ccdf(3.0));
    assertTrue(instance2.ccdf(3.0) > instance2.ccdf(4.0));
  }

  /**
   * Test of logccdf method, of class GammaDistribution.
   */
  @Test
  public void testLogccdf()
  {
    // Test Gamma distribution with shape=1, scale=1 (exponential distribution)
    GammaDistribution instance = new GammaDistribution(1.0, 1.0);

    // Test at various points, comparing to log(ccdf(x))
    double x = 1.0;
    assertEquals(instance.logccdf(x), Math.log(instance.ccdf(x)), 1e-12);

    x = 2.0;
    assertEquals(instance.logccdf(x), Math.log(instance.ccdf(x)), 1e-12);

    x = 5.0;
    assertEquals(instance.logccdf(x), Math.log(instance.ccdf(x)), 1e-12);

    // Test with different shape and scale
    GammaDistribution instance2 = new GammaDistribution(2.0, 3.0);
    x = 4.0;
    assertEquals(instance2.logccdf(x), Math.log(instance2.ccdf(x)), 1e-12);
  }

  /**
   * Test of cdfinv method, of class GammaDistribution.
   * This method throws UnsupportedOperationException.
   */
  @Test(expectedExceptions = UnsupportedOperationException.class)
  public void testCdfinv()
  {
    double p = 0.5;
    GammaDistribution instance = new GammaDistribution(1.0, 1.0);
    instance.cdfinv(p);
  }

  /**
   * Test of toString method, of class GammaDistribution.
   */
  @Test
  public void testToString()
  {
    GammaDistribution instance = new GammaDistribution(2.0, 3.0);
    String result = instance.toString();
    assertEquals(result, "Gamma Distribution shape=2.0 scale=3.0");
  }
}