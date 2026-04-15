/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for DurationUtilities.
 */
public class DurationUtilitiesNGTest
{

  public DurationUtilitiesNGTest()
  {
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
   * Test of format method, of class DurationUtilities.
   */
  @Test
  public void testFormat1() throws Exception
  {
    String duration = "99";
    String expResult = "99";
    String result = DurationUtilities.format(duration);
    assertEquals(result, expResult);
  }

  /**
   * Test of format method, of class DurationUtilities.
   */
  @Test
  public void testFormat2() throws Exception
  {
    String duration = "PT8M20.00001231231222339234334S";
    String expResult = "PT8M20.000012312S";
    String result = DurationUtilities.format(duration);
    assertEquals(result, expResult);
  }
}
