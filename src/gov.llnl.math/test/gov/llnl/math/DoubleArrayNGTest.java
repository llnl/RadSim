/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import org.testng.annotations.Test;
import support.MatrixTestGenerator;

/**
 * Test code for DoubleArray.
 */
public class DoubleArrayNGTest
{
  MatrixTestGenerator tg = new MatrixTestGenerator();

  public DoubleArrayNGTest()
  {
  }

  /**
   * Test of assign method, of class DoubleArray.
   */
  @Test
  public void testAssign_doubleArr_doubleArr()
  {
    double[] out = new double[10];
    double[] in = tg.newArray(10);
    DoubleArray.assign(out, in);
    Assert.assertTrue(DoubleArray.equivalent(in, out));
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testAssign_doubleArr_doubleArr_IndexOutOfBoundsException()
  {
    double[] out = new double[9];
    double[] in = tg.newArray(10);
    DoubleArray.assign(out, in);
  }

  /**
   * Test of assign method, of class DoubleArray.
   */
  @Test
  public void testAssign_5args()
  {
    double[] out = new double[10];
    double[] in = tg.newArray(10);
    DoubleArray.assign(out, 2, in, 3, 6);
    Assert.assertTrue(DoubleArray.equivalent(out, 2, in, 3, 6));
    for (int i = 0; i < 2; i++)
      Assert.assertEquals(out[i], 0.0);
    for (int i = 8; i < 10; i++)
      Assert.assertEquals(out[i], 0.0);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testAssign_5args_IndexOutOfBoundsException1()
  {
    double[] out = new double[5];
    double[] in = tg.newArray(10);
    DoubleArray.assign(out, 2, in, 3, 6);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testAssign_5args_IndexOutOfBoundsException2()
  {
    double[] out = new double[10];
    double[] in = tg.newArray(5);
    DoubleArray.assign(out, 2, in, 3, 6);
  }

  /**
   * Test of copyOf method, of class DoubleArray.
   */
  @Test
  public void testCopyOf()
  {
    double[] in = tg.newArray(10);
    double[] out = DoubleArray.copyOf(in);
    Assert.assertTrue(DoubleArray.equivalent(in, out));
    Assert.assertTrue(in != out);
    out = DoubleArray.copyOf(null);
    Assert.assertTrue(out == null);
  }

  /**
   * Test of copyOfRange method, of class DoubleArray.
   */
  @Test
  public void testCopyOfRange()
  {
    double[] in = tg.newArray(10);
    double[] out = DoubleArray.copyOfRange(in, 2, 8);
    Assert.assertTrue(DoubleArray.equivalent(in, 2, out, 0, 8 - 2));
    Assert.assertTrue(in != out);
    out = DoubleArray.copyOfRange(null, 2, 8);
    Assert.assertTrue(out == null);
  }

  /**
   * Test of fill method, of class DoubleArray.
   */
  @Test
  public void testFill()
  {
    double[] in = new double[5];
    DoubleArray.fill(in, 2);
    int index = DoubleArray.findIndexOfFirstRange(in, 0, in.length,
            new DoubleConditional.NotEqual(2));
    Assert.assertEquals(index, -1);
  }

  /**
   * Test of fillRange method, of class DoubleArray.
   */
  @Test
  public void testFillRange()
  {
    double[] in = new double[10];
    DoubleArray.fillRange(in, 4, 8, 2.0);
    int index = DoubleArray.findIndexOfFirstRange(in, 4, 8,
            new DoubleConditional.NotEqual(2));
    Assert.assertEquals(index, -1);
  }
  
    /**
   * Test of fill method, of class DoubleArray.
   */
  @Test
  public void testFill_DoubleSupplier()
  {
    double[] in = new double[5];
    DoubleArray.fill(in, ()->2.0);
    int index = DoubleArray.findIndexOfFirstRange(in, 0, in.length,
            new DoubleConditional.NotEqual(2));
    Assert.assertEquals(index, -1);
  }

  /**
   * Test of fillRange method, of class DoubleArray.
   */
  @Test
  public void testFillRange_DoubleRange()
  {
    double[] in = new double[10];
    DoubleArray.fillRange(in, 4, 8, ()->2.0);
    int index = DoubleArray.findIndexOfFirstRange(in, 4, 8,
            new DoubleConditional.NotEqual(2));
    Assert.assertEquals(index, -1);
  }

  /**
   * Test of addAssign method, of class DoubleArray.
   */
  @Test
  public void testAddAssign_doubleArr_double()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    DoubleArray.addAssign(in, 4.0);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] + 4.0);
  }

