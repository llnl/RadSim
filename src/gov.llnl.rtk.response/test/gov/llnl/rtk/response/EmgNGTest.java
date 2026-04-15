/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.response.EmgUtilities;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class EmgNGTest
{
  @Test
  public void testComputeMode()
  {
    assertEquals(EmgUtilities.computeMode(0), 0.0, 0.0);
    assertEquals(EmgUtilities.computeMode(1), 0.6973749521727918, 0.0);
    assertEquals(EmgUtilities.computeMode(2), 1.0179191624370438, 0.0);
  }

  @Test
  public void testComputeFwhm()
  {
    double theta = 0.5;
    double negativeTail = 1.0;
    double positiveTail = 1.0;
    double expResult = 2.83316435172986;
    double result = EmgUtilities.computeFwhm(theta, negativeTail, positiveTail);
    assertEquals(result, expResult, 1e-8);
  }

  @Test
  public void testEmg11()
  {
    double x = 0.0;
    double mu = 10.0;
    double sigma = 1.0;
    double theta = 0.5;
    double tau_m = 1.0;
    double tau_p = 0.4;
    double expResult = 3.742_591_494_385_03e-5;
    double result = EmgUtilities.emg11(x, mu, sigma, theta, tau_m, tau_p);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testEmg11pdf()
  {
    double x = 0.0;
    double mu_m = 1.0;
    double mu_p = 1.0;
    double sigma = 1.0;
    double theta = 0.5;
    double tau_m = 1.0;
    double tau_p = 0.4;
    double expResult = 0.23225966016652425;
    double result = EmgUtilities.emg11pdf(x, mu_m, mu_p, sigma, theta, tau_m, tau_p);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGauscdf()
  {
    double x = 0.0;
    double expResult = 0.5;
    double result = EmgUtilities.gauscdf(x);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testEmg11cdf()
  {
    double x = 0.0;
    double mu_m = 1.0;
    double mu_p = 1.0;
    double sigma = 1.0;
    double theta = 0.5;
    double tau_m = 1.0;
    double tau_p = 0.4;
    double expResult = 1 - 0.7219628792357309;
    double result = EmgUtilities.emg11cdf(x, mu_m, mu_p, sigma, theta, tau_m, tau_p);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testEmg11ccdf()
  {
    double x = 0.0;
    double mu_m = 1.0;
    double mu_p = 1.0;
    double sigma = 1.0;
    double theta = 0.5;
    double tau_m = 1.0;
    double tau_p = 0.4;
    double expResult = 0.7219628792357309;
    double result = EmgUtilities.emg11ccdf(x, mu_m, mu_p, sigma, theta, tau_m, tau_p);
    assertEquals(result, expResult, 0.0);
  }

}
