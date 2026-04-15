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
 * Test code for MutableDouble.
 */
public class MutableDoubleNGTest
{

  /**
   * Test of intValue method, of class MutableDouble.
   */
  @Test
  public void testIntValue()
  {
    MutableDouble instance = new MutableDouble(4.1);
    int expResult = (int) 4.1;
    int result = instance.intValue();
    assertEquals(result, expResult);
  }

  /**
   * Test of longValue method, of class MutableDouble.
   */
  @Test
  public void testLongValue()
  {
    MutableDouble instance = new MutableDouble(Long.MAX_VALUE);
    long expResult = (long) ((double) Long.MAX_VALUE);
    long result = instance.longValue();
    assertEquals(result, expResult);
  }

  /**
   * Test of floatValue method, of class MutableDouble.
   */
  @Test
  public void testFloatValue()
  {
    MutableDouble instance = new MutableDouble(Float.MAX_VALUE);
    float expResult = Float.MAX_VALUE;
    float result = instance.floatValue();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of doubleValue method, of class MutableDouble.
   */
  @Test
  public void testDoubleValue()
  {
    MutableDouble instance = new MutableDouble(Double.MAX_VALUE);
    double expResult = Double.MAX_VALUE;
    double result = instance.doubleValue();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of setValue method, of class MutableDouble.
   */
  @Test
  public void testSetValue()
  {
    MutableDouble instance = new MutableDouble();
    instance.setValue(Double.MAX_VALUE);
    assertEquals(instance.doubleValue(), Double.MAX_VALUE);
  }

}
