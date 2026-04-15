/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ElementHandlerImpl;
import gov.llnl.utility.TestSupport.ElementGroupHarness;
import gov.llnl.utility.TestSupport.ElementHandlerHarness;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ElementGroup;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.Reader.Option;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ElementHandlerImpl.
 */
public class ElementHandlerImplNGTest
{

  public ElementHandlerImplNGTest()
  {
  }

  /**
   * Test of ElementHandlerImpl constructor, of class ElementHandlerImpl.
   */
  @Test
  public void testConstructor()
  {
    String key = "key";
    EnumSet<Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.DEFERRABLE);
    Class<Integer> resultCls = Integer.class;
    BiConsumer<String, String> method = (a, b) -> System.out.print("");

    ElementHandlerImpl instance = new ElementHandlerImpl(key, flags, resultCls, method);
    assertSame(instance.getKey(), key);
    assertSame(instance.method, method);
    assertTrue(flags.equals(instance.options));
    assertSame(instance.getObjectClass(), resultCls);
  }

  /**
   * Test of setOptions method, of class ElementHandlerImpl.
   */
  @Test
  public void testAddOption()
  {
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, null, null);
    // Immediate return
    instance.addOption(null);

    // Set options
    assertNull(instance.options);
    Option flags = Reader.Option.ANY_ALL;
    instance.addOption(flags);
    assertEquals(instance.options, EnumSet.of(flags));

    // Add options
    Option flags2 = Reader.Option.DEFERRABLE;
    instance.addOption(flags2);
    EnumSet<Option> results = EnumSet.of(flags, flags2);
    assertEquals(instance.options, results);

    // Remove Option.OPTIONAL
    instance.options.add(Reader.Option.OPTIONAL);
    assertTrue(instance.options.contains(Reader.Option.OPTIONAL));
    instance.addOption(Reader.Option.REQUIRED);
    assertTrue(instance.options.contains(Reader.Option.REQUIRED));
    assertFalse(instance.options.contains(Reader.Option.OPTIONAL));

    // Revmoe Option.REQUIRED
    assertTrue(instance.options.contains(Reader.Option.REQUIRED));
    instance.addOption(Reader.Option.OPTIONAL);
    assertTrue(instance.options.contains(Reader.Option.OPTIONAL));
    assertFalse(instance.options.contains(Reader.Option.REQUIRED));
  }

  /**
   * Test of onStart method, of class ElementHandlerImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnStart() throws Exception
  {
    assertNull(new ElementHandlerImpl(null, null, null, null).onStart(null, null));
  }

  /**
   * Test of onEnd method, of class ElementHandlerImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnEnd() throws Exception
  {
    assertNull(new ElementHandlerImpl(null, null, null, null).onEnd(null));
  }

  /**
   * Test of onCall method, of class ElementHandlerImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnCall() throws Exception
  {
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, null, null);
    // Immediate return
    instance.onCall(null, null, null);

    BiConsumer<String, String> doNothing = (a, b) -> System.out.print("");
    instance = new ElementHandlerImpl(null, null, null, doNothing);
    instance.onCall(null, "Hello", "World");

    // Test exceptions
    // RuntimeException with ReaderException cause
    instance = new ElementHandlerImpl(null, null, null, ElementHandlerImplNGTest::throwEx1);
    try
    {
      instance.onCall(null, new Object(), new Object());
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "ReaderException");
    }

    // Wrapped ReaderException 
    instance = new ElementHandlerImpl(null, null, null, ElementHandlerImplNGTest::throwEx2);
    try
    {
      instance.onCall(null, new Object(), new Object());
    }
    catch (ReaderException re)
    {
      assertEquals(re.getCause().getMessage(), "Wrapped ReaderException");
    }

    // parent is null
    instance = new ElementHandlerImpl(null, null, null, ElementHandlerImplNGTest::throwEx3);
    try
    {
      instance.onCall(null, null, null);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "Parent object was not constructed by start.");
      assertEquals(re.getCause().getMessage(), "Test3");
    }

    // Runtime with no caused
    instance = new ElementHandlerImpl(null, null, null, ElementHandlerImplNGTest::throwEx4);
    try
    {
      instance.onCall(null, new Object(), new Object());
    }
    catch (ReaderException re)
    {
      assertEquals(re.getCause().getMessage(), "Test4");
    }
  }

  static void throwEx1(Object obj1, Object obj2)
  {
    throw new RuntimeException("Test1", new ReaderException("ReaderException"));
  }

  static void throwEx2(Object obj1, Object obj2)
  {
    throw new RuntimeException("Test2", new RuntimeException("Wrapped ReaderException"));
  }

  static void throwEx3(Object obj1, Object obj2)
  {
    throw new RuntimeException("Test3");
  }

  static void throwEx4(Object obj1, Object obj2)
  {
    throw new RuntimeException("Test4");
  }

  /**
   * Test of onTextContent method, of class ElementHandlerImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOnTextContent() throws Exception
  {
    assertNull(new ElementHandlerImpl(null, null, null, null).onTextContent(null, null));
  }

  /**
   * Test of getHandlers method, of class ElementHandlerImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    assertNull(new ElementHandlerImpl(null, null, null, null).getHandlers(null));
  }

  /**
   * Test of getKey method, of class ElementHandlerImpl.
   */
  @Test
  public void testGetKey()
  {
    assertEquals(new ElementHandlerImpl("key", null, null, null).getKey(), "key");
  }

  /**
   * Test of getName method, of class ElementHandlerImpl.
   */
  @Test
  public void testGetName()
  {
    assertEquals(new ElementHandlerImpl("key", null, null, null).getName(), "key");
    assertEquals(new ElementHandlerImpl("#key", null, null, null).getName(), "");
    assertEquals(new ElementHandlerImpl("key#lock", null, null, null).getName(), "key");
  }

  /**
   * Test of createSchemaElement method, of class ElementHandlerImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaElement() throws Exception
  {
    // Empty body
    new ElementHandlerImpl(null, null, null, null).createSchemaElement(null, null);
  }

  /**
   * Test of getReaderFor method, of class ElementHandlerImpl.
   */
  @Test
  public void testGetReader()
  {
    assertNull(new ElementHandlerImpl(null, null, null, null).getReader());
  }

  /**
   * Test of getOptions method, of class ElementHandlerImpl.
   */
  @Test
  public void testGetOptions()
  {
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, null, null);
    EnumSet<Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.ANY_SKIP, Reader.Option.DEFERRABLE,
            Reader.Option.REQUIRED);
    for (Option flag : flags)
    {
      instance.addOption(flag);
    }
    assertEquals(instance.options, flags);
  }

  /**
   * Test of getObjectClass method, of class ElementHandlerImpl.
   */
  @Test
  public void testGetTargetClass()
  {
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, String.class, null);
    assertEquals(instance.getObjectClass(), String.class);
  }

  /**
   * Test of getParentGroup method, of class ElementHandlerImpl.
   */
  @Test
  public void testGetParentGroup()
  {
    ElementGroupHarness teg = new ElementGroupHarness();
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, null, null);
    instance.setParentGroup(teg);
    assertTrue(instance.getParentGroup() instanceof ElementGroup);
    assertSame(instance.getParentGroup(), teg);
  }

  /**
   * Test of getNextHandler method, of class ElementHandlerImpl.
   */
  @Test
  public void testGetNextHandler()
  {
    ElementHandlerHarness teh = new ElementHandlerHarness();
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, null, null);
    instance.setNextHandler(teh);
    assertTrue(instance.getNextHandler() instanceof Reader.ElementHandler);
    assertSame(instance.getNextHandler(), teh);
  }

  /**
   * Test of setParentGroup method, of class ElementHandlerImpl.
   */
  @Test
  public void testSetParentGroup()
  {
    ElementGroupHarness teg = new ElementGroupHarness();
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, null, null);
    instance.setParentGroup(teg);
    assertTrue(instance.getParentGroup() instanceof ElementGroup);
    assertSame(instance.getParentGroup(), teg);
  }

  /**
   * Test of setNextHandler method, of class ElementHandlerImpl.
   */
  @Test
  public void testSetNextHandler()
  {
    ElementHandlerHarness teh = new ElementHandlerHarness();
    ElementHandlerImpl instance = new ElementHandlerImpl(null, null, null, null);
    instance.setNextHandler(teh);
    assertTrue(instance.getNextHandler() instanceof Reader.ElementHandler);
    assertSame(instance.getNextHandler(), teh);
  }

}
