/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.interp;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for MultiLinearInterp.
 */
public class MultiLinearInterpNGTest
{

  public MultiLinearInterpNGTest()
  {
  }

  /**
   * Test of get and evaluate methods using linear interpolation.
   */
  @Test
  public void testInterpolation()
  {
    // Create test data
    double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
    double[] y1 = {10.0, 20.0, 30.0, 40.0, 50.0}; // Linear function
    double[] y2 = {100.0, 90.0, 80.0, 70.0, 60.0}; // Linear decreasing function

    // Create the interpolator
    MultiLinearInterp interp = new MultiLinearInterp(x, y1, y2);

    // Get an evaluator from the interpolator
    MultiInterpolator.Evaluator evaluator = interp.get();

    // Verify evaluator size
    assertEquals(evaluator.size(), 2);

    // Test interpolation at exact points
    evaluator.seek(1.0);
    assertEquals(evaluator.evaluate(0), 10.0, 1e-12);
    assertEquals(evaluator.evaluate(1), 100.0, 1e-12);

    evaluator.seek(3.0);
    assertEquals(evaluator.evaluate(0), 30.0, 1e-12);
    assertEquals(evaluator.evaluate(1), 80.0, 1e-12);

    evaluator.seek(5.0);
    assertEquals(evaluator.evaluate(0), 50.0, 1e-12);
    assertEquals(evaluator.evaluate(1), 60.0, 1e-12);

    // Test interpolation between points
    evaluator.seek(1.5);
    assertEquals(evaluator.evaluate(0), 15.0, 1e-12); // Halfway between 10 and 20
    assertEquals(evaluator.evaluate(1), 95.0, 1e-12); // Halfway between 100 and 90

    evaluator.seek(2.75);
    assertEquals(evaluator.evaluate(0), 27.5, 1e-12); // 75% between 20 and 30
    assertEquals(evaluator.evaluate(1), 82.5, 1e-12); // 75% between 90 and 80

    evaluator.seek(4.2);
    assertEquals(evaluator.evaluate(0), 42.0, 1e-12); // 20% between 40 and 50
    assertEquals(evaluator.evaluate(1), 68.0, 1e-12); // 20% between 70 and 60
  }

  /**
   * Test interpolator with multiple functions and non-uniform spacing.
   */
  @Test
  public void testNonUniformSpacing()
  {
    // Create test data with non-uniform spacing
    double[] x = {1.0, 2.0, 4.0, 7.0, 11.0};
    double[] y1 = {10.0, 20.0, 40.0, 70.0, 110.0}; // Linear function matching x values
    double[] y2 = {100.0, 90.0, 70.0, 40.0, 0.0};  // Decreasing function
    double[] y3 = {0.0, 4.0, 16.0, 49.0, 121.0};   // Square function

    // Create the interpolator
    MultiLinearInterp interp = new MultiLinearInterp(x, y1, y2, y3);

    // Get an evaluator from the interpolator
    MultiInterpolator.Evaluator evaluator = interp.get();

    // Verify evaluator size
    assertEquals(evaluator.size(), 3);

    // Test interpolation at exact points
    evaluator.seek(2.0);
    assertEquals(evaluator.evaluate(0), 20.0, 1e-12);
    assertEquals(evaluator.evaluate(1), 90.0, 1e-12);
    assertEquals(evaluator.evaluate(2), 4.0, 1e-12);

    // Test interpolation between points
    evaluator.seek(3.0);
    double fraction = 0.5; // 3.0 is halfway between 2.0 and 4.0
    assertEquals(evaluator.evaluate(0), 20.0 + fraction * (40.0 - 20.0), 1e-12);
    assertEquals(evaluator.evaluate(1), 90.0 + fraction * (70.0 - 90.0), 1e-12);
    assertEquals(evaluator.evaluate(2), 4.0 + fraction * (16.0 - 4.0), 1e-12);

    // Test with a point near the end of the range
    evaluator.seek(10.0);
    fraction = 0.75; // 10.0 is 75% of the way from 7.0 to 11.0
    assertEquals(evaluator.evaluate(0), 70.0 + fraction * (110.0 - 70.0), 1e-12);
    assertEquals(evaluator.evaluate(1), 40.0 + fraction * (0.0 - 40.0), 1e-12);
    assertEquals(evaluator.evaluate(2), 49.0 + fraction * (121.0 - 49.0), 1e-12);
  }

  /**
   * Test behavior at edge cases (extrapolation).
   */
  @Test
  public void testExtrapolation()
  {
    // Create test data
    double[] x = {1.0, 2.0, 3.0, 4.0, 5.0};
    double[] y1 = {10.0, 20.0, 30.0, 40.0, 50.0}; // Linear function
    double[] y2 = {100.0, 90.0, 80.0, 70.0, 60.0}; // Linear decreasing function

    // Create the interpolator
    MultiLinearInterp interp = new MultiLinearInterp(x, y1, y2);

    // Get an evaluator from the interpolator
    MultiInterpolator.Evaluator evaluator = interp.get();

    // Test extrapolation below the lowest point
    evaluator.seek(0.0);
    // The expected behavior is that it will use the first interval for extrapolation
    assertEquals(evaluator.evaluate(0), 0.0, 1e-12); // Extrapolated from first interval
    assertEquals(evaluator.evaluate(1), 110.0, 1e-12); // Extrapolated from first interval

    // Test extrapolation above the highest point
    evaluator.seek(6.0);
    // The expected behavior is that it will use the last interval for extrapolation
    assertEquals(evaluator.evaluate(0), 60.0, 1e-12); // Extrapolated from last interval
    assertEquals(evaluator.evaluate(1), 50.0, 1e-12); // Extrapolated from last interval
  }
}