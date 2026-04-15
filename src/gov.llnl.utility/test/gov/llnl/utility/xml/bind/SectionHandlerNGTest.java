/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ReaderBuilderImpl;
import gov.llnl.utility.xml.bind.ElementContextImpl;
import gov.llnl.utility.xml.bind.SectionHandler;
import gov.llnl.utility.xml.bind.ElementHandlerMapImpl;
import gov.llnl.utility.xml.bind.ReaderContextImpl;
import gov.llnl.utility.TestSupport;
import gov.llnl.utility.TestSupport.TestReaderContext;
import gov.llnl.utility.TestSupport.TestSection;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.util.EnumSet;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for SectionHandler.
 */
public class SectionHandlerNGTest
{

  /**
   * Test of SectionHandler constructor, of class SectionHandler.
   */
  @Test
  public void testConstructor()
  {
    TestSection testSection = new TestSection();
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.OPTIONAL, Reader.Option.REQUIRED);
    SectionHandler instance = new SectionHandler(flags, testSection);

    assertSame(instance.section, testSection);
    assertEquals(instance.getKey(), testSection.getHandlerKey());
    assertTrue(instance.options.equals(flags));
    assertNull(instance.getObjectClass());
    assertNull(instance.method);
  }

  /**
   * Test of onStart method, of class SectionHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnStart() throws Exception
  {
    Integer intObj = 0;
    ReaderContextImpl testReaderContext = new ReaderContextImpl();
    ElementContextImpl parentContext = new ElementContextImpl(null, null, null, null, null);
    ElementContextImpl hci = new ElementContextImpl(parentContext, intObj, null, null, null);
    hci.parentContext.state = new Object();
    testReaderContext.currentContext = hci;
    org.xml.sax.helpers.AttributesImpl attributes = new org.xml.sax.helpers.AttributesImpl();

    TestSection testSection = new TestSection();
    SectionHandler instance = new SectionHandler(null, testSection);

    Object obj = instance.onStart(testReaderContext, attributes);

    assertSame(hci.parentObject, intObj);
    assertSame(hci.targetObject, hci.parentObject);
    assertSame(obj, null);
    assertSame(testSection.attributes, attributes);
    assertSame(hci.state, hci.parentContext.state);
  }

  /**
   * Test of onEnd method, of class SectionHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnEnd() throws Exception
  {
    TestSection testSection = new TestSection();
    SectionHandler instance = new SectionHandler(null, testSection);
    assertNull(instance.onEnd(null));
  }

  /**
   * Test of onTextContent method, of class SectionHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnTextContent() throws Exception
  {
    TestSection testSection = new TestSection();
    SectionHandler instance = new SectionHandler(null, testSection);
    assertNull(instance.onTextContent(null, null));
  }

  /**
   * Test of getHandlers method, of class SectionHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    TestSection testSection = new TestSection();
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();
    ElementHandlerMapImpl emap = ElementHandlerMapImpl.newInstance("#aot", handlerList);
    testSection.elementHandlerMap = emap;

    SectionHandler instance = new SectionHandler(null, testSection);
    assertSame(instance.getHandlers(null), emap);
  }

  /**
   * Test of getReaderFor method, of class SectionHandler.
   */
  @Test
  public void testGetReader()
  {
    TestSection testSection = new TestSection();
    SectionHandler instance = new SectionHandler(null, testSection);
    assertSame(instance.getReader(), testSection);
  }

  /**
   * Test of createSchemaElement method, of class SectionHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaElement() throws Exception
  {
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.REQUIRED);
    TestSection testSection = new TestSection();
    SectionHandler instance = new SectionHandler(flags, testSection);

    Element testElement = newElement("TestElement");
    DomBuilder group = new DomBuilder(testElement);

    instance.createSchemaElement(null, group);

    for (Reader.Option opt : flags)
    {
      if (opt.getKey() == null)
        continue;
      assertNotNull(testElement.getAttribute(opt.getKey()));
      assertSame(testElement.getAttribute(opt.getKey()), opt.getValue());
    }
  }

}
