/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.TestSupport.TestPackage;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.UtilityPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.Reader.ProcessContents;
import java.util.EnumSet;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * Test code for Reader.
 */
public class ReaderNGTest
{

  public ReaderNGTest()
  {
  }

  /**
   * Test of start method, of class Reader.
   */
  @Test
  public void testStart() throws Exception
  {
    Reader instance = new ReaderImpl();
    assertNull(instance.start(null, null));
  }

  /**
   * Test of contents method, of class Reader.
   */
  @Test
  public void testContents() throws Exception
  {
    Reader instance = new ReaderImpl();
    assertNull(instance.contents(null, ""));
  }

  /**
   * Test of end method, of class Reader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testEnd() throws Exception
  {
    Reader instance = new ReaderImpl();
    assertNull(instance.end(null));
  }

  /**
   * Test of getHandlers method, of class Reader.
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    Reader instance = new ReaderImpl();
    assertNull(instance.getHandlers(null));
  }

  /**
   * Test of getObjectClass method, of class Reader.
   */
  @Test
  public void testGetObjectClass()
  {
    Reader instance = new ReaderImpl();
    assertSame(instance.getObjectClass(), Reader.class);

    // Test RuntimeException
    ReaderVoidImpl rvi = new ReaderVoidImpl();
    try
    {
      rvi.getObjectClass();
    }
    catch (RuntimeException re)
    {
      assertEquals(re.getMessage(), "object class not defined " + rvi.getClass().getCanonicalName());
    }
  }

  /**
   * Test of getDeclaration method, of class Reader.
   */
  @Test
  public void testGetDeclaration()
  {
    Reader instance = new ReaderImpl();
    Reader.Declaration result = instance.getDeclaration();
    assertNotNull(result);

    // Test NullPointerException
    ReaderNoDec rnd = new ReaderNoDec();
    String exceptionMsg = "";
    try
    {
      rnd.getDeclaration();
    }
    catch (NullPointerException ne)
    {
      exceptionMsg = ne.getMessage();
    }
    assertEquals(exceptionMsg, "Declaration not found for " + rnd.getClass());

    // Covered all Declaration 
    ReaderVoidImpl rv = new ReaderVoidImpl();
    result = rv.getDeclaration();
    assertEquals(result.pkg().getClass(), Class.class);
    assertEquals(result.name(), "ReaderVoidImpl");
    assertEquals(result.order(), Reader.Order.FREE);
    assertFalse(result.referenceable());
    assertEquals(result.contents(), Reader.Contents.ELEMENTS);
    assertFalse(result.copyable());
    assertEquals(result.typeName(), Reader.Declaration.NULL);
    assertTrue(result.document());
    assertEquals(Reader.Declaration.NULL, "");
    assertFalse(result.autoAttributes());
    assertEquals(result.cls(), void.class);
    assertEquals(result.impl(), void.class);
  }

  /**
   * Test of getXmlName method, of class Reader.
   */
  @Test
  public void testGetXmlName()
  {
    Reader instance = new ReaderImpl();
    assertEquals(instance.getXmlName(), "ReaderImpl");
  }

  /**
   * Test of getPackage method, of class Reader.
   */
  @Test
  public void testGetPackage()
  {
    Reader instance = new ReaderImpl();
    assertEquals(instance.getPackage().getClass(), UtilityPackage.class);
    assertEquals(instance.getPackage(), UtilityPackage.SELF);
  }

  /**
   * Test of getHandlerKey method, of class Reader.
   */
  @Test
  public void testGetHandlerKey()
  {
    Reader instance = new ReaderImpl();
    assertEquals(instance.getHandlerKey(), "");
  }

  /**
   * Test of getSchemaType method, of class Reader.
   */
  @Test
  public void testGetSchemaType()
  {
    Reader instance = new ReaderImpl();
    assertEquals(instance.getSchemaType(), "");
  }

  /**
   * Test of createSchemaType method, of class Reader.
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    SchemaBuilder builder = new SchemaBuilder();
    ReaderImpl instance = new ReaderImpl();
    instance.createSchemaType(builder);
    // FIXME needs addition testing
  }

  /**
   * Test of createSchemaElement method, of class Reader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaElement() throws Exception
  {
    SchemaBuilder builder = new SchemaBuilder();
    String name = "name";
    DomBuilder group = new DomBuilder(newElement("First"));
    boolean topLevel = true;
    ReaderImpl instance = new ReaderImpl();
    builder.addNamespace(instance.getPackage());
    DomBuilder result = instance.createSchemaElement(builder, name, group, topLevel);
    assertNotNull(result);
    Element te = result.toElement();
    assertTrue(te.getAttributes().getLength() > 0);
  }

  /**
   * Test of getAttributesDecl method, of class Reader.
   */
  @Test
  public void testGetAttributesDecl()
  {
    Reader instance = new ReaderAnyAttrib();

    // First if branch that uses AttributesDecl
    Reader.Attribute[] result = instance.getAttributesDecl();
    assertNotNull(result);
    assertTrue(result.length > 0);

    // Second if branch that uses Attribute
    ReaderVoidImpl rvi = new ReaderVoidImpl();
    result = rvi.getAttributesDecl();
    assertNotNull(result);
    assertTrue(result.length > 0);
    // Test Attribute
    Reader.Attribute attr = result[0];
    assertEquals(attr.name(), Reader.Attribute.NULL);
    assertSame(attr.type(), String.class);
    assertFalse(attr.required());
    assertEquals(Reader.Attribute.NULL, "##null");

    ReaderNoDec rnd = new ReaderNoDec();
    assertNull(rnd.getAttributesDecl());
  }

