/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.io;

import gov.llnl.utility.io.FieldProperties.Property;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * Test code for FieldProperties.
 */
public class FieldPropertiesNGTest
{

  public FieldPropertiesNGTest()
  {
  }

  public class PropertyTest
  {
    FieldProperties proxy = new FieldProperties(this);

    @Property
    int value;

    @Property
    String name;
  }

  /**
   * Test of setProperty method, of class FieldProperties.
   */
  @Test
  public void testSetProperty() throws Exception
  {
    PropertyTest pt = new PropertyTest();
    pt.proxy.setProperty("name", "hello");
    pt.proxy.setProperty("value", 25);
    assertEquals(pt.name, "hello");
    assertEquals(pt.value, 25);
  }

  /**
   * Test of getProperty method, of class FieldProperties.
   */
  @Test
  public void testGetProperty() throws Exception
  {
    PropertyTest pt = new PropertyTest();
    pt.name = "hello";
    pt.value = 25;
    assertEquals(pt.proxy.getProperty("name"), "hello");
    assertEquals(pt.proxy.getProperty("value"), 25);
  }

}
