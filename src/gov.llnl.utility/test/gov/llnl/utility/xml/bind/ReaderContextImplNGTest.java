/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ElementContextImpl;
import gov.llnl.utility.xml.bind.ReaderContextImpl;
import java.net.URI;
import java.net.URISyntaxException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ReaderContextImpl.
 */
public class ReaderContextImplNGTest
{

  public ReaderContextImplNGTest()
  {
  }

  /**
   * Test of getLocation method, of class ReaderContextImpl.
   */
  @Test
  public void testGetLocation()
  {
    // Tested end to end.
  }

  /**
   * Test of getElementPath method, of class ReaderContextImpl.
   */
  @Test
  public void testGetElementPath()
  {
    // Tested end to end.
  }

  /**
   * Test of setFile method, of class ReaderContextImpl.
   *
   * @throws java.net.URISyntaxException
   */
  @Test
  public void testSetFile() throws URISyntaxException
  {
    String myUrl = "test.txt";
    URI file = new URI(myUrl);
    ReaderContextImpl instance = new ReaderContextImpl();
    instance.setFile(file);
    assertEquals(instance.getFile(), file);
  }

  /**
   * Test of getFile method, of class ReaderContextImpl.
   */
  @Test
  public void testGetFile()
  {
    ReaderContextImpl instance = new ReaderContextImpl();
    URI expResult = null;
    URI result = instance.getFile();
    assertEquals(result, expResult);
  }

  /**
   * Test of setLocator method, of class ReaderContextImpl.
   */
  @Test
  public void testSetLocator()
  {
    // Tested end to end.
  }

  /**
   * Test of pushTemporaryContext method, of class ReaderContextImpl.
   */
  @Test
  public void testPushTemporaryContext()
  {
    Object parent = 1;
    Object child = 1;
    ReaderContextImpl instance = new ReaderContextImpl();
    ElementContextImpl curr = instance.currentContext;
    instance.pushTemporaryContext(parent, child);
    assertEquals(instance.currentContext.parentObject, parent);
    assertEquals(instance.currentContext.targetObject, child);
    assertEquals(instance.currentContext.localName, "#deferred");
    assertEquals(instance.currentContext.parentContext, curr);
  }

  /**
   * Test of popTemporaryContext method, of class ReaderContextImpl.
   */
  @Test
  public void testPopTemporaryContext()
  {
    ReaderContextImpl instance = new ReaderContextImpl();
    ElementContextImpl curr = instance.currentContext;

    // Push new content into the stack
    ElementContextImpl context = new ElementContextImpl(instance.currentContext, 1, null, "#deferred", null);
    context.targetObject = 1;
    instance.currentContext = context;

    instance.popTemporaryContext();
    assertEquals(instance.currentContext, curr);
  }

  /**
   * Test of addDeferred method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAddDeferred_4args() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of addDeferred method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testAddDeferred_String_DeferredHandler() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of hasDeferred method, of class ReaderContextImpl.
   */
  @Test
  public void testHasDeferred()
  {
    // Tested end to end.
  }

  /**
   * Test of getDeferredElements method, of class ReaderContextImpl.
   */
  @Test
  public void testGetDeferredElements()
  {
    // Tested end to end.
  }

  /**
   * Test of getExternal method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetExternal() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of put method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testPut() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of putScoped method, of class ReaderContextImpl.
   */
  @Test
  public void testPutScoped()
  {
    // Tested end to end.
  }

  /**
   * Test of get method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGet_String_Class() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of getReferences method, of class ReaderContextImpl.
   */
  @Test
  public void testGetReferences()
  {
    // Tested end to end.
  }

  /**
   * Test of get method, of class ReaderContextImpl.
   */
  @Test
  public void testGet_String()
  {
    // Tested end to end.
  }

  /**
   * Test of setPropertyHandler method, of class ReaderContextImpl.
   */
  @Test
  public void testSetPropertyHandler()
  {
    // Tested end to end.
  }

  /**
   * Test of getCurrentContext method, of class ReaderContextImpl.
   */
  @Test
  public void testGetCurrentHandlerContext()
  {
    ReaderContextImpl instance = new ReaderContextImpl();
    instance.currentContext = new ElementContextImpl(null, null, null, null, null);
    ElementContextImpl expResult = new ElementContextImpl(null, null, null, null, null);
    ElementContextImpl result = instance.getCurrentContext();
    assertEquals(result, expResult);
  }

//  /**
//   * Test of getChildContext method, of class ReaderContextImpl.
//   * 
//   * @throws java.lang.Exception
//   */
//  @Test
//  public void testGetLastHandlerContext() throws Exception
//  {
//    ReaderContextImpl instance = new ReaderContextImpl();
//    ElementContextImpl expResult = new ElementContextImpl();
//    instance. = expResult;
//    ElementContextImpl result = instance.getChildContext();
//    assertSame(result, expResult);
//  }
  /**
   * Test of startElement method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStartElement() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of endElement method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testEndElement() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of setErrorHandler method, of class ReaderContextImpl.
   */
  @Test
  public void testSetErrorHandler()
  {
    // Tested end to end.
  }

  /**
   * Test of handleException method, of class ReaderContextImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testHandleException() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of getDocumentReader method, of class ReaderContextImpl.
   */
  @Test
  public void testGetDocumentReader()
  {
    // Tested end to end.
  }

}
