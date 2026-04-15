/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import gov.llnl.math.interp.SingleInterpolator;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class CubicSplineNGTest
{

  public CubicSplineNGTest()
  {
  }

  @Test
  public void testUpdate()
  {
    double[] x = new double[]
    {
      0, 1, 2, 3, 4
    };
    double[] y = new double[]
    {
      0, 1, 4, 9, 16
    };
    CubicSpline instance = new CubicSplineBuilder(x, y).create();
    assertEquals(instance.m[0], 0.57142857, 1e-8);
    assertEquals(instance.m[1], 1.85714286, 1e-8);
    assertEquals(instance.m[2], 4, 1e-8);
    assertEquals(instance.m[3], 6.14285714, 1e-8);
    assertEquals(instance.m[4], 7.42857143, 1e-8);
  }

  @Test
  public void testUpdate2()
  {
    double[] x = new double[]
    {
      0, 1.5, 2
    };
    double[] y = new double[]
    {
      0, 1.5 * 1.5, 4
    };
    CubicSpline instance = new CubicSplineBuilder(x, y).create();
    assertEquals(instance.m[0], 0.75, 1e-8);
    assertEquals(instance.m[1], 3, 1e-8);
    assertEquals(instance.m[2], 3.75, 1e-8);
  }

  @Test
  public void testApply()
  {
    double[] x = new double[]
    {
      0, 1.5, 2
    };
    double[] y = new double[]
    {
      0, 1.5 * 1.5, 4
    };
    double[] xi = new double[]
    {
      -1, 0, 0.5, 1, 1.5, 2.0, 3.0
    };
    CubicSpline instance = new CubicSplineBuilder(x, y)
            .extrapolation(CubicSplineExtrapolation.CLAMP0)
            .create();
    double[] result = instance.get().applyAll(xi);
    assertEquals(result[0], 0.0, 0.0);
    assertEquals(result[1], 0.0, 0.0);
    assertEquals(result[2], 0.41666666666666, 1e-8);
    assertEquals(result[3], 1.08333333333333, 1e-8);
    assertEquals(result[4], 2.25, 0.0);
    assertEquals(result[5], 4.0, 0.0);
    assertEquals(result[6], 0.0, 0.0);
  }

  @Test
  public void testApplyAsDouble()
  {
    double[] x = new double[]
    {
      0, 1.5, 2
    };
    double[] y = new double[]
    {
      0, 1.5 * 1.5, 4
    };
    CubicSpline instance = new CubicSplineBuilder(x, y)
            .extrapolation(CubicSplineExtrapolation.CLAMP0).create();
    SingleInterpolator.Evaluator eval = instance.get();
    assertEquals(eval.applyAsDouble(-1), 0.0, 0.0);
    assertEquals(eval.applyAsDouble(0.0), 0.0, 0.0);
    assertEquals(eval.applyAsDouble(0.5), 0.41666666666666, 1e-8);
    assertEquals(eval.applyAsDouble(1.0), 1.08333333333333, 1e-8);
    assertEquals(eval.applyAsDouble(1.5), 2.25, 0.0);
    assertEquals(eval.applyAsDouble(2), 4.0, 0.0);
    assertEquals(eval.applyAsDouble(6.0), 0.0, 0.0);
  }

  @Test
  public void testNewEvaluator()
  {
    CubicSpline instance = new CubicSplineBuilder().create();
    SingleInterpolator.Evaluator result = instance.get();
    assertNotNull(result);
  }

}
