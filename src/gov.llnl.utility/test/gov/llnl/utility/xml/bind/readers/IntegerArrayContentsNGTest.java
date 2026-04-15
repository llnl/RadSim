/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind.readers;

import gov.llnl.utility.xml.bind.readers.IntegerArrayContents;
import gov.llnl.utility.ArrayEncoding;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.SchemaBuilder;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for IntegerArrayContents.
 */
public class IntegerArrayContentsNGTest
{

  public IntegerArrayContentsNGTest()
  {
  }

  /**
   * Test of createSchemaType method, of class IntegerArrayContents.
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    SchemaBuilder builder = null;
    IntegerArrayContents instance = new IntegerArrayContents();
    instance.createSchemaType(builder);
  }

  /**
   * Test of createSchemaElement method, of class IntegerArrayContents.
   */
  @Test
  public void testCreateSchemaElement()
  {
    IntegerArrayContents iac = new IntegerArrayContents();
    Element first = newElement("First");
    DomBuilder domBuilder = new DomBuilder(first);

    DomBuilder tooDomTooBuildrious = iac.createSchemaElement(null, "Toretto&O'Conner", domBuilder, false);
    assertEquals(tooDomTooBuildrious.toElement(), domBuilder.toElement().getFirstChild());

    Element second = tooDomTooBuildrious.toElement();
    assertEquals(second.getTagName(), "xs:element");
    assertEquals(second.getAttribute("name"), "Toretto&O'Conner");
    assertEquals(second.getAttribute("type"), "util:string-attr");
  }

  /**
   * Test of contents method, of class IntegerArrayContents.
   */
  @Test(expectedExceptions =
  {
    ReaderException.class
  })
  public void testContents() throws Exception
  {
    int[] intArray = new int[]
    {
      1, 1, 5, 5, 6, 6, 5, 4, 4, 3, 3, 2, 2, 1
    };
    String textContents = ArrayEncoding.encodeIntegers(intArray);
    IntegerArrayContents instance = new IntegerArrayContents();
    assertEquals(instance.contents(null, textContents), intArray);

    // Test ReaderException
    instance.contents(null, "z");
  }
}
