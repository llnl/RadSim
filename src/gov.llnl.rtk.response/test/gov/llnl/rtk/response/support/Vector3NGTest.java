/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import gov.llnl.math.euclidean.Vector3;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class Vector3NGTest
{
  
  public Vector3NGTest()
  {
  }

  @Test
  public void testOf()
  {
    double x = 1.0;
    double y = 2.0;
    double z = 3.0;
    Vector3 result = Vector3.of(x, y, z);
    assertEquals(result.getX(), x, 1.0);
    assertEquals(result.getY(), y, 2.0);
    assertEquals(result.getZ(), z, 3.0);
  }

  @Test
  public void testGetX()
  {
//    System.out.println("getX");
//    Vector3 instance = null;
//    double expResult = 0.0;
//    double result = instance.getX();
//    assertEquals(result, expResult, 0.0);
//    fail("The test case is a prototype.");
  }

  @Test
  public void testGetY()
  {
//    System.out.println("getY");
//    Vector3 instance = null;
//    double expResult = 0.0;
//    double result = instance.getY();
//    assertEquals(result, expResult, 0.0);
//    fail("The test case is a prototype.");
  }

  @Test
  public void testGetZ()
  {
//    System.out.println("getZ");
//    Vector3 instance = null;
//    double expResult = 0.0;
//    double result = instance.getZ();
//    assertEquals(result, expResult, 0.0);
//    fail("The test case is a prototype.");
  }

  @Test
  public void testNorm()
  {
    Vector3 instance = Vector3.of(2,3,6);
    double expResult = 7.0;
    double result = instance.norm();
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testDot()
  {
    Vector3 instance = Vector3.of(1,2,3);
    Vector3 vec2 = Vector3.of(5,6,7);
    double result = instance.dot(vec2);
    assertEquals(result, 1*5+2*6+3*7, 0.0);
  }

  @Test
  public void testNorm2()
  {
    Vector3 instance = Vector3.of(2,3,6);
    double expResult = 49;
    double result = instance.norm2();
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testToArray()
  {
    Vector3 instance = Vector3.of(1,2,3);
    double[] expResult = {1,2,3};
    double[] result = instance.toArray();
    assertEquals(result, expResult);
  }

  
}
