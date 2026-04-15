/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for Benchmarker.
 */
public class BenchmarkerNGTest
{

  public BenchmarkerNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

  /**
   * Test of addTask method, of class Benchmarker.
   */
  @Test
  public void testAddTask()
  {
    String name = "";
    Benchmarker.Task task = null;
    Benchmarker instance = new Benchmarker();
    Benchmarker.Working result = instance.addTask(name, task);
    assertEquals(result.label, name);
    assertEquals(result.task, task);
  }

  /**
   * Test of warmupAll method, of class Benchmarker.
   */
  @Test
  public void testWarmupAll()
  {
    // Benchmarking is not used in production code
  }

  /**
   * Test of assessTarget method, of class Benchmarker.
   */
  @Test
  public void testAssessTarget()
  {
    // Benchmarking is not used in production code
  }

  /**
   * Test of test method, of class Benchmarker.
   */
  @Test
  public void testTest()
  {
    // Benchmarking is not used in production code
  }

}
