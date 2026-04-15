/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.Serializable;
import java.util.TreeMap;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ExpandableObject.
 */
public class ExpandableObjectNGTest
{

  public ExpandableObjectNGTest()
  {
  }

  /**
   * Test copy constructor
   */
  @Test(expectedExceptions =
  {
    NullPointerException.class
  })
  public void testCopyConstructor()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);
    instance.setAttribute("double", 3.14159265359);

    ExpandableObject other = new ExpandableObject(instance);
    assertEquals(other.getAttributes(), instance.getAttributes());

    // Test NullPointerException
    instance = new ExpandableObject(null);
  }

  /**
   * Test of setAttribute method, of class ExpandableObject.
   */
  @Test
  public void testSetAttribute()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);
    instance.setAttribute("double", 3.14159265359);

    assertEquals(instance.getAttribute("int"), 1);
    assertEquals(instance.getAttribute("double"), 3.14159265359);
    assertNull(instance.getAttribute("live long and prosper"));
  }

  /**
   * Test of getAttribute method, of class ExpandableObject.
   */
  @Test
  public void testGetAttribute()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);
    instance.setAttribute("double", 3.14159265359);

    assertEquals(instance.getAttribute("int"), 1);
    assertEquals(instance.getAttribute("double"), 3.14159265359);
    assertNull(instance.getAttribute("live long and prosper"));
  }

  /**
   * Test of getAttribute method, of class ExpandableObject.
   */
  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testGetAttribute_2args_1()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);
    instance.setAttribute("double", 3.14159265359);

    // Test ClassCastException
    instance.getAttribute("int", Double.TYPE);

    assertNull(instance.getAttribute("string", String.class));

    assertEquals(instance.getAttribute("int", Integer.TYPE), Integer.valueOf(1));
    assertEquals((Double) instance.getAttribute("double", Double.TYPE), (Double) 3.14159265359D);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testGetAttribute_1args_1()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);
    instance.setAttribute("double", 3.14159265359);

    // Test ClassCastException
    instance.getAttribute(Attribute.of("int", Double.TYPE));

    assertNull(instance.getAttribute(Attribute.of("AutobotsRollOut!", Integer.TYPE)));

    assertEquals(instance.getAttribute(Attribute.of("int", Integer.TYPE)), Integer.valueOf(1));
    assertEquals((Double) instance.getAttribute(Attribute.of("double", Double.TYPE)), (Double) 3.14159265359D);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testGetAttribute_3args_1()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);
    instance.setAttribute("double", 3.14159265359);

    // Test ClassCastException
    instance.getAttribute("int", Double.TYPE, null);

    assertNull(instance.getAttribute("Ohana means family. Family means no one gets left behind.", String.class, null));

    assertEquals(instance.getAttribute("int", Integer.TYPE, null), Integer.valueOf(1));
    assertEquals((Double) instance.getAttribute("double", Double.TYPE, null), (Double) 3.14159265359D);
    assertEquals(instance.getAttribute("You are who you choose to be", String.class, "Superman"), "Superman");
  }

  /**
   * Test of getAttributes method, of class ExpandableObject.
   */
  @Test
  public void testGetAttributes()
  {
    ExpandableObject instance = new ExpandableObject();
    assertEquals(instance.getAttributes().size(), 0);

    instance.setAttribute("int", 1);
    instance.setAttribute("double", 3.14159265359);

    TreeMap<String, Serializable> controlTreeMap = new TreeMap<>();
    controlTreeMap.put("int", 1);
    controlTreeMap.put("double", 3.14159265359);

    assertEquals(instance.getAttributes(), controlTreeMap);
  }

  @Test
  public void testhasAttribute()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);

    assertTrue(instance.hasAttribute("int"));
    assertTrue(instance.hasAttribute("True") == false);
  }

  @Test
  public void testRemoveAttribute()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);

    assertEquals(instance.getAttribute("int"), 1);
    instance.removeAttribute("int");
    assertNull(instance.getAttribute("int"));
  }

  @Test
  public void testCopyAttributes()
  {
    ExpandableObject instance = new ExpandableObject();
    instance.setAttribute("int", 1);

    ExpandableObject other = new ExpandableObject();
    other.copyAttributes(instance);

    assertEquals(other.getAttributes(), instance.getAttributes());
  }

}
