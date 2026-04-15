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
 * Test code for QuadraticFunction.
 */
public class QuadraticFunctionNGTest
{
  
  public QuadraticFunctionNGTest()
  {
  }
  
  /**
   * Test constructor of class QuadraticFunction.
   */
  @Test
  public void testConstructor()
  {
    QuadraticFunction instance = new QuadraticFunction();
  }

  /**
   * Test of applyAsDouble method, of class QuadraticFunction.
   */
  @Test
  public void testApplyAsDouble()
  {
    QuadraticFunction instance = new QuadraticFunction(1,2,3);
    assertEquals(instance.applyAsDouble(0.0), 1.0, 0.0);
    assertEquals(instance.applyAsDouble(1.0), 6.0, 0.0);
  }

  /**
   * Test of inverse method, of class QuadraticFunction.
   */
  @Test (expectedExceptions = {
    MathExceptions.RangeException.class
  })
  public void testInverse()
  {
    QuadraticFunction instance = new QuadraticFunction(1,2,3);
    assertEquals(instance.inverse(1.0), 0.0, 0.0);
    assertEquals(instance.inverse(6.0), 1.0, 0.0);
    instance = new QuadraticFunction(1,2,0);
    assertEquals(instance.inverse(1.0), 0.0, 0.0);
    instance = new QuadraticFunction(1,1,1);
    assertEquals(instance.inverse(1), -0.0, 0.0);
    instance = new QuadraticFunction(-1,2,-3);
    assertEquals(instance.inverse(1), -0.0, 0.0);
  }

  /**
   * Test of derivative method, of class QuadraticFunction.
   */
  @Test
  public void testDerivative()
  {
    QuadraticFunction instance = new QuadraticFunction(1,2,3);
    assertEquals(instance.derivative(0.0), 2.0, 0.0);
    assertEquals(instance.derivative(1.0), 2.0+6.0, 0.0);
  }

  /**
   * Test of toArray method, of class QuadraticFunction.
   */
  @Test
  public void testToArray()
  {
    QuadraticFunction instance = new QuadraticFunction(1,2,3);
    double[] expResult = new double[]{1,2,3};
    double[] result = instance.toArray();
    assertEquals(result, expResult);
  }

  /**
   * Test of clone method, of class QuadraticFunction.
   */
  @Test
  public void testClone() throws Exception
  {
    QuadraticFunction instance = new QuadraticFunction(1,2,3);
    QuadraticFunction result = instance.clone();
    assertNotNull(result);
    assertEquals(result, instance);
    assertNotSame(result, instance);
  }
  
  /**
   * Test of equals method, of class QuadraticFunction.
   */
  @Test
  public void testEquals()
  {
    QuadraticFunction instance = new QuadraticFunction(1,2,3);
    QuadraticFunction expectedInstance = new QuadraticFunction(1,2,3);
    boolean result = instance.equals(expectedInstance);
    assertTrue(result);
    result = instance.equals(new int[]{1,2,3});
    assertFalse(result);
  }
  
  /**
   * Test of hashCode method, of class QuadraticFunction.
   */
  @Test
  public void testHashCode()
  {
    QuadraticFunction instance = new QuadraticFunction(1,2,3);
    int result = instance.hashCode();
    assertEquals(result, 66614552);
  }
}
