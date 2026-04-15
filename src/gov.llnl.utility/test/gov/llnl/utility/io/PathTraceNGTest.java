/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.io;

import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for PathTrace.
 */
public class PathTraceNGTest
{

  public PathTraceNGTest()
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
   * Test of add method, of class PathTrace.
   */
  @Test
  public void testAdd()
  {
    PathLocation location = null;
    PathTrace instance = new PathTrace();
    instance.add(location);
  }

  /**
   * Test of toString method, of class PathTrace.
   */
  @Test
  public void testToString()
  {
    PathTrace instance = new PathTrace();
    String expResult = "";
    String result = instance.toString();
    assertEquals(result, expResult);
  }

}
