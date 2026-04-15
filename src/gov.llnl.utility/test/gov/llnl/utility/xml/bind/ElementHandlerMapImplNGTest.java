/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.TestSupport.ElementGroupHarness;
import gov.llnl.utility.TestSupport.ElementHandlerHarness;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.xml.DomBuilder;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.EnumSet;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ElementHandlerMapImpl.
 */
public class ElementHandlerMapImplNGTest
{

  public ElementHandlerMapImplNGTest()
  {
  }

  /**
   * Test of newInstance method, of class ElementHandlerMapImpl.
   */
  @Test
  public void testNewInstance()
  {
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();
    // Setup Handlers
//    BiConsumer<Object, Object> onlyYesterday = (Taeko, Okajima) -> System.out.println("Ai ha Hana, Kimi ha Sono Tane");

    ElementHandlerHarness firstHandler = new ElementHandlerHarness();
    firstHandler.key = "##safflower";
    handlerList.add(firstHandler);

//    firstHandler.method = onlyYesterday;
    ReaderHandler anyHandler = new ReaderHandler("##Toshio",
            EnumSet.of(Reader.Option.ANY), null, AnyReader.of(Object.class));
    handlerList.add(anyHandler);

    // Setup HandlerList
    handlerList.firstHandler = firstHandler;

    // Test
    ElementHandlerMapImpl instance = ElementHandlerMapImpl.newInstance("#StudioGhibli",
            handlerList);
    assertEquals(instance.namespaceURI, "#StudioGhibli");
    assertSame(instance.first, firstHandler);
    assertEquals(instance.handlers.length, 2);
    assertSame(instance.handlers[0], anyHandler);
    assertSame(instance.handlers[1], firstHandler);
    assertTrue(instance.hasAny);

    // Test exceptions 
    // namespace issue
    try
    {
      ElementHandlerMapImpl.newInstance("StudioGhibli", handlerList);
    }
    catch (RuntimeException ex)
    {
      assertEquals(ex.getMessage(), "namespace issue");
    }

    // bad key
    try
    {
      firstHandler.key = "#safflower";
      ElementHandlerMapImpl.newInstance("#StudioGhibli", handlerList);
    }
    catch (RuntimeException ex)
    {
      assertEquals(ex.getMessage(), "bad key #safflower");
    }

    // null key 
    try
    {
      firstHandler.key = null;
      ElementHandlerMapImpl.newInstance("#StudioGhibli", handlerList);
    }
    catch (RuntimeException ex)
    {
      assertEquals(ex.getMessage(), "Null pointer getting key " + firstHandler);
    }
  }

  /**
   * Test of toList method, of class ElementHandlerMapImpl.
   */
  @Test
  public void testToList()
  {
    // Setup handlers
    ElementHandlerHarness firstHandler = new ElementHandlerHarness();
    firstHandler.key = "##Ashitaka";
    ElementHandlerHarness secondHandler = new ElementHandlerHarness();
    secondHandler.key = "##San";
    firstHandler.nextHandler = secondHandler;
    // Setup HandlerList
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();
    handlerList.firstHandler = firstHandler;

    // Test
    ElementHandlerMapImpl instance = ElementHandlerMapImpl.newInstance("#PrincessMononoke", handlerList);
    List<Reader.ElementHandler> list = instance.toList();
    assertTrue(list.contains(firstHandler));
    assertTrue(list.contains(secondHandler));
  }

  /**
   * Test of get method, of class ElementHandlerMapImpl.
   */
  @Test
  public void testGet()
  {
    // Setup handlers
    ElementHandlerHarness firstHandler = new ElementHandlerHarness();
    firstHandler.key = "##OneSummersDay";
    ElementHandlerHarness anyHandler = new ElementHandlerHarness();
    anyHandler.key = "##ARoadtoSomewhere";
    firstHandler.nextHandler = anyHandler;
    // Setup HandlerList
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();
    handlerList.firstHandler = firstHandler;

    // Test
    ElementHandlerMapImpl instance = ElementHandlerMapImpl.newInstance("#SpiritedAwayOST", handlerList);

    assertNull(instance.get(null, "#"));
    assertSame(instance.get("OneSummersDay", "#"), firstHandler);
    assertSame(instance.get("ARoadtoSomewhere", "#"), anyHandler);
  }

  /**
   * Test of isEmpty method, of class ElementHandlerMapImpl.
   */
  @Test
  public void testIsEmpty()
  {
    // Setup HandlerList
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();

    // Test
    ElementHandlerMapImpl instance = ElementHandlerMapImpl.newInstance("#SpiritedAwayOST", handlerList);
    assertTrue(instance.isEmpty());
  }

  /**
   * Test of createSchemaType method, of class ElementHandlerMapImpl.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    // Setup handlers
    ElementGroupHarness grandElementGroup = new ElementGroupHarness();

    ElementHandlerHarness firstHandler = new ElementHandlerHarness();
    firstHandler.key = "##TheReturn";
    ElementGroupHarness firstParentElementGroup = new ElementGroupHarness();
    firstParentElementGroup.parent = grandElementGroup;
    firstHandler.parentElementGroup = firstParentElementGroup;

    ElementHandlerHarness anyHandler = new ElementHandlerHarness();
    anyHandler.key = "##Reprise";
    anyHandler.parentElementGroup = new ElementGroupHarness();
    firstHandler.nextHandler = anyHandler;
    // Setup HandlerList
    ReaderBuilderImpl.HandlerList handlerList = new ReaderBuilderImpl.HandlerList();

    // Test
    ElementHandlerMapImpl instance = ElementHandlerMapImpl.newInstance("#SpiritedAwayOST", handlerList);

    // return immediately
    instance.createSchemaType(null, null);

    DomBuilder db = new DomBuilder(newElement("TestElement"));
    handlerList.firstHandler = firstHandler;
    instance = ElementHandlerMapImpl.newInstance("#SpiritedAwayOST", handlerList);
    instance.createSchemaType(null, db);

    // Test BuildSchema - cover else block in process()
    firstParentElementGroup.parent = null;
    instance = ElementHandlerMapImpl.newInstance("#SpiritedAwayOST", handlerList);
    instance.createSchemaType(null, db);
  }

}
