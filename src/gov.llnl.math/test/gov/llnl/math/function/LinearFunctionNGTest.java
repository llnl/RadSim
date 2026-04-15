/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.function;

import gov.llnl.math.MathExceptions;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for LinearFunction.
 */
public class LinearFunctionNGTest
{
  
  public LinearFunctionNGTest()
  {
  }
  
  /**
   * Test constructor of class LinearFunction.
   */
  @Test
  public void testConstructor()
  {
    LinearFunction instance = new LinearFunction();
  }

  /**
   * Test of applyAsDouble method, of class LinearFunction.
   */
  @Test
  public void testApplyAsDouble()
  {
    LinearFunction instance = new LinearFunction(1,2);
    assertEquals(instance.applyAsDouble(0.0), 1.0, 0.0);
    assertEquals(instance.applyAsDouble(1.0), 3.0, 0.0);
  }

  /**
   * Test of inverse method, of class LinearFunction.
   */
  @Test (expectedExceptions = {
    MathExceptions.RangeException.class
  })
  public void testInverse()
  {
    LinearFunction instance = new LinearFunction(1, 2);
    assertEquals(instance.inverse(1.0), 0.0, 0.0);
    assertEquals(instance.inverse(3.0), 1.0, 0.0);
    instance.slope = 0;
    instance.inverse(1.0);
  }

  /**
   * Test of derivative method, of class LinearFunction.
   */
  @Test
  public void testDerivative()
  {
    double y = 0.0;
    LinearFunction instance = new LinearFunction(1,2);
    assertEquals(instance.derivative(0),2.0, 0.0);
    assertEquals(instance.derivative(1),2.0, 0.0);
  }

  /**
   * Test of clone method, of class LinearFunction.
   */
  @Test
  public void testClone() throws Exception
  {
    LinearFunction instance = new LinearFunction(1,2);
    LinearFunction result = instance.clone();
    assertEquals(result, instance);
    assertNotSame(result, instance);
  }

  /**
   * Test of toArray method, of class LinearFunction.
   */
  @Test
  public void testToArray()
  {
    LinearFunction instance = new LinearFunction(1,2);
    double[] expResult = new double[]{1,2};
    double[] result = instance.toArray();
    assertEquals(result, expResult);
  }
  
  /**
   * Test of equals method, of class LinearFunction.
   */
  @Test
  public void testEquals()
  {
    LinearFunction instance = new LinearFunction(1,2);
    instance.slope = 1;
    instance.offset = 1;
    LinearFunction expectedInstance = new LinearFunction(1,2);
    expectedInstance.slope = 1;
    expectedInstance.offset = 1;
    boolean result = instance.equals(expectedInstance);
    assertTrue(result);
    // Unequal slope
    instance.slope = 0;
    result = instance.equals(expectedInstance);
    assertFalse(result);
    // Unequal offset
    instance.offset = 0;
    instance.slope = 1;
    result = instance.equals(expectedInstance);
    assertFalse(result);
    result = instance.equals(new int[]{});
    assertFalse(result);
  }
}
