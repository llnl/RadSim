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
 * Test code for Pair.
 */
public class PairNGTest
{

  public PairNGTest()
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
   * Test of getKey method, of class Pair.
   */
  @Test
  public void testGetKey()
  {
    Pair instance = new Pair();
    instance.key = "test";
    Object expResult = "test";
    Object result = instance.getKey();
    assertEquals(result, expResult);
  }

  /**
   * Test of getValue method, of class Pair.
   */
  @Test
  public void testGetValue()
  {
    Pair instance = new Pair();
    instance.value = "test";
    Object expResult = "test";
    Object result = instance.getValue();
    assertEquals(result, expResult);
  }

  /**
   * Test of setValue method, of class Pair.
   */
  @Test
  public void testSetValue()
  {
    Object v = "value";
    Pair instance = new Pair();
    Object result = instance.setValue(v);
    assertEquals(result, instance.getValue());
  }

  /**
   * Test of setKey method, of class Pair.
   */
  @Test
  public void testSetKey()
  {
    Object k = "key";
    Pair instance = new Pair();
    Object result = instance.setKey(k);
    assertEquals(result, instance.getKey());
  }

}
