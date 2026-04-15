/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ElementGroupImpl;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.Reader;
import java.util.EnumSet;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for ElementGroupImpl.
 */
public class ElementGroupImplNGTest
{

  public ElementGroupImplNGTest()
  {
  }

  /**
   * Test of ElementGroupImpl constructor, of class ElementGroupImpl.
   */
  @Test
  public void testConstructor()
  {
    TestElementGroupImpl parent = new TestElementGroupImpl(null, null);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL);

    ElementGroupImpl instance = new TestElementGroupImpl(parent, flags);
    assertSame(instance.getParent(), parent);
    assertSame(instance.flags, flags);
    assertTrue(flags.equals(instance.flags));
  }

  /**
   * Test of getElementOptions method, of class ElementGroupImpl.
   */
  @Test
  public void testGetElementOptions()
  {
    ElementGroupImpl instance = new TestElementGroupImpl(null, null);
    assertNull(instance.getElementOptions());
  }

  /**
   * Test of getParent method, of class ElementGroupImpl.
   */
  @Test
  public void testGetParent()
  {
    ElementGroupImpl instance = new TestElementGroupImpl(null, null);
    assertNull(instance.getParent());
    TestElementGroupImpl parent = new TestElementGroupImpl(null, null);
    instance = new TestElementGroupImpl(parent, null);
    assertSame(instance.getParent(), parent);
  }

  /**
   * Test of newInstance method, of class ElementGroupImpl.
   */
  @Test
  public void testNewInstance()
  {

    assertSame(
            ElementGroupImpl.newInstance(null, Reader.Order.ALL, null).getClass(),
            ElementGroupImpl.AllGroup.class
    );
    assertSame(
            ElementGroupImpl.newInstance(null, Reader.Order.OPTIONS, null).getClass(),
            ElementGroupImpl.OptionsGroup.class
    );

    assertSame(
            ElementGroupImpl.newInstance(null, Reader.Order.SEQUENCE, null).getClass(),
            ElementGroupImpl.SequenceGroup.class
    );

    assertSame(
            ElementGroupImpl.newInstance(null, Reader.Order.CHOICE, null).getClass(),
            ElementGroupImpl.ChoiceGroup.class
    );

    assertSame(
            ElementGroupImpl.newInstance(null, Reader.Order.FREE, null).getClass(),
            ElementGroupImpl.FreeGroup.class
    );

    // Cannot test RuntimeException    
  }

  /**
   * Test of createSchemaGroup method, of class ElementGroupImpl.
   */
  @Test
  public void testCreateSchemaGroup()
  {
    Element testElement = newElement("TestElement");
    DomBuilder type = new DomBuilder(testElement);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL, Reader.Option.ANY_SKIP);
    ElementGroupImpl instance = new TestElementGroupImpl(null, flags);

    DomBuilder result = instance.createSchemaGroup(type);
    assertSame(result, type);
    assertSame(result.toElement(), testElement);

    Element element = result.toElement();
    for (Reader.Option opt : flags)
    {
      assertNotNull(element.getAttribute(opt.getKey()));
      assertEquals(element.getAttribute(opt.getKey()), opt.getValue());
    }
  }

  /**
   * Test of createSchemaGroupImpl method, of class ElementGroupImpl.
   */
  @Test
  public void testCreateSchemaGroupImpl()
  {
    Element testElement = newElement("TestElement");
    DomBuilder type = new DomBuilder(testElement);

    ElementGroupImpl instance = new TestElementGroupImpl(null, null);
    assertSame(instance.createSchemaGroupImpl(type), type);
  }

  /**
   * Test of AllGroup static class, of class ElementGroupImpl.
   */
  @Test
  public void testAllGroup()
  {
    TestElementGroupImpl parent = new TestElementGroupImpl(null, null);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL);

    ElementGroupImpl.AllGroup instance = new ElementGroupImpl.AllGroup(parent, flags);

    assertSame(instance.getParent(), parent);
    assertSame(instance.flags, flags);

    Element testElement = newElement("TestElement");
    DomBuilder type = new DomBuilder(testElement);

    DomBuilder type2 = instance.createSchemaGroupImpl(type);
    assertEquals(type2.toElement().getTagName(), "xs:all");
  }

  /**
   * Test of OptionsGroup static class, of class ElementGroupImpl.
   */
  @Test
  public void testOptionsGroup()
  {
    TestElementGroupImpl parent = new TestElementGroupImpl(null, null);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL);

    ElementGroupImpl.OptionsGroup instance = new ElementGroupImpl.OptionsGroup(parent, flags);

    assertSame(instance.getParent(), parent);
    assertSame(instance.flags, flags);

    assertTrue(EnumSet.of(Reader.Option.OPTIONAL).equals(instance.getElementOptions()));

    Element testElement = newElement("TestElement");
    DomBuilder type = new DomBuilder(testElement);

    DomBuilder type2 = instance.createSchemaGroupImpl(type);
    assertEquals(type2.toElement().getTagName(), "xs:all");
  }

  /**
   * Test of SequenceGroup static class, of class ElementGroupImpl.
   */
  @Test
  public void testSequenceGroup()
  {
    TestElementGroupImpl parent = new TestElementGroupImpl(null, null);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL);

    ElementGroupImpl.SequenceGroup instance = new ElementGroupImpl.SequenceGroup(parent, flags);

    assertSame(instance.getParent(), parent);
    assertSame(instance.flags, flags);

    Element testElement = newElement("TestElement");
    DomBuilder type = new DomBuilder(testElement);

    DomBuilder type2 = instance.createSchemaGroupImpl(type);
    assertEquals(type2.toElement().getTagName(), "xs:sequence");
  }

  /**
   * Test of ChoiceGroup static class, of class ElementGroupImpl.
   */
  @Test
  public void testChoiceGroup()
  {
    TestElementGroupImpl parent = new TestElementGroupImpl(null, null);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL);

    ElementGroupImpl.ChoiceGroup instance = new ElementGroupImpl.ChoiceGroup(parent, flags);

    assertSame(instance.getParent(), parent);
    assertSame(instance.flags, flags);

    Element testElement = newElement("TestElement");
    DomBuilder type = new DomBuilder(testElement);

    DomBuilder type2 = instance.createSchemaGroupImpl(type);
    assertEquals(type2.toElement().getTagName(), "xs:choice");
  }

  /**
   * Test of FreeGroup static class, of class ElementGroupImpl.
   */
  @Test
  public void testFreeGroup()
  {
    TestElementGroupImpl parent = new TestElementGroupImpl(null, null);
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.ANY_ALL);

    ElementGroupImpl.FreeGroup instance = new ElementGroupImpl.FreeGroup(parent, flags);

    assertSame(instance.getParent(), parent);
    assertSame(instance.flags, flags);

    Element testElement = newElement("TestElement");
    DomBuilder type = new DomBuilder(testElement);

    DomBuilder type2 = instance.createSchemaGroupImpl(type);
    Element childElement = type2.toElement();
    assertEquals(childElement.getTagName(), "xs:choice");

    assertNotNull(childElement.getAttribute("maxOccurs"));
    assertEquals(childElement.getAttribute("maxOccurs"), "unbounded");
  }

  static public class TestElementGroupImpl extends ElementGroupImpl
  {
    public TestElementGroupImpl(ElementGroupImpl parent, EnumSet<Reader.Option> flags)
    {
      super(parent, flags);
    }

    @Override
    public DomBuilder createSchemaGroupImpl(DomBuilder type)
    {
      return type;
    }
  }

}
