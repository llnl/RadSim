/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ReferenceHandler;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.Reader;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for ReferenceHandler.
 */
public class ReferenceHandlerNGTest
{

  /**
   * Test of ReferenceHandler constructor, of class ReferenceHandler.
   */
  @Test
  public void testConstructor()
  {
    String key = "key";
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.DEFERRABLE);
    Class<Integer> resultCls = Integer.class;
    BiConsumer<String, String> method = (a, b) -> System.out.print("");

    ReferenceHandler instance = new ReferenceHandler(key, flags, resultCls, method);
    assertSame(instance.getKey(), key);
    assertSame(instance.method, method);
    assertTrue(flags.equals(instance.options));
    assertSame(instance.getObjectClass(), resultCls);
  }

  /**
   * Test of createSchemaElement method, of class ReferenceHandler.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaElement() throws Exception
  {
    String key = "key";
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.OPTIONAL, Reader.Option.ANY_STRICT);
    Element testElement = newElement("TestElement");
    DomBuilder group = new DomBuilder(testElement);

    ReferenceHandler instance = new ReferenceHandler(key, flags, null, null);
    instance.createSchemaElement(null, group);

    Element childElement = (Element) testElement.getFirstChild();

    assertEquals(childElement.getNodeName(), "xs:element");
    assertNotNull(childElement.getAttribute("name"));
    assertEquals(childElement.getAttribute("name"), key);
    assertNotNull(childElement.getAttribute("type"));
    assertEquals(childElement.getAttribute("type"), "util:reference-type");

    for (Reader.Option opt : flags)
    {
      if (opt.getKey() == null)
        continue;
      assertNotNull(childElement.getAttribute(opt.getKey()));
      assertSame(childElement.getAttribute(opt.getKey()), opt.getValue());
    }
  }

}
