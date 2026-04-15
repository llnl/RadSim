/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for FutureQueue.
 */
public class FutureQueueNGTest
{

  public FutureQueueNGTest()
  {
  }

  /**
   * Test of put method, of class FutureQueue.
   */
  @Test
  public void testPut()
  {
    // Tested under isEmpty() and get()
  }

  /**
   * Test of isEmpty method, of class FutureQueue.
   */
  @Test
  public void testIsEmpty() throws CancellationException, InterruptedException, ExecutionException
  {
    FutureQueue instance = new FutureQueue();
    assertTrue(instance.isEmpty());
    boolean[] done = new boolean[1];
    Future<Integer> future = new Future()
    {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
        return false;
      }

      @Override
      public boolean isCancelled()
      {
        return false;
      }

      @Override
      public boolean isDone()
      {
        return done[0];
      }

      @Override
      public Object get() throws InterruptedException, ExecutionException
      {
        return 1;
      }

      @Override
      public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
      {
        return null;
      }
    };
    instance.put(future);
    assertFalse(instance.isEmpty());
    instance.get();
    assertFalse(instance.isEmpty());
    done[0] = true;
    instance.get();
    assertTrue(instance.isEmpty());
  }

  /**
   * Test of size method, of class FutureQueue.
   */
  @Test
  public void testSize() throws CancellationException, InterruptedException, ExecutionException
  {
    FutureQueue instance = new FutureQueue();
    assertEquals(instance.size(), 0);
    boolean[] done = new boolean[1];
    Future<Integer> future = new Future()
    {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
        return false;
      }

      @Override
      public boolean isCancelled()
      {
        return false;
      }

      @Override
      public boolean isDone()
      {
        return done[0];
      }

      @Override
      public Object get() throws InterruptedException, ExecutionException
      {
        return 1;
      }

      @Override
      public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
      {
        return null;
      }
    };
    instance.put(future);
    assertEquals(instance.size(), 1);
    instance.get();
    assertEquals(instance.size(), 1);
    done[0] = true;
    instance.get();
    assertEquals(instance.size(), 0);
  }

  /**
   * Test of get method, of class FutureQueue.
   */
  @Test
  public void testGet() throws Exception
  {
    FutureQueue instance = new FutureQueue();
    assertNull(instance.get());
    boolean[] done = new boolean[1];
    Future<Integer> future = new Future()
    {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
        return false;
      }

      @Override
      public boolean isCancelled()
      {
        return false;
      }

      @Override
      public boolean isDone()
      {
        return done[0];
      }

      @Override
      public Object get() throws InterruptedException, ExecutionException
      {
        return 1;
      }

      @Override
      public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
      {
        return null;
      }
    };
    instance.put(future);
    assertNull(instance.get());
    done[0] = true;
    Object result = instance.get();
    assertNotNull(result);
    assertEquals(result, 1);
  }

}
