/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import static java.lang.Double.NaN;
import org.testng.annotations.Test;

/**
 * Test code for MathAssert.
 */
public class MathAssertNGTest
{
  
  public MathAssertNGTest()
  {
  }
  
  /**
   * Test constructor of class MathAssert.
   */
  @Test
  public void testConstructor()
  {
    MathAssert instance = new MathAssert();
  }

  /**
   * Test of assertEqualLength method, of class MathAssert.
   */
  @Test
  public void testAssertEqualLength()
  {
    MathAssert.assertEqualLength(new double[5], new double[5]);
    MathAssert.assertEqualLength(new int[5], new double[5]);
    MathAssert.assertEqualLength(new double[5], new int[5]);
    MathAssert.assertEqualLength(new Object[5], new double[5]);
    MathAssert.assertEqualLength(new double[5], new int[5]);
  }
  
    /**
   * Test of assertEqualLength method, of class MathAssert.
   */
  @Test (expectedExceptions = {
    NullPointerException.class
  })
  public void testAssertEqualLength2()
  {
    String msg = null;
    MathAssert.assertEqualLength(null, null, msg);
    MathAssert.assertEqualLength(null, 5, msg);
    MathAssert.assertEqualLength(5, null, msg);
  }


  /**
   * Test of assertLengthEqual method, of class MathAssert.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testAssertLengthEqual_3args_1()
  {
    String[] msg = null;
    MathAssert.assertLengthEqual(new int[5], 5, msg);
    MathAssert.assertLengthEqual(new double[5], 5, msg);
    MathAssert.assertLengthEqual(new Object[5], 5, msg);
    MathAssert.assertLengthEqual(new Object[1], 5, msg);
  }

  /**
   * Test of assertNotNaN method, of class MathAssert.
   */
  @Test (expectedExceptions = {
    RuntimeException.class
  })
  public void testAssertNotNaN_double()
  {
    double x = 0.0;
    MathAssert.assertNotNaN(x);
    MathAssert.assertNotNaN(NaN);
  }

  /**
   * Test of assertNotNaN method, of class MathAssert.
   */
  @Test
  public void testAssertNotNaN_doubleArr()
  {
    double[] x = new double[]{1,2,3,4};
    MathAssert.assertNotNaN(x);
  }
  
  @Test(expectedExceptions=RuntimeException.class)
  public void testAssertNotNaN_doubleArr2()
  {
    double[] x = new double[]{1,2,3,4, Double.NaN};
    MathAssert.assertNotNaN(x);
  }


  /**
   * Test of assertSortedDoubleUnique method, of class MathAssert.
   */
  @Test
  public void testAssertSortedDoubleUnique()
  {
    double[] array = new double[]{1,2,3,4};
    MathAssert.assertSortedDoubleUnique(array);
  }

  /**
   * Test of assertSortedDoubleUnique method, of class MathAssert.
   */
  @Test(expectedExceptions=RuntimeException.class)
  public void testAssertSortedDoubleUnique1()
  {
    double[] array = new double[]{1,2,3,Double.NaN,5};
    MathAssert.assertSortedDoubleUnique(array);
  }
  
  /**
   * Test of assertSortedDoubleUnique method, of class MathAssert.
   */
  @Test(expectedExceptions=RuntimeException.class)
  public void testAssertSortedDoubleUnique2()
  {
    double[] array = new double[]{1,2,3,-4};
    MathAssert.assertSortedDoubleUnique(array);
  }  
}
