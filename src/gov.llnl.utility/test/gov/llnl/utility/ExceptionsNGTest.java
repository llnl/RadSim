/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for Exceptions.
 */
public class ExceptionsNGTest
{

  public ExceptionsNGTest()
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
   * Test of getContext method, of class Exceptions.
   */
  @Test
  public void testGetContext()
  {
    Throwable throwable = new Throwable();
    Map result = Exceptions.getContext(throwable);
    assertTrue(result instanceof Map);
  }

  /**
   * Test of add method, of class Exceptions.
   */
  @Test
  public void testAdd()
  {
    Throwable throwable = new Throwable();
    Throwable result = Exceptions.add(throwable, "key", "value");
    assertEquals(result, throwable);
    assertNotEquals(Exceptions.getContext(throwable), null);
  }

  /**
   * Test of builder method, of class Exceptions.
   */
  @Test
  public void testBuilder()
  {
    Throwable t = new Throwable();
    Exceptions.builder(t)
            .put("key", "value")
            .put("apple", 1);
    Map<String, Object> result = Exceptions.getContext(t);
    assertTrue(result.containsKey("key"));
    assertTrue(result.containsKey("apple"));
  }

}
