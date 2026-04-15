/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.SectionImpl;
import gov.llnl.utility.TestSupport.TestSectionImpl;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for SectionImpl.
 */
public class SectionImplNGTest
{

  public SectionImplNGTest()
  {
  }

  /**
   * Test of start method, of class SectionImpl.
   */
  @Test
  public void testStart() throws Exception
  {
    assertNull(new TestSectionImpl().start(null, null));
    assertNull(new TestSectionImpl().start(null, new org.xml.sax.helpers.AttributesImpl()));
  }

  /**
   * Test of contents method, of class SectionImpl.
   */
  @Test
  public void testContents() throws Exception
  {
    assertNull(new TestSectionImpl().contents(null, "textContents"));
  }

  /**
   * Test of end method, of class SectionImpl.
   */
  @Test
  public void testEnd() throws Exception
  {
    assertNull(new TestSectionImpl().end(null));
  }

  /**
   * Test of getHandlerKey method, of class SectionImpl.
   */
  @Test
  public void testGetHandlerKey()
  {
    SectionImpl instance = new TestSectionImpl();
    assertEquals(instance.getHandlerKey(), "TestSectionImpl#http://utility.llnl.gov");
  }

  /**
   * Test of getXmlPrefix method, of class SectionImpl.
   */
  @Test
  public void testGetXmlPrefix()
  {
    SectionImpl instance = new TestSectionImpl();
    assertEquals(instance.getXmlPrefix(), "TestSectionPrefix:");
  }

  /**
   * Test of getSchemaType method, of class SectionImpl.
   */
  @Test
  public void testGetSchemaType()
  {
    SectionImpl instance = new TestSectionImpl();
    assertEquals(instance.getSchemaType(), "TestSupport-TestSectionImpl-type");
  }

  /**
   * Test of createSchemaType method, of class SectionImpl.
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    // Tested end to end.
  }

}
