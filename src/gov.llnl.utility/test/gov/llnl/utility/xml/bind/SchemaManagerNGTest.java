/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.PackageResource;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.io.WriterException;
import java.net.URI;
import java.net.URL;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.xml.sax.EntityResolver;

/**
 * Test code for SchemaManager.
 */
public class SchemaManagerNGTest
{

  public SchemaManagerNGTest()
  {
  }

  /**
   * Test of getInstance method, of class SchemaManager.
   */
  @Test
  public void testGetInstance()
  {
    SchemaManager instance = new SchemaManagerImpl();
    SchemaManager result = SchemaManager.getInstance();
    assertNotNull(result);
    assertNotSame(result, instance);
    assertSame(result, SchemaManager.SELF);
  }

  /**
   * Test of alias method, of class SchemaManager.
   */
  @Test
  public void testAlias()
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    instance.alias(null, null);
  }

  /**
   * Test of getObjectClass method, of class SchemaManager.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetObjectClass() throws Exception
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    assertNull(instance.getObjectClass("", ""));
  }

  /**
   * Test of findObjectReader method, of class SchemaManager.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testFindObjectReader() throws Exception
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    assertNull(instance.findObjectReader(String.class));
  }

  /**
   * Test of findObjectWriter method, of class SchemaManager.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testFindObjectWriter() throws Exception
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    assertNull(instance.findObjectWriter(null, Double.class));
  }

  /**
   * Test of registerReaderFactory method, of class SchemaManager.
   */
  @Test
  public void testRegisterReaderFactory()
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    instance.registerReaderFactory(null);
  }

  /**
   * Test of registerReaderFor method, of class SchemaManager.
   */
  @Test
  public void testRegisterReaderFor()
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    instance.registerReaderFor(null, null);
  }

  /**
   * Test of registerWriterFactory method, of class SchemaManager.
   */
  @Test
  public void testRegisterWriterFactory()
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    instance.registerWriterFactory(null);
  }

  /**
   * Test of registerWriterFor method, of class SchemaManager.
   */
  @Test
  public void testRegisterWriterFor()
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    instance.registerWriterFor(null, null);
  }

  /**
   * Test of getEntityResolver method, of class SchemaManager.
   */
  @Test
  public void testGetEntityResolver()
  {
    SchemaManager instance = new TestSchemaManagerImpl();
    assertNull(instance.getEntityResolver());
  }

  /**
   * Test of ReaderFactory interface, of class SchemaManager.
   */
  @Test
  public void testReaderFactory()
  {
    TestSchemaManagerImpl sm = new TestSchemaManagerImpl();
    SchemaManager.ReaderFactory instance = sm.new TestReaderFactory();
    assertNull(instance.getReader(null));
  }

  /**
   * Test of WriterFactory interface, of class SchemaManager.
   */
  @Test
  public void testWriterFactory()
  {
    TestSchemaManagerImpl sm = new TestSchemaManagerImpl();
    SchemaManager.WriterFactory instance = sm.new TestWriterFactory();
    assertNull(instance.getWriter(null));
  }

  public class TestSchemaManagerImpl implements SchemaManager
  {
    @Override
    public void alias(URI systemId, URL location)
    {
    }

    @Override
    public Class<?> getObjectClass(String namespaceURI, String name) throws ClassNotFoundException
    {
      return null;
    }

    @Override
    public <T> ObjectReader<T> findObjectReader(Class<T> cls)
    {
      return null;
    }

    @Override
    public <T> ObjectWriter<T> findObjectWriter(WriterContext context, Class<T> cls) throws WriterException
    {
      return null;
    }

    @Override
    public void registerReaderFactory(ReaderFactory factory)
    {
    }

    @Override
    public void registerReaderFor(Class cls, Class<? extends ObjectReader> readerCls)
    {
    }

    @Override
    public void registerWriterFactory(WriterFactory factory)
    {
    }

    @Override
    public void registerWriterFor(Class cls, Class<? extends ObjectWriter> writerCls)
    {
    }

    @Override
    public EntityResolver getEntityResolver()
    {
      return null;
    }

    @Override
    public void registerPackage(PackageResource pkg)
    {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PackageResource findPackage(String name)
    {
      throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public class TestReaderFactory implements SchemaManager.ReaderFactory
    {
      @Override
      public ObjectReader getReader(Class cls)
      {
        return null;
      }
    }

    public class TestWriterFactory implements SchemaManager.WriterFactory
    {
      @Override
      public ObjectWriter getWriter(Class cls)
      {
        return null;
      }
    }
  }

}
