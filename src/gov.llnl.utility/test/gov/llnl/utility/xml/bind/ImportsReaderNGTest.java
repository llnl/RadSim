/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ImportsReader;
import gov.llnl.utility.TestSupport.TestReader;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ImportsReader.
 */
public class ImportsReaderNGTest
{

  public ImportsReaderNGTest()
  {
  }

  /**
   * Test of ImportsReader constructor, of class ImportsReader.
   */
  @Test
  public void testConstructor()
  {
    TestReader testReader = TestReader.of(String.class);
    ImportsReader instance = new ImportsReader(testReader);
    assertSame(instance.objectReader, testReader);
    assertNotNull(instance.documentReader);
    assertSame(instance.documentReader.objectReader, testReader);
  }

  /**
   * Test of getDeclaration method, of class ImportsReader.
   */
  @Test
  public void testGetDeclaration()
  {
    TestReader testReader = TestReader.of(String.class);
    ImportsReader instance = new ImportsReader(testReader);

    Reader.Declaration dec = instance.getDeclaration();
    assertNotNull(dec);
    assertSame(dec.pkg(), testReader.getDeclaration().pkg());
    assertEquals(dec.name(), testReader.getDeclaration().name());
    assertEquals(dec.order(), Reader.Order.FREE);
  }

  /**
   * Test of start method, of class ImportsReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStart() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of getObjectClass method, of class ImportsReader.
   */
  @Test
  public void testGetObjectClass()
  {
    TestReader testReader = TestReader.of(String.class);
    ImportsReader instance = new ImportsReader(testReader);
    assertSame(instance.getObjectClass(), String.class);
    assertSame(instance.getObjectClass(), testReader.getObjectClass());
  }

  /**
   * Test of getHandlers method, of class ImportsReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    TestReader testReader = TestReader.of(String.class);
    ImportsReader instance = new ImportsReader(testReader);
    assertNotNull(instance.getHandlers(null));
  }

  /**
   * Test of createSchemaType method, of class ImportsReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    ImportsReader instance = new ImportsReader(TestReader.of(String.class));
    // Empty method body
    instance.createSchemaType(null);
  }

  /**
   * Test of createSchemaElement method, of class ImportsReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaElement() throws Exception
  {
    DomBuilder type = new DomBuilder(newElement("NewElement"));
    ImportsReader instance = new ImportsReader(TestReader.of(String.class));
    assertSame(instance.createSchemaElement(null, null, type, true), type);
  }

}
