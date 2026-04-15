/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import static java.lang.Float.NaN;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for FloatArray.
 */
public class FloatArrayNGTest
{
  
  public FloatArrayNGTest()
  {
    FloatArray instance = new FloatArray();
  }

  /**
   * Test of parseFloatArray method, of class FloatArray.
   */
  @Test
  public void testParseFloatArray()
  {
    String str = "0 -1 2 -3 4 -5";
    float[] expResult = new float[]{0,-1,2,-3,4,-5};
    float[] result = FloatArray.parseFloatArray(str);
    assertEquals(result, expResult);
  }

  /**
   * Test of toString method, of class FloatArray.
   */
  @Test
  public void testToString_floatArr()
  {
    float[] values = new float[]{1,2,3};
    String expResult = "1.0 2.0 3.0";
    String result = FloatArray.toString(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of toString method, of class FloatArray.
   */
  @Test
  public void testToString_floatArr_String()
  {
    float[] values = new float[]{1,2,3};;
    String format = "%f";
    String expResult = "1.000000 2.000000 3.000000";
    String result = FloatArray.toString(values, format);
    assertEquals(result, expResult);
  }

  /**
   * Test of fill method, of class FloatArray.
   */
  @Test
  public void testFill()
  {
    float[] x = new float[3];
    float value = 1.0F;
    FloatArray.fill(x, value);
    assertNotNull(x);
  }

  /**
   * Test of fillRange method, of class FloatArray.
   */
  @Test
  public void testFillRange()
  {
    float[] x = new float[3];
    int start = 0;
    int end = x.length;
    float value = 1.0F;
    FloatArray.fillRange(x, start, end, value);
    assertNotNull(x);
    for(int i = 0; i < x.length; ++i)
    {
      assertEquals(x[i], value, 0.0);
    }
  }

  /**
   * Test of assign method, of class FloatArray.
   */
  @Test (expectedExceptions = {
    MathExceptions.SizeException.class
  })
  public void testAssign_floatArr_floatArr()
  {
    float[] target = new float[]{1,2,3};
    float[] source = new float[]{7,8,9};
    float[] expResult = new float[]{7,8,9};
    float[] result = FloatArray.assign(target, source);
    assertEquals(result, expResult);
    source = new float[]{7};
    FloatArray.assign(target, source);
  }

  /**
   * Test of assign method, of class FloatArray.
   */
  @Test
  public void testAssign_5args()
  {
    float[] target = new float[]{1,2,3};
    int targetOffset = 0;
    float[] src = new float[]{7,8,9};
    int srcOffset = 0;
    int length = target.length;
    float[] expResult = new float[]{7,8,9};
    float[] result = FloatArray.assign(target, targetOffset, src, srcOffset, length);
    assertEquals(result, expResult);
  }

  /**
   * Test of addAssign method, of class FloatArray.
   */
  @Test
  public void testAddAssign_floatArr_floatArr()
  {
    float[] target = new float[]{1,2,3};
    float[] source = new float[]{1,2,3};
    float[] expResult = new float[]{2,4,6};
    float[] result = FloatArray.addAssign(target, source);
    assertEquals(result, expResult);
  }

  /**
   * Test of addAssign method, of class FloatArray.
   */
  @Test
  public void testAddAssign_5args()
  {
    float[] target = new float[]{1,2,3};
    int targetOffset = 1;
    float[] source = new float[]{1,1,1};
    int sourceOffset = 1;
    int length = 2;
    float[] expResult = new float[]{1.0F,3.0F,4.0F};
    float[] result = FloatArray.addAssign(target, targetOffset, source, sourceOffset, length);
    assertEquals(result, expResult);
  }

  /**
   * Test of subtractAssign method, of class FloatArray.
   */
  @Test
  public void testSubtractAssign_5args()
  {
    float[] target = new float[]{1,2,3};
    int targetOffset = 1;
    float[] source = new float[]{1,1,1};
    int sourceOffset = 1;
    int length = 2;
    float[] expResult = new float[]{1.0F,1.0F,2.0F};
    float[] result = FloatArray.subtractAssign(target, targetOffset, source, sourceOffset, length);
    assertEquals(result, expResult);
  }

  /**
   * Test of addAssign method, of class FloatArray.
   */
  @Test
  public void testAddAssign_floatArr_float()
  {
    float[] target = new float[]{1,2,3};
    float value = 1.0F;
    float[] expResult = new float[]{2,3,4};
    float[] result = FloatArray.addAssign(target, value);
    assertEquals(result, expResult);
  }

  /**
   * Test of subtractAssign method, of class FloatArray.
   */
  @Test
  public void testSubtractAssign_floatArr_floatArr()
  {
    float[] target = new float[]{1,2,3};
    float[] value = new float[]{1,2,3};
    float[] expResult = new float[]{0,0,0};
    float[] result = FloatArray.subtractAssign(target, value);
    assertEquals(result, expResult);
  }

  /**
   * Test of add method, of class FloatArray.
   */
  @Test
  public void testAdd()
  {
    float[] target = new float[3];
    float[] x1 = new float[]{1,2,3};
    float[] x2 = new float[]{1,1,1};
    float[] expResult = new float[]{2,3,4};
    float[] result = FloatArray.add(target, x1, x2);
    assertEquals(result, expResult);
  }

  /**
   * Test of addAssignRange method, of class FloatArray.
   */
  @Test
  public void testAddAssignRange()
  {
    float[] target = new float[]{1,2,3};
    int begin = 0;
    int end = 3;
    float value = 1.0F;
    float[] expResult = new float[]{2,3,4};
    float[] result = FloatArray.addAssignRange(target, begin, end, value);
    assertEquals(result, expResult);
  }

  /**
   * Test of addScaled method, of class FloatArray.
   */
  @Test
  public void testAddScaled()
  {
    float[] target = new float[3];
    float[] x1 = new float[]{1,2,3};
    float[] x2 = new float[]{1,1,1};
    float d = 0.5F;
    float[] expResult = new float[]{1.5F,2.5F,3.5F};
    float[] result = FloatArray.addScaled(target, x1, x2, d);
    assertEquals(result, expResult);
  }

  /**
   * Test of subtract method, of class FloatArray.
   */
  @Test
  public void testSubtract()
  {
    float[] target = new float[3];
    float[] x1 = new float[]{1,2,3};
    float[] x2 = new float[]{1,1,1};
    float[] expResult = new float[]{0,1,2};
    float[] result = FloatArray.subtract(target, x1, x2);
    assertEquals(result, expResult);
  }

  /**
   * Test of multiplyAssignRange method, of class FloatArray.
   */
  @Test
  public void testMultiplyAssignRange()
  {
    float[] target = new float[]{1,2,3};
    int begin = 0;
    int end = target.length;
    float value = 2.0F;
    float[] expResult = new float[]{2,4,6};
    float[] result = FloatArray.multiplyAssignRange(target, begin, end, value);
    assertEquals(result, expResult);
  }

  /**
   * Test of negate method, of class FloatArray.
   */
  @Test
  public void testNegate()
  {
    float[] v = new float[]{1,2,3};
    float[] expResult = new float[]{-1,-2,-3};
    float[] result = FloatArray.negate(v);
    assertEquals(result, expResult);
  }

  /**
   * Test of negateRange method, of class FloatArray.
   */
  @Test
  public void testNegateRange()
  {
    float[] v = new float[]{1,2,3};
    int start = 0;
    int end = v.length;
    float[] expResult = new float[]{-1,-2,-3};
    float[] result = FloatArray.negateRange(v, start, end);
    assertEquals(result, expResult);
  }

  /**
   * Test of divideAssign method, of class FloatArray.
   */
  @Test
  public void testDivideAssign()
  {
    float[] v = new float[]{1,2,3};
    float d = 1.0F;
    float[] expResult = new float[]{1,2,3};
    float[] result = FloatArray.divideAssign(v, d);
    assertEquals(result, expResult);
  }

  /**
   * Test of divideAssignRange method, of class FloatArray.
   */
  @Test
  public void testDivideAssignRange()
  {
    float[] v = new float[]{1,2,3};
    int begin = 0;
    int end = v.length;
    float d = 1.0F;
    float[] expResult = new float[]{1,2,3};
    float[] result = FloatArray.divideAssignRange(v, begin, end, d);
    assertEquals(result, expResult);
  }

  /**
   * Test of equivalent method, of class FloatArray.
   */
  @Test
  public void testEquivalent_floatArr_floatArr()
  {
    float[] operand1 = new float[]{1,2,3};
    float[] operand2 = new float[]{1};
    boolean result = FloatArray.equivalent(operand1, operand2);
    assertEquals(result, false);
    operand2 = new float[]{1,2,3};
    result = FloatArray.equivalent(operand1, operand2);
    assertEquals(result, true);
  }

  /**
   * Test of equivalent method, of class FloatArray.
   */
  @Test
  public void testEquivalent_5args()
  {
    float[] a = new float[]{1,2,3};
    int aOffset = 0;
    float[] b = new float[]{1,1,1};
    int bOffset = 0;
    int length = a.length;
    boolean result = FloatArray.equivalent(a, aOffset, b, bOffset, length);
    assertEquals(result, false);
  }

  /**
   * Test of isNaN method, of class FloatArray.
   */
  @Test
  public void testIsNaN()
  {
    float[] vector = new float[]{1,2,3};
    boolean result = FloatArray.isNaN(vector);
    assertFalse(result);
    vector = new float[]{NaN};
    result = FloatArray.isNaN(vector);
    assertTrue(result);
  }

  /**
   * Test of isNaNRange method, of class FloatArray.
   */
  @Test
  public void testIsNaNRange()
  {
    float[] vector = new float[]{1,2,3};
    int begin = 0;
    int end = vector.length;
    boolean result = FloatArray.isNaNRange(vector, begin, end);
    assertEquals(result, false);
    vector = new float[]{0,1,NaN};
    result = FloatArray.isNaNRange(vector, begin, end);
    assertEquals(result, true);
  }

  /**
   * Test of solveDestructive method, of class FloatArray.
   */
  @Test
  public void testSolveDestructive()
  {
    float[] C = new float[]{8};
    float[] A = new float[]{3,0,1};
    int n = C.length;
    int result = FloatArray.solveDestructive(C, A, n);
    assertEquals(result, 0);
    A = new float[]{1e-17F,1e-17F,1e-17F};
    result = FloatArray.solveDestructive(C, A, n);
    assertEquals(result, -1);
  }
  
}
