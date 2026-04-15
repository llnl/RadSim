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
 * Test code for QuaternionOps.
 */
public class QuaternionOpsNGTest
{
  
  public QuaternionOpsNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    QuaternionOps instance = new QuaternionOps();
  }
  
  /**
   * Test of multiply method, of class QuaternionOps.
   */
  @Test
  public void testMultiply_Quaternion_Quaternion()
  {
    Quaternion q1 = new QuaternionImpl(1, 2, 3, 4);
    Quaternion q2 = new QuaternionImpl(1, 1, 1, 1);
    Quaternion expResult = new QuaternionImpl(-8, 2, 6, 4);
    Quaternion result = QuaternionOps.multiply(q1, q2);
    assertEquals(result.getClass(), expResult.getClass());
    assertEquals(result.getU(), expResult.getU(), 0.0);
    assertEquals(result.getX(), expResult.getX(), 0.0);
    assertEquals(result.getY(), expResult.getY(), 0.0);
    assertEquals(result.getZ(), expResult.getZ(), 0.0);
  }

  /**
   * Test of multiply method, of class QuaternionOps.
   */
  @Test
  public void testMultiply_Quaternion_Vector3()
  {
    Quaternion q1 = new QuaternionImpl(1, 2, 3, 4);
    Vector3 q2 = new Vector3() {
      @Override
      public double getX()
      {
        return 1.0;
      }

      @Override
      public double getY()
      {
        return 1.0;
      }

      @Override
      public double getZ()
      {
        return 1.0;
      }
    };
    Quaternion expResult = new QuaternionImpl(-9, 0, 3, 0);
    Quaternion result = QuaternionOps.multiply(q1, q2);
    assertEquals(result.getClass(), expResult.getClass());
    assertEquals(result.getU(), expResult.getU());
    assertEquals(result.getX(), expResult.getX());
    assertEquals(result.getY(), expResult.getY());
    assertEquals(result.getZ(), expResult.getZ());
  }
  
}
