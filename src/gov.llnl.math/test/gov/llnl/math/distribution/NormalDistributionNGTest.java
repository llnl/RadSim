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
 * Test code for NormalDistribution.
 */
public class NormalDistributionNGTest
{

  public NormalDistributionNGTest()
  {
  }

  /**
   * Test of pdf method, of class NormalDistribution.
   */
  @Test
  public void testPdf()
  {
    // Test standard normal distribution (mean=0, std=1)
    NormalDistribution instance = new NormalDistribution();

    // Test at mean (should be 1/sqrt(2*pi))
    double result = instance.pdf(0.0);
    assertEquals(result, 0.3989422804014327, 1e-12);

    // Test at +/- 1 sigma
    assertEquals(instance.pdf(1.0), 0.24197072451914337, 1e-12);
    assertEquals(instance.pdf(-1.0), 0.24197072451914337, 1e-12);

    // Test at +/- 2 sigma
    assertEquals(instance.pdf(2.0), 0.05399096651318806, 1e-12);
    assertEquals(instance.pdf(-2.0), 0.05399096651318806, 1e-12);

    // Test with different mean and sigma
    NormalDistribution instance2 = new NormalDistribution(10.0, 2.0);
    assertEquals(instance2.pdf(10.0), 0.19947114020071635, 1e-12); // at mean
    assertEquals(instance2.pdf(12.0), 0.12098536225957168, 1e-12); // at mean + 1 sigma
  }

  /**
   * Test of cdf method, of class NormalDistribution.
   */
  @Test
  public void testCdf()
  {
    // Test standard normal distribution
    NormalDistribution instance = new NormalDistribution();

    // Test at mean (should be 0.5)
    assertEquals(instance.cdf(0.0), 0.5, 1e-12);

    // Test at +/- 1 sigma
    assertEquals(instance.cdf(1.0), 0.8413447460685429, 1e-12);
    assertEquals(instance.cdf(-1.0), 0.15865525393145705, 1e-12);

    // Test at +/- 2 sigma
    assertEquals(instance.cdf(2.0), 0.9772498680518208, 1e-12);
    assertEquals(instance.cdf(-2.0), 0.022750131948179195, 1e-12);

    // Test with different mean and sigma
    NormalDistribution instance2 = new NormalDistribution(10.0, 2.0);
    assertEquals(instance2.cdf(10.0), 0.5, 1e-12); // at mean
    assertEquals(instance2.cdf(12.0), 0.8413447460685429, 1e-12); // at mean + 1 sigma
  }

  /**
   * Test of cdfinv method, of class NormalDistribution.
   */
  @Test
  public void testCdfinv()
  {
    // Test standard normal distribution
    NormalDistribution instance = new NormalDistribution();

    // Test inverse of cdf(0) = 0.5
    assertEquals(instance.cdfinv(0.5), 0.0, 1e-12);

    // Test inverse of cdf(1) = 0.8413447460685429
    assertEquals(instance.cdfinv(0.8413447460685429), 1.0, 1e-8);

    // Test inverse of cdf(-1) = 0.15865525393145705
    assertEquals(instance.cdfinv(0.15865525393145705), -1.0, 1e-8);

    // Test with different mean and sigma
    NormalDistribution instance2 = new NormalDistribution(10.0, 2.0);
    assertEquals(instance2.cdfinv(0.5), 10.0, 1e-12); // should return mean
    assertEquals(instance2.cdfinv(0.8413447460685429), 12.0, 1e-8); // should return mean + 1 sigma
  }

  /**
   * Test of cdfinv method with out-of-range values.
   */
  @Test(expectedExceptions = RuntimeException.class)
  public void testCdfinvOutOfRange1()
  {
    NormalDistribution instance = new NormalDistribution();
    instance.cdfinv(-0.1); // Should throw exception for negative probability
  }

  /**
   * Test of cdfinv method with out-of-range values.
   */
  @Test(expectedExceptions = RuntimeException.class)
  public void testCdfinvOutOfRange2()
  {
    NormalDistribution instance = new NormalDistribution();
    instance.cdfinv(1.1); // Should throw exception for probability > 1
  }

  /**
   * Test of ccdf method, of class NormalDistribution.
   */
  @Test
  public void testCcdf()
  {
    // Test standard normal distribution
    NormalDistribution instance = new NormalDistribution();

    // Test at mean (should be 0.5)
    assertEquals(instance.ccdf(0.0), 0.5, 1e-12);

    // Test at +/- 1 sigma
    assertEquals(instance.ccdf(1.0), 0.15865525393145705, 1e-12);
    assertEquals(instance.ccdf(-1.0), 0.8413447460685429, 1e-12);

    // Verify ccdf + cdf = 1
    double x = 1.5;
    assertEquals(instance.ccdf(x) + instance.cdf(x), 1.0, 1e-12);
  }

  /**
   * Test of logccdf method, of class NormalDistribution.
   */
  @Test
  public void testLogccdf()
  {
    // Test standard normal distribution
    NormalDistribution instance = new NormalDistribution();

    // Test at various points, comparing to log(ccdf(x))
    double x = 1.0;
    assertEquals(instance.logccdf(x), Math.log(instance.ccdf(x)), 1e-12);

    x = 2.0;
    assertEquals(instance.logccdf(x), Math.log(instance.ccdf(x)), 1e-12);

    x = 3.0;
    assertEquals(instance.logccdf(x), Math.log(instance.ccdf(x)), 1e-12);
  }

  /**
   * Test of cdfinv array method, of class NormalDistribution.
   */
  @Test
  public void testCdfinvArray()
  {
    double[] probs = {0.5, 0.8413447460685429, 0.15865525393145705};
    NormalDistribution instance = new NormalDistribution();

    double[] result = instance.cdfinv(probs);
    assertEquals(result.length, probs.length);
    assertEquals(result[0], 0.0, 1e-12);
    assertEquals(result[1], 1.0, 1e-8);
    assertEquals(result[2], -1.0, 1e-8);
  }

  /**
   * Test of toString method, of class NormalDistribution.
   */
  @Test
  public void testToString()
  {
    NormalDistribution instance = new NormalDistribution(1.0, 2.0);
    String result = instance.toString();
    assertEquals(result, "Normal Distribution(mean=1.0, var=2.0)");
  }
}