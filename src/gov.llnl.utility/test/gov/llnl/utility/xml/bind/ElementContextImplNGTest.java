/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.ElementContextImpl;
import java.util.TreeMap;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ElementContextImpl.
 */
public class ElementContextImplNGTest
{

  public ElementContextImplNGTest()
  {
  }

  /**
   * Test of getNamespaceURI method, of class ElementContextImpl.
   */
  @Test
  public void testGetNamespaceURI()
  {
    ElementContextImpl instance = new ElementContextImpl(null, null, null, null, null);
    assertNull(instance.getNamespaceURI());
    instance = new ElementContextImpl(null, null, "subjectofymir", null, null);
    assertEquals(instance.getNamespaceURI(), "subjectofymir");
  }

  /**
   * Test of getLocalName method, of class ElementContextImpl.
   */
  @Test
  public void testGetLocalName()
  {
    ElementContextImpl instance = new ElementContextImpl(null, null, null, null, null);
    assertNull(instance.getLocalName());
    instance = new ElementContextImpl(null, null, null, "subjectofymir", null);
    assertEquals(instance.getLocalName(), "subjectofymir");
  }

  /**
   * Test of characters method, of class ElementContextImpl.
   */
  @Test
  public void testCharacters()
  {
    ElementContextImpl instance = new ElementContextImpl(null, null, null, null, null);
    // textContent is null
    instance.characters(null, 0, 0);

    char[] msg = new char[]
    {
      'l', 'e', 'v', 'i', 'a', 'c', 'k', 'e', 'r', 'm', 'a', 'n'
    };
    instance.textContent = new StringBuilder();
    instance.characters(msg, 0, msg.length);
    assertEquals(instance.textContent.toString(), String.valueOf(msg));
  }

//  /**
//   * Test of getReference method, of class ElementContextImpl.
//   */
//  @Test
//  public void testGetReference()
//  {
//    assertNull(new ElementContextImpl().getReference(""));
//
//    // Setup
//    ElementContextImpl grandparent = new ElementContextImpl();
//    ElementContextImpl parent = new ElementContextImpl();
//    ElementContextImpl child = new ElementContextImpl();
//
//    grandparent.references = new TreeMap<>()
//    {
//      {
//        put("Integer", 0);
//        put("Double", (double) 1.0);
//      }
//    };
//    parent.references = new TreeMap<>()
//    {
//      {
//        put("Float", (float) 2.0);
//        put("Boolean", true);
//      }
//    };
//    child.references = new TreeMap<>()
//    {
//      {
//        put("Byte", (byte) 64);
//        put("attacktitan", "ErenYeager");
//      }
//    };
//
//    child.parentContext = parent;
//    parent.parentContext = grandparent;
//
//    // Test
//    assertNull(child.getReference("scoutregiment"));
//    assertEquals(child.getReference("attacktitan"), "ErenYeager");
//    assertEquals(child.getReference("Byte"), Byte.valueOf("64"));
//    assertEquals(child.getReference("Integer"), 0);
//    assertEquals(child.getReference("Double"), 1D);
//    assertEquals(child.getReference("Float"), 2.0f);
//    assertEquals(child.getReference("Boolean"), true);
//  }
//  /**
//   * Test of putReference method, of class ElementContextImpl.
//   */
//  @Test
//  public void testPutReference()
//  {
//    ElementContextImpl instance = new ElementContextImpl();
//    instance.references = new TreeMap<>();
//    assertNotNull(instance.putReference("Historia", "Reiss"));
//    assertEquals(instance.references.get("Historia"), "Reiss");
//    
//    instance.references = null;
//    assertNotNull(instance.putReference("Historia", "Reiss"));
//    assertEquals(instance.references.get("Historia"), "Reiss");
//  }
  /**
   * Test of getObject method, of class ElementContextImpl.
   */
  @Test
  public void testGetTargetObject()
  {
    ElementContextImpl instance = new ElementContextImpl(null, null, null, null, null);
    instance.targetObject = new Object();
    assertSame(instance.getObject(), instance.targetObject);
  }

  /**
   * Test of getParentObject method, of class ElementContextImpl.
   */
  @Test
  public void testGetParentObject()
  {
    ElementContextImpl instance = new ElementContextImpl(null, new Object(), null, null, null);
    assertSame(instance.getParentObject(), instance.parentObject);
  }

  @Test
  public void testEquals()
  {
    ElementContextImpl parent = new ElementContextImpl(null, null, null, null, null);
    ElementContextImpl ancestor = new ElementContextImpl(null, null, null, "elem", null);
    ElementContextImpl child = new ElementContextImpl(ancestor, null, null, null, null);
    ElementContextImpl other = new ElementContextImpl(ancestor, null, null, null, null);
    Object o = 1;

    assertTrue(child.equals(other));
    assertFalse(parent.equals(o));
    assertFalse(parent.equals(child));
  }
}
