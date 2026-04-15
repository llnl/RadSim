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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for UnsignedUtilities.
 */
public class UnsignedUtilitiesNGTest
{

  public UnsignedUtilitiesNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    UnsignedUtilities instance = new UnsignedUtilities();
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
   * Test of getUnsignedShort method, of class UnsignedUtilities.
   */
  @Test
  public void testGetUnsignedShort()
  {
    short s = 0;
    int expResult = 0;
    int result = UnsignedUtilities.getUnsignedShort(s);
    assertEquals(result, expResult);
    s = -1;
    result = UnsignedUtilities.getUnsignedShort(s);
    assertEquals(result, 65535);
  }

  /**
   * Test of getUnsignedInt method, of class UnsignedUtilities.
   */
  @Test
  public void testGetUnsignedInt()
  {
    int i = 0;
    long expResult = 0L;
    long result = UnsignedUtilities.getUnsignedInt(i);
    assertEquals(result, expResult);
    i = -1;
    result = UnsignedUtilities.getUnsignedInt(i);
    assertEquals(result, 4294967295L);
  }

}
