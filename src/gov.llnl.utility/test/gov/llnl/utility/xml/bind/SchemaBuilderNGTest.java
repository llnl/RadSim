/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * Test code for SchemaBuilder.
 *
 * SchemaBuilder is used in code generation not production. Unfortunately, it
 * cannot be a Java processor because it does not currently fit into the one
 * source file at a time used by a standard processor.
 *
 * We will not be testing this at the unit level because it is exercised
 * regularly every time a file is compiled.
 *
 */
public class SchemaBuilderNGTest
{

  public SchemaBuilderNGTest()
  {
  }

  /**
   * Test of getXmlPrefix method, of class SchemaBuilder.
   */
  @Test
  public void testGetXmlPrefix()
  {
//    Reader reader = null;
//    String expResult = "";
//    String result = SchemaBuilder.getXmlPrefix(reader);
//    assertEquals(result, expResult);
  }

  /**
   * Test of getRoot method, of class SchemaBuilder.
   */
  @Test
  public void testGetRoot()
  {
//    SchemaBuilder instance = new SchemaBuilder();
//    DomBuilder expResult = null;
//    DomBuilder result = instance.getRoot();
//    assertEquals(result, expResult);
  }

  /**
   * Test of addObjectReader method, of class SchemaBuilder.
   */
  @Test
  public void testAddObjectReader() throws Exception
  {
//    Reader reader = null;
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.addObjectReader(reader);
  }

  /**
   * Test of addObjectReaderForClass method, of class SchemaBuilder.
   */
  @Test
  public void testAddObjectReaderForClass() throws Exception
  {
//    Class cls = null;
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.addObjectReaderForClass(cls);
  }

  /**
   * Test of alias method, of class SchemaBuilder.
   */
  @Test
  public void testAlias() throws Exception
  {
//    Class<T> cls = null;
//    String name = "";
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.alias(cls, name);
  }

  /**
   * Test of createReaderSchemaElement method, of class SchemaBuilder.
   */
  @Test
  public void testCreateReaderSchemaElement() throws Exception
  {
//    Reader reader = null;
//    String name = "";
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.createReaderSchemaElement(reader, name);
  }

  /**
   * Test of createReaderSchemaType method, of class SchemaBuilder.
   */
  @Test
  public void testCreateReaderSchemaType() throws Exception
  {
//    Reader reader = null;
//    boolean recursive = false;
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.createReaderSchemaType(reader, recursive);
  }

  /**
   * Test of getDocument method, of class SchemaBuilder.
   */
  @Test
  public void testGetDocument()
  {
//    SchemaBuilder instance = new SchemaBuilder();
//    Document expResult = null;
//    Document result = instance.getDocument();
//    assertEquals(result, expResult);
  }

  /**
   * Test of include method, of class SchemaBuilder.
   */
  @Test
  public void testInclude()
  {
//    String file = "";
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.include(file);
  }

  /**
   * Test of imports method, of class SchemaBuilder.
   */
  @Test
  public void testImports()
  {
//    PackageResource resource = null;
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.imports(resource);
  }

  /**
   * Test of addNamespace method, of class SchemaBuilder.
   */
  @Test
  public void testAddNamespace()
  {
//    PackageResource resource = null;
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.addNamespace(resource);
  }

  /**
   * Test of setTargetNamespace method, of class SchemaBuilder.
   */
  @Test
  public void testSetTargetNamespace()
  {
//    PackageResource resource = null;
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.setTargetNamespace(resource);
  }

  /**
   * Test of scanForReaders method, of class SchemaBuilder.
   */
  @Test
  public void testScanForReaders_Path() throws Exception
  {
//    Path dir = null;
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.scanForReaders(dir);
  }

  /**
   * Test of scanForReaders method, of class SchemaBuilder.
   */
  @Test
  public void testScanForReaders_Path_String() throws Exception
  {
//    Path dir = null;
//    String extension = "";
//    SchemaBuilder instance = new SchemaBuilder();
//    instance.scanForReaders(dir, extension);
  }

  /**
   * Test of getReaderContext method, of class SchemaBuilder.
   */
  @Test
  public void testGetReaderContext()
  {
//    Class cls = null;
//    SchemaBuilder instance = new SchemaBuilder();
//    ReaderContext expResult = null;
//    ReaderContext result = instance.getReaderContext(cls);
//    assertEquals(result, expResult);
  }

  /**
   * Test of getError method, of class SchemaBuilder.
   */
  @Test
  public void testGetError()
  {
    SchemaBuilder instance = new SchemaBuilder();
    assertEquals(instance.getError(), instance.error);
    instance.error = 2;
    assertEquals(instance.getError(), instance.error);
  }

//  /**
//   * Test of notUsed method, of class SchemaBuilder.
//   */
//  @Test(expectedExceptions=UnsupportedOperationException.class)
//  public void testNotUsed()
//  {
//    Object expResult = null;
//    Object result = SchemaBuilder.notUsed();
//    assertEquals(result, expResult);
//  }
}
