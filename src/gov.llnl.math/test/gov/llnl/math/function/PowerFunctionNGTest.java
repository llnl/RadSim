/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.function;

import static java.lang.Double.NaN;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for PowerFunction.
 */
public class PowerFunctionNGTest
{
  
  public PowerFunctionNGTest()
  {
  }

  /**
   * Test of inverse method, of class PowerFunction.
   */
  @Test
  public void testInverse()
  {
    PowerFunction instance = new PowerFunction(1,2);
    assertEquals(instance.inverse(1.0), 1.0, 0.0);
    assertEquals(instance.inverse(4.0), 2.0, 0.0);
    assertTrue(Double.isNaN(instance.inverse(0.0)));
  }

  /**
   * Test of applyAsDouble method, of class PowerFunction.
   */
  @Test
  public void testApplyAsDouble()
  {
    PowerFunction instance = new PowerFunction(1,2);
    assertEquals(instance.applyAsDouble(1.0), 1.0, 0.0);
    assertEquals(instance.applyAsDouble(2.0), 4.0, 0.0);
  }

  /**
   * Test of toArray method, of class PowerFunction.
   */
  @Test
  public void testToArray()
  {
    PowerFunction instance = new PowerFunction(1,2);
    double[] expResult = new double[]{1,2};
    double[] result = instance.toArray();
    assertEquals(result, expResult);
  }
  
  /**
   * Test of equals method, of class PowerFunction.
   */
  @Test
  public void testEquals()
  {
    PowerFunction instance = new PowerFunction(1,2);
    PowerFunction expectedInstance = new PowerFunction(1,2);
    boolean result = instance.equals(expectedInstance);
    assertTrue(result);
    expectedInstance = new PowerFunction(-1,2);
    result = instance.equals(expectedInstance);
    assertFalse(result);
    expectedInstance = new PowerFunction(1,-2);
    result = instance.equals(expectedInstance);
    assertFalse(result);
    result = instance.equals(new int[]{1,2,3});
    assertFalse(result);
  }
  
  /**
   * Test of hashCode method, of class PowerFunction.
   */
  @Test
  public void testHashCode()
  {
    PowerFunction instance = new PowerFunction(1,2);
    int result = instance.hashCode();
    assertEquals(result, -49272027);
  }
  
}
