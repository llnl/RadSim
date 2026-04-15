/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Test code for DomUtilities.
 */
public class DomUtilitiesNGTest
{

  public DomUtilitiesNGTest()
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
   * Test of findFirst method, of class DomUtilities.
   */
  @Test
  public void testFindFirst()
  {
    // Tested end to end.
  }

  /**
   * Test of findAll method, of class DomUtilities.
   */
  @Test
  public void testFindAll()
  {
    // Tested end to end.
  }

  /**
   * Test of removeWhitespace method, of class DomUtilities.
   */
  @Test
  public void testRemoveWhitespace()
  {
    // Tested end to end.
  }

  /**
   * Test of printXml method, of class DomUtilities.
   */
  @Test
  public void testPrintXml()
  {
    // Tested end to end.
  }

  /**
   * Test of newPrinter method, of class DomUtilities.
   */
  @Test
  public void testNewPrinter()
  {
    // Tested end to end.
  }

  /**
   * Test of appendElement method, of class DomUtilities.
   */
  @Test
  public void testAppendElement_Element_String()
  {
    // Tested end to end.
  }

  /**
   * Test of appendElement method, of class DomUtilities.
   */
  @Test
  public void testAppendElement_3args_1()
  {
    // Tested end to end.
  }

  /**
   * Test of appendElement method, of class DomUtilities.
   */
  @Test
  public void testAppendElement_3args_2()
  {
    // Tested end to end.
  }

  /**
   * Test of appendElement method, of class DomUtilities.
   */
  @Test
  public void testAppendElement_3args_3()
  {
    // Tested end to end.
  }

  /**
   * Test of getXmlPath method, of class DomUtilities.
   */
  @Test
  public void testGetXmlPath()
  {
    // Tested end to end.
  }

  /**
   * Test of parseStreamToDOM method, of class DomUtilities.
   */
  @Test
  public void testParseStreamToDOM() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of parseFileToDOM method, of class DomUtilities.
   */
  @Test
  public void testParseFileToDOM() throws Exception
  {
    // Tested end to end.
  }

  /**
   * Test of toFloat method, of class DomUtilities.
   */
  @Test
  public void testToFloat()
  {
    // Tested end to end.
  }

  /**
   * Test of toDouble method, of class DomUtilities.
   */
  @Test
  public void testToDouble() throws ParserConfigurationException
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element element = doc.createElement("root");
    doc.appendChild(element);
    element.setTextContent("1.0");
    double expResult = 1.0;
    double result = DomUtilities.toDouble(element);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of toInteger method, of class DomUtilities.
   */
  @Test
  public void testToInteger() throws ParserConfigurationException
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element element = doc.createElement("root");
    doc.appendChild(element);
    element.setTextContent("123");
    int expResult = 123;
    int result = DomUtilities.toInteger(element);
    assertEquals(result, expResult);
  }

  /**
   * Test of toIntegerAttribute method, of class DomUtilities.
   */
  @Test
  public void testToIntegerAttribute() throws ParserConfigurationException
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element element = doc.createElement("root");
    doc.appendChild(element);
    element.setAttribute("foo", "123");
    String attribute = "foo";
    int defaultValue = 0;
    int expResult = 123;
    int result = DomUtilities.toIntegerAttribute(element, attribute, defaultValue);
    assertEquals(result, expResult);
  }

  /**
   * Test of toBoolean method, of class DomUtilities.
   */
  @Test
  public void testToBoolean() throws ParserConfigurationException
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element element = doc.createElement("root");
    doc.appendChild(element);
    element.setTextContent("True");
    boolean expResult = true;
    boolean result = DomUtilities.toBoolean(element);
    assertEquals(result, expResult);
  }

  /**
   * Test of toDoubleAttribute method, of class DomUtilities.
   */
  @Test
  public void testToDoubleAttribute() throws ParserConfigurationException
  {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = dbf.newDocumentBuilder();
    Document doc = builder.newDocument();
    Element element = doc.createElement("root");
    doc.appendChild(element);
    element.setAttribute("foo", "123.1");
    String attribute = "foo";
    double defaultValue = 0.0;
    double expResult = 123.1;
    double result = DomUtilities.toDoubleAttribute(element, attribute, defaultValue);
    assertEquals(result, expResult, 0.0);
  }

  /**
   * Test of createNamespaceContext method, of class DomUtilities.
   */
  @Test
  public void testCreateNamespaceContext()
  {
    // Tested end to end.
  }

  /**
   * Test of trimWhitespace method, of class DomUtilities.
   */
  @Test
  public void testTrimWhitespace()
  {
    // Tested end to end.
  }

}
