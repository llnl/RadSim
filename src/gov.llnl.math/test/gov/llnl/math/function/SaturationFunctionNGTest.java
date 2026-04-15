/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.function;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for SaturationFunction.
 */
public class SaturationFunctionNGTest
{
  
  public SaturationFunctionNGTest()
  {
  }

  /**
   * Test of applyAsDouble method, of class SaturationFunction.
   */
  @Test
  public void testApplyAsDouble()
  {
    SaturationFunction instance = new SaturationFunction(1.0,10.0,2.0);
    assertEquals(instance.applyAsDouble(0), 1.0, 0.0);
    assertEquals(instance.applyAsDouble(1.0), (10.0*1.0+1.0)/(1+2.0), 0.0);
    assertEquals(instance.applyAsDouble(10.0), (10.0*10.0+1.0)/(1+2.0*10), 0.0);
  }

  /**
   * Test of toArray method, of class SaturationFunction.
   */
  @Test
  public void testToArray()
  {
    SaturationFunction instance = new SaturationFunction(1,2,3);
    double[] expResult = new double[]{3,2,1};
    double[] result = instance.toArray();
    assertEquals(result, expResult);
  }
  
  /**
   * Test of equals method, of class SaturationFunction.
   */
  @Test
  public void testEquals()
  {
    SaturationFunction instance = new SaturationFunction(1, 2, 3);
    SaturationFunction expectedInstance = new SaturationFunction(1, 2, 3);
    boolean result = instance.equals(expectedInstance);
    assertTrue(result);
    expectedInstance = new SaturationFunction(-1, 2, 3);
    result = instance.equals(expectedInstance);
    assertFalse(result);
    expectedInstance = new SaturationFunction(1, -2, 3);
    result = instance.equals(expectedInstance);
    assertFalse(result);
    expectedInstance = new SaturationFunction(1, 2, -3);
    result = instance.equals(expectedInstance);
    assertFalse(result);
    result = instance.equals(new int[]{1,2,3});
    assertFalse(result);
  }
  
  /**
   * Test of hashCode method, of class SaturationFunction.
   */
  @Test
  public void testHashCode()
  {
    SaturationFunction instance = new SaturationFunction(1,2,3);
    int result = instance.hashCode();
    assertEquals(result, 1108722833);
  }
}
