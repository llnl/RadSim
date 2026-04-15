/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ReverseCollection.
 */
public class ReverseCollectionNGTest
{
  public ReverseCollectionNGTest()
  {
  }

  /**
   * Test of iterator method, of class ReverseCollection.
   */
  @Test
  public void testIterator()
  {
    List<Integer> testList = new ArrayList(Arrays.asList(0, 1, 2, 3));
    ReverseCollection rc = new ReverseCollection(testList);
    Iterator<Integer> iterator = rc.iterator();
    assertNotNull(iterator);

    int testValue = 3;
    while (iterator.hasNext())
    {
      int value = iterator.next();
      assertEquals(value, testValue);
      --testValue;
      iterator.remove();
    }
    assertEquals(iterator.hasNext(), false);
  }

  /**
   * Test of size method, of class ReverseCollection.
   */
  @Test
  public void testSize()
  {
    assertEquals(new ReverseCollection(new ArrayList()).size(), 0);

    List<Integer> testList = Arrays.asList(0, 1, 2, 3);
    assertEquals(new ReverseCollection(testList).size(), 4);
  }

  /**
   * Test of isEmpty method, of class ReverseCollection.
   */
  @Test
  public void testIsEmpty()
  {
    assertTrue(new ReverseCollection(new ArrayList()).isEmpty());
    List<Integer> testList = Arrays.asList(0, 1, 2, 3);
    assertEquals(new ReverseCollection(testList).isEmpty(), false);
  }

  /**
   * Test of contains method, of class ReverseCollection.
   */
  @Test
  public void testContains()
  {
    List<Integer> testList = Arrays.asList(0, 1, 2, 3);
    ReverseCollection rc = new ReverseCollection(testList);
    assertEquals(rc.contains(4), false);
    assertEquals(rc.contains(0), true);
  }

  /**
   * Test of toArray method, of class ReverseCollection.
   */
  @Test
  public void testToArray_0args()
  {
    List<Integer> testList = Arrays.asList(0, 1, 2, 3);
    ReverseCollection rc = new ReverseCollection(testList);
    Object[] objArray = rc.toArray();
    assertEquals(objArray.length, testList.size());
    assertTrue(objArray[0] instanceof Object);
    int testValue = 3;
    for (Object obj : objArray)
    {
      assertEquals(((Integer) obj).intValue(), testValue);
      --testValue;
    }
  }

  /**
   * Test of toArray method, of class ReverseCollection.
   */
  @Test
  public void testToArray_GenericType()
  {
    List<Integer> testList = Arrays.asList(0, 1, 2, 3);
    ReverseCollection<Integer> rc = new ReverseCollection<>(testList);
    Integer[] intArray = new Integer[testList.size()];
    intArray = rc.toArray(intArray);
    assertEquals(intArray.length, testList.size());
    Integer testValue = 3;
    for (Integer value : intArray)
    {
      assertEquals(value, testValue);
      --testValue;
    }
  }

  /**
   * Test of add method, of class ReverseCollection.
   */
  @Test
  public void testAdd()
  {
    ReverseCollection rc = new ReverseCollection(new ArrayList());
    assertEquals(rc.size(), 0);
    rc.add("One");
    assertEquals(rc.size(), 1);
    rc.add(2);
    assertEquals(rc.size(), 2);
    rc.add(3.3333);
    assertEquals(rc.size(), 3);
    rc.add(new Object());
    assertEquals(rc.size(), 4);

    rc = new ReverseCollection(new ArrayList());
    rc.add(3);
    rc.add(2);
    rc.add(1);
    rc.add(0);
    Integer testValue = 3;
    for (Object obj : rc.toArray())
    {
      assertEquals((Integer) obj, testValue);
      --testValue;
    }
  }

  /**
   * Test of remove method, of class ReverseCollection.
   */
  @Test
  public void testRemove()
  {
    List<Integer> testList = Arrays.asList(0, 1, 2, 3);
    ReverseCollection<Integer> rc = new ReverseCollection<>(new ArrayList(testList));
    int testSize = testList.size();
    assertEquals(rc.size(), testSize);
    for (Integer value : testList)
    {
      boolean removeStatus = rc.remove(value);
      assertTrue(removeStatus);
      --testSize;
      assertEquals(rc.size(), testSize);
    }
    assertEquals(rc.size(), 0);
    assertEquals(rc.remove(0), false);
  }

  /**
   * Test of containsAll method, of class ReverseCollection.
   */
  @Test
  public void testContainsAll()
  {
    List<Integer> testList = new ArrayList(Arrays.asList(0, 1, 2, 3));
    ReverseCollection<Integer> rc = new ReverseCollection<>(new ArrayList());
    assertEquals(rc.containsAll(testList), false);
    rc.add(0);
    assertEquals(rc.containsAll(testList), false);
    rc.add(1);
    rc.add(2);
    assertEquals(rc.containsAll(testList), false);
    rc.add(4);
    assertEquals(rc.containsAll(testList), false);
    rc.add(3);
    assertEquals(rc.containsAll(testList), true);
  }

  /**
   * Test of addAll method, of class ReverseCollection.
   */
  @Test
  public void testAddAll()
  {
    List<Integer> testList = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
    ReverseCollection<Integer> rc = new ReverseCollection<>(new ArrayList());
    rc.addAll(testList);
    assertEquals(rc.size(), testList.size());
    Integer testValue = 0;
    for (Object obj : rc.toArray())
    {
      assertEquals((Integer) obj, testValue);
      ++testValue;
    }
  }

  /**
   * Test of removeAll method, of class ReverseCollection.
   */
  @Test
  public void testRemoveAll()
  {
    List<Integer> testList = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
    ReverseCollection<Integer> rc = new ReverseCollection<>(new ArrayList(testList));
    assertTrue(rc.removeAll(testList));
    assertEquals(rc.size(), 0);

    rc.add(4);
    rc.add(5);
    rc.addAll(testList);
    rc.removeAll(testList);
    assertEquals(rc.size(), 2);
  }

  /**
   * Test of retainAll method, of class ReverseCollection.
   */
  @Test
  public void testRetainAll()
  {
    List<Integer> testList = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
    ReverseCollection<Integer> rc = new ReverseCollection<>(new ArrayList(testList));
    rc.add(4);
    rc.add(5);
    rc.add(6);
    assertTrue(rc.retainAll(testList));
    assertEquals(rc.size(), 4);
    assertEquals(rc.contains(4), false);
    assertEquals(rc.contains(5), false);
    assertEquals(rc.contains(6), false);
    Integer testValue = 3;
    for (Object obj : rc.toArray())
    {
      assertEquals((Integer) obj, testValue);
      --testValue;
    }
  }

  /**
   * Test of clear method, of class ReverseCollection.
   */
  @Test
  public void testClear()
  {
    List<Integer> testList = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
    ReverseCollection<Integer> rc = new ReverseCollection<>(testList);
    assertEquals(rc.size(), 4);
    rc.clear();
    assertEquals(rc.size(), 0);
  }

}
