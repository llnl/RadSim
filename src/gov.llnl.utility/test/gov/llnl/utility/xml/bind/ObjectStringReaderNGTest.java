/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.TestSupport;
import gov.llnl.utility.io.ReaderException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ObjectStringReader.
 */
public class ObjectStringReaderNGTest
{

  public ObjectStringReaderNGTest()
  {
  }

  /**
   * Test of start method, of class ObjectStringReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStart() throws Exception
  {
    ObjectStringReader instance = new ObjectStringReaderImpl();
    assertNull(instance.start(null, null));
  }

  /**
   * Test of contents method, of class ObjectStringReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testContents() throws Exception
  {
    ObjectStringReader instance = new ObjectStringReaderImpl();
    assertNull(instance.contents(null, null));
  }

  /**
   * Test of end method, of class ObjectStringReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testEnd() throws Exception
  {
    ObjectStringReader instance = new ObjectStringReaderImpl();
    assertNull(instance.end(null));
  }

  /**
   * Test of getHandlers method, of class ObjectStringReader.
   */
  @Test
  public void testGetHandlers()
  {
    ObjectStringReader instance = new ObjectStringReaderImpl();
    assertNull(instance.getHandlers(null));
  }

  /**
   * Test of createSchemaType method, of class ObjectStringReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    ObjectStringReader instance = new ObjectStringReaderImpl();
    instance.createSchemaType(new SchemaBuilder());
  }

  @Reader.Declaration(pkg = TestSupport.TestPackage.class, name = "ObjectStringReaderImpl")
  public class ObjectStringReaderImpl extends ObjectStringReader
  {
    @Override
    public Object contents(ReaderContext context, String textContents) throws ReaderException
    {
      return null;
    }

  }

}
