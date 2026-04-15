/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for MD5FilterInputStream.
 */
public class MD5FilterInputStreamNGTest
{
  InputStream is;

  public MD5FilterInputStreamNGTest() throws IOException
  {
    Path path = Paths.get(".", "test", "resources", "test.txt");
    is = Files.newInputStream(path);
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
   * Test of read method, of class MD5FilterInputStream.
   */
  @Test
  public void testRead_0args() throws Exception
  {
    MD5FilterInputStream instance = new MD5FilterInputStream(is);
    int expResult = 109;
    int result = instance.read();
    assertEquals(result, expResult);
  }

  /**
   * Test of read method, of class MD5FilterInputStream.
   */
  @Test
  public void testRead_byteArr() throws Exception
  {
    byte[] b = new byte[]
    {
      (byte) 1, (byte) 2, (byte) 4, (byte) 16,
      (byte) 32, (byte) 64, (byte) 128
    };
    MD5FilterInputStream instance = new MD5FilterInputStream(is);
    int expResult = 7;
    int result = instance.read(b);
    assertEquals(result, expResult);
  }

  /**
   * Test of read method, of class MD5FilterInputStream.
   */
  @Test
  public void testRead_3args() throws Exception
  {
    byte[] b = new byte[]
    {
      (byte) 1, (byte) 2, (byte) 4, (byte) 16,
      (byte) 32, (byte) 64, (byte) 128
    };
    int off = 0;
    int len = 6;
    MD5FilterInputStream instance = new MD5FilterInputStream(is);
    int expResult = 6;
    int result = instance.read(b, off, len);
    assertEquals(result, expResult);
  }

  /**
   * Test of getChecksum method, of class MD5FilterInputStream.
   */
  @Test
  public void testGetChecksum() throws IOException
  {
    MD5FilterInputStream instance = new MD5FilterInputStream(is);
    String expResult = "d41d8cd98f00b204e9800998ecf8427e";
    String result = instance.getChecksum();
    assertEquals(result, expResult);
  }

}
