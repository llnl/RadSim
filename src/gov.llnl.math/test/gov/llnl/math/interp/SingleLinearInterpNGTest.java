/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.interp;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import java.lang.reflect.Constructor;

/**
 * Test code for SingleLinearInterp.
 */
public class SingleLinearInterpNGTest
{
  public SingleLinearInterpNGTest()
  {
  }

  /**
   * Test of get and evaluate methods using linear interpolation.
   *
   * Note: Since SingleLinearInterp is a package-private class, we need to use
   * reflection to access it for testing.
   */
  @Test
  public void testInterpolation() throws Exception
  {
    // Use reflection to create a SingleLinearInterp instance
    Constructor<?> constructor = SingleLinearInterp.class.getDeclaredConstructor(double[].class, double[].class);
    constructor.setAccessible(true);

    // Create test data
    double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
    double[] y = {10.0, 20.0, 30.0, 40.0, 50.0};

    // Create the interpolator using reflection
    SingleInterpolator interp = (SingleInterpolator) constructor.newInstance(x, y);

    // Get an evaluator from the interpolator
    SingleInterpolator.Evaluator evaluator = interp.get();

    // Test interpolation at exact points
    evaluator.seek(1.0);
    assertEquals(evaluator.evaluate(), 10.0, 1e-12);

    evaluator.seek(3.0);
    assertEquals(evaluator.evaluate(), 30.0, 1e-12);

    evaluator.seek(5.0);
    assertEquals(evaluator.evaluate(), 50.0, 1e-12);

    // Test interpolation between points
    evaluator.seek(1.5);
    assertEquals(evaluator.evaluate(), 15.0, 1e-12);

    evaluator.seek(2.75);
    assertEquals(evaluator.evaluate(), 27.5, 1e-12);

    evaluator.seek(4.2);
    assertEquals(evaluator.evaluate(), 42.0, 1e-12);

    // Test extrapolation behavior (if applicable)
    evaluator.seek(0.5);
    // This assumes the implementation extends the first interval
    double extrapolatedValue = evaluator.evaluate();
    assertEquals(extrapolatedValue, 5.0, 1e-12);

    evaluator.seek(5.5);
    // This assumes the implementation extends the last interval
    extrapolatedValue = evaluator.evaluate();
    assertEquals(extrapolatedValue, 55.0, 1e-12);
  }

  /**
   * Test with non-uniform spacing in x values.
   */
  @Test
  public void testNonUniformSpacing() throws Exception
  {
    // Use reflection to create a SingleLinearInterp instance
    Constructor<?> constructor = SingleLinearInterp.class.getDeclaredConstructor(double[].class, double[].class);
    constructor.setAccessible(true);

    // Create test data with non-uniform spacing
    double[] x = {1.0, 2.0, 4.0, 7.0, 11.0};
    double[] y = {10.0, 20.0, 40.0, 70.0, 110.0};

    // Create the interpolator using reflection
    SingleInterpolator interp = (SingleInterpolator) constructor.newInstance(x, y);
    SingleInterpolator.Evaluator evaluator = interp.get();

    // Test interpolation at midpoints
    evaluator.seek(1.5);
    assertEquals(evaluator.evaluate(), 15.0, 1e-12);

    evaluator.seek(3.0);
    assertEquals(evaluator.evaluate(), 30.0, 1e-12);

    evaluator.seek(5.5);
    assertEquals(evaluator.evaluate(), 55.0, 1e-12);

    evaluator.seek(9.0);
    assertEquals(evaluator.evaluate(), 90.0, 1e-12);
  }
}