/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ReaderHandler;
import gov.llnl.utility.TestSupport;
import gov.llnl.utility.TestSupport.TestReader;
import gov.llnl.utility.TestSupport.TestReaderContext;
import gov.llnl.utility.TestSupport.TestReaderMixed;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.AnyReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.Reader.Option;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for ReaderHandler.
 */
public class ReaderHandlerNGTest
{

  public ReaderHandlerNGTest()
  {
  }

  /**
   * Test of ReaderHandler constructor, of class ReaderHandler.
   */
  @Test
  public void testConstructor()
  {
    BiConsumer<Object, Object> doNothing = (a, b) -> System.out.print("");
    String key = "key";
    String target = "target";
    AnyReader anyReader = AnyReader.of(String.class);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.REQUIRED);

    ReaderHandler instance = new ReaderHandler(key, flags, doNothing, anyReader);

    assertEquals(instance.getKey(), key);
    assertSame(instance.method, doNothing);
    assertSame(instance.options, flags);
    assertEquals(instance.getObjectClass(), anyReader.getObjectClass());

    assertSame(instance.reader, anyReader);
    assertSame(instance.decl, anyReader.getDeclaration());
  }

  /**
   * Test of onStart method, of class ReaderHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnStart() throws Exception
  {
    String str = "str";
    TestReader testReader = TestReader.of(String.class);
    testReader.obj = str;
    org.xml.sax.helpers.AttributesImpl attributes = new org.xml.sax.helpers.AttributesImpl();
    ReaderContext context = TestReaderContext.create();
    ReaderHandler instance = new ReaderHandler(null, null, null, testReader);
    Object obj = instance.onStart(context, attributes);
    assertSame(obj, str);

    // Test ReaderException
    try
    {
      testReader.obj = null;
      obj = instance.onStart(context, attributes);
    }
    catch (ReaderException re)
    {
      assertSame(re.getMessage(), "Auto attributes applied to null object");
    }
  }

  /**
   * Test of onEnd method, of class ReaderHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnEnd() throws Exception
  {
    String str = "str";
    TestReader testReader = TestReader.of(String.class);
    testReader.obj = str;

    ReaderHandler instance = new ReaderHandler(null, null, null, testReader);
    assertSame(instance.onEnd(null), str);
  }

  /**
   * Test of onTextContent method, of class ReaderHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnTextContent() throws Exception
  {
    AnyReader anyReader = AnyReader.of(String.class);
    ReaderHandler instance = new ReaderHandler(null, null, null, anyReader);

    // AnyReader.contents method return null
    assertNull(instance.onTextContent(null, ""));

    String str = "str";
    TestReader testReader = TestReader.of(String.class);
    testReader.obj = str;
    instance = new ReaderHandler(null, null, null, testReader);
    assertSame(instance.onTextContent(null, ""), str);
  }

  /**
   * Test of getHandlers method, of class ReaderHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    // Setup
    BiConsumer<Object, Object> doNothing = (a, b) -> System.out.print("");
    String key = "key";
    String target = "target";
    AnyReader anyReader = AnyReader.of(String.class);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.REQUIRED);
    ReaderHandler instance = new ReaderHandler(key, flags, doNothing, anyReader);
    ReaderContext context = TestReaderContext.create();
    // Call
    Reader.ElementHandlerMap handlerMap = instance.getHandlers(context);
    // Verify
    assertEquals(handlerMap.toList().size(), 1);
    Reader.ElementHandler anyHandlerImpl = handlerMap.toList().get(0);
    // FIXME
//    assertEquals(anyHandlerImpl.getKey(), "##any");
//    assertEquals(anyHandlerImpl.getObjectClass(), String.class);
  }

  /**
   * Test of getReaderFor method, of class ReaderHandler.
   */
  @Test
  public void testGetReader()
  {
    AnyReader anyReader = AnyReader.of(String.class);
    ReaderHandler instance = new ReaderHandler(null, null, null, anyReader);
    assertSame(instance.getReader(), anyReader);
  }

  /**
   * Test of createSchemaElement method, of class ReaderHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaElement() throws Exception
  {
    TestReader testReader = TestReader.of(String.class);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.REQUIRED);

    ReaderHandler instance = new ReaderHandler("", flags, null, testReader);

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

  /**
   * Test of hasTextContent method, of class ReaderHandler.
   */
  @Test
  public void testHasTextContent()
  {
    AnyReader anyReader = AnyReader.of(String.class);

    ReaderHandler instance = new ReaderHandler(null, null, null, anyReader);
    assertFalse(instance.hasOption(Option.CAPTURE_TEXT));

    TestReader testReader = TestReader.of(String.class);
    instance = new ReaderHandler(null, null, null, testReader);
    assertTrue(instance.hasOption(Option.CAPTURE_TEXT));

    TestReaderMixed testReaderMixed = TestReaderMixed.of(String.class);
    instance = new ReaderHandler(null, null, null, testReaderMixed);
    assertTrue(instance.hasOption(Option.CAPTURE_TEXT));
  }

}
