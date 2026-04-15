/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ReaderDeclarationImpl;
import gov.llnl.utility.PackageResource;
import gov.llnl.utility.UtilityPackage;
import gov.llnl.utility.xml.bind.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ReaderDeclarationImpl.
 */
public class ReaderDeclarationImplNGTest
{

  public ReaderDeclarationImplNGTest()
  {
  }

  /**
   * Test of pkg method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testPkg()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertNull(instance.pkg());

    TestDeclarationBase tdb = new TestDeclarationBase();
    tdb.packageResoruce = UtilityPackage.getInstance();
    instance = new TestReaderDeclarationImpl(tdb);
    assertEquals(instance.pkg(), tdb.packageResoruce.getClass());
  }

  /**
   * Test of name method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testName()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertNull(instance.name());

    TestDeclarationBase tdb = new TestDeclarationBase();
    tdb.name = "name";
    instance = new TestReaderDeclarationImpl(tdb);
    assertEquals(instance.name(), tdb.name);
  }

  /**
   * Test of order method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testOrder()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertEquals(instance.order(), Reader.Order.FREE);

    TestDeclarationBase tdb = new TestDeclarationBase();
    tdb.order = Reader.Order.ALL;
    instance = new TestReaderDeclarationImpl(tdb);
    assertEquals(instance.order(), tdb.order);
  }

  /**
   * Test of referenceable method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testReferenceable()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertFalse(instance.referenceable());

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertTrue(instance.referenceable());
  }

  /**
   * Test of contents method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testContents()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertEquals(instance.contents(), Reader.Contents.ELEMENTS);

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertEquals(instance.contents(), Reader.Contents.MIXED);
  }

  /**
   * Test of copyable method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testCopyable()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertFalse(instance.copyable());

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertTrue(instance.copyable());
  }

  /**
   * Test of typeName method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testTypeName()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertNull(instance.typeName());

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertEquals(instance.typeName(), "typename");
  }

  /**
   * Test of document method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testDocument()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertFalse(instance.document());

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertTrue(instance.document());
  }

  /**
   * Test of autoAttributes method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testAutoAttributes()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertFalse(instance.autoAttributes());

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertTrue(instance.autoAttributes());
  }

  /**
   * Test of impl method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testImpl()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertNull(instance.impl());

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertEquals(instance.impl(), TestDeclarationBase.class);
  }

  /**
   * Test of cls method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testCls()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertNull(instance.cls());

    TestDeclarationBase tdb = new TestDeclarationBase();
    instance = new TestReaderDeclarationImpl(tdb);
    assertEquals(instance.cls(), TestDeclarationBase.class);
  }

  /**
   * Test of annotationType method, of class ReaderDeclarationImpl.
   */
  @Test
  public void testAnnotationType()
  {
    ReaderDeclarationImpl instance = new TestReaderDeclarationImpl();
    assertEquals(instance.annotationType(), Reader.Declaration.class);
  }

  class TestReaderDeclarationImpl extends ReaderDeclarationImpl
  {
    public TestReaderDeclarationImpl()
    {
      super();
    }

    public TestReaderDeclarationImpl(Reader.Declaration base)
    {
      super(base);

    }
  }

  class TestDeclarationBase extends ReaderDeclarationImpl
  {
    public PackageResource packageResoruce;
    public String name;
    public Reader.Order order;

    @Override
    public Class<? extends PackageResource> pkg()
    {
      return packageResoruce.getClass();
    }

    @Override
    public String name()
    {
      return name;
    }

    @Override
    public Reader.Order order()
    {
      return order;
    }

    @Override
    public boolean referenceable()
    {
      return true;
    }

    @Override
    public Reader.Contents contents()
    {
      return Reader.Contents.MIXED;
    }

    @Override
    public boolean copyable()
    {
      return true;
    }

    @Override
    public String typeName()
    {
      return "typename";
    }

    @Override
    public boolean document()
    {
      return true;
    }

    @Override
    public boolean autoAttributes()
    {
      return true;
    }

    @Override
    public Class impl()
    {
      return this.getClass();
    }

    @Override
    public Class cls()
    {
      return impl();
    }
  }

}
