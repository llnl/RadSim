/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.function.ToDoubleFunction;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ListUtilities.
 */
public class ListUtilitiesNGTest
{

  /**
   * Test of newList method, of class ListUtilities.
   */
  @Test
  public void testNewList()
  {
    Iterable<Integer> iterable = Arrays.asList(1, 2, 3);
    List expResult = Arrays.asList(1, 2, 3);
    List result = ListUtilities.newList(iterable);
    assertEquals(result, expResult);
  }

  /**
   * Test of permute method, of class ListUtilities.
   */
  @Test
  public void testPermute()
  {
    List<Integer> control = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    int range = list.size();
    ListUtilities.permute(list, range);
    // Do we have the same list object
    assertEquals(list, list);

    // We need at least one element not in its original place
    int swappedCounter = 0;
    for (int i = 0; i < control.size(); ++i)
    {
      if (control.get(i).intValue() != list.get(i).intValue())
      {
        ++swappedCounter;
      }
    }
    assertTrue(swappedCounter > 0);
  }

  /**
   * Test of unique method, of class ListUtilities.
   */
  @Test
  public void testUnique()
  {
    List<Integer> list = Arrays.asList(1, 4, 2, 2, 1, 2, 3, 4, 3, 1, 3, 4);
    List expResult = Arrays.asList(1, 2, 3, 4);
    List<Integer> result = ListUtilities.unique(list);
    assertEquals(result, expResult);
  }

  /**
   * Test of getDoubleField method, of class ListUtilities.
   */
  @Test
  public void testGetDoubleField()
  {
    List<Integer> list = Arrays.asList(1, 2, 3, 4);
    ToDoubleFunction<Integer> mapper = value -> value * 2.0;
    double[] expResult = new double[]
    {
      2.0, 4.0, 6.0, 8.0
    };
    double[] result = ListUtilities.getDoubleField(list, mapper);
    assertEquals(result, expResult);
  }

  /**
   * Test of reverse method, of class ListUtilities.
   */
  @Test
  public void testReverse_1args_1()
  {
    List<Integer> list = Arrays.asList(1, 2, 3, 4);
    Integer[] expResult = Arrays.asList(4, 3, 2, 1).toArray(Integer[]::new);
    ReverseCollection<Integer> result = ListUtilities.reverse(list);

    Integer[] resultArray = new Integer[result.size()];
    result.toArray(resultArray);
    assertEquals(resultArray, expResult);
  }

  /**
   * Test of reverse method, of class ListUtilities.
   */
  @Test
  public void testReverse_1args_2()
  {
    Integer[] array = new Integer[]
    {
      1, 2, 3, 4
    };
    Integer[] expResult = new Integer[]
    {
      4, 3, 2, 1
    };
    ReverseCollection<Integer> result = ListUtilities.reverse(array);
    Integer[] resultArray = new Integer[result.size()];
    result.toArray(resultArray);
    assertEquals(resultArray, expResult);
  }

  /**
   * Test of findFirst method, of class ListUtilities.
   */
  @Test
  public void testFindFirst()
  {
    List<Integer> list = Arrays.asList(1, 2, 3, 4);
    ListUtilities.FindMatcher<Integer> matcher = value -> value > 2;
    ListIterator result = ListUtilities.findFirst(list, matcher);
    assertEquals(result.next(), 3);
    assertEquals(result.next(), 4);
    assertTrue(result.hasNext() == false);
  }

  /**
   * Test of replaceFirst method, of class ListUtilities.
   */
  @Test
  public void testReplaceFirst()
  {
    List<Integer> list = Arrays.asList(1, 2, 3, 4);
    Integer replacement = 21;
    ListUtilities.FindMatcher<Integer> matcher = value -> value == 2;
    Integer result = ListUtilities.replaceFirst(list, replacement, matcher);
    assertEquals(result.intValue(), 2);
    assertEquals(list.get(1), replacement);
    assertNull(ListUtilities.replaceFirst(list, replacement, matcher));
  }

  /**
   * Test of shrinkTo method, of class ListUtilities.
   */
  @Test(expectedExceptions =
  {
    IllegalArgumentException.class
  })
  public void testShrinkTo()
  {
    List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
    int size = 2;

    List<Integer> result = ListUtilities.shrinkTo(list, size);
    // result and list is the same object
    assertEquals(result, list);
    assertEquals(System.identityHashCode(result), System.identityHashCode(list));
    assertEquals(list.size(), size);
    assertEquals(result.size(), size);

    // Test IllegalArgumentException
    ListUtilities.shrinkTo(list, list.size() * 2);
  }

  /**
   * Test of ensureSize method, of class ListUtilities.
   */
  @Test
  public void testEnsureSize_ArrayList_int()
  {
    ArrayList<Integer> list = new ArrayList<>(0);
    int requiredSize = 10;
    ListUtilities.ensureSize(list, requiredSize);

    assertEquals(list.size(), requiredSize);
  }

  /**
   * Test of ensureSize method, of class ListUtilities.
   */
  @Test
  public void testEnsureSize_List_int()
  {
    List<Integer> list = new ArrayList<>(0);
    int requiredSize = 10;
    ListUtilities.ensureSize(list, requiredSize);
    assertEquals(list.size(), requiredSize);
  }

}
