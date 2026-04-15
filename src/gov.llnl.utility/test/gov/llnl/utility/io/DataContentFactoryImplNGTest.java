/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.io;

import org.testng.annotations.Test;

/**
 * Test code for DataContentFactory.
 */
public class DataContentFactoryImplNGTest
{

  public DataContentFactoryImplNGTest()
  {
  }

  /**
   * Test of addContentHandler method, of class DataContentFactoryImpl.
   */
  @Test
  public void testAddContentHandler()
  {
    DataContentFactoryImpl.ContentHandler handler = null;
    DataContentFactoryImpl instance = new DataContentFactoryImpl();
    instance.addContentHandler(handler);
  }

  /**
   * Test of getDataFileReader method, of class DataContentFactoryImpl.
   */
  @Test
  public void testGetDataFileReader()
  {
    String uri = "";
//    Class<T> cls = null;
//    DataContentFactoryImpl instance = new DataContentFactoryImpl();
//    DataFileReader expResult = null;
//    DataFileReader result = instance.getDataFileReader(uri, cls);
//    assertEquals(result, expResult);
  }

  /**
   * Test of getDataStreamReader method, of class DataContentFactoryImpl.
   */
  @Test
  public void testGetDataStreamReader()
  {
    String uri = "";
//    Class<T> cls = null;
//    DataContentFactoryImpl instance = new DataContentFactoryImpl();
//    DataStreamReader expResult = null;
//    DataStreamReader result = instance.getDataStreamReader(uri, cls);
//    assertEquals(result, expResult);
  }

  /**
   * Test of getDataFileWriter method, of class DataContentFactoryImpl.
   */
  @Test
  public void testGetDataFileWriter()
  {
//    String uri = "";
//    Class<T> cls = null;
//    DataContentFactoryImpl instance = new DataContentFactoryImpl();
//    DataFileWriter expResult = null;
//    DataFileWriter result = instance.getDataFileWriter(uri, cls);
//    assertEquals(result, expResult);
  }

  /**
   * Test of getDataStreamWriter method, of class DataContentFactoryImpl.
   */
  @Test
  public void testGetDataStreamWriter()
  {
//    String uri = "";
//    Class<T> cls = null;
//    DataContentFactoryImpl instance = new DataContentFactoryImpl();
//    DataStreamWriter expResult = null;
//    DataStreamWriter result = instance.getDataStreamWriter(uri, cls);
//    assertEquals(result, expResult);
  }

}
