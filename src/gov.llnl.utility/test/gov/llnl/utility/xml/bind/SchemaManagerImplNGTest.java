/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.SchemaManagerImpl;
import gov.llnl.utility.xml.bind.ObjectWriter;
import gov.llnl.utility.xml.bind.SchemaManager;
import java.net.URI;
import java.net.URL;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Test code for SchemaManagerImpl.
 */
public class SchemaManagerImplNGTest
{

  public SchemaManagerImplNGTest()
  {
  }

  /**
   * Test of mangleURI method, of class SchemaManagerImpl.
   */
  @Test
  public void testMangleURI()
  {
    // Tested end to end.
  }

  /**
   * Test of getObjectClass method, of class SchemaManagerImpl.
   */
  @Test
  public void testGetObjectClass() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of resolveEntity method, of class SchemaManagerImpl.
   */
  @Test
  public void testResolveEntity() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of alias method, of class SchemaManagerImpl.
   */
  @Test
  public void testAlias()
  {
    // Tested end to end.
  }

  /**
   * Test of findObjectWriter method, of class SchemaManagerImpl.
   */
  @Test
  public void testFindObjectWriter() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of findObjectReader method, of class SchemaManagerImpl.
   */
  @Test
  public void testFindObjectReader() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of getEntityResolver method, of class SchemaManagerImpl.
   */
  @Test
  public void testGetEntityResolver()
  {
    SchemaManagerImpl instance = new SchemaManagerImpl();
    EntityResolver expResult = instance;
    EntityResolver result = instance.getEntityResolver();
    assertEquals(result, expResult);
  }

  /**
   * Test of registerReaderFactory method, of class SchemaManagerImpl.
   */
  @Test
  public void testRegisterReaderFactory()
  {
    // Tested end to end.
  }

  /**
   * Test of registerReaderFor method, of class SchemaManagerImpl.
   */
  @Test
  public void testRegisterReaderFor()
  {
    // Tested end to end.
  }

  /**
   * Test of registerWriterFactory method, of class SchemaManagerImpl.
   */
  @Test
  public void testRegisterWriterFactory()
  {
    // Tested end to end.
  }

  /**
   * Test of registerWriterFor method, of class SchemaManagerImpl.
   */
  @Test
  public void testRegisterWriterFor()
  {
    // Tested end to end.
  }

  /**
   * Test of scanSchema method, of class SchemaManagerImpl.
   */
  @Test
  public void testScanSchema()
  {
    // Tested end to end.
  }

  /**
   * Test of processSchemaLocation method, of class SchemaManagerImpl.
   */
  @Test
  public void testProcessSchemaLocation()
  {
    // Tested end to end.
  }

}
