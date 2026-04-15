/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.function;

import java.util.function.DoubleUnaryOperator;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for FunctionUtilities.
 */
public class FunctionUtilitiesNGTest
{
  
  public FunctionUtilitiesNGTest()
  {
  }
  
  /**
   * Test constructor of class FunctionUtilities.
   */
  @Test
  public void testConstructor()
  {
    FunctionUtilities instance = new FunctionUtilities();
  }

  /**
   * Test of evaluate method, of class FunctionUtilities.
   */
  @Test
  public void testEvaluate()
  {
    DoubleUnaryOperator function = new LinearFunction(1,2);
    double[] values = new double[]{1,2,3,4,5};
    double[] expResult = new double[]{3,5,7,9,11};
    double[] result = FunctionUtilities.evaluate(function, values);
    assertEquals(result, expResult);
  }

  /**
   * Test of evaluateRange method, of class FunctionUtilities.
   */
  @Test
  public void testEvaluateRange()
  {
    DoubleUnaryOperator function = new LinearFunction(1,2);
    double[] values = new double[]{1,2,3,4,5};
    double[] expResult = new double[]{5,7,9};
    double[] result = FunctionUtilities.evaluateRange(function, values,1,4);
    assertEquals(result, expResult);
  }
  
}
