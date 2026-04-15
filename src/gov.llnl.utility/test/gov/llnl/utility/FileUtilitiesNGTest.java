/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for FileUtilities.
 */
public class FileUtilitiesNGTest
{

  public FileUtilitiesNGTest()
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

  static class TestFileCase
  {
    Path file;
    Object expResult;

    private TestFileCase(Path file, Object string)
    {
      this.file = file;
      this.expResult = string;
    }
  }

  /**
   * Test of getFileExtension method, of class FileUtilities.
   */
  @Test
  public void testGetFileExtensionPosition()
  {
    TestFileCase[] cases = new TestFileCase[]
    {
      new TestFileCase(Paths.get("test"), -1),
      new TestFileCase(Paths.get("test.gz"), 4),
      new TestFileCase(Paths.get("test.txt"), 4),
      new TestFileCase(Paths.get(".test"), -1),
      new TestFileCase(Paths.get(".test.txt"), 5),
      new TestFileCase(Paths.get("test.tar.gz"), 4),
      new TestFileCase(Paths.get("test.myname.tar.gz"), 11)
    };
    for (TestFileCase test : cases)
    {
      int result = PathUtilities.getFileExtensionPosition(test.file);
      assertEquals(result, test.expResult);
    }
  }

  /**
   * Test of getFileExtension method, of class FileUtilities.
   */
  @Test
  public void testGetFileExtension()
  {
    TestFileCase[] cases = new TestFileCase[]
    {
      new TestFileCase(Paths.get("a_test"), ""),
      new TestFileCase(Paths.get("a_test.tar"), "tar"),
      new TestFileCase(Paths.get("a_test.tar.gz"), "tar.gz")
    };
    for (TestFileCase test : cases)
    {
      String result = PathUtilities.getFileExtension(test.file);
      assertEquals(result, test.expResult);
    }
  }

  /**
   * Test of md5Checksum method, of class FileUtilities.
   */
  @Test
  public void testMd5Checksum() throws Exception
  {
    // No need to test this one is it was just a pass through to md5sum
//    Path file = null;
//    String expResult = "";
//    String result = FileUtilities.md5Checksum(file);
//    assertEquals(result, expResult);
  }
}
