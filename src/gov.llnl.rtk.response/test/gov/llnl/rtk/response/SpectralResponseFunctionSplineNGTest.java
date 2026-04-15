/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class SpectralResponseFunctionSplineNGTest
{

  public SpectralResponseFunctionSplineNGTest()
  {
  }

  @Test
  public void testNewEvaluator()
  {
    SpectralResponseFunctionSplineBuilder builder = new SpectralResponseFunctionSplineBuilder();
    builder.photon().energy(10).continuum(new double[0], new int[3], 1.5)
            .line(RenderItem.PHOTOELECTRIC, 10, 1, 1)
            .create();
    builder.photon().energy(20).continuum(new double[0], new int[3], 1.5)
            .line(RenderItem.PHOTOELECTRIC, 20, 1, 1)
            .create();
    builder.photon().energy(30).continuum(new double[0], new int[3], 1.5)
            .line(RenderItem.PHOTOELECTRIC, 30, 1, 1)
            .create();
    SpectralResponseFunctionSpline instance = builder.create();
    SpectralResponseEvaluator result = instance.newEvaluator();
    assertNotNull(result);
  }

  @Test
  public void testDump()
  {
    // Debug code to be removed
  }

  @Test
  public void testHashCode()
  {
    SpectralResponseFunctionSpline instance = new SpectralResponseFunctionSpline();
    int expResult = 272650567;
    int result = instance.hashCode();
    assertEquals(result, expResult);
  }

  @Test
  public void testEquals()
  {
    Object obj = new SpectralResponseFunctionSpline();
    SpectralResponseFunctionSpline instance = new SpectralResponseFunctionSpline();
    assertTrue(instance.equals(obj));
  }

  @Test
  public void testCreatePoints()
  {
//    double[] x = new double[15];
//    double r1 = 10.0;
//    double r2 = 20.0;
//    double r3 = 30.0;
//    int s1 = 5;
//    int s2 = 5;
//    int s3 = 5;
//    double[] expResult = TestSupport.base64DecodeToDoubles(
//                    "AAAADz/++dsi0OVgQBCsCDEm6XpAG1P3ztkWiEAiIMSbpeNVQCQAAAAAAABAJhR64Ue"
//                    + "uFEArCj1wo9cLQDB64UeuFHtAMvXCj1wo9kA0AAAAAAAAQDUKPXCj1wpAN4UeuFHr"
//                    + "hUA6euFHrhR7QDz1wo9cKPZAPgAAAAAAAA==");
//    SpectralResponseFunctionSpline instance = new SpectralResponseFunctionSpline();
//    instance.createPoints(x, new double[]{r1, r2, r3}, new int[]{s1, s2, s3});
//    assertEquals(x, expResult);
  }
}
