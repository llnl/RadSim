/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for PropertyUtilities.
 */
public class PropertyUtilitiesNGTest
{

  public PropertyUtilitiesNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    PropertyUtilities instance = new PropertyUtilities();
  }

  /**
   * Test of get method, of class PropertyUtilities.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testGet()
  {
    assertEquals(PropertyUtilities.get("java.home", "Nakama"), System.getProperty("java.home"));
    assertEquals(PropertyUtilities.get("ONE_PIECE", "Nakama"), "Nakama");
    assertEquals(PropertyUtilities.get("java.home", 1999), Integer.valueOf(1999));
    // Test RuntimeException
    PropertyUtilities.get("ONE_PIECE", null);
  }
}
