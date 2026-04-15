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
 * Test code for ChiSquaredDistribution.
 */
public class ChiSquaredDistributionNGTest
{

  public ChiSquaredDistributionNGTest()
  {
  }

  /**
   * Test of pdf method, of class ChiSquaredDistribution.
   */
  @Test
  public void testPdf()
  {
    double x = 1.0;
    ChiSquaredDistribution instance = new ChiSquaredDistribution(2.0);
    double expResult = 0.303265329856317; // Expected value for chi-square with 2 degrees of freedom at x=1
    double result = instance.pdf(x);
    assertEquals(result, expResult, 1e-12);

    // Test at x=0 (boundary case)
    assertEquals(instance.pdf(0.0), 0.5, 1e-12);

    // Test negative value (should be 0)
    assertEquals(instance.pdf(-1.0), 0.0, 1e-12);

    // Test with different degrees of freedom
    ChiSquaredDistribution instance4df = new ChiSquaredDistribution(4.0);
    assertEquals(instance4df.pdf(3.0), 0.168, 1e-3);
  }

  /**
   * Test of cdf method, of class ChiSquaredDistribution.
   */
  @Test
  public void testCdf()
  {
    double x = 2.0;
    ChiSquaredDistribution instance = new ChiSquaredDistribution(2.0);
    double expResult = 0.632120558828558; // Expected value for chi-square with 2 degrees of freedom at x=2
    double result = instance.cdf(x);
    assertEquals(result, expResult, 1e-9);

    // Test at x=0 (boundary case)
    assertEquals(instance.cdf(0.0), 0.0, 1e-12);

    // Test negative value (should be 0)
    assertEquals(instance.cdf(-1.0), 0.0, 1e-12);
  }

  /**
   * Test of ccdf method, of class ChiSquaredDistribution.
   */
  @Test
  public void testCcdf()
  {
    double x = 2.0;
    ChiSquaredDistribution instance = new ChiSquaredDistribution(2.0);
    double expResult = 1.0 - 0.632120558828558; // 1 - cdf
    double result = instance.ccdf(x);
    assertEquals(result, expResult, 1e-9);
  }

  /**
   * Test of logccdf method, of class ChiSquaredDistribution.
   */
  @Test
  public void testLogccdf()
  {
    double x = 2.0;
    ChiSquaredDistribution instance = new ChiSquaredDistribution(2.0);
    double expResult = Math.log(1.0 - 0.632120558828558); // log(1 - cdf)
    double result = instance.logccdf(x);
    assertEquals(result, expResult, 1e-10);
  }

  /**
   * Test of cdfinv method, of class ChiSquaredDistribution.
   * This method throws UnsupportedOperationException.
   */
  @Test(expectedExceptions = UnsupportedOperationException.class)
  public void testCdfinv()
  {
    double p = 0.5;
    ChiSquaredDistribution instance = new ChiSquaredDistribution(2.0);
    instance.cdfinv(p);
  }

  /**
   * Test of toString method, of class ChiSquaredDistribution.
   */
  @Test
  public void testToString()
  {
    ChiSquaredDistribution instance = new ChiSquaredDistribution(3.0);
    String result = instance.toString();
    assertEquals(result, "ChiSquared Distribution df=3.0");
  }
}