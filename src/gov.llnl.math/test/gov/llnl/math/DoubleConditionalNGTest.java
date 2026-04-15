/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test code for DoubleConditional.
 */
public class DoubleConditionalNGTest
{
  static final double EPS = 1e-6;

  /**
   * Test of test method from class GreaterThan, of class DoubleConditional.
   */
  @Test
  public void testEvaluateGreaterThan()
  {
    DoubleConditional cond = new DoubleConditional.GreaterThan(5);
    Assert.assertTrue(!cond.test(5.0 - EPS));
    Assert.assertTrue(!cond.test(5.0));
    Assert.assertTrue(cond.test(5.0 + EPS));
  }

  /**
   * Test of test method from class GreaterThanEqual, of class
   * DoubleConditional.
   */
  @Test
  public void testEvaluateGreaterThanEqual()
  {
    DoubleConditional cond = new DoubleConditional.GreaterThanEqual(5);
    Assert.assertTrue(!cond.test(5.0 - EPS));
    Assert.assertTrue(cond.test(5.0));
    Assert.assertTrue(cond.test(5.0 + EPS));
  }

  /**
   * Test of test method from class LessThan, of class DoubleConditional.
   */
  @Test
  public void testEvaluateLessThan()
  {
    DoubleConditional cond = new DoubleConditional.LessThan(5);
    Assert.assertTrue(cond.test(5.0 - EPS));
    Assert.assertTrue(!cond.test(5.0));
    Assert.assertTrue(!cond.test(5.0 + EPS));
  }

  /**
   * Test of test method from class LessThanEqual, of class DoubleConditional.
   */
  @Test
  public void testEvaluateLessThanEqual()
  {
    DoubleConditional cond = new DoubleConditional.LessThanEqual(5);
    Assert.assertTrue(cond.test(5.0 - EPS));
    Assert.assertTrue(cond.test(5.0));
    Assert.assertTrue(!cond.test(5.0 + EPS));
  }

  /**
   * Test of test method from class NotEqual, of class DoubleConditional.
   */
  @Test
  public void testEvaluateNotEqual()
  {
    DoubleConditional cond = new DoubleConditional.NotEqual(1);
    Assert.assertTrue(cond.test(5.0 - EPS));
    Assert.assertTrue(cond.test(5.0));
    Assert.assertTrue(cond.test(5.0 + EPS));
  }

}
