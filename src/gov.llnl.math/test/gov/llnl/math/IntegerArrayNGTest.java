/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntSupplier;
import org.testng.Assert;
import support.IntegerTestGenerator;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import org.testng.annotations.Test;

/**
 * Test code for IntegerArray.
 */
public class IntegerArrayNGTest
{
  IntegerTestGenerator tg = new IntegerTestGenerator();

  public IntegerArrayNGTest()
  {
  }
  
  /**
   * Test constructor of class IntegerArray.
   */
  @Test
  public void testConstructor()
  {
    IntegerArray instance = new IntegerArray();
  }

  /**
   * Test of colon method, of class IntegerArray.
   */
  @Test
  public void testColon()
  {
    int[] v = IntegerArray.colon(10, 15);
    int j = 0;
    for (int i = 10; i < 15; ++i, ++j)
    {
      assertEquals(v[j], i);
    }
  }

  /**
   * Test of addAssign method, of class IntegerArray.
   */
  @Test
  public void testAddAssign()
  {
    for (int i = 0; i < 10; ++i)
    {
      int len = tg.uniformRand(1, 65);
      int[] a1 = tg.newArray(len);
      int[] a2 = tg.newArray(len);
      int[] a3 = new int[len];
      for (int j = 0; j < len; ++j)
      {
        a3[j] = a1[j] + a2[j];
      }
      IntegerArray.addAssign(a1, a2);

      for (int j = 0; j < len; ++j)
      {
        assertEquals(a3[j], a1[j]);
      }
    }
  }

  /**
   * Test of findIndexOfFirstRange method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testFindIndexOfFirstRange()
  {
    int[] v = IntegerArray.colon(0, 5);
    int[] ans = new int[]
    {
      0, 0, -1, -1,
      1, 0, -1, 0,
      2, 1, 0, 0,
      3, 2, 0, 0,
      4, 3, 0, 0,
      -1, 4, 0, 0,
      -1, -1, 0, 0
    };

    int j = 0;
    for (int i = -1; i < 6; i++)
    {
      int u0 = IntegerArray.findIndexOfFirstRange(v, 0, v.length, new IntegerConditional.GreaterThan(i));
      int u1 = IntegerArray.findIndexOfFirstRange(v, 0, v.length, new IntegerConditional.GreaterThanEqual(i));
      int u2 = IntegerArray.findIndexOfFirstRange(v, 0, v.length, new IntegerConditional.LessThan(i));
      int u3 = IntegerArray.findIndexOfFirstRange(v, 0, v.length, new IntegerConditional.LessThanEqual(i));
      int u4 = IntegerArray.findIndexOfFirstRange(IntegerArray.colon(0, 1), 0, 3, new IntegerConditional.GreaterThan(3));

      assertEquals(u0, ans[j++]);
      assertEquals(u1, ans[j++]);
      assertEquals(u2, ans[j++]);
      assertEquals(u3, ans[j++]);
      assertEquals(u4, 0);
    }
  }

  /**
   * Test of findIndexOfLastRange method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testFindIndexOfLastRange()
  {
    int[] v = IntegerArray.colon(0, 5);
    int[] ans = new int[]
    {
      4, 4, -1, -1, // -1
      4, 4, -1, 0, //   0
      4, 4, 0, 1, //    1 
      4, 4, 1, 2, //    2
      4, 4, 2, 3, //    3
      -1, 4, 3, 4, //   4
      -1, -1, 4, 4  //  5
    };

    int j = 0;
    for (int i = -1; i < 6; i++)
    {
      int u0 = IntegerArray.findIndexOfLastRange(v, 0, v.length, new IntegerConditional.GreaterThan(i));
      int u1 = IntegerArray.findIndexOfLastRange(v, 0, v.length, new IntegerConditional.GreaterThanEqual(i));
      int u2 = IntegerArray.findIndexOfLastRange(v, 0, v.length, new IntegerConditional.LessThan(i));
      int u3 = IntegerArray.findIndexOfLastRange(v, 0, v.length, new IntegerConditional.LessThanEqual(i));
      int u4 = IntegerArray.findIndexOfLastRange(IntegerArray.colon(0, 1), 0, 3, new IntegerConditional.GreaterThan(3));


      assertEquals(u0, ans[j++]);
      assertEquals(u1, ans[j++]);
      assertEquals(u2, ans[j++]);
      assertEquals(u3, ans[j++]);
      assertEquals(u4, 0);
    }
  }

  /**
   * Test of assign method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testAssign()
  {
    int[] target = new int[6];
    int targetOffset = 1;
    int[] source = new int[]{1,2,3,4};
    int sourceOffset = 0;
    int length = 3;
    int[] expResult = new int[]{0,1,2,3, 0, 0};
    int[] result = IntegerArray.assign(target, targetOffset, source, sourceOffset, length);
    assertEquals(result, expResult);
    IntegerArray.assign(target, targetOffset, source, sourceOffset, 5);
  }

  /**
   * Test of copyOf method, of class IntegerArray.
   */
  @Test
  public void testCopyOf()
  {
    int[] input = new int[]{1,2,3,4,5};
    int[] expResult = new int[]{1,2,3,4,5};
    int[] result = IntegerArray.copyOf(input);
    assertEquals(result, expResult);
    Assert.assertNotSame(result, input);
    assertEquals(IntegerArray.copyOf(null), null);
    
  }

