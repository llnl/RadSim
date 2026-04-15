/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for ByteArrayChannel.
 */
public class ByteArrayChannelNGTest
{

  public ByteArrayChannelNGTest()
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
   * Test of read method, of class ByteArrayChannel.
   */
  @Test
  public void testRead() throws Exception
  {
    ByteBuffer bb = ByteBuffer.allocate(20);
    byte[] buffer = new byte[100];
    for (int i = 0; i < 100; ++i)
    {
      buffer[i] = (byte) i;
    }
    ByteArrayChannel instance = new ByteArrayChannel(buffer);
    int expResult = 20;
    int result = instance.read(bb);
    assertEquals(result, expResult);
    assertEquals(bb.position(), 20);
    assertEquals(bb.get(0), 0);
    assertEquals(bb.get(19), 19);
    instance.location = 111;
    result = instance.read(bb);
    assertEquals(result, 0);
    bb = ByteBuffer.allocate(1);
    instance.location = 100;
    result = instance.read(bb);
  }

  /**
   * Test of write method, of class ByteArrayChannel.
   */
  @Test
  public void testWrite() throws Exception
  {
    ByteBuffer bb = ByteBuffer.allocate(10);
    for (int i = 0; i < 10; ++i)
    {
      bb.put((byte) i);
    }
    bb.rewind();
    ByteArrayChannel instance = new ByteArrayChannel(new byte[15]);
    assertEquals(instance.write(bb), 10);
    bb.rewind();
    assertEquals(instance.write(bb), 5);

    bb = ByteBuffer.allocate(1);
    for (int j = 0; j < 1; ++j)
    {
      bb.put((byte) j);
    }
    bb.rewind();
    instance = new ByteArrayChannel(new byte[1]);
    instance.location = 15;
    assertEquals(instance.write(bb), 0);
  }

  /**
   * Test of position method, of class ByteArrayChannel.
   */
  @Test
  public void testPosition_0args() throws Exception
  {
    byte[] buffer = new byte[]
    {
    };
    ByteArrayChannel instance = new ByteArrayChannel(buffer);
    instance.location = 1;
    long expResult = 1L;
    long result = instance.position();
    assertEquals(result, expResult);
  }

  /**
   * Test of position method, of class ByteArrayChannel.
   */
  @Test(expectedExceptions =
  {
    IllegalArgumentException.class
  })
  public void testPosition_long() throws Exception
  {
    byte[] buffer = new byte[100];
    ByteArrayChannel instance = new ByteArrayChannel(buffer);
    SeekableByteChannel result = instance.position(50);
    assertEquals(result.position(), 50);
    result = instance.position(-1);
    assertEquals(result.position(), -1);
  }

  /**
   * Test of size method, of class ByteArrayChannel.
   */
  @Test
  public void testSize() throws Exception
  {
    byte[] buffer = new byte[100];
    ByteArrayChannel instance = new ByteArrayChannel(buffer);
    long expResult = 100L;
    long result = instance.size();
    assertEquals(result, expResult);
  }

  /**
   * Test of truncate method, of class ByteArrayChannel.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testTruncate() throws Exception
  {
    ByteArrayChannel instance = new ByteArrayChannel(new byte[100]);
    SeekableByteChannel expResult = null;
    SeekableByteChannel result = instance.truncate(1);
  }

  /**
   * Test of isOpen method, of class ByteArrayChannel.
   */
  @Test
  public void testIsOpen() throws IOException
  {
    byte[] buffer = new byte[100];
    ByteArrayChannel instance = new ByteArrayChannel(buffer);
    assertTrue(instance.isOpen());
    instance.close();
    assertFalse(instance.isOpen());
  }

  /**
   * Test of close method, of class ByteArrayChannel.
   */
  @Test
  public void testClose() throws Exception
  {
    ByteArrayChannel instance = new ByteArrayChannel(new byte[128]);
    instance.close();
    try
    {
      instance.read(ByteBuffer.allocate(10));
      fail("channel is not closed");
    }
    catch (ClosedChannelException ex)
    {
    }
  }

  @Test
  public void testEquals()
  {
    ByteArrayChannel instance_1 = new ByteArrayChannel(new byte[128]);
    ByteArrayChannel instance_2 = new ByteArrayChannel(new byte[128]);
    Object o = 1;

    assertEquals(instance_1.equals(instance_2), true);
    assertEquals(instance_1.equals(o), false);
    instance_2.location = 10;
    assertEquals(instance_1.equals(instance_2), false);
    instance_2.location = 0;
    instance_2 = new ByteArrayChannel(new byte[10]);
    assertEquals(instance_1.equals(instance_2), false);
  }

}
