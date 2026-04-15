/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for TemporalUtilities.
 */
public class TemporalUtilitiesNGTest
{

  public TemporalUtilitiesNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    TemporalUtilities instance = new TemporalUtilities();
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

  /**
   * Test of toSeconds method, of class TemporalUtilities.
   */
  @Test
  public void testToSeconds_Duration()
  {
    double expResult = 6.0;
    double result = TemporalUtilities.toSeconds(MutableDuration.ofSeconds(6));
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of toSeconds method, of class TemporalUtilities.
   */
  @Test
  public void testToSeconds_TemporalAmount()
  {
    double expResult = 6.0;
    double result = TemporalUtilities.toSeconds(Duration.ofSeconds(6));
    assertEquals(result, expResult, 0.0);
    TemporalAmount value = Duration.ofSeconds(5, 1000000);
    expResult = 5.001;
    result = TemporalUtilities.toSeconds(value);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of ofSeconds method, of class TemporalUtilities.
   */
  @Test
  public void testOfSeconds()
  {
    TemporalAmount expResult = Duration.ofSeconds(5, 1000000);
    TemporalAmount result = TemporalUtilities.ofSeconds(5.001000000);
    assertEquals(result, expResult);
  }

}
