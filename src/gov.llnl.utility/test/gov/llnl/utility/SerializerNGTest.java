/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Serializer.
 */
public class SerializerNGTest
{

  public SerializerNGTest()
  {
  }

  /**
   * Test of renameClass method, of class Serializer.
   */
  @Test
  public void testRenameClass()
  {
    String revised = "";
    String original = "";
    Serializer instance = new Serializer();
    instance.renameClass(revised, original);
  }

  /**
   * Test of setIgnoreVersionUID method, of class Serializer.
   */
  @Test
  public void testSetIgnoreVersionUID_String()
  {
    Serializer instance = new Serializer();
    instance.setIgnoreVersionUID(Object.class.getName());
    assertTrue(instance.overrides.containsKey(Object.class.getName()));
  }

  /**
   * Test of setIgnoreVersionUID method, of class Serializer.
   */
  @Test
  public void testSetIgnoreVersionUID_0args()
  {
    Serializer instance = new Serializer();
    instance.setIgnoreVersionUID();
    assertEquals(instance.defaultOverride.ignoreVersionUID, true);
  }

  /**
   * Test of setCompress method, of class Serializer.
   */
  @Test
  public void testSetCompress()
  {
    boolean compress = true;
    Serializer instance = new Serializer();
    instance.setCompress(compress);
    assertEquals(instance.compress, true);
  }

  /**
   * Test of load method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testLoad() throws Exception
  {
    Path file = Paths.get("build/test/foo");
    Serializer instance = new Serializer();
    Serializable expResult = new ArrayList(Arrays.asList("a", "b", 1, 2));
    instance.save(file, expResult);
    Serializable result = instance.load(file);
    assertEquals(result, expResult);
  }

  /**
   * Test of save method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testSave() throws Exception
  {
    Path file = Paths.get("build/test/foo");
    Serializable object = new ArrayList(Arrays.asList("a", "b", 1, 2));
    Serializer instance = new Serializer();
    instance.save(file, object);
  }

  /**
   * Test of convert method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testConvert() throws Exception
  {
    // Not used in production.
//    Path file = Paths.get("test.ez");
//    Serializer instance = new Serializer();
//    instance.convert(file);
  }

  /**
   * Test of pack method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testPack() throws Exception
  {
    Serializable object = new ArrayList(Arrays.asList("a", "b", 1, 2));
    Serializer instance = new Serializer();
    byte[] expResult = new byte[]
    {
      -84, -19, 0, 5, 115, 114, 0, 19, 106, 97, 118, 97, 46, 117, 116, 105, 108,
      46, 65, 114, 114, 97, 121, 76, 105, 115, 116, 120, -127, -46, 29, -103,
      -57, 97, -99, 3, 0, 1, 73, 0, 4, 115, 105, 122, 101, 120, 112, 0, 0, 0, 4,
      119, 4, 0, 0, 0, 4, 116, 0, 1, 97, 116, 0, 1, 98, 115, 114, 0, 17, 106,
      97, 118, 97, 46, 108, 97, 110, 103, 46, 73, 110, 116, 101, 103, 101, 114,
      18, -30, -96, -92, -9, -127, -121, 56, 2, 0, 1, 73, 0, 5, 118, 97, 108,
      117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46,
      78, 117, 109, 98, 101, 114, -122, -84, -107, 29, 11, -108, -32, -117, 2,
      0, 0, 120, 112, 0, 0, 0, 1, 115, 113, 0, 126, 0, 4, 0, 0, 0, 2, 120,
    };
    byte[] result = instance.pack(object);
//    for (byte b : result)
//    {
//      System.out.print(b + ", ");
//    }
//    System.out.println();
    assertEquals(result, expResult);
  }

  /**
   * Test of unpack method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testUnpack() throws Exception
  {
    byte[] buffer = new byte[]
    {
      -84, -19, 0, 5, 115, 114, 0, 19, 106, 97, 118, 97, 46, 117, 116, 105, 108,
      46, 65, 114, 114, 97, 121, 76, 105, 115, 116, 120, -127, -46, 29, -103,
      -57, 97, -99, 3, 0, 1, 73, 0, 4, 115, 105, 122, 101, 120, 112, 0, 0, 0, 4,
      119, 4, 0, 0, 0, 4, 116, 0, 1, 97, 116, 0, 1, 98, 115, 114, 0, 17, 106,
      97, 118, 97, 46, 108, 97, 110, 103, 46, 73, 110, 116, 101, 103, 101, 114,
      18, -30, -96, -92, -9, -127, -121, 56, 2, 0, 1, 73, 0, 5, 118, 97, 108,
      117, 101, 120, 114, 0, 16, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46,
      78, 117, 109, 98, 101, 114, -122, -84, -107, 29, 11, -108, -32, -117, 2,
      0, 0, 120, 112, 0, 0, 0, 1, 115, 113, 0, 126, 0, 4, 0, 0, 0, 2, 120,
    };
    Serializer instance = new Serializer();
    Serializable expResult = new ArrayList(Arrays.asList("a", "b", 1, 2));
    Serializable result = instance.unpack(buffer);
    assertEquals(result, expResult);
  }

  /**
   * Test of encode method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testEncode() throws Exception
  {
    Serializable object = new ArrayList(Arrays.asList("a", "b", 1, 2));
    String expResult
            = "H4sIAAAAAAAAAFvzloG1uIhBOCuxLFGvtCQzR8+xqCix0iezuKSi8ZLszOOJc5kZGD"
            + "0ZWIozq1IrChgYGFjKWUBkCQNjIhAnATULgjXnJOal63nmlaSmpxYJPVqw5HtjuwUT"
            + "SCtrWWJOaWpFEYMAQp1faW5SalHbmqmy3FMedDMxMICNZiwuZKhjABnPVAEA0tN8IJ"
            + "kAAAA=";
    String result = Serializer.encode(object);
    assertEquals(result, expResult);
  }

  /**
   * Test of decode method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testDecode() throws Exception
  {
    String str = "H4sIAAAAAAAAAFvzloG1uIhBOCuxLFGvtCQzR8+xqCix0iezuKSi8ZLszOOJc5kZGD0ZWIozq1IrChgYGFjKWUBkCQNjIhAnATULgjXnJOal63nmlaSmpxYJPVqw5HtjuwUTSCtrWWJOaWpFEYMAQp1faW5SalHbmqmy3FMedDMxMICNZiwuZKhjABnPVAEA0tN8IJkAAAA=";
    Serializable expResult = new ArrayList(Arrays.asList("a", "b", 1, 2));
    Serializable result = Serializer.decode(str);
    assertEquals(result, expResult);
  }

  /**
   * Test of copy method, of class Serializer.
   * @throws java.lang.Exception
   */
  @Test
  public void testCopy() throws Exception
  {
    ArrayList<Object> object = new ArrayList(Arrays.asList("a", "b", 1, 2));
    Object expResult = Arrays.asList("a", "b", 1, 2);
    Object result = Serializer.copy(object);
    assertEquals(result, expResult);
  }

}
