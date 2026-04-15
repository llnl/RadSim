/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Random;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for InputStreamUtilities.
 */
public class InputStreamUtilitiesNGTest
{

  public InputStreamUtilitiesNGTest()
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
   * Test of readAllBytes method, of class InputStreamUtilities.
   */
  @Test
  public void testReadAllBytes() throws Exception
  {
    Random r = new Random();
    r.setSeed(1);
    ByteBuffer bb = ByteBuffer.allocate(1024 * 4);
    r.ints().limit(1024).forEach(bb::putInt);
    bb.rewind();
    byte[] contents = bb.array().clone();
    InputStream inputStream = new ByteBufferInputStream(bb);
    byte[] result = InputStreamUtilities.readAllBytes(inputStream);
    assertEquals(result, contents);
  }

  /**
   * Test of md5Checksum method, of class InputStreamUtilities.
   */
  @Test
  public void testMd5Checksum() throws Exception
  {
    InputStream is = new ByteArrayInputStream("testing".getBytes());
    String expResult = "ae2b1fca515949e5d54fb22b8ed95575";
    String result = InputStreamUtilities.md5Checksum(is);
    assertEquals(result, expResult);
  }

  /**
   * Test of newByteChannel method, of class InputStreamUtilities.
   */
  @Test
  public void testNewByteChannel()
  {
    byte[] buffer = new byte[]
    {
    };
    SeekableByteChannel expResult = new ByteArrayChannel(buffer);
    SeekableByteChannel result = InputStreamUtilities.newByteChannel(buffer);
    assertEquals(result, expResult);
  }

}
