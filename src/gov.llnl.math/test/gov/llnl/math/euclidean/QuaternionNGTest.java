/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.euclidean;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Quaternion.
 */
public class QuaternionNGTest
{
  
  public QuaternionNGTest()
  {
  }

  /**
   * Test of of method, of class Quaternion.
   */
  @Test
  public void testOf()
  {
    double u = 0.0;
    double i = 0.0;
    double j = 0.0;
    double k = 0.0;
    Quaternion expResult = new QuaternionImpl(u, i, j, k);
    Quaternion result = Quaternion.of(u, i, j, k);
    assertEquals(result.getClass(), expResult.getClass());
    assertEquals(result.getU(), expResult.getU(), 0.0);
    assertEquals(result.getX(), expResult.getX(), 0.0);
    assertEquals(result.getY(), expResult.getY(), 0.0);
    assertEquals(result.getZ(), expResult.getZ(), 0.0);
  }
  
}
