/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class CubicSplineFactoryNGTest
{

  public CubicSplineFactoryNGTest()
  {
  }

  @Test
  public void testBoundary()
  {
    CubicSplineBoundary boundary = CubicSplineBoundary.NOTAKNOT;
    CubicSplineBuilder instance = new CubicSplineBuilder();
    CubicSplineBuilder result = instance.boundary(boundary);
    assertEquals(result.startBoundary, boundary);
    assertEquals(result.endBoundary, boundary);
  }

  @Test
  public void testStart_CubicSplineBoundary()
  {
    CubicSplineBoundary boundary = CubicSplineBoundary.NOTAKNOT;
    CubicSplineBuilder instance = new CubicSplineBuilder();
    CubicSplineBuilder result = instance.start(boundary);
    assertEquals(result.startBoundary, boundary);
    assertNotEquals(result.endBoundary, boundary);
  }

  @Test
  public void testEnd_CubicSplineBoundary()
  {
    CubicSplineBoundary boundary = CubicSplineBoundary.NOTAKNOT;
    CubicSplineBuilder instance = new CubicSplineBuilder();
    CubicSplineBuilder result = instance.end(boundary);
    assertEquals(result.startBoundary, CubicSplineBoundary.NATURAL);
    assertEquals(result.endBoundary, boundary);
  }

  @Test
  public void testExtrapolation()
  {
    CubicSplineExtrapolation extrapolation = CubicSplineExtrapolation.CLAMP0;
    CubicSplineBuilder instance = new CubicSplineBuilder();
    CubicSplineBuilder result = instance.extrapolation(extrapolation);
    assertEquals(result.startExtrapolation, extrapolation);
    assertEquals(result.endExtrapolation, extrapolation);
  }

  @Test
  public void testStart_CubicSplineExtrapolation()
  {
    CubicSplineExtrapolation extrapolation = CubicSplineExtrapolation.CLAMP0;
    CubicSplineBuilder instance = new CubicSplineBuilder();
    CubicSplineBuilder result = instance.start(extrapolation);
    assertEquals(result.startExtrapolation, extrapolation);
    assertNotEquals(result.endExtrapolation, extrapolation);
  }

  @Test
  public void testEnd_CubicSplineExtrapolation()
  {
    CubicSplineExtrapolation extrapolation = CubicSplineExtrapolation.CLAMP0;
    CubicSplineBuilder instance = new CubicSplineBuilder();
    CubicSplineBuilder result = instance.end(extrapolation);
    assertEquals(result.endExtrapolation, extrapolation);
    assertNotEquals(result.startExtrapolation, extrapolation);
  }

  @Test
  public void testCreate()
  {
    CubicSplineBuilder instance = new CubicSplineBuilder();
    CubicSpline result = instance.create();
    assertNotNull(result);
  }

}
