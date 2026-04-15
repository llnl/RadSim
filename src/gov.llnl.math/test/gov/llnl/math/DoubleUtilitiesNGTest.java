/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for DoubleUtilities.
 */
public class DoubleUtilitiesNGTest
{

  /**
   * Test of sqr method, of class DoubleUtilities.
   */
  @Test
  public void testSqr()
  {
    double d = -2.0;
    double expResult = 4.0;
    double result = DoubleUtilities.sqr(d);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of cube method, of class DoubleUtilities.
   */
  @Test
  public void testCube()
  {
    double d = -2.0;
    double expResult = -8.0;
    double result = DoubleUtilities.cube(d);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of pow32 method, of class DoubleUtilities.
   */
  @Test
  public void testPow32()
  {
    double d = 4.0;
    double expResult = 8.0;
    double result = DoubleUtilities.pow32(d);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of clamp method, of class DoubleUtilities.
   */
  @Test
  public void testClamp()
  {
    assertEquals(DoubleUtilities.clamp(-1, 0, 1), 0.0);
    assertEquals(DoubleUtilities.clamp(0.5, 0, 1), 0.5);
    assertEquals(DoubleUtilities.clamp(2, 0, 1), 1.0);
  }

  /**
   * Test of solveDestructive method, of class DoubleUtilities.
   */
  @Test
  public void testSolveDestructive()
  {
    // Deprecated not used
//    double[] C = null;
//    double[] A = null;
//    int sz = 0;
//    double[] expResult = null;
//    double[] result = DoubleUtilities.solveDestructive(C, A, sz);
//    assertEquals(result, expResult);
  }

  /**
   * Test of clip method, of class RPM8ComputedVehicleExtractor2.
   */
  @Test
  public void testClip()
  {
    assertEquals(gov.llnl.math.DoubleUtilities.clip(-1, 0, 1), 0.0, 0.0);
    assertEquals(gov.llnl.math.DoubleUtilities.clip(2, 0, 1), 1.0, 0.0);
    assertEquals(gov.llnl.math.DoubleUtilities.clip(0.5, 0, 1), 0.5, 0.0);
  }

  /**
   * Test of clip equivalent, of class RPM8ComputedVehicleExtractor2.
   */
  @Test
  public void testEquivalent()
  {
    assertTrue(gov.llnl.math.DoubleUtilities.equivalent(1.0, 1.0));
    assertFalse(gov.llnl.math.DoubleUtilities.equivalent(1.0, 2.0));
    assertFalse(gov.llnl.math.DoubleUtilities.equivalent(2.0, 1.0));
  }
}
