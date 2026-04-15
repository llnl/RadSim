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
 * Test code for PolynomialFunction.
 */
public class PolynomialFunctionNGTest
{

  public PolynomialFunctionNGTest()
  {
  }

  /**
   * Test of toString method, of class PolynomialFunction.
   */
  @Test
  public void testToString()
  {
    PolynomialFunction instance = new PolynomialFunction(1, 2, 3);
    String expResult = "polynomial(1.0, 2.0, 3.0)";
    String result = instance.toString();
    assertEquals(result, expResult);
  }

  /**
   * Test of applyAsDouble method, of class PolynomialFunction.
   */
  @Test
  public void testApplyAsDouble()
  {
    PolynomialFunction instance = new PolynomialFunction(1, 2, 3);
    assertEquals(instance.applyAsDouble(0), 3.0, 0.0);
    assertEquals(instance.applyAsDouble(1), 6.0, 0.0);
  }

  /**
   * Test of toArray method, of class PolynomialFunction.
   */
  @Test
  public void testToArray()
  {
    PolynomialFunction instance = new PolynomialFunction(1, 2, 3);
    double[] expResult = new double[]
    {
      1, 2, 3
    };
    double[] result = instance.toArray();
    assertEquals(result, expResult);
  }

  /**
   * Test of polyval method, of class PolynomialFunction.
   */
  @Test
  public void testPolyval()
  {
    double[] coef = new double[]
    {
      3, 2, 1
    };
    assertEquals(PolynomialFunction.polyval(0.0, coef), 3.0, 0.0);
    assertEquals(PolynomialFunction.polyval(1.0, coef), 6.0, 0.0);
    assertEquals(PolynomialFunction.polyval(2.0, coef), 11.0, 0.0);
  }

  /**
   * Test of polyval2 method, of class PolynomialFunction.
   */
  @Test
  public void testPolyval2()
  {
    // Not currently used in code.
  }

  @Test(expectedExceptions =
  {
    NullPointerException.class
  })
  public void testPolynomialFunctionConstructor()
  {
    PolynomialFunction instance = new PolynomialFunction(null);
  }
  
  /**
   * Test of equals method, of class PolynomialFunction.
   */
  @Test
  public void testEquals()
  {
    PolynomialFunction instance = new PolynomialFunction(1, 2, 3);
    PolynomialFunction expectedInstance = new PolynomialFunction(1, 2, 3);
    boolean result = instance.equals(expectedInstance);
    assertTrue(result);
    result = instance.equals(new int[]{1,2,3});
    assertFalse(result);
  }
}