  /**
   * Test of addAssignRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testAddAssignRange()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    DoubleArray.addAssignRange(in, 2, 8, 4.0);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] + ((i >= 2 && i < 8) ? 4.0 : 0));
    double[] result = DoubleArray.addAssignRange(in, 2, 8, 0.0);
    Assert.assertEquals(result, in);
    DoubleArray.addAssignRange(in, 0, 11, 4.0);
  }

  /**
   * Test of addAssign method, of class DoubleArray.
   */
  @Test
  public void testAddAssign_doubleArr_doubleArr()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    double[] op = tg.newArray(10);
    DoubleArray.addAssign(in, op);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] + op[i]);
  }

  /**
   * Test of addAssign method, of class DoubleArray.
   */
  @Test
  public void testAddAssign_5args()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    double[] op = tg.newArray(6);
    DoubleArray.addAssign(in, 2, op, 0, 6);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] + ((i >= 2 && i < 8) ? op[i - 2] : 0));
  }

  /**
   * Test of addAssignScaled method, of class DoubleArray.
   */
  @Test
  public void testAddAssignScaled_3args()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    double[] op = tg.newArray(10);
    DoubleArray.addAssignScaled(in, op, 1.25);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] + op[i] * 1.25);
  }

  /**
   * Test of addAssignScaled method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testAddAssignScaled_6args()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    double[] op = tg.newArray(6);
    DoubleArray.addAssignScaled(in, 2, op, 0, 6, 1.25);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] + ((i >= 2 && i < 8) ? op[i - 2] * 1.25 : 0));
    Assert.assertEquals(DoubleArray.addAssignScaled(in, 2, op, 0, 6, 0), in);
    DoubleArray.addAssignScaled(in, 2, op, 0, 11, 1.25);
  }

  /**
   * Test of subtractAssign method, of class DoubleArray.
   */
  @Test
  public void testSubtractAssign_doubleArr_doubleArr()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    double[] op = tg.newArray(10);
    DoubleArray.subtractAssign(in, op);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] - op[i]);
  }

  /**
   * Test of subtractAssign method, of class DoubleArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testSubtractAssign_5args()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    double[] op = tg.newArray(6);
    DoubleArray.subtractAssign(in, 2, op, 0, 6);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] - ((i >= 2 && i < 8) ? op[i - 2] : 0));
    DoubleArray.subtractAssign(in, 2, op, 0, 10);
  }

  @Test
  public void testSubtract()
  {
    double[] u =
    {
      6, 4
    };
    double[] v =
    {
      2, 2
    };
    double[] actual = DoubleArray.subtract(u, v);
    double[] expected =
    {
      4, 2
    };
    for (int i = 0; i < u.length; i++)
      Assert.assertEquals(actual[i], expected[i], 0.1);
  }

  /**
   * Test of negateAssign method, of class DoubleArray.
   */
  @Test
  public void testNegateAssign()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    DoubleArray.negateAssign(in);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], -orig[i]);
  }

  /**
   * Test of negateAssignRange method, of class DoubleArray.
   */
  @Test
  public void testNegateAssignRange()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    DoubleArray.negateAssignRange(in, 2, 8);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] * ((i >= 2 && i < 8) ? -1 : 1));
  }

  /**
   * Test of multiply method, of class DoubleArray.
   */
  @Test
  public void testMultiply_doubleArr_double()
  {
    double[] in = tg.newArray(10);
    double[] out = DoubleArray.multiply(in, 4.223);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(out[i], in[i] * 4.223);
  }

  /**
   * Test of multiply method, of class DoubleArray.
   */
  @Test
  public void testMultiply_doubleArr_doubleArr()
  {
    double[] in1 = tg.newArray(10);
    double[] in2 = tg.newArray(10);
    double[] out = DoubleArray.multiply(in1, in2);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(out[i], in1[i] * in2[i]);
  }

  /**
   * Test of multiply method, of class DoubleArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testMultiply_7args()
  {
    double[] in1 = tg.newArray(10);
    double[] in2 = tg.newArray(10);
    double[] out = new double[10];
    DoubleArray.multiply(out, 1, in1, 2, in2, 3, 4);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(out[i], ((i >= 1) && (i < 5)) ? (in1[i + 1] * in2[i + 2]) : 0);
    
    in1 = tg.newArray(16);
    in2 = tg.newArray(16);
    out = new double[16];
    DoubleArray.multiply(out, 0, in1, 0, in2, 0, 16);
    
    in1 = tg.newArray(8);
    in2 = tg.newArray(8);
    out = new double[8];
    DoubleArray.multiply(out, 0, in1, 0, in2, 0, 8);
    
    in1 = tg.newArray(2);
    in2 = tg.newArray(2);
    out = new double[2];
    DoubleArray.multiply(out, 0, in1, 0, in2, 0, 2);
    
    in1 = tg.newArray(1);
    in2 = tg.newArray(1);
    out = new double[1];
    DoubleArray.multiply(out, 0, in1, 0, in2, 0, 1);
    
    out = new double[1];
    DoubleArray.multiply(out, 0, in1, 0, in2, 0, 2);
  }

  /**
   * Test of multiplyAssign method, of class DoubleArray.
   */
  @Test
  public void testMultiplyAssign_doubleArr_double()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    DoubleArray.multiplyAssign(in, 4.231);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] * 4.231);
  }

  /**
   * Test of multiplyAssign method, of class DoubleArray.
   */
  @Test
  public void testMultiplyAssign_doubleArr_doubleArr()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    double[] p = tg.newArray(10);
    DoubleArray.multiplyAssign(in, p);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] * p[i]);
  }

  /**
   * Test of divideAssign method, of class DoubleArray.
   */
  @Test
  public void testDivideAssign()
  {
    double[] in = tg.newArray(10);
    double[] orig = DoubleArray.copyOf(in);
    DoubleArray.divideAssign(in, 4.231);
    for (int i = 0; i < 10; ++i)
      Assert.assertEquals(in[i], orig[i] / 4.231);
  }

  /**
   * Test of normColumns1 method, of class DoubleArray.
   */
  @Test
  public void testNormColumns1()
  {
    double[] in = tg.newArray(10);
    DoubleArray.normColumns1(in);
    double sum = 0;
    for (int i = 0; i < in.length; ++i)
      sum += Math.abs(in[i]);
    Assert.assertTrue(DoubleUtilities.equivalent(sum, 1.0));
  }

  /**
   * Test of normColumns2 method, of class DoubleArray.
   */
  @Test
  public void testNormColumns2()
  {
    double[] in = tg.newArray(10);
    DoubleArray.normColumns2(in);
    double d = DoubleArray.sumSqr(in);
    Assert.assertTrue(DoubleUtilities.equivalent(d, 1.0));
  }

  /**
   * Test of normColumns1Range method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testNormColumns1Range()
  {
    double[] in = tg.newArray(10);
    DoubleArray.normColumns1Range(in, 2, 8);
    double sum = 0;
    for (int i = 2; i < 8; ++i)
      sum += Math.abs(in[i]);
    Assert.assertTrue(DoubleUtilities.equivalent(sum, 1.0));
    double[] input = new double[10];
    double[] result = DoubleArray.normColumns1Range(input, 2, 8);
    Assert.assertEquals(result, input);
    DoubleArray.normColumns1Range(input, 2, 11);
  }

  /**
   * Test of normColumns2Range method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testNormColumns2Range()
  {
    double[] in = tg.newArray(10);
    DoubleArray.normColumns2Range(in, 2, 8);
    Assert.assertTrue(DoubleUtilities.equivalent(
            DoubleArray.sumSqrRange(in, 2, 8), 1.0));
    double[] input = new double[10];
    double[] result = DoubleArray.normColumns2Range(input, 2, 8);
    Assert.assertEquals(result, input);
    DoubleArray.normColumns2Range(input, 2, 11);
  }

  /**
   * Test of sum method, of class DoubleArray.
   */
  @Test
  public void testSum()
  {
    double[] m = tg.newArray(10);
    double sum = 0;
    for (int i = 0; i < 10; i++)
      sum += m[i];
    Assert.assertTrue(sum == DoubleArray.sum(m));
  }

  /**
   * Test of sumSqr method, of class DoubleArray.
   */
  @Test
  public void testSumSqr()
  {
    double[] in = tg.newArray(10);
    double sum = 0;
    for (int i = 0; i < in.length; ++i)
      sum += in[i] * in[i];
    Assert.assertTrue(DoubleUtilities.equivalent(sum, DoubleArray.sumSqr(in)));
  }

  /**
   * Test of sumSqrRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testSumSqrRange()
  {
    double[] in = tg.newArray(10);
    double sum = 0;
    for (int i = 2; i < 8; ++i)
      sum += in[i] * in[i];
    Assert.assertTrue(sum == DoubleArray.sumSqrRange(in, 2, 8));
    Assert.assertFalse(sum == DoubleArray.sumSqrRange(in, 0, 11));   
  }

  @Test
  public void testProject()
  {
    double[] u =
    {
      4, 2
    };
    double[] v =
    {
      2, 4
    };
    double[] actual = DoubleArray.project(u, v);
    double[] expected =
    {
      1.6, 3.2
    };
    for (int i = 0; i < u.length; i++)
      Assert.assertEquals(actual[i], expected[i], 0.1);
  }

  @Test
  public void testEuclideanDistance()
  {
    double[] u =
    {
      0, 0, 0
    };
    double[] v =
    {
      5, 0, -6
    };
    Assert.assertEquals(7.81, DoubleArray.computeEuclideanDistance(u, v), 0.1);
  }

  @Test
  public void testIsInElementWiseRange1()
  {
    double[] u =
    {
      4, 5
    };
    double[] v =
    {
      7, 9
    };
    double[] x =
    {
      5, 7
    };
    Assert.assertTrue(Utilities.isInElementWiseRange(x, u, v));
  }

  @Test
  public void testIsInElementWiseRange2()
  {
    double[] u =
    {
      4, 5
    };
    double[] v =
    {
      7, 9
    };
    double[] x =
    {
      2, 7
    }; // 1st coordinate out of range.
    Assert.assertFalse(Utilities.isInElementWiseRange(x, u, v));
  }

  @Test
  public void testIsInElementWiseRange3()
  {
    double[] u =
    {
      4, 5
    };
    double[] v =
    {
      7, 9
    };
    double[] x =
    {
      5, 14
    }; // 2nd coordinate out of range.
    Assert.assertFalse(Utilities.isInElementWiseRange(x, u, v));
  }

  /**
   * Test of findIndexOfMaximum method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfMaximum()
  {
    double[] in = tg.newArray(10);
    int index = DoubleArray.findIndexOfMaximum(in);
    for (int i = 0; i < in.length; ++i)
    {
      Assert.assertTrue(in[index] >= in[i]);
    }
  }

  /**
   * Test of findMaximumAbsolute method, of class DoubleArray.
   */
  @Test
  public void testFindMaximumAbsolute()
  {
    double[] in = tg.newArray(10);
    double v = DoubleArray.findMaximumAbsolute(in);
    for (int i = 0; i < in.length; ++i)
    {
      Assert.assertTrue(v >= Math.abs(in[i]));
    }
    v = DoubleArray.findMaximumAbsolute(new double[]{});
    Assert.assertEquals(v, 0.0);
  }

  /**
   * Test of findIndexOfMinimum method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfMinimum()
  {
    double[] in = tg.newArray(10);
    int index = DoubleArray.findIndexOfMinimum(in);
    for (int i = 0; i < in.length; ++i)
    {
      Assert.assertTrue(in[index] <= in[i]);
    }
  }

  /**
   * Test of findIndexOfMinimumRange method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfMinimumRange()
  {
    double[] in = tg.newArray(10);
    int index = DoubleArray.findIndexOfMinimumRange(in, 2, 8);
    for (int i = 2; i < 8; ++i)
    {
      Assert.assertTrue(in[index] <= in[i]);
    }
  }

  /**
   * Test of findIndexOfExtrema method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfExtrema()
  {
    double[] in = new double[] 
    { 
      1, 2, 3, 4, 5 
    };
    int[] result = DoubleArray.findIndexOfExtrema(in);
    int resultMin = result[0];
    int resultMax = result[1];
    assertEquals(resultMin, 0);
    assertEquals(resultMax, 4);
    result = DoubleArray.findIndexOfExtrema(null);
    assertEquals(result, null);
  }

  /**
   * Test of findIndexOfFirstRange method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfFirstRange()
  {
    double[] in = tg.newArray(10);
    Arrays.sort(in);
    // Not a great test because assumes no repeats
    Assert.assertTrue(DoubleArray.findIndexOfFirstRange(in, 0, in.length,
            new DoubleConditional.Equal(in[5])) == 5);
  }

  /**
   * Test of findIndexOfLastRange method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfLastRange()
  {
    double[] in = tg.newArray(10);
    Arrays.sort(in);
    // Not a great test because assumes no repeats
    Assert.assertTrue(DoubleArray.findIndexOfFirstRange(in, 0, in.length,
            new DoubleConditional.Equal(in[5])) == 5);
    
    int result = DoubleArray.findIndexOfLastRange(in, 0, in.length, new DoubleConditional.Equal(in[5]));
    assertEquals(result, 5);
    result = DoubleArray.findIndexOfLastRange(in, 0, -1, new DoubleConditional.Equal(in[5]));
    assertEquals(result, -1);
  }

  /**
   * Test of findIndexOfRange method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfRange()
  {
    double[] in = tg.newArray(10);
    int max = DoubleArray.findIndexOfRange(in, 0, in.length, new DoubleComparator.Positive());
    int min = DoubleArray.findIndexOfRange(in, 0, in.length, new DoubleComparator.Negative());
    for (int i = 0; i < in.length; ++i)
    {
      Assert.assertTrue(in[max] >= in[i]);
    }
    for (int i = 0; i < in.length; ++i)
    {
      Assert.assertTrue(in[min] <= in[i]);
    }
  }

  /**
   * Test of equivalent method, of class DoubleArray.
   */
  @Test
  public void testEquals_doubleArr_doubleArr()
  {
    double[] m1 = tg.newArray(10);
    double[] m2 = DoubleArray.copyOf(m1);
    for (int i = 0; i < 10; ++i)
    {
      m2[i] = m2[i] + 1;
      Assert.assertTrue(!DoubleArray.equivalent(m1, m2));
      m2[i] = m2[i] - 1;
      Assert.assertTrue(DoubleArray.equivalent(m1, m2));
    }
  }

  /**
   * Test of equivalent method, of class DoubleArray.
   */
  @Test
  public void testEquals_5args()
  {
    double[] m1 = tg.newArray(10);
    double[] m2 = DoubleArray.copyOf(m1);
    for (int i = 0; i < 10; ++i)
    {
      boolean b = (i >= 2 && i < 8);
      m2[i] = m2[i] + 1;
      Assert.assertTrue(DoubleArray.equivalent(m1, 2, m2, 2, 6) != b);
      m2[i] = m2[i] - 1;
      Assert.assertTrue(DoubleArray.equivalent(m1, 2, m2, 2, 6));
    }
  }

  /**
   * Test of isNaN method, of class DoubleArray.
   */
  @Test
  public void testIsNaN()
  {
    double[] in = tg.newArray(5);
    Assert.assertTrue(!DoubleArray.isNaN(in));
    in[4] = Double.NaN;
    Assert.assertTrue(DoubleArray.isNaN(in));
  }

  /**
   * Test of isNaNRange method, of class DoubleArray.
   */
  @Test
  public void testIsNaNRange()
  {
    double[] in = tg.newArray(5);
    Assert.assertTrue(!DoubleArray.isNaNRange(in, 0, 5));
    in[4] = Double.NaN;
    Assert.assertTrue(DoubleArray.isNaNRange(in, 0, 5));
    Assert.assertTrue(!DoubleArray.isNaNRange(in, 0, 4));
  }

  /**
   * Test of newArray method, of class DoubleArray.
   */
  @Test
  public void testNewArray()
  {
    double[] a = DoubleArray.newArray(0, 1, 2, 3, 4);
    for (int i = 0; i < 5; ++i)
      Assert.assertEquals(a[i], i * 1.0);
  }

  /**
   * Test of exp method, of class DoubleArray.
   */
  @Test
  public void testExp()
  {
    double[] m1 = tg.newArray(10);
    double[] m2 = DoubleArray.exp(m1);
    for (int i = 0; i < 10; ++i)
    {
      Assert.assertTrue(Math.exp(m1[i]) == m2[i]);
    }
  }

  /**
   * Test of cat method, of class DoubleArray.
   */
  @Test
  public void testCat()
  {
    double[][] arrays = new double[][]
    {
      {
        1, 2, 3
      },
      {
        4, 5, 6
      }
    };
    double[] expResult = new double[]
    {
      1, 2, 3, 4, 5, 6
    };
    double[] result = DoubleArray.cat(arrays);
    assertEquals(result, expResult);
  }

  /**
   * Test of of method, of class DoubleArray.
   */
  @Test
  public void testOf()
  {
    double[] expResult = new double[]
    {
      1, 2, 3, 4
    };
    double[] result = DoubleArray.of(1, 2, 3, 4);
    assertEquals(result, expResult);
  }

  /**
   * Test of fillRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testFillRange_4args_1()
  {
    double[] values = new double[5];
    double[] expResult = new double[]
    {
      0.0, 3.0, 3.0, 3.0, 0.0
    };
    double[] result = DoubleArray.fillRange(values, 1, 4, 3);
    assertEquals(result, expResult);
    DoubleArray.fillRange(values, 0, 6, 3);
  }

  /**
   * Test of fillRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testFillRange_4args_2()
  {
    DoubleSupplier ds = () -> 3.0;
    double[] values = new double[5];
    double[] expResult = new double[]
    {
      0.0, 3.0, 3.0, 3.0, 0.0
    };
    double[] result = DoubleArray.fillRange(values, 1, 4, ds);
    assertEquals(result, expResult);
    DoubleArray.fillRange(values, 1, 6, ds);
  }

  /**
   * Test of add method, of class DoubleArray.
   */
  @Test
  public void testAdd()
  {
    double[] source = new double[3];
    double scalar = 1.0;
    double[] expResult = new double[]
    {
      1, 1, 1
    };
    double[] result = DoubleArray.add(source, scalar);
    assertEquals(result, expResult);
  }

  /**
   * Test of multiplyAssignRange method, of class DoubleArray.
   */
  @Test
  public void testMultiplyAssignRange_4args_1()
  {
    double[] target = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] source = new double[]
    {
      1, 2, 3, 4
    };
    double[] expResult = new double[]
    {
      1, 4, 9, 16, 5
    };
    double[] result = DoubleArray.multiplyAssignRange(target, source, 1, 4);
    assertEquals(result, expResult);
    assertEquals(result, target);
  }

  /**
   * Test of multiplyAssignRange method, of class DoubleArray.
   */
  @Test
  public void testMultiplyAssignRange_4args_2()
  {
    double[] target = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] expResult = new double[]
    {
      1, 4, 6, 8, 5
    };
    double[] result = DoubleArray.multiplyAssignRange(target, 1, 4, 2);
    assertEquals(result, expResult);
  }

  /**
   * Test of multiplyInnerRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testMultiplyInnerRange()
  {
    double[] srcA = new double[]
    {
      1, 2, 3
    };
    double[] srcB = new double[]
    {
      4, 5, 6
    };
    assertEquals(DoubleArray.multiplyInnerRange(srcA, srcB, 0, 3), 32.0, 0.0);
    assertEquals(DoubleArray.multiplyInnerRange(srcA, srcB, 1, 3), 28.0, 0.0);
    assertEquals(DoubleArray.multiplyInnerRange(srcA, srcB, 0, 2), 14.0, 0.0);
    assertEquals(DoubleArray.multiplyInnerRange(srcA, srcB, 0, 4), 0.0, 0.0);
  }

  /**
   * Test of divideAssign method, of class DoubleArray.
   */
  @Test
  public void testDivideAssign_doubleArr_double()
  {
    double[] target = new double[]
    {
      1, 2, 3, 4, 5
    };
    double scalar = 0.5;
    double[] expResult = new double[]
    {
      2, 4, 6, 8, 10
    };
    double[] result = DoubleArray.divideAssign(target, scalar);
    assertEquals(result, expResult);
  }

  /**
   * Test of divideAssign method, of class DoubleArray.
   */
  @Test
  public void testDivideAssign_doubleArr_doubleArr()
  {
    double[] target = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] source = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] expResult = new double[]
    {
      1, 1, 1, 1, 1
    };
    double[] result = DoubleArray.divideAssign(target, source);
    assertEquals(result, expResult);
  }

  /**
   * Test of divideAssign method, of class DoubleArray.
   */
  @Test
  public void testDivideAssign_5args()
  {
    double[] target = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] source = new double[]
    {
      2, 3, 4
    };
    double[] expResult = new double[]
    {
      1, 1, 1, 1, 5
    };
    double[] result = DoubleArray.divideAssign(target, 1, source, 0, 3);
    assertEquals(result, expResult);
  }

  /**
   * Test of mean method, of class DoubleArray.
   */
  @Test
  public void testMean()
  {
    double[] vector = new double[]
    {
      1, 2, 3, 4, 5
    };
    double expResult = 3.0;
    double result = DoubleArray.mean(vector);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of accumulateRange method, of class DoubleArray.
   */
  @Test
  public void testAccumulateRange()
  {
    double[] source = new double[]
    {
      1, 2, 3, 4, 5
    };
    assertEquals(DoubleArray.accumulateRange(source, 0, 4, (p) -> 2 * p), 20.0, 0.0);
    assertEquals(DoubleArray.accumulateRange(source, 1, 4, (p) -> 2 * p), 18.0, 0.0);
    assertEquals(DoubleArray.accumulateRange(source, 0, 3, (p) -> 2 * p), 12.0, 0.0);
  }

  /**
   * Test of computeEuclideanDistance method, of class DoubleArray.
   */
  @Test
  public void testComputeEuclideanDistance()
  {
    double[] u = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] v = new double[]
    {
      0, 1, 2, 3, 4
    };
    assertEquals(DoubleArray.computeEuclideanDistance(u, u), 0.0);
    assertEquals(DoubleArray.computeEuclideanDistance(u, v), Math.sqrt(5.0));
  }

  /**
   * Test of indexOf method, of class DoubleArray.
   */
  @Test
  public void testIndexOf()
  {
    double[] values = new double[]
    {
      0, 1, 2, 3, 4, 5
    };
    double d = 0.0;
    for (int i = 0; i < 5; ++i)
      assertEquals(DoubleArray.indexOf(values, i), i);
    assertEquals(DoubleArray.indexOf(values, 6), -1);
    int result = DoubleArray.indexOf(null, 0);
    assertEquals(result, -1);
  }

  /**
   * Test of indexOfRange method, of class DoubleArray.
   */
  @Test
  public void testIndexOfRange()
  {
    double[] values = new double[]
    {
      0, 1, 2, 3, 4
    };
    for (int b = 0; b < 5; ++b)
      for (int e = 0; e < 5; ++e)
        for (int i = 0; i < 5; ++i)
          assertEquals(DoubleArray.indexOfRange(values, b, e, i), (i >= b) && (i < e) ? i : -1);
  }

  /**
   * Test of max method, of class DoubleArray.
   */
  @Test
  public void testMax()
  {
    double[] vector = new double[]
    {
      1, 2, 3, 4
    };
    double expResult = 4.0;
    double result = DoubleArray.max(vector);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of min method, of class DoubleArray.
   */
  @Test
  public void testMin()
  {
    double[] vector = new double[]
    {
      -1, 1, 2
    };
    double expResult = -1.0;
    double result = DoubleArray.min(vector);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of std method, of class DoubleArray.
   */
  @Test
  public void testStd()
  {
    double[] vector = new double[]
    {
      1, 2, 3, 4, 5
    };
    double expResult = 0.0;
    double result = DoubleArray.std(vector);
    assertEquals(result, expResult, 15.0);
    vector = new double[]
    {
      1
    };
    result = DoubleArray.std(vector);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of countConditional method, of class DoubleArray.
   */
  @Test
  public void testCountConditional()
  {
    double[] vector = new double[]
    {
      1, 2, 3, 4, 5
    };
    assertEquals(DoubleArray.countConditional(vector, (p) -> true), 5);
    assertEquals(DoubleArray.countConditional(vector, (p) -> p > 3), 2);
  }

  /**
   * Test of countConditionalRange method, of class DoubleArray.
   */
  @Test
  public void testCountConditionalRange()
  {
    double[] vector = new double[]
    {
      1, 2, 3, 4, 5
    };
    assertEquals(DoubleArray.countConditionalRange(vector, 0, 5, (p) -> true), 5);
    assertEquals(DoubleArray.countConditionalRange(vector, 0, 5, (p) -> p > 3), 2);
    assertEquals(DoubleArray.countConditionalRange(vector, 0, 4, (p) -> p > 3), 1);
    assertEquals(DoubleArray.countConditionalRange(vector, 3, 5, (p) -> p > 3), 2);
  }

  /**
   * Test of reverseAssign method, of class DoubleArray.
   */
  @Test
  public void testReverseAssign()
  {
    double[] in = new double[]{1,2,3,4,5};
    double[] expResult = new double[]{5,4,3,2,1};
    double[] result = DoubleArray.reverseAssign(in);
    assertEquals(result, expResult);
  }

  /**
   * Test of assertIntegerArray method, of class DoubleArray.
   */
  @Test
  public void testAssertIntegerArray()
  {
    double[] values = new double[]
    {
      1, 2, 3, 4
    };
    DoubleArray.assertIntegerArray(values);
  }

  @Test(expectedExceptions = MathExceptions.MathException.class)
  public void testAssertIntegerArray2()
  {
    double[] values = new double[]
    {
      1, 2.1, 3, 4
    };
    DoubleArray.assertIntegerArray(values);
  }

  /**
   * Test of equivalent method, of class DoubleArray.
   */
  @Test
  public void testEquivalent_doubleArr_doubleArr()
  {
    double[] operand1 = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] operand2 = new double[]
    {
      1, 2, 3, 4, 6
    };
    assertEquals(DoubleArray.equivalent(operand1, operand2), false);
    assertEquals(DoubleArray.equivalent(operand1, operand1), true);
    assertEquals(DoubleArray.equivalent(operand1, new double[0]), false);
    assertEquals(DoubleArray.equivalent(operand1, null), false);
    assertEquals(DoubleArray.equivalent(null, null), true);
    assertEquals(DoubleArray.equivalent(null, operand2), false);
  }

  /**
   * Test of equivalent method, of class DoubleArray.
   */
  @Test
  public void testEquivalent_5args()
  {
    double[] a = new double[]
    {
      1, 2, 3, 4
    };
    double[] b = new double[]
    {
      1, 2, 3, 5
    };
    assertEquals(DoubleArray.equivalent(a, 0, b, 0, 4), false);
    assertEquals(DoubleArray.equivalent(a, 0, b, 0, 3), true);
    assertEquals(DoubleArray.equivalent(a, 1, b, 1, 3), false);
    assertEquals(DoubleArray.equivalent(a, 1, b, 1, 2), true);
  }

  /**
   * Test of pow method, of class DoubleArray.
   */
  @Test
  public void testPow()
  {
    double[] source = new double[]
    {
      1, 2, 3, 4, 5
    };
    double power = 0.5;
    double[] result = DoubleArray.pow(source, power);
    for (int i = 0; i < 5; ++i)
      assertEquals(result[i], Math.pow(source[i], power), 0.0);
  }

  /**
   * Test of apply method, of class DoubleArray.
   */
  @Test
  public void testApply()
  {
    double[] input = new double[]
    {
      1, 2, 3, 4
    };
    DoubleUnaryOperator operator = p -> p * 2;
    double[] expResult = new double[]
    {
      2, 4, 6, 8
    };
    double[] result = DoubleArray.apply(input, operator);
    assertEquals(result, expResult);
    assertNotEquals(result, input);
    
    result = DoubleArray.apply(null, operator);
    assertEquals(result, null);
  }

  /**
   * Test of applyAssign method, of class DoubleArray.
   */
  @Test
  public void testApplyAssign()
  {
    double[] input = new double[]
    {
      1, 2, 3, 4
    };
    DoubleUnaryOperator operator = p -> p * 2;
    double[] expResult = new double[]
    {
      2, 4, 6, 8
    };
    double[] result = DoubleArray.applyAssign(input, operator);
    assertEquals(result, expResult);
    assertEquals(result, input);
    
    double[] output = new double[]
    {
      1, 2, 3, 4
    };
    
    expResult = new double[]
    {
      4, 8, 12, 16
    };
    result = DoubleArray.applyAssign(output, input, operator);
    assertEquals(result, expResult);
  }

  /**
   * Test of applyAssignRange method, of class DoubleArray.
   */
  @Test
  public void testApplyAssignRange()
  {
    double[] input = null;
    DoubleUnaryOperator operator = null;
    int begin = 0;
    int end = 0;
    double[] expResult = null;
    double[] result = DoubleArray.applyAssignRange(input, operator, begin, end);
    assertEquals(result, expResult);
  }

  /**
   * Test of multiplyAssign method, of class DoubleArray.
   */
  @Test
  public void testMultiplyAssign_5args()
  {
    double[] target = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] source = new double[]
    {
      1, 2, 3
    };
    double[] expResult = new double[]
    {
      1, 2, 6, 12, 5
    };
    double[] result = DoubleArray.multiplyAssign(target, 1, source, 0, 3);
    assertEquals(result, expResult);
  }

  /**
   * Test of multiplyInner method, of class DoubleArray.
   */
  @Test
  public void testMultiplyInner_doubleArr_doubleArr()
  {
    double[] srcA = new double[]{1,2,3,4,5};
    double[] srcB = new double[]{6,7,8,9,10};
    double expResult = 130.0;
    double result = DoubleArray.multiplyInner(srcA, srcB);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of multiplyInner method, of class DoubleArray.
   */
  @Test
  public void testMultiplyInner_5args()
  {
    double[] srcA = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] srcB = new double[]
    {
      6, 7, 8, -9, 10
    };
    assertEquals(DoubleArray.multiplyInner(srcA, 0, srcB, 0, 5), 58.0, 0.0);
    assertEquals(DoubleArray.multiplyInner(srcA, 1, srcB, 0, 4), 20.0, 0.0);
    assertEquals(DoubleArray.multiplyInner(srcA, 0, srcB, 1, 4), 36.0, 0.0);
    assertEquals(DoubleArray.multiplyInner(srcA, 2, srcB, 0, 3), 86.0, 0.0);
    assertEquals(DoubleArray.multiplyInner(srcA, 0, srcB, 2, 3), 20.0, 0.0);
    assertEquals(DoubleArray.multiplyInner(srcA, 1, srcB, 1, 3), 2.0, 0.0);
  }

  /**
   * Test of divideAssignRange method, of class DoubleArray.
   */
  @Test
  public void testDivideAssignRange()
  {
    double[] target = new double[]
    {
      2, 4, 6, 8, 10
    };
    double[] expResult = new double[]
    {
      2, 2, 3, 4, 10
    };
    double[] result = DoubleArray.divideAssignRange(target, 1, 4, 2);
    assertEquals(result, expResult);
  }

  /**
   * Test of fromString method, of class DoubleArray.
   */
  @Test
  public void testFromString()
  {
    String str = "1,2,3";
    double[] expResult = new double[]
    {
      1, 2, 3
    };
    double[] result = DoubleArray.fromString(str);
    assertEquals(result, expResult);
  }

  /**
   * Test of toString method, of class DoubleArray.
   */
  @Test
  public void testToString_doubleArr()
  {
    double[] values = new double[]{-1,1,1e6, 0};
    String result = DoubleArray.toString(values);
    assertEquals(result, "-1.0 1.0 1000000.0 0.0");
  }

  /**
   * Test of toString method, of class DoubleArray.
   */
  @Test
  public void testToString_doubleArr_String()
  {
    double[] values = new double[]
    {
      0, 1, 0.1, 0.01, 0.001
    };
    String result = DoubleArray.toString(values, "%3.2f");
    assertEquals(result, "0.00 1.00 0.10 0.01 0.00");
  }

  /**
   * Test of sumRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testSumRange()
  {
    double[] source = new double[]
    {
      1, 2, 3, 4, 5
    };
    assertEquals(DoubleArray.sumRange(source, 0, 5), 15.0);
    assertEquals(DoubleArray.sumRange(source, 1, 5), 14.0);
    assertEquals(DoubleArray.sumRange(source, 0, 4), 10.0);
    assertEquals(DoubleArray.sumRange(source, 0, 6), 15.0);
  }

  /**
   * Test of findIndexOfMaximumRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testFindIndexOfMaximumRange()
  {
    double[] source = new double[]
    {
      1, 2, 3, 4, 5, -1000, 1000
    };
    assertEquals(DoubleArray.findIndexOfMaximumRange(source, 0, 7), 6);
    assertEquals(DoubleArray.findIndexOfMaximumRange(source, 1, 7), 6);
    assertEquals(DoubleArray.findIndexOfMaximumRange(source, 0, 6), 4);
    assertEquals(DoubleArray.findIndexOfMaximumRange(source, 1, 6), 4);
    assertEquals(DoubleArray.findIndexOfMaximumRange(source, 0, 4), 3);
    assertEquals(DoubleArray.findIndexOfMaximumRange(source, 0, 8), 6);
  }

  /**
   * Test of findIndexOfExtremaRange method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfExtremaRange()
  {
    double[] source = new double[]
    {
      1, 2, 3, 4, 5, -1000, 1000
    };
    assertEquals(DoubleArray.findIndexOfExtremaRange(source, 0, 7), new int[]
    {
      5, 6
    });
    assertEquals(DoubleArray.findIndexOfExtremaRange(source, 0, 6), new int[]
    {
      5, 4
    });
    assertEquals(DoubleArray.findIndexOfExtremaRange(source, 0, 5), new int[]
    {
      0, 4
    });
    assertEquals(DoubleArray.findIndexOfExtremaRange(source, 1, 5), new int[]
    {
      1, 4
    });
    assertEquals(DoubleArray.findIndexOfExtremaRange(null, 0, 5), null);
  }

  /**
   * Test of findIndexOfMedian method, of class DoubleArray.
   */
  @Test
  public void testFindIndexOfMedian()
  {
    double[] vector = new double[]
    {
      3, 3, 5, 1, 5, 6, 2, 2, 10, 100
    };
    int result = DoubleArray.findIndexOfMedian(vector);
    assertEquals(result, 2);
  }

  /**
   * Test of toPrimitives method, of class DoubleArray.
   */
  @Test
  public void testToPrimitives_DoubleArr()
  {
    Double[] source = new Double[]
    {
      1.0, 2.0, 3.0, 4.0
    };
    double[] expResult = new double[]
    {
      1, 2, 3, 4
    };
    double[] result = DoubleArray.toPrimitives(source);
    assertEquals(result, expResult);
  }

  /**
   * Test of toPrimitives method, of class DoubleArray.
   */
  @Test
  public void testToPrimitives_Collection()
  {
    Collection<? extends Number> source = Arrays.asList(new Double[]
    {
      1.0, 2.0, 3.0, 4.0
    });
    double[] expResult = new double[]
    {
      1, 2, 3, 4
    };
    double[] result = DoubleArray.toPrimitives(source);
    assertEquals(result, expResult);
  }

  /**
   * Test of toObjects method, of class DoubleArray.
   */
  @Test
  public void testToObjects()
  {
    double[] source = new double[]
    {
      1, 2, 3, 4
    };
    Double[] expResult = new Double[]
    {
      1.0, 2.0, 3.0, 4.0
    };
    Double[] result = DoubleArray.toObjects(source);
    assertEquals(result, expResult);
    
    result = DoubleArray.toObjects(null);
    assertEquals(result, null);
  }

  /**
   * Test of sortPairs method, of class DoubleArray.
   */
  @Test
  public void testSortPairs()
  {
    double[] a = new double[]
    {
      5, 4, 3, 2, 1
    };
    double[] b = new double[]
    {
      1, 2, 3, 4, 5
    };
    double[] c = new double[]
    {
      5, 4, 3, 2, 1
    };
    double[] d = new double[]
    {
      1, 2, 3, 4, 5
    };
    DoubleArray.sortPairs(c, d);
    assertEquals(c, b);
    assertEquals(a, d);
  }

  /**
   * Test of assignMaximumOf method, of class DoubleArray.
   */
  @Test
  public void testAssignMaximumOf()
  {
    double[] target = new double[]{1,2,3,4,5,6};
    double scalar = 3.5;
    double[] expResult = new double[]{3.5,3.5,3.5,4,5,6};
    double[] result = DoubleArray.assignMaximumOf(target, scalar);
    assertEquals(result, expResult);
  }

  /**
   * Test of assignMinimumOf method, of class DoubleArray.
   */
  @Test
  public void testAssignMinimumOf()
  {
    double[] target = new double[]{1,2,3,4,5,6};
    double scalar = 3.5;
    double[] expResult = new double[]{1,2,3,3.5,3.5,3.5};
    double[] result = DoubleArray.assignMinimumOf(target, scalar);
    assertEquals(result, expResult);
  }

  /**
   * Test of add method, of class DoubleArray.
   */
  @Test
  public void testAdd_doubleArr_double()
  {
    double[] source = new double[]{1,2,3,4,5};
    double scalar = 5.0;
    double[] expResult = new double[]{6,7,8,9,10};
    double[] result = DoubleArray.add(source, scalar);
    assertEquals(result, expResult);
  }

  /**
   * Test of add method, of class DoubleArray.
   */
  @Test
  public void testAdd_doubleArr_doubleArr()
  {
    double[] a = new double[]{1,2,3,4,5};
    double[] b = new double[]{5,4,3,2,1};
    double[] expResult = new double[]{6,6,6,6,6};
    double[] result = DoubleArray.add(a, b);
    assertEquals(result, expResult);
  }

  /**
   * Test of productInnerRange method, of class DoubleArray.
   */
  @Test (expectedExceptions = IndexOutOfBoundsException.class)
  public void testProductInnerRange()
  {
    double[] a = new double[]{1,2,3,4,5,6,7,8,9,10};
    double[] b = new double[]{10,9,8,7,6,5,4,3,2,1};
    double expResult = 80.0;
    DoubleBinaryOperator func = (x, y) -> { return x * y; };
    double result = DoubleArray.productInnerRange(a, b, 0, 4, func);
    assertEquals(result, expResult);
    DoubleArray.productInnerRange(a, b, 0, 11, func);
  }
}
