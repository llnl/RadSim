/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.DocumentWriterImpl;
import gov.llnl.utility.xml.bind.WriterBuilderImpl;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.DocumentWriter;
import gov.llnl.utility.xml.bind.ObjectWriter;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for WriterBuilderImpl.
 */
public class WriterBuilderImplNGTest
{

  public WriterBuilderImplNGTest()
  {
  }

  public static class TestWriter extends ObjectWriter
  {
    TestWriter()
    {
      super(0, "test", null);
    }

    @Override
    public void attributes(WriterAttributes attributes, Object object) throws WriterException
    {
    }

    @Override
    public void contents(Object object) throws WriterException
    {
    }

  }

  static WriterBuilderImpl newInstance()
  {
    TestWriter test = new TestWriter();
    DocumentWriterImpl out = (DocumentWriterImpl) DocumentWriter.create(test);
    out.getContext().newDocument(test);
    WriterBuilderImpl instance = (WriterBuilderImpl) out.getContext().newBuilder(test);
    return instance;
  }

  /**
   * Test of element method, of class WriterBuilderImpl.
   */
  @Test
  public void testElement()
  {
//    WriterBuilderImpl instance = newInstance();
//    WriterBuilderImpl result = instance.element("foo");
//    assertEquals(result.elementName, "foo");
  }

  /**
   * Test of writer method, of class WriterBuilderImpl.
   */
  @Test
  public void testWriter()
  {
//    WriterBuilderImpl instance = newInstance();
//    instance.writer(new TestWriter());
//    assertNotNull(instance.producer);
  }

  /**
   * Test of contents method, of class WriterBuilderImpl.
   */
  @Test
  public void testContents() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    instance.contents(String.class);
  }

  /**
   * Test of comment method, of class WriterBuilderImpl.
   */
  @Test
  public void testComment() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    instance.comment("comment");
  }

  /**
   * Test of put method, of class WriterBuilderImpl.
   */
  @Test
  public void testPut_Object() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    Object object = null;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.put(object);
//    assertEquals(result, expResult);
  }

  /**
   * Test of section method, of class WriterBuilderImpl.
   */
  @Test
  public void testSection() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    ObjectWriter.Section writer = null;
//    instance.section(writer);
  }

  /**
   * Test of id method, of class WriterBuilderImpl.
   */
  @Test
  public void testId() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    TestWriter test = new TestWriter();
//    DocumentWriter out = DocumentWriter.create(test);
//    String id = "";
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.id(id);
//    assertEquals(result, expResult);
  }

  /**
   * Test of attr method, of class WriterBuilderImpl.
   */
  @Test
  public void testAttr() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    String key = "foo";
//    Object value = null;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.attr(key, value);
//    assertEquals(result, expResult);
  }

  /**
   * Test of putContents method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutContents() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    Object value = null;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putContents(value);
//    assertEquals(result, expResult);
  }

  /**
   * Test of putList method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutList() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
////    Iterable<Type> value = null;
////    ObjectWriter<Type> writer = null;
////    WriterBuilderImpl instance = null;
////    WriterBuilderImpl expResult = null;
////    WriterBuilderImpl result = instance.putList(value, writer);
////    assertEquals(result, expResult);
  }

  /**
   * Test of reference method, of class WriterBuilderImpl.
   */
  @Test
  public void testReference() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    String id = "";
//    instance.reference(id);
  }

  /**
   * Test of put method, of class WriterBuilderImpl.
   */
  @Test
  public void testPut_0args() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.put();
//    assertEquals(result, expResult);
  }

  /**
   * Test of putString method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutString() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putString("test");
//    assertEquals(result, expResult);
  }

  /**
   * Test of putInteger method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutInteger() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    int value = 0;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putInteger(value);
//    assertEquals(result, expResult);
  }

  /**
   * Test of putLong method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutLong() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    long value = 0L;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putLong(value);
//    assertEquals(result, expResult);
  }

  /**
   * Test of putDouble method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutDouble_double() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    double value = 0.0;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putDouble(value);
//    assertEquals(result, expResult);
  }

  /**
   * Test of putDouble method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutDouble_double_String() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    double value = 0.0;
//    String format = "";
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putDouble(value, format);
//    assertEquals(result, expResult);
  }

  /**
   * Test of putBoolean method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutBoolean() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    boolean value = false;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putBoolean(value);
//    assertEquals(result, expResult);
  }

  /**
   * Test of putFlag method, of class WriterBuilderImpl.
   */
  @Test
  public void testPutFlag() throws Exception
  {
//    WriterBuilderImpl instance = newInstance();
//    boolean value = false;
//    WriterBuilderImpl expResult = null;
//    WriterBuilderImpl result = instance.putFlag(value);
//    assertEquals(result, expResult);
  }

}
