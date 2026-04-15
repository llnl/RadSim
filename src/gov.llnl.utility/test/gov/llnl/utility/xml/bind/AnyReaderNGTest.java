/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.TestSupport.TestReaderContext;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for AnyReader.
 */
public class AnyReaderNGTest
{

  public AnyReaderNGTest()
  {
  }

  /**
   * Test of AnyReader constructor, of class AnyReader.
   */
  @Test
  public void testConstructor()
  {
    AnyReader instance = new AnyReader();
    assertNull(instance.cls);
  }

  /**
   * Test of of method, of class AnyReader.
   */
  @Test
  public void testOf()
  {
    AnyReader result = AnyReader.of(String.class);
    assertEquals(result.cls, String.class);
  }

  /**
   * Test of end method, of class AnyReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testEnd() throws Exception
  {
    AnyReader instance = new AnyReader();
    ReaderContext context = TestReaderContext.create();
    assertNull(instance.end(context));
    String str = "Me";
    context.setState(str);
    assertSame(instance.end(context), str);
  }

  /**
   * Test of getHandlers method, of class AnyReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    AnyReader instance = new AnyReader(Double.class);
    ReaderContext context = TestReaderContext.create();
    assertNotNull(instance.getHandlers(context));
  }

  /**
   * Test of getObjectClass method, of class AnyReader.
   */
  @Test
  public void testGetObjectClass()
  {
    AnyReader instance = new AnyReader();
    assertNull(instance.getObjectClass());
    instance = new AnyReader(AnyReader.class);
    assertEquals(instance.getObjectClass(), AnyReader.class);
  }

}
