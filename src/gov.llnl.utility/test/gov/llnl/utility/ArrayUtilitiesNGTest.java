/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * Test for for ArrayUtilities.
 */
public class ArrayUtilitiesNGTest
{

  public ArrayUtilitiesNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    ArrayUtilities instance = new ArrayUtilities();
  }

  /**
   * Test of parseDoubleFromString method, of class ArrayUtilities.
   */
  @Test
  public void testParseDoubleFromString()
  {
    assertEquals(ArrayUtilities.parseDoubleFromString("foo0bar", 3, 4), 0.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo1bar", 3, 4), 1.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2bar", 3, 4), 2.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2.bar", 3, 5), 2.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e1bar", 3, 6), 20.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e-1bar", 3, 7), 0.2, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e-1bar", 3, -15), 0.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e-1bar", 3, -1), 0.2, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e-1bar", 15, 7), 0.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e-1bar", 15, 15), 0.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e+1bar", 5, 15), 1.0, 0.0);
    assertEquals(ArrayUtilities.parseDoubleFromString("foo2e-1bar", 5, 15), -1.0, 0.0);
  }

  /**
   * Test of pow10 method, of class ArrayUtilities.
   */
  @Test
  public void testPow10()
  {
    assertEquals(ArrayUtilities.pow10(0, false), 1.0);
    assertEquals(ArrayUtilities.pow10(1, false), 10.0);
    assertEquals(ArrayUtilities.pow10(2, false), 100.0);
    assertEquals(ArrayUtilities.pow10(3, false), 1000.0);
    assertEquals(ArrayUtilities.pow10(4, false), 10000.0);
    assertEquals(ArrayUtilities.pow10(5, false), 100000.0);

    assertEquals(ArrayUtilities.pow10(0, true), 1.0);
    assertEquals(ArrayUtilities.pow10(1, true), 0.1);
    assertEquals(ArrayUtilities.pow10(2, true), 0.01);
    assertEquals(ArrayUtilities.pow10(3, true), 0.001);
    assertEquals(ArrayUtilities.pow10(4, true), 0.0001);
    assertEquals(ArrayUtilities.pow10(5, true), 0.00001);
  }

  /**
   * Test of parseIntegerFromString method, of class ArrayUtilities.
   */
  @Test
  public void testParseIntegerFromString()
  {
    // Tested in other methods but cases below are meant to satisfy other conditions
    assertEquals(ArrayUtilities.parseIntegerFromString("0,1,2,3,4,5,6,7,8,9", 25, 1), 0);
    assertEquals(ArrayUtilities.parseIntegerFromString("0-1-2-3-4-5-6-7-8-9", 1, 2), -3);
    assertEquals(ArrayUtilities.parseIntegerFromString("+1-2-3-4-5-6-7-8-9", 0, 10), 1);
  }

  /**
   * Test of parseDoubleArray method, of class ArrayUtilities.
   */
  @Test
  public void testParseDoubleArray()
  {
    double[] expResult = new double[]
    {
      0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0
    };
    assertEquals(ArrayUtilities.parseDoubleArray("0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0", 10, ','), expResult);
  }

  /**
   * Test of countDelimitor method, of class ArrayUtilities.
   */
  @Test
  public void testCountDelimitor()
  {
    assertEquals(ArrayUtilities.countDelimitor("0,1,2,3,4,5,6,7,8,9", ','), 9);
  }

  /**
   * Test of fastSplit method, of class ArrayUtilities.
   */
  @Test
  public void testFastSplit()
  {
    assertEquals(ArrayUtilities.fastSplit("\t1\t2\t3"), new String[]
    {
      "", "1", "2", "3"
    });
    assertEquals(ArrayUtilities.fastSplit("0\t1\t2\t3"), new String[]
    {
      "0", "1", "2", "3"
    });
  }

  /**
   * Test of parseIntArray method, of class ArrayUtilities.
   */
  @Test
  public void testParseIntArray()
  {
    int[] expResult = new int[]
    {
      0, 1, 2, 3, 4, 5, 6, 7, 8, 9
    };
    assertEquals(ArrayUtilities.parseIntArray("0,1,2,3,4,5,6,7,8,9", 10, ','), expResult);
  }

}
