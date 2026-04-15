/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for IteratorInputStream.
 */
public class IteratorInputStreamNGTest
{

  public IteratorInputStreamNGTest()
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
   * Test of create method, of class IteratorInputStream.
   */
  @Test
  public void testCreate_Iterator() throws IOException
  {
    Iterable<String> src = new ArrayList<>(Arrays.asList("a", "b", "c"));
    InputStream result = IteratorInputStream.create(src);
    String contents = new String(result.readAllBytes());
    assertEquals(contents, "abc");
  }

  /**
   * Test of create method, of class IteratorInputStream.
   */
  @Test
  public void testCreate_Iterable() throws IOException
  {
    Iterable<String> src = new ArrayList<>(Arrays.asList("a", "b", "c"));
    InputStream expResult = new IteratorInputStream<>(src.iterator(), (String s) -> s.getBytes());
    InputStream result = IteratorInputStream.create(src);
    byte[] contents = result.readAllBytes();
    assertEquals(new String(contents), "abc");
  }

  /**
   * Test of read method, of class IteratorInputStream.
   */
  @Test
  public void testRead() throws Exception
  {
    Iterable<String> src = new ArrayList<>(Arrays.asList("a", "bc", "d"));
    InputStream instance1 = new IteratorInputStream<>(src.iterator(), (String s) -> s.getBytes());
    IteratorInputStream instance2 = (IteratorInputStream) instance1;
    assertEquals(instance2.read(), 'a');
    assertEquals(instance2.read(), 'b');
    assertEquals(instance2.read(), 'c');
    assertEquals(instance2.read(), 'd');
    assertEquals(instance2.read(), -1);
  }

  /**
   * Test of read method, of class IteratorInputStream.
   */
  @Test
  public void testRead_3args() throws Exception
  {
    Iterable<String> src = new ArrayList<>(Arrays.asList("a", "bc", "d"));
    byte[] b = new byte[10];
    int off = 0;
    int len = 10;
    IteratorInputStream instance = IteratorInputStream.create(src);
    int expResult = 4;
    int result = instance.read(b, off, len);
    assertEquals(result, expResult);
    assertEquals(b[0], 'a');
    assertEquals(b[1], 'b');
    assertEquals(b[2], 'c');
    assertEquals(b[3], 'd');
  }

}
