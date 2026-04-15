/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind.readers;

import gov.llnl.utility.xml.bind.readers.UUIDContents;
import java.util.UUID;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for UUIDContents.
 */
public class UUIDContentsNGTest
{

  public UUIDContentsNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

  /**
   * Test of getObjectClass method, of class UUIDContents.
   */
  @Test
  public void testGetObjectClass()
  {
    UUIDContents instance = new UUIDContents();
    String expResult = "class java.util.UUID";
    String result = instance.getObjectClass().toString();
    assertEquals(result.equals(expResult), true);
  }

  /**
   * Test of contents method, of class UUIDContents.
   */
  @Test
  public void testContents() throws Exception
  {
    // Tested end to end.
  }

}
