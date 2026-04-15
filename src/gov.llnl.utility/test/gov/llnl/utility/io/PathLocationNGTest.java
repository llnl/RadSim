/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.io;

import java.net.URI;
import java.net.URISyntaxException;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for PathLocation.
 */
public class PathLocationNGTest
{

  public PathLocationNGTest()
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
   * Test of toString method, of class PathLocation.
   */
  @Test
  public void testToString() throws URISyntaxException
  {
    String myUrl = "test.txt";
    URI myURI = new URI(myUrl);
    int lineNumber = 1;
    String section = "test";
    PathLocation instance = new PathLocation(myURI, lineNumber, section);
    String expResult = "test.txt:1#test";
    String result = instance.toString();
    assertEquals(result, expResult);
  }

}
