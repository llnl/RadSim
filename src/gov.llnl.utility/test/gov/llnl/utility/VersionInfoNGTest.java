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
 * Test code for VersionInfo.
 */
public class VersionInfoNGTest
{

  public VersionInfoNGTest()
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
   * Test of getVersion method, of class VersionInfo.
   */
  @Test
  public void testGetVersion()
  {
    VersionInfo instance = new VersionInfo();
    String expResult = "2.0.0.0";
    String result = instance.getVersion();
    assertEquals(result, expResult);
  }

  /**
   * Test of getVersionString method, of class VersionInfo.
   */
  @Test
  public void testGetVersionString()
  {
    VersionInfo instance = new VersionInfo();
    String expResult = "gov.llnl.utility(2.0.0.0,";
    String result = instance.getVersionString();
    assertTrue(result.startsWith(expResult));
  }
}