  /**
   * Test of copyOfRange method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    NullPointerException.class,
    IndexOutOfBoundsException.class
  })
  public void testCopyOfRange()
  {
    int[] input = new int[]{1,2,3,4,5};
    int begin = 1;
    int end = 4;
    int[] expResult = new int[]{2,3,4};
    int[] result = IntegerArray.copyOfRange(input, begin, end);
    assertEquals(result, expResult);
    IntegerArray.copyOfRange(null, begin, end);
    IntegerArray.copyOfRange(input, 0, 6);
    IntegerArray.copyOfRange(input, 0, end);
    IntegerArray.copyOfRange(input, 1, 6);
    IntegerArray.copyOfRange(input, 1, end);
    IntegerArray.copyOfRange(input, begin, 6);
  }
    
  /**
   * Test of fill method, of class IntegerArray.
   */
  @Test
  public void testFill_intArr_int()
  {
    int[] target = new int[]{1,2,3,4,5};
    int value = 6;
    int[] expResult = new int[]{6,6,6,6,6};
    int[] result = IntegerArray.fill(target, value);
    assertEquals(result, expResult);
    assertSame(result, target);
    assertNull(IntegerArray.fill(null, value));
  }

  /**
   * Test of fillRange method, of class IntegerArray.
   */
  @Test
  public void testFillRange_4args_1()
  {
    int[] target = new int[]{1,2,3,4,5};
    int begin = 1;
    int end = 4;
    int value = 6;
    int[] expResult = new int[]{1,6,6,6,5};
    int[] result = IntegerArray.fillRange(target, begin, end, value);
    assertEquals(result, expResult);
    assertSame(result, target);
  }

  /**
   * Test of fill method, of class IntegerArray.
   */
  @Test
  public void testFill_intArr_IntSupplier()
  {
    int[] target = new int[]{1,2,3,4,5};
    IntSupplier value = ()->6;
    int[] expResult = new int[]{6,6,6,6,6};
    int[] result = IntegerArray.fill(target, value);
    assertEquals(result, expResult);
    assertSame(result, target);
    assertNull(IntegerArray.fill(null, value));
  }

  /**
   * Test of fillRange method, of class IntegerArray.
   */
  @Test
  public void testFillRange_4args_2()
  {
    int[] target = new int[]{1,2,3,4,5};
    int begin = 1;
    int end = 4;
    IntSupplier value = ()->6;
    int[] expResult = new int[]{1,6,6,6,5};
    int[] result = IntegerArray.fillRange(target, begin, end, value);
    assertEquals(result, expResult);
    assertSame(result, target);
    assertNull(IntegerArray.fillRange(null, begin, end, value));
  }

  /**
   * Test of add method, of class IntegerArray.
   */
  @Test
  public void testAdd()
  {
    int[] a = new int[]{1,2,3,4,5,1,2,3,4,5};
    int[] b = new int[]{5,4,3,2,1,5,4,3,2,1};
    int[] expResult = new int[]{6,6,6,6,6,6,6,6,6,6};
    int[] result = IntegerArray.add(a, b);
    assertEquals(result, expResult);
    assertNotSame(result, a);
    assertEquals(a, new int[]{1,2,3,4,5,1,2,3,4,5});
  }

  /**
   * Test of subtractAssign method, of class IntegerArray.
   */
  @Test
  public void testSubtractAssign()
  {
    int[] target = new int[]{1,2,3,4,5,1,2,3,4,5};
    int[] source = new int[]{5,4,3,2,1,5,4,3,2,1};
    int[] expResult = new int[]{-4,-2,0,2,4,-4,-2,0,2,4};
    int[] result = IntegerArray.subtractAssign(target, source);
    assertEquals(result, expResult);
    assertSame(result, target);
  }

  /**
   * Test of fromString method, of class IntegerArray.
   */
  @Test
  public void testFromString()
  {
    String str = "1,2,3";
    int[] expResult = new int[]{1,2,3};
    int[] result = IntegerArray.fromString(str);
    assertEquals(result, expResult);
  }

  /**
   * Test of promoteToDoubles method, of class IntegerArray.
   */
  @Test
  public void testPromoteToDoubles()
  {
    int[] in = new int[]{1,2,3,4};
    double[] expResult = new double[]{1,2,3,4};
    double[] result = IntegerArray.promoteToDoubles(in);
    assertEquals(result, expResult);
    assertNull(IntegerArray.promoteToDoubles(null));
  }

  /**
   * Test of toPrimitives method, of class IntegerArray.
   */
  @Test
  public void testToPrimitives_IntegerArr()
  {
    Integer[] in = new Integer[]{1,2,3,4};
    int[] expResult = new int[]{1,2,3,4};
    int[] result = IntegerArray.toPrimitives(in);
    assertEquals(result, expResult);
  }

