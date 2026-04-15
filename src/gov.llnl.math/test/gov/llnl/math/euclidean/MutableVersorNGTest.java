/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.euclidean;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class MutableVersorNGTest
{

  public MutableVersorNGTest()
  {
  }

  /**
   * Test of getU method, of class MutableVersor.
   */
  @Test
  public void testGetU()
  {
    MutableVersor instance = new MutableVersor();
    instance.assign(1, 0, 0, 0);
    double expResult = 1.0;
    double result = instance.getU();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of getI method, of class MutableVersor.
   */
  @Test
  public void testGetI()
  {
    MutableVersor instance = new MutableVersor();
    instance.assign(0, 1, 0, 0);
    double expResult = 1.0;
    double result = instance.getX();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of getJ method, of class MutableVersor.
   */
  @Test
  public void testGetJ()
  {
    MutableVersor instance = new MutableVersor();
    instance.assign(0, 0, 1, 0);
    double expResult = 1.0;
    double result = instance.getY();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of getK method, of class MutableVersor.
   */
  @Test
  public void testGetK()
  {
    MutableVersor instance = new MutableVersor();
    instance.assign(0, 0, 0, 1);
    double expResult = 1.0;
    double result = instance.getZ();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of assignEuler method, of class MutableVersor.
   */
  @Test
  public void testAssignEuler()
  {
    double roll = Math.PI / 2;
    double pitch = 0.0;
    double yaw = 0.0;
    MutableVersor instance = new MutableVersor();
    instance.assignEuler(roll, pitch, yaw);
    System.out.println(instance);
  }

  /**
   * Test of assign method, of class MutableVersor.
   */
  @Test
  public void testAssign_Versor()
  {
    Versor v = Versor.ZERO;
    MutableVersor instance = new MutableVersor();
    instance.assign(v);
  }

  /**
   * Test of assign method, of class MutableVersor.
   */
  @Test
  public void testAssign_4args()
  {
    double w = 1.0;
    double i = 0.0;
    double j = 0.0;
    double k = 0.0;
    MutableVersor instance = new MutableVersor();
    instance.assign(w, i, j, k);
  }

  /**
   * Test of toString method, of class MutableVersor.
   */
  @Test
  public void testToString()
  {
    MutableVersor instance = new MutableVersor();
    String expResult = "Versor(1.00000000, 0.00000000, 0.00000000, 0.00000000)";
    String result = instance.toString();
    System.out.println(result);
    assertEquals(result, expResult);
  }

  /**
   * Test of addAssign method, of class MutableVersor.
   */
  @Test
  public void testAddAssign()
  {
    Quaternion q = Quaternion.of(1, 0, 0, 0);
    MutableVersor instance = new MutableVersor();
    instance.addAssign(q);
  }

  /**
   * Test of normalize method, of class MutableVersor.
   */
  @Test
  public void testNormalize()
  {
    MutableVersor instance = new MutableVersor();
    instance.x = 1;
    instance.y = 1;
    instance.z = 1;
    instance.u = 1;
    instance.normalize();
    assertEquals(instance.u, 0.5);
  }

  /**
   * Test of multiplyAssign method, of class MutableVersor.
   */
  @Test
  public void testMultiplyAssign()
  {
    Quaternion q = Quaternion.of(0, -1, 0, 0);
    MutableVersor instance = new MutableVersor();
    instance.assign(1, 0, 0, 0);
    instance.multiplyAssign(q);
    assertEquals(instance.x, -1.0, 0.0);
    assertEquals(instance.y, 0.0, 0.0);
    assertEquals(instance.z, 0.0, 0.0);
    assertEquals(instance.u, 0.0, 0.0);
  }

  /**
   * Test of multiplyInvAssign method, of class MutableVersor.
   */
  @Test
  public void testMultiplyInvAssign()
  {
    Quaternion q = Quaternion.of(0, -1, 0, 0);
    MutableVersor instance = new MutableVersor();
    instance.multiplyAssign(q);
    instance.multiplyInvAssign(q);
    assertEquals(instance.u, 1.0, 0.0);
    q = Quaternion.of(0, 0, 1, 0);
    instance.multiplyAssign(q);
    instance.multiplyInvAssign(q);
    assertEquals(instance.u, 1.0, 0.0);
  }

  /**
   * Test of toArray method, of class MutableVersor.
   */
  @Test
  public void testToArray()
  {
    MutableVersor instance = new MutableVersor();
    instance.assign(-0.5, 0.5, 0.5, -0.5);
    double[] expResult = new double[]
    {
      0.5, -0.5, -0.5, 0.5
    };
    double[] result = instance.toArray();
    assertEquals(result, expResult);
  }
//
//  /**
//   * Test of toEuler method, of class MutableVersor.
//   */
//  @Test
//  public void testToEuler()
//  {
//    MutableVector3 v = new MutableVector3();
//    MutableVersor instance = new MutableVersor();
//    instance.assign(1, 0, 0, 0);
//    instance.toEuler(v);
//    System.out.println(v);
//
//    instance.assign(0, 1, 0, 0);
//    instance.toEuler(v);
//    System.out.println(v);
//    instance.assignEuler(v.x, v.y, v.z);
//    System.out.println(instance);
//
//    instance.assign(0, 0, 1, 0);
//    instance.toEuler(v);
//    System.out.println(v);
//    instance.assignEuler(v.x, v.y, v.z);
//    System.out.println(instance);
//
//    instance.assign(0, 0, 0, 1);
//    instance.toEuler(v);
//    System.out.println(v);
//    instance.assignEuler(v.x, v.y, v.z);
//    System.out.println(instance);
//
//    System.out.println("=== neg");
//    instance.assign(0.01, -0.99, 0, 0);
//    instance.toEuler(v);
//    System.out.println(v);
//    instance.assignEuler(v.x, v.y, v.z);
//    System.out.println(instance);
//
//    instance.assign(0, 0, -1, 0);
//    instance.toEuler(v);
//    System.out.println(v);
//    instance.assignEuler(v.x, v.y, v.z);
//    System.out.println(instance);
//
//    instance.assign(0, 0, 0, -1);
//    instance.toEuler(v);
//    System.out.println(v);
//    instance.assignEuler(v.x, v.y, v.z);
//    System.out.println(instance);
//
//    instance.assign(-0.5, 0.5, 0.5, -0.5);
//    instance.toEuler(v);
//    System.out.println(v);
//    instance.assignEuler(v.x, v.y, v.z);
//    System.out.println(instance);
//
//  }

}
