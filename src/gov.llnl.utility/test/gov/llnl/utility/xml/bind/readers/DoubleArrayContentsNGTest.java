/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind.readers;

import gov.llnl.utility.xml.bind.readers.DoubleArrayContents;
import gov.llnl.utility.ArrayEncoding;
import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.DomBuilder;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for DoubleArrayContents.
 */
public class DoubleArrayContentsNGTest
{

  public DoubleArrayContentsNGTest()
  {
  }

  /**
   * Test of contents method, of class DoubleArrayContents.
   */
  @Test(expectedExceptions =
  {
    ReaderException.class
  })
  public void testContents() throws Exception
  {
    double[] doubleArray = new double[]
    {
      1D, 1D, 5D, 5D, 6D, 6D, 5D, 4D, 4D, 3D, 3D, 2D, 2D, 1D
    };
    String textContents = ArrayEncoding.encodeDoubles(doubleArray);
    DoubleArrayContents instance = new DoubleArrayContents();
    assertEquals(instance.contents(null, textContents), doubleArray);

    // Test ReaderException
    instance.contents(null, "z");
  }

  /**
   * Test of getObjectClass method, of class DoubleArrayContents.
   */
  @Test
  public void testGetObjectClass()
  {
    assertEquals(new DoubleArrayContents().getObjectClass(), double[].class);
  }

  /**
   * Test of createSchemaType method, of class DoubleArrayContents.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    new DoubleArrayContents().createSchemaType(null);
  }

  /**
   * Test of createSchemaElement method, of class DoubleArrayContents.
   */
  @Test
  public void testCreateSchemaElement()
  {
    DoubleArrayContents dac = new DoubleArrayContents();
    Element first = newElement("First");
    DomBuilder domBuilder = new DomBuilder(first);

    DomBuilder tooDomTooBuildrious = dac.createSchemaElement(null, "Toretto&O'Conner", domBuilder, false);
    assertEquals(tooDomTooBuildrious.toElement(), domBuilder.toElement().getFirstChild());

    Element second = tooDomTooBuildrious.toElement();
    assertEquals(second.getTagName(), "xs:element");
    assertEquals(second.getAttribute("name"), "Toretto&O'Conner");
    assertEquals(second.getAttribute("type"), "util:string-attr");
  }

}
