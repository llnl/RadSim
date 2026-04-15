/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for CoreDump.
 */
public class CoreDumpNGTest
{

  public CoreDumpNGTest()
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
   * Test of add method, of class CoreDump.
   */
  @Test
  public void testAdd()
  {
    CoreDump instance = new CoreDump("any", true);
    CoreDump result = instance.add("key", "value");
    assertEquals(result, instance);
    assertTrue(instance.core.containsKey("key"));
  }

  /**
   * Test of write method, of class CoreDump.
   */
  @Test
  public void testWrite() throws IOException, ClassNotFoundException
  {
    CoreDump instance = new CoreDump("any", true);
    instance.add("key", "value");
    boolean result = instance.write("test");
    assertEquals(result, true);
    assertTrue(Files.exists(instance.getResult()));
    Serializer ser = new Serializer();
    HashMap map = (HashMap) ser.load(instance.getResult());
    assertTrue(map.containsKey("key"));
    Files.deleteIfExists(instance.getResult());
  }

}
