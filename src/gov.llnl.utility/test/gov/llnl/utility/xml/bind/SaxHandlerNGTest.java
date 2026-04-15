/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ReaderContextImpl;
import gov.llnl.utility.xml.bind.ElementHandlerMapImpl;
import gov.llnl.utility.xml.bind.ElementContextImpl;
import gov.llnl.utility.xml.bind.SaxHandler;
import gov.llnl.utility.xml.bind.ReaderBuilderImpl;
import gov.llnl.utility.xml.bind.ElementHandlerImpl;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import javax.xml.XMLConstants;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * Test code for SaxHandler.
 */
public class SaxHandlerNGTest
{

  public SaxHandlerNGTest()
  {
  }

  /**
   * Test of SaxHandler constructor, of class SaxHandler.
   */
  @Test
  public void testConstructor()
  {
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    assertNotNull(instance);
    try
    {
      instance = new SaxHandler(null);
      assertNotNull(instance);
    }
    catch (NullPointerException ne)
    {
      assertEquals(ne.getMessage(), "readerContext fail");
    }
  }

  /**
   * Test of setDocumentLocator method, of class SaxHandler.
   */
  @Test
  public void testSetDocumentLocator()
  {
    Locator locator = new TestLocator();
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    instance.setDocumentLocator(locator);
  }

  /**
   * Test of startPrefixMapping method, of class SaxHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStartPrefixMapping() throws Exception
  {
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    instance.startPrefixMapping("a", "b");
  }

  /**
   * Test of endPrefixMapping method, of class SaxHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testEndPrefixMapping() throws Exception
  {
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    // Empty body
    instance.endPrefixMapping(null);
  }

  /**
   * Test of startElement method, of class SaxHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStartElement() throws Exception
  {
    // Incomplete test, can't test class attributes for correct states
    String nameSpace = "first";
    String localName = "#";
    String key = "##first";

    org.xml.sax.helpers.AttributesImpl attributes = new org.xml.sax.helpers.AttributesImpl();

    // Setup required parts
    ReaderContextImpl rci = new ReaderContextImpl();
    rci.currentContext = new ElementContextImpl(null, null, null, null, null);
    // Setup handler
    BiConsumer<Object, Object> doNothing = (foo, bar) ->
    {
    };
    ElementHandlerImpl firstHandler = new ElementHandlerImpl(
            key, EnumSet.of(Reader.Option.ANY_ALL), String.class, doNothing);
    // Setup HandlerList
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();
    handlerList.firstHandler = firstHandler;
    // Set handlerMap
    ElementHandlerMapImpl handlerMap = ElementHandlerMapImpl.newInstance("#ElementHandler", handlerList);
    rci.currentContext.handlerMap = handlerMap;

    // skip if body
    SaxHandler instance = new SaxHandler(rci);
    instance.startElement(nameSpace, localName, "", attributes);

    // test if body
    rci = new ReaderContextImpl();
    rci.currentContext = new ElementContextImpl(null, null, null, null, null);
    // Setup handler
    firstHandler = new ElementHandlerImpl(
            key, EnumSet.of(Reader.Option.ANY_ALL), String.class, doNothing);
    // Setup HandlerList
    handlerList = new ReaderBuilderImpl.HandlerList();
    handlerList.firstHandler = firstHandler;
    // Set handlerMap
    handlerMap = ElementHandlerMapImpl.newInstance("#ElementHandler", handlerList);
    rci.currentContext.handlerMap = handlerMap;

    attributes.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation", "", "string", "somewhere");
    instance = new SaxHandler(rci);
    instance.startElement(nameSpace, localName, "", attributes);

    // Test SAXExceptionProxy inner ReaderException
    try
    {
      instance = new SaxHandler(new ReaderContextImpl());
      instance.startElement("", "", "", new org.xml.sax.helpers.AttributesImpl());
    }
    catch (SaxHandler.SAXExceptionProxy sp)
    {
      assertEquals(sp.exception.getClass(), ReaderException.class);
    }

    // Test SAXExceptionProxy 
    try
    {
      instance = new SaxHandler(new ReaderContextImpl());
      instance.startElement(null, null, null, null);
    }
    catch (SaxHandler.SAXExceptionProxy sp)
    {
      assertEquals(sp.exception.getClass(), ReaderException.class);
    }

  }

  /**
   * Test of endElement method, of class SaxHandler.
   */
  @Test
  public void testEndElement() throws Exception
  {
    // Setup required parts
    ReaderContextImpl rci = new ReaderContextImpl();
    ElementContextImpl parentContext = new ElementContextImpl(null, null, null, null, null);
    rci.currentContext = new ElementContextImpl(parentContext, null, null, null, null);

    // Setup handler
    BiConsumer<Object, Object> doNothing = (foo, bar) ->
    {
    };
    ElementHandlerImpl firstHandler = new ElementHandlerImpl(
            "", EnumSet.of(Reader.Option.ANY_ALL), String.class, doNothing);
    // Setup HandlerList
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();
    handlerList.firstHandler = firstHandler;
    // Set handlerMap
    ElementHandlerMapImpl handlerMap = ElementHandlerMapImpl.newInstance("#ElementHandler", handlerList);
    rci.currentContext.handlerMap = handlerMap;
    rci.currentContext.currentHandler = firstHandler;

    SaxHandler instance = new SaxHandler(rci);
    instance.endElement(null, null, null);

    // Test SAXExceptionProxy inner ReaderException
    try
    {
      firstHandler = new ElementHandlerImpl(
              "", EnumSet.of(Reader.Option.ANY_ALL), String.class, SaxHandlerNGTest::throwError);
      rci = new ReaderContextImpl();
      rci.currentContext = new ElementContextImpl(parentContext, null, null, null, null);
      rci.currentContext.currentHandler = firstHandler;
      instance = new SaxHandler(rci);
      instance.endElement(null, null, null);
    }
    catch (SaxHandler.SAXExceptionProxy sp)
    {
      assertEquals(sp.exception.getClass(), ReaderException.class);
    }
  }

