/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.xml.bind.AttributesUtilities;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.Reader;
import java.util.ArrayList;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for AttributesUtilities.
 */
public class AttributesUtilitiesNGTest
{

  public AttributesUtilitiesNGTest()
  {
  }

  /**
   * Test of createSchemaType method, of class AttributesUtilities.
   */
  @Test
  public void testCreateSchemaType()
  {
    // Tested end to end.
  }

  /**
   * Test of declareAttribute method, of class AttributesUtilities.
   */
  @Test
  public void testDeclareAttribute()
  {
    // Tested end to end.
  }

  /**
   * Test of getAttributeType method, of class AttributesUtilities.
   */
  @Test
  public void testGetAttributeType_String()
  {
    Object s = "test";
    Class<?> parameterType = s.getClass();
    String expResult = "xs:string";
    String result = AttributesUtilities.getAttributeType(parameterType);
    assertEquals(result, expResult);
  }

  /**
   * Test of getAttributeType method, of class AttributesUtilities.
   */
  @Test
  public void testGetAttributeType_Double()
  {
    Object d = 1.0;
    Class<?> parameterType = d.getClass();
    String expResult = "xs:double";
    String result = AttributesUtilities.getAttributeType(parameterType);
    assertEquals(result, expResult);
  }

  /**
   * Test of getAttributeType method, of class AttributesUtilities.
   */
  @Test
  public void testGetAttributeType_Int()
  {
    Object i = 1;
    Class<?> parameterType = i.getClass();
    String expResult = "xs:int";
    String result = AttributesUtilities.getAttributeType(parameterType);
    assertEquals(result, expResult);
  }

  /**
   * Test of getAttributeType method, of class AttributesUtilities.
   */
  @Test
  public void testGetAttributeType_Boolean()
  {
    Object b = true;
    Class<?> parameterType = b.getClass();
    String expResult = "xs:boolean";
    String result = AttributesUtilities.getAttributeType(parameterType);
    assertEquals(result, expResult);
  }

  /**
   * Test of getAttributeType method, of class AttributesUtilities.
   */
  @Test
  public void testGetAttributeType_Enum()
  {
    // Tested end to end.
  }

  /**
   * Test of getAttributeType method, of class AttributesUtilities.
   */
  @Test(expectedExceptions = RuntimeException.class)
  public void testGetAttributeType()
  {
    Object o = new ArrayList<>();
    Class<?> parameterType = o.getClass();
    String expResult = "";
    String result = AttributesUtilities.getAttributeType(parameterType);
    assertEquals(result, expResult);
  }
}
