/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.TestSupport;
import gov.llnl.utility.TestSupport.TestPackage;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.xml.bind.readers.PrimitiveReaderImpl;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.ObjectReader.Section;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for ObjectReader.
 */
public class ObjectReaderNGTest
{

  public ObjectReaderNGTest()
  {
  }

  /**
   * Test of create method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreate() throws Exception
  {
    ObjectReader<Long> result = ObjectReader.create(Long.class);
    PrimitiveReaderImpl pri = (PrimitiveReaderImpl) result;
    // Cast successful so test was successful but we'll call an assert
    assertSame(pri.getObjectClass(), Long.class);
  }

  /**
   * Test of start method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStart() throws Exception
  {
    ObjectReader instance = new ObjectReaderImpl();
    assertNull(instance.start(null, null));
  }

  /**
   * Test of getAttribute method, of class ObjectReader.
   */
  @Test
  public void testGetAttribute_4args()
  {
    org.xml.sax.helpers.AttributesImpl attr = new org.xml.sax.helpers.AttributesImpl();
    attr.addAttribute("uri", "localName", "one", "type", "1");

    assertEquals((int) ObjectReader.getAttribute(attr, "one", int.class, 0), 1);

    // Default value
    assertEquals((int) ObjectReader.getAttribute(attr, "two", int.class, 0), 0);
  }

  /**
   * Test of getAttribute method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetAttribute_3args() throws Exception
  {
    org.xml.sax.helpers.AttributesImpl attr = new org.xml.sax.helpers.AttributesImpl();
    attr.addAttribute("uri", "localName", "one", "type", "1");

    assertEquals((int) ObjectReader.getAttribute(attr, "one", int.class), 1);

    try
    {
      ObjectReader.getAttribute(attr, "two", int.class);
    }
    catch (ReaderException re)
    {
      assertEquals(re.getMessage(), "Required attribute two was not found.");
    }
  }

  /**
   * Test of getHandlers method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    ObjectReader instance = new ObjectReaderImpl();
    assertNull(instance.getHandlers(null));
  }

  /**
   * Test of contents method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testContents() throws Exception
  {
    ObjectReader instance = new ObjectReaderImpl();
    assertNull(instance.contents(null, null));
  }

  /**
   * Test of end method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testEnd() throws Exception
  {
    ObjectReader instance = new ObjectReaderImpl();
    assertNull(instance.end(null));
  }

  /**
   * Test of getHandlerKey method, of class ObjectReader.
   */
  @Test
  public void testGetHandlerKey()
  {
    // null check condiiton will not be true
    ObjectReader instance = new ObjectReaderImpl();
    assertEquals(instance.getHandlerKey(), "ObjectReaderImpl#TestPackage");
  }

  /**
   * Test of getSchemaType method, of class ObjectReader.
   */
  @Test
  public void testGetSchemaType()
  {
    ObjectReader instance = new ObjectReaderImpl();
    assertEquals(instance.getSchemaType(), "ObjectReaderImpl");

    // Automatic naming
    instance = new TheOtherObjReader();
    assertEquals(instance.getSchemaType(), "ObjectReaderNGTest-TheOtherObjReader-type");
  }

  /**
   * Test of newBuilder method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testNewBuilder_0args() throws Exception
  {
    ObjectReader instance = new ObjectReaderImpl();
    ReaderBuilderImpl result = (ReaderBuilderImpl) instance.newBuilder();
    assertNotNull(result);
  }

  /**
   * Test of newBuilder method, of class ObjectReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testNewBuilder_Class() throws Exception
  {
    ObjectReader instance = new ObjectReaderImpl();
    Reader.ReaderBuilder result = instance.newBuilder(String.class);
    assertNotNull(result);
  }

  /**
   * Test abstract class Section
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testSection() throws Exception
  {
    ObjectReaderImpl instance = new ObjectReaderImpl();

    ObjectReaderImpl.ObjectSection os = instance.new ObjectSection();
    Integer targetObj = 0;
    ReaderContext context = new ReaderContextImpl();
    context.pushTemporaryContext(null, targetObj);

    // Test ctor
    assertEquals(os.order, Reader.Order.FREE);
    assertEquals(os.name, "name");

    ReaderDeclarationImpl osDec = (ReaderDeclarationImpl) os.getDeclaration();
    assertEquals(osDec.pkg().getName(), "gov.llnl.utility.TestSupport$TestPackage");
    assertEquals(osDec.name(), "name");
    assertEquals(osDec.order(), Reader.Order.FREE);

    assertNull(os.start(context, null));
    assertNull(os.contents(context, null));
    assertNull(os.end(context));
    assertSame(os.getObjectClass(), String.class);

    ReaderBuilderImpl rbi = (ReaderBuilderImpl) os.newBuilder();
    assertNotNull(rbi);

    rbi = (ReaderBuilderImpl) os.newBuilder(String.class);
    assertNotNull(rbi);

    SchemaBuilder builder = new SchemaBuilder();
    os.createSchemaType(builder);

    builder = new SchemaBuilder();
    Element te = newElement("TestElement");
    DomBuilder type = new DomBuilder(te);
    builder.addNamespace(os.getPackage());
    DomBuilder domOut = os.createSchemaElement(builder, "name", type, false);
  }

  /**
   * Test abstract class StringSection
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStringSection() throws Exception
  {
    ObjectReaderImpl instance = new ObjectReaderImpl();

    ObjectReaderImpl.StrObjectSection os = instance.new StrObjectSection();
    Integer targetObj = 0;
    ReaderContext context = new ReaderContextImpl();
    context.pushTemporaryContext(null, targetObj);

    assertNull(os.start(context, null));
    assertNull(os.contents(context, null));
    assertNull(os.end(context));
    assertNull(os.getHandlers(context));
    assertSame(os.getObjectClass(), String.class);
  }

  /**
   * Test nested class Imports
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testImports() throws Exception
  {
    ObjectReaderImpl instance = new ObjectReaderImpl();

    ObjectReaderImpl.Imports imports = instance.new Imports();

    assertEquals(imports.order, Reader.Order.FREE);
    assertEquals(imports.name, "imports");
    assertNotNull(imports.getHandlers(null));
    imports.createSchemaType(new SchemaBuilder());
  }

  /**
   * Test nested class Defines
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testDefines() throws Exception
  {
    ObjectReaderImpl instance = new ObjectReaderImpl();

    ObjectReaderImpl.Defines defines = instance.new Defines();

    assertEquals(defines.order, Reader.Order.FREE);
    assertEquals(defines.name, "defines");
    assertNotNull(defines.getHandlers(null));
  }

  // <editor-fold defaultstate="collapsed" desc="Support Classes">
  @Reader.Declaration(pkg = TestSupport.TestPackage.class,
          name = "ObjectReaderImpl",
          typeName = "ObjectReaderImpl",
          cls = String.class)
  public class ObjectReaderImpl extends ObjectReader
  {

    public class ObjectSection extends Section
    {
      public ObjectSection()
      {
        super(Reader.Order.FREE, "name");
      }

      @Override
      public Reader.ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
      {
        return null;
      }
    }

    public class StrObjectSection extends StringSection
    {
      @Override
      public Object contents(ReaderContext context, String textContents) throws ReaderException
      {
        return null;
      }

    }
  }

  @Reader.Declaration(pkg = TestPackage.class, name = "TheOtherObjReader")
  public class TheOtherObjReader extends ObjectReader
  {
  }

  // </editor-fold>
}