  public static void throwError(Object o1, Object o2)
  {
    throw new RuntimeException("");
  }

  /**
   * Test of characters method, of class SaxHandler.
   */
  @Test
  public void testCharacters() throws Exception
  {
    // Setup required parts
    String nameSpace = "first";
    String localName = "#";
    String key = "##first";
    org.xml.sax.helpers.AttributesImpl attributes = new org.xml.sax.helpers.AttributesImpl();
    ReaderContextImpl rci = new ReaderContextImpl();
    rci.currentContext = new ElementContextImpl(null, null, null, null, null);
    // Setup handler
    BiConsumer<Object, Object> doNothing = (foo, bar) ->
    {
    };
    ElementHandlerImpl firstHandler = new ElementHandlerImpl(
            key, EnumSet.of(Reader.Option.ANY_ALL), String.class, doNothing);
    // Setup HandlerList
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();
    handlerList.firstHandler = firstHandler;
    // Set handlerMap
    ElementHandlerMapImpl handlerMap = ElementHandlerMapImpl.newInstance("#ElementHandler", handlerList);
    rci.currentContext.handlerMap = handlerMap;

    SaxHandler instance = new SaxHandler(rci);
    instance.startElement(nameSpace, localName, "", attributes);
    instance.characters(nameSpace.toCharArray(), 0, nameSpace.length());
  }

  /**
   * Test of ignorableWhitespace method, of class SaxHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testIgnorableWhitespace() throws Exception
  {
    // empty body
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    instance.ignorableWhitespace(null, -1, -1);
  }

  /**
   * Test of warning method, of class SaxHandler.
   *
   * @throws java.lang.Exception
   */
  @Test(expectedExceptions =
  {
    SAXParseException.class
  })
  public void testWarning() throws Exception
  {
    SAXParseException ex = new SAXParseException("", null);
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    instance.warning(ex);
  }

  /**
   * Test of error method, of class SaxHandler.
   *
   * @throws java.lang.Exception
   */
  @Test(expectedExceptions =
  {
    SAXParseException.class
  })
  public void testError() throws Exception
  {
    SAXParseException ex = new SAXParseException("", null);
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    instance.error(ex);
  }

  /**
   * Test of fatalError method, of class SaxHandler.
   *
   * @throws java.lang.Exception
   */
  @Test(expectedExceptions =
  {
    SAXParseException.class
  })
  public void testFatalError() throws Exception
  {
    SAXParseException ex = new SAXParseException("", null);
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    instance.fatalError(ex);
  }

  /**
   * Test of isValidate method, of class SaxHandler.
   */
  @Test
  public void testIsValidate()
  {
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    assertFalse(instance.isValidate());
  }

  /**
   * Test of setValidate method, of class SaxHandler.
   */
  @Test
  public void testSetValidate()
  {
    SaxHandler instance = new SaxHandler(new ReaderContextImpl());
    instance.setValidate(true);
    assertTrue(instance.isValidate());
    instance.setValidate(false);
    assertFalse(instance.isValidate());
  }

  public class TestLocator implements Locator
  {
    @Override
    public String getPublicId()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getSystemId()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getLineNumber()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnNumber()
    {
      throw new UnsupportedOperationException();
    }

  }
}
