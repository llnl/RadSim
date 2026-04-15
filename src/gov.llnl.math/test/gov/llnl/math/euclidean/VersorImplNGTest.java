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
 *
 * @author her1
 */
public class VersorImplNGTest
{
  
  public VersorImplNGTest()
  {
  }

  /**
   * Test of getI method, of class VersorImpl.
   */
  @Test
  public void testGetI()
  {
    Vector3Impl axis = new Vector3Impl(1, 2, 3);
    double angle = 1.0;
    VersorImpl instance = (VersorImpl) Versor.of(axis, angle);
    double expResult = 0.12813186485189226;
    double result = instance.getX();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of getJ method, of class VersorImpl.
   */
  @Test
  public void testGetJ()
  {
    double u = 1.0;
    double i = 1.0;
    double j = 1.0;
    double k = 1.0;
    VersorImpl instance = new VersorImpl(u, i, j, k);
    double expResult = 0.5;
    double result = instance.getY();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of getK method, of class VersorImpl.
   */
  @Test
  public void testGetK()
  {
    double u = 1.0;
    double i = 1.0;
    double j = 1.0;
    double k = 1.0;
    VersorImpl instance = new VersorImpl(u, i, j, k);
    double expResult = 0.5;
    double result = instance.getZ();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of getU method, of class VersorImpl.
   */
  @Test
  public void testGetU()
  {
    double u = 1.0;
    double i = 1.0;
    double j = 1.0;
    double k = 1.0;
    VersorImpl instance = new VersorImpl(u, i, j, k);
    double expResult = 0.5;
    double result = instance.getU();
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of toString method, of class VersorImpl.
   */
  @Test
  public void testToString()
  {
    double u = 1.0;
    double i = 1.0;
    double j = 1.0;
    double k = 1.0;
    VersorImpl instance = new VersorImpl(u, i, j, k);
    String expResult = "Versor(0.500000,0.500000,0.500000,0.500000)";
    String result = instance.toString();
    assertEquals(result, expResult);
  }
  
}
