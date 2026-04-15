/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ReaderContextImpl;
import gov.llnl.utility.xml.bind.DocumentReaderImpl;
import gov.llnl.utility.xml.bind.AnyReader;
import gov.llnl.utility.xml.bind.DocumentReader;
import static gov.llnl.utility.xml.bind.DocumentReader.SCHEMA_SOURCE;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PropertyMap;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.util.Map;
import java.util.TreeMap;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for DocumentReaderImpl.
 */
public class DocumentReaderImplNGTest
{

  /**
   * Test of getContext method, of class DocumentReaderImpl.
   */
  @Test
  public void testGetContext()
  {
    AnyReader anyReader = AnyReader.of(Integer.class);
    ReaderContextImpl r = new ReaderContextImpl();
    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
    instance.readerContext = r;
    ReaderContext expResult = r;
    ReaderContext result = instance.getContext();
    assertEquals(result, expResult);
  }

  /**
   * Test of clearContext method, of class DocumentReaderImpl.
   */
  @Test
  public void testClearContext()
  {
    AnyReader anyReader = AnyReader.of(Integer.class);
    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
    instance.readerContext = new ReaderContextImpl();
    instance.clearContext();
    assertEquals(instance.readerContext, null);
  }

  /**
   * Test of createContext method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateContext() throws Exception
  {
    DocumentReader instance = DocumentReader.create(Object.class);
    instance.setErrorHandler((rc, e) ->
    {
    });
    ReaderContextImpl result = (ReaderContextImpl) instance.createContext();
    assertNotNull(result);
    assertSame(result.getDocumentReader(), instance);
    assertNotNull(result.propertyHandler);
    assertNotNull(result.exceptionHandler);
  }

  /**
   * Test of createContext method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateContext2() throws Exception
  {
    DocumentReader instance = DocumentReader.create(AnyReader.of(Object.class));
    instance.setErrorHandler((rc, e) ->
    {
    });
    ReaderContextImpl result = (ReaderContextImpl) instance.createContext();
    assertNotNull(result);
    assertSame(result.getDocumentReader(), instance);
    assertNotNull(result.propertyHandler);
    assertNotNull(result.exceptionHandler);
  }

  /**
   * Test of loadStream method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testLoadStream() throws Exception
  {
    // Tested in end to end.
//    System.out.println("loadStream");
//    InputStream stream = null;
//    DocumentReaderImpl instance = null;
//    Object expResult = null;
//    Object result = instance.loadStream(stream);
//    assertEquals(result, expResult);
  }

  /**
   * Test of loadResource method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testLoadResource() throws Exception
  {
    // Tested in end to end.
//    String resourceName = "";
//    AnyReader anyReader = AnyReader.of(Integer.class);
//    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
//    Object expResult = null;
//    Object result = instance.loadResource(resourceName);
//    assertEquals(result, expResult);
  }

  /**
   * Test of loadFile method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testLoadFile() throws Exception
  {
    // Tested in end to end.
//    Path file = null;
//    AnyReader anyReader = AnyReader.of(Integer.class);
//    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
//    Object expResult = null;
//    Object result = instance.loadFile(file);
//    assertEquals(result, expResult);
  }

  /**
   * Test of loadURL method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testLoadURL() throws Exception
  {
    // Tested in end to end
//    URL url = null;
//    DocumentReaderImpl instance = null;
//    Object expResult = null;
//    Object result = instance.loadURL(url);
//    assertEquals(result, expResult);
  }

  /**
   * Test of loadSource method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testLoadSource() throws Exception
  {
    // Tested in end to end
//    System.out.println("loadSource");
//    InputSource inputSource = null;
//    DocumentReaderImpl instance = null;
//    Object expResult = null;
//    Object result = instance.loadSource(inputSource);
//    assertEquals(result, expResult);
  }

  /**
   * Test of getObjectReader method, of class DocumentReaderImpl.
   */
  @Test
  public void testGetObjectReader()
  {
    AnyReader anyReader = AnyReader.of(Integer.class);
    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
    ObjectReader expResult = anyReader;
    ObjectReader result = instance.getObjectReader();
    assertEquals(result, expResult);
  }

  /**
   * Test of setErrorHandler method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testSetErrorHandler() throws Exception
  {
    ReaderContext.ExceptionHandler exceptionHandler = null;
    DocumentReaderImpl instance = (DocumentReaderImpl) DocumentReader.create(Object.class);
    instance.setErrorHandler(exceptionHandler);
  }

  /**
   * Test of setProperty method, of class DocumentReaderImpl.
   */
  @Test
  public void testSetProperty()
  {
    String key = "";
    Object value = null;
    AnyReader anyReader = AnyReader.of(Integer.class);
    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
    instance.setProperty(key, value);
    assertEquals(instance.properties.get(key), value);
  }

  /**
   * Test of getProperty method, of class DocumentReaderImpl.
   */
  @Test
  public void testGetProperty()
  {
    String key = "";
    AnyReader anyReader = AnyReader.of(Integer.class);
    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
    instance.properties.put(key, "value");
    Object expResult = "value";
    Object result = instance.getProperty(key);
    assertEquals(result, expResult);
  }

  /**
   * Test of getProperties method, of class DocumentReaderImpl.
   */
  @Test
  public void testGetProperties()
  {
    AnyReader anyReader = AnyReader.of(Integer.class);
    DocumentReaderImpl instance = new DocumentReaderImpl(anyReader);
    Map expResult = new TreeMap<>();
    String key = SCHEMA_SOURCE;
    String value = "http://utility.llnl.gov/schema/utility.xsd";
    expResult.put(key, value);
    Map result = instance.getProperties();
    assertEquals(result, expResult);
  }

  /**
   * Test of setPropertyHandler method, of class DocumentReaderImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testSetPropertyHandler() throws Exception
  {
    PropertyMap handler = null;
    DocumentReaderImpl instance = (DocumentReaderImpl) DocumentReader.create(Object.class);
    instance.setPropertyHandler(handler);
  }

  @Test
  public void testSetSchema()
  {
    ObjectReader reader = AnyReader.of(Object.class);
    DocumentReaderImpl instance = new DocumentReaderImpl(reader);
    instance.setSchema(reader);
    assertEquals(instance.getProperty(SCHEMA_SOURCE), "http://utility.llnl.gov/schema/utility.xsd");
  }

}
