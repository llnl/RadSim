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
 * Test code for FunctionFactory.
 */
public class FunctionFactoryNGTest
{
  
  public FunctionFactoryNGTest()
  {
  }
  
  /**
   * Test constructor of class FunctionFactory.
   */
  public void testConstructor()
  {
    FunctionFactory instance = new FunctionFactory();
  }

  /**
   * Test of newLinear method, of class FunctionFactory.
   */
  @Test
  public void testNewLinear()
  {
    double k = 1.0;
    LinearFunction result = FunctionFactory.newLinear(k);
    assertEquals(result.applyAsDouble(0), 0.0);
    assertEquals(result.applyAsDouble(1), 1.0);
  }

  /**
   * Test of newQuadratic method, of class FunctionFactory.
   */
  @Test
  public void testNewQuadratic()
  {
    double offset = 1.0;
    double slope = 2.0;
    double accel = 3.0;
    QuadraticFunction result = FunctionFactory.newQuadratic(offset, slope, accel);
    assertEquals(result.applyAsDouble(0), 1.0, 0.0);
    assertEquals(result.applyAsDouble(1), 6.0, 0.0);
  }

  /**
   * Test of newPolynomial method, of class FunctionFactory.
   */
  @Test
  public void testNewPolynomial()
  {
    double[] a = new double[]{1,2,3};
    PolynomialFunction result = FunctionFactory.newPolynomial(a);
    assertEquals(result.applyAsDouble(0), 3.0, 0.0);
    assertEquals(result.applyAsDouble(1), 6.0, 0.0);
  }

  /**
   * Test of newPower method, of class FunctionFactory.
   */
  @Test
  public void testNewPower()
  {
    double k = 1.0;
    double p = 2.0;
    PowerFunction expResult = null;
    PowerFunction result = FunctionFactory.newPower(k, p);
    assertEquals(result.applyAsDouble(0), 0.0, 0.0);
    assertEquals(result.applyAsDouble(1), 1.0, 0.0);
    assertEquals(result.applyAsDouble(2), 4.0, 0.0);
  }
  
}