  /**
   * Test of toPrimitives method, of class IntegerArray.
   */
  @Test
  public void testToPrimitives_Collection()
  {
    Collection<? extends Number> in = Arrays.asList(new Integer[]{1,2,3,4});
    int[] expResult = new int[]{1,2,3,4};
    int[] result = IntegerArray.toPrimitives(in);
    assertEquals(result, expResult);
  }

  /**
   * Test of toObjects method, of class IntegerArray.
   */
  @Test
  public void testToObjects()
  {
    int[] v = new int[]{1,2,3,4};
    Integer[] expResult = new Integer[]{1,2,3,4};
    Integer[] result = IntegerArray.toObjects(v);
    assertEquals(result, expResult);
    assertNull(IntegerArray.toObjects(null));
  }

  /**
   * Test of toString method, of class IntegerArray.
   */
  @Test
  public void testToString()
  {
    int[] values = new int[]{1,2,3,4};
    String expResult = "1 2 3 4";
    String result = IntegerArray.toString(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of sum method, of class IntegerArray.
   */
  @Test
  public void testSum()
  {
    int[] d = new int[]{1,2,3,4,5};
    int expResult = 15;
    int result = IntegerArray.sum(d);
    assertEquals(result, expResult);
  }

  /**
   * Test of sumRange method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testSumRange()
  {
    int[] d = new int[]{1,2,3,4,5,6,7,8,9,10};
    assertEquals(IntegerArray.sumRange(d, 0, 5), 15);
    assertEquals(IntegerArray.sumRange(d, 1, 5), 14);
    assertEquals(IntegerArray.sumRange(d, 0, 4), 10);
    assertEquals(IntegerArray.sumRange(d, 1, 4), 9);
    assertEquals(IntegerArray.sumRange(d, 1, 9), 44);
    assertEquals(IntegerArray.sumRange(d, 1, 11), 44);
    
  }

  /**
   * Test of findIndexOfMinimum method, of class IntegerArray.
   */
  @Test
  public void testFindIndexOfMinimum()
  {
    int[] values = new int[]{1,2,-13,4,5,6};
    int expResult = 2;
    int result = IntegerArray.findIndexOfMinimum(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of findIndexOfMaximum method, of class IntegerArray.
   */
  @Test
  public void testFindIndexOfMaximum()
  {
    int[] values = new int[]{1,2,13,4,5,6};
    int expResult = 2;
    int result = IntegerArray.findIndexOfMaximum(values);
    assertEquals(result, expResult);
  }

  /**
   * Test of findIndexOfMinimumRange method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testFindIndexOfMinimumRange()
  {
    int[] values = new int[]{-10,1,2,3,4,-15};
    assertEquals(IntegerArray.findIndexOfMinimumRange(values, 0, 6), 5);
    assertEquals(IntegerArray.findIndexOfMinimumRange(values, 1, 6), 5);
    assertEquals(IntegerArray.findIndexOfMinimumRange(values, 0, 5), 0);
    assertEquals(IntegerArray.findIndexOfMinimumRange(values, 0, 7), 0);
  }

  /**
   * Test of findIndexOfMaximumRange method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testFindIndexOfMaximumRange()
  {
    int[] values = new int[]{10,1,2,3,4,15};
    assertEquals(IntegerArray.findIndexOfMaximumRange(values, 0, 6), 5);
    assertEquals(IntegerArray.findIndexOfMaximumRange(values, 1, 6), 5);
    assertEquals(IntegerArray.findIndexOfMaximumRange(values, 0, 5), 0);
    assertEquals(IntegerArray.findIndexOfMaximumRange(values, 0, 10), 0);
  }

  /**
   * Test of findIndexOfExtrema method, of class IntegerArray.
   */
  @Test
  public void testFindIndexOfExtrema()
  {
    int[] data = new int[]{10,1,2,3,4,-15};
    int[] expResult = new int[]{5,0};
    int[] result = IntegerArray.findIndexOfExtrema(data);
    assertEquals(result, expResult);
  }

  /**
   * Test of findIndexOfExtremaRange method, of class IntegerArray.
   */
  @Test (expectedExceptions = {
    IndexOutOfBoundsException.class
  })
  public void testFindIndexOfExtremaRange()
  {
    int[] data = new int[]{10,1,2,3,4,-15};
    assertEquals(IntegerArray.findIndexOfExtremaRange(data, 0, 6), new int[]{5,0});
    assertEquals(IntegerArray.findIndexOfExtremaRange(data, 1, 5), new int[]{1,4});
    assertEquals(IntegerArray.findIndexOfExtremaRange(data, 1, 7), new int[]{1,4});
  }

  /**
   * Test of anyEquals method, of class IntegerArray.
   */
  @Test
  public void testAnyEquals()
  {
    int[] in = new int[]{1,2,3,4,5};
    assertEquals(IntegerArray.anyEquals(in, 1), true);
    assertEquals(IntegerArray.anyEquals(in, 5), true);
    assertEquals(IntegerArray.anyEquals(in, 0), false);
    assertEquals(IntegerArray.anyEquals(in, 6), false);
  }

}