  /**
   * Test of getAnyAttributeDecl method, of class Reader.
   */
  @Test
  public void testGetAnyAttributeDecl()
  {
    Reader instance = new ReaderAnyAttrib();
    Reader.AnyAttribute result = instance.getAnyAttributeDecl();
    assertNotNull(result);
    assertEquals(result.id(), Reader.AnyAttribute.NULL);
    assertEquals(result.namespace(), "##any");
    assertEquals(result.processContents(), ProcessContents.Strict);
    assertEquals(Reader.AnyAttribute.NULL, "##null");
  }

  /**
   * Test of getTextContents method, of class Reader.
   */
  @Test
  public void testGetTextContents()
  {
    Reader instance = new ReaderAnyAttrib();
    Reader.TextContents result = instance.getTextContents();
    assertNotNull(result);
    assertEquals(result.base(), "xs:string");
  }

  /**
   * Test others in Reader
   *
   * @throws java.lang.NoSuchFieldException
   * @throws java.lang.NoSuchMethodException
   */
  @Test
  public void testOthers() throws NoSuchFieldException, NoSuchMethodException
  {
    TestDummy td = new TestDummy();
    Reader.ElementDeclaration ed = td.getClass().getDeclaredField("str").getAnnotation(Reader.ElementDeclaration.class);
    assertSame(ed.pkg(), TestPackage.class);
    assertEquals(ed.name(), "str");
    assertSame(ed.type(), String.class);

    Reader.Element e = td.getClass().getDeclaredMethod("method")
            .getAnnotation(Reader.Element.class);
    assertNotNull(e);
    assertEquals(e.name(), "method");
    assertSame(e.type(), Reader.Element.NULL.class);
    assertFalse(e.any());
    assertFalse(e.required());
    assertFalse(e.unbounded());
    assertFalse(e.deferrable());
    Reader.Element.NULL nullObj = new Reader.Element.NULL();

    // For coverage 
    Reader.Order order = Reader.Order.ALL;
    order = Reader.Order.OPTIONS;
    order = Reader.Order.SEQUENCE;
    order = Reader.Order.CHOICE;
    order = Reader.Order.FREE;
    Reader.Contents contents = Reader.Contents.NONE;
    contents = Reader.Contents.TEXT;
    contents = Reader.Contents.ELEMENTS;
    contents = Reader.Contents.MIXED;
    Reader.ProcessContents pc = Reader.ProcessContents.Strict;
    pc = Reader.ProcessContents.Lax;
    pc = Reader.ProcessContents.Skip;
  }

  /**
   * Test enum Option
   */
  @Test
  public void testOptions()
  {
    // test is really for coverage
    EnumSet<Reader.Option> flags = EnumSet.of(Reader.Option.OPTIONAL,
            Reader.Option.REQUIRED,
            Reader.Option.UNBOUNDED,
            Reader.Option.ANY_OTHER,
            Reader.Option.ANY_ALL,
            Reader.Option.ANY_SKIP,
            Reader.Option.ANY_STRICT,
            Reader.Option.ANY_LAX,
            Reader.Option.NO_REFERENCE,
            Reader.Option.DEFERRABLE,
            //            Reader.Option.NO_CACHE,
            Reader.Option.NO_ID
    );

    for (Reader.Option opt : flags)
    {
      opt.getKey();
      opt.getValue();
    }
  }

  // <editor-fold defaultstate="collapsed" desc="Support Classes">
  @Reader.Declaration(pkg = UtilityPackage.class,
          name = "ReaderImpl", cls = Reader.class)
  public class ReaderImpl implements Reader
  {
    @Override
    public Object start(ReaderContext context, Attributes attributes) throws ReaderException
    {
      return null;
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      return null;
    }

    @Override
    public String getHandlerKey()
    {
      return "";
    }

    @Override
    public String getSchemaType()
    {
      return "";
    }

  }

  @Reader.Declaration(pkg = UtilityPackage.class,
          name = "ReaderVoidImpl")
  @Reader.Attribute()
  public class ReaderVoidImpl implements Reader
  {
    @Override
    public Object start(ReaderContext context, Attributes attributes) throws ReaderException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getHandlerKey()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getSchemaType()
    {
      throw new UnsupportedOperationException();
    }
  }

  public class ReaderNoDec implements Reader
  {
    @Override
    public Object start(ReaderContext context, Attributes attributes) throws ReaderException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getHandlerKey()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getSchemaType()
    {
      throw new UnsupportedOperationException();
    }

  }

  @Reader.AttributesDecl(@Reader.Attribute)
  @Reader.TextContents()
  @Reader.AnyAttribute()
  public class ReaderAnyAttrib implements Reader
  {
    @Override
    public Object start(ReaderContext context, Attributes attributes) throws ReaderException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getHandlerKey()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getSchemaType()
    {
      throw new UnsupportedOperationException();
    }

  }

  public class TestDummy
  {
    @Reader.ElementDeclaration(pkg = TestPackage.class, name = "str")
    String str = "str";

    @Reader.Element(name = "method")
    public void method()
    {
    }
  }

  // </editor-fold>
}
