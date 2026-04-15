/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.Arrays;
import java.util.Iterator;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for LimitedSizeQueue.
 */
public class LimitedSizeQueueNGTest
{

  public LimitedSizeQueueNGTest()
  {
  }

  /**
   * Test of enqueueAll method, of class LimitedSizeQueue.
   */
  @Test
  public void testEnqueueAll()
  {
    Iterable<String> in = Arrays.asList("a", "b", "c", "d", "e");
    LimitedSizeQueue<String> instance = new LimitedSizeQueue(3);
    Iterable expResult = Arrays.asList("a", "b");
    Iterable result = instance.enqueueAll(in);
    assertEquals(result, expResult);
  }

  /**
   * Test of enqueue method, of class LimitedSizeQueue.
   */
  @Test
  public void testEnqueue_1()
  {
    Object in = 2;
    int size = 0;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    Object expResult = 2;
    Object result = instance.enqueue(in);
    assertEquals(result, expResult);
  }

  /**
   * Test of enqueue method, of class LimitedSizeQueue.
   */
  @Test
  public void testEnqueue_2()
  {
    Object in = null;
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    Object expResult = null;
    Object result = instance.enqueue(in);
    assertEquals(result, expResult);
  }

  /**
   * Test of enqueue method, of class LimitedSizeQueue.
   */
  @Test
  public void testEnqueue_3()
  {
    Object in = 2;
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    Object expResult = null;
    Object result = instance.enqueue(in);
    assertEquals(result, expResult);
  }

  /**
   * Test of clear method, of class LimitedSizeQueue.
   */
  @Test
  public void testClear()
  {
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    instance.storage[size - 1] = "test";
    instance.index++;
    if (instance.index == size)
      instance.index = 0;
    if (instance.filled < size)
      instance.filled++;
    instance.clear();
    assertEquals(instance.storage, new Object[]
    {
      null
    });
    assertEquals(instance.index, -1);
    assertEquals(instance.filled, 0);
  }

  /**
   * Test of get method, of class LimitedSizeQueue.
   */
  @Test
  public void testGet_1()
  {
    int i = 0;
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    instance.storage[size - 1] = 1;
    instance.index++;
    if (instance.index == size)
      instance.index = 0;
    if (instance.filled < size)
      instance.filled++;
    Object expResult = 1;
    Object result = instance.get(i);
    assertEquals(result, expResult);
  }

  /**
   * Test of get method, of class LimitedSizeQueue.
   */
  @Test
  public void testGet_2()
  {
    int i = 2;
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    instance.storage[size - 1] = 1;
    instance.index++;
    if (instance.index == size)
      instance.index = 0;
    if (instance.filled < size)
      instance.filled++;
    Object expResult = null;
    Object result = instance.get(i);
    assertEquals(result, expResult);
  }

  /**
   * Test of size method, of class LimitedSizeQueue.
   */
  @Test
  public void testSize()
  {
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    instance.storage[size - 1] = 1;
    instance.index++;
    if (instance.index == size)
      instance.index = 0;
    if (instance.filled < size)
      instance.filled++;
    int expResult = 1;
    int result = instance.size();
    assertEquals(result, expResult);
  }

  /**
   * Test of isFull method, of class LimitedSizeQueue.
   */
  @Test
  public void testIsFull()
  {
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    instance.storage[size - 1] = 1;
    instance.index++;
    if (instance.index == size)
      instance.index = 0;
    if (instance.filled < size)
      instance.filled++;
    boolean expResult = true;
    boolean result = instance.isFull();
    assertEquals(result, expResult);
  }

  /**
   * Test of iterator method, of class LimitedSizeQueue.
   */
  @Test
  public void testIterator()
  {
    int size = 1;
    LimitedSizeQueue instance = new LimitedSizeQueue(size);
    Iterator expResult = instance.new IteratorImpl();
    Iterator result = instance.iterator();
    assertEquals(result, expResult);
  }

}
