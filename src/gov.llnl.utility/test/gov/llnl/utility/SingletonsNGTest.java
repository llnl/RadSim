/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Singletons.
 */
public class SingletonsNGTest
{

  static class TestSingleton implements Singletons.Singleton
  {
    private static TestSingleton instance;

    private TestSingleton()
    {
    }

    public static TestSingleton getInstance()
    {
      if (instance == null)
      {
        instance = new TestSingleton();
      }
      return instance;
    }
  }

  static class TestSingletonRequired implements Singletons.SingletonRequired
  {
    private static TestSingletonRequired instance;

    private TestSingletonRequired()
    {
    }

    public static TestSingletonRequired getInstance()
    {
      if (instance == null)
      {
        instance = new TestSingletonRequired();
      }
      return instance;
    }
  }

  static class MyTest1
  {
    private static Integer getInstance()
    {
      return 0;
    }
  }

  static class MyTest2
  {
    public static MyTest2 getInstance() throws Exception
    {
      throw new Exception("STOP! In the name of LOVE!");
    }
  }

  public SingletonsNGTest()
  {
  }

  /**
   * Test of getSingleton method, of class Singletons.
   */
  @Test(expectedExceptions =
  {
    RuntimeException.class
  })
  public void testGetSingleton()
  {
    assertEquals(Singletons.getSingleton(TestSingleton.class), TestSingleton.getInstance());
    assertEquals(Singletons.getSingleton(TestSingletonRequired.class), TestSingletonRequired.getInstance());

    // Test IllegalAccessException
    Singletons.getSingleton(MyTest1.class);

    // Test NoSuchMethodException
    Singletons.getSingleton(Integer.class);

    // Test InvocationTargetException
    Singletons.getSingleton(MyTest2.class);
  }

}
