/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.TestSupport.TestPackage;
import gov.llnl.utility.TestSupport.TestPolyReader;
import gov.llnl.utility.TestSupport.TestReader;
import gov.llnl.utility.UtilityPackage;
import gov.llnl.utility.io.ReaderException;
import java.lang.reflect.Field;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ObjectListReader.
 */
public class ObjectListReaderNGTest
{

  public ObjectListReaderNGTest()
  {
  }

  /**
   * Test of ObjectListReader constructor, of class ObjectListReader.
   */
  @Test
  public void testConstructor() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException
  {
    TestReader testReader = TestReader.of(String.class);
    String name = "name";
    ObjectListReader instance = new ObjectListReader(testReader, name, UtilityPackage.getInstance());

    // Use reflection to get fields' information
    Field readerField = ObjectListReader.class.getDeclaredField("reader");
    Field nameField = ObjectListReader.class.getDeclaredField("name");
    Field schemaField = ObjectListReader.class.getDeclaredField("schema");
    readerField.setAccessible(true);
    nameField.setAccessible(true);
    schemaField.setAccessible(true);

    assertSame(readerField.get(instance), testReader);
    assertSame(nameField.get(instance), name);
    assertSame(schemaField.get(instance).getClass(), UtilityPackage.class);
  }

  /**
   * Test of getDeclaration method, of class ObjectListReader.
   */
  @Test
  public void testGetDeclaration()
  {
    TestReader testReader = TestReader.of(String.class);
    String name = "name";
    ObjectListReader instance = new ObjectListReader(testReader, name, UtilityPackage.getInstance());

    Reader.Declaration dec = instance.getDeclaration();
    assertSame(dec.pkg(), UtilityPackage.class);
    assertSame(dec.name(), name);
  }

  /**
   * Test of start method, of class ObjectListReader.
   */
  @Test
  public void testStart() throws Exception
  {
    ObjectListReader instance = new ObjectListReader(ObjectReader.create(String.class), null, null);
    List result = instance.start(null, null);
    assertEquals(result.size(), 0);
  }

  /**
   * Test of getHandlers method, of class ObjectListReader.
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    // PolymorphicReader branch
    TestPolyReader tpr = new TestPolyReader();
    String name = "name";
    ObjectListReader instance = new ObjectListReader(tpr, name, TestPackage.getInstance());
    Reader.ElementHandlerMap result = instance.getHandlers(null);
    // No exception means it passed

    // AnyReader branch
    AnyReader anyReader = AnyReader.of(String.class);
    instance = new ObjectListReader(anyReader, name, TestPackage.getInstance());
    result = instance.getHandlers(null);
    // No exception means it passed

    // Other reader branch
    TestReader tr = TestReader.of(String.class);
    instance = new ObjectListReader(tr, name, TestPackage.getInstance());
    result = instance.getHandlers(null);
    // No exception means it passed
  }

  /**
   * Test of getObjectClass method, of class ObjectListReader.
   */
  @Test
  public void testGetObjectClass() throws ReaderException
  {
    ObjectListReader instance = new ObjectListReader(ObjectReader.create(String.class), null, null);
    assertSame(instance.getObjectClass(), List.class);
  }

  /**
   * Test of getSchemaType method, of class ObjectListReader.
   */
  @Test
  public void testGetSchemaType()
  {
    TestReader testReader = TestReader.of(String.class);
    ObjectListReader instance = new ObjectListReader(testReader, null, null);
    assertEquals(instance.getSchemaType(), "List-" + testReader.getSchemaType());
  }

  @Test
  public void testGetHandlerKey()
  {
    TestReader testReader = TestReader.of(String.class);
    String name = "name";
    AnyReader anyReader = AnyReader.of(String.class);
    ObjectListReader instance = new ObjectListReader(anyReader, name, TestPackage.getInstance());
    String result = instance.getHandlerKey();
    assertEquals(result, "name#TestPackage");
  }

}
