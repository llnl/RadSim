/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml;

import gov.llnl.utility.PackageResource;
import gov.llnl.utility.UtilityPackage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test code for DomBuilder.
 */
public class DomBuilderNGTest
{

  public static DomBuilder newInstance()
  {
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbf.newDocumentBuilder();
      Document doc = builder.newDocument();

      // create the root element node
      Element element = doc.createElement("root");
      doc.appendChild(element);
      return new DomBuilder(element);
    }
    catch (ParserConfigurationException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Test of attr method, of class DomBuilder.
   */
  @Test
  public void testAttr()
  {
    String name = "name";
    String value = "value";
    DomBuilder instance = newInstance();
    DomBuilder result = instance.attr(name, value);
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root name=\"value\"/>";
    String strResult = DomUtilities.printXmlToString(result.toElement().getOwnerDocument()).replaceAll(System.lineSeparator(), "");
    assertEquals(strResult, expResult);
  }

  /**
   * Test of attrNS method, of class DomBuilder.
   */
  @Test
  public void testAttrNS()
  {
    String namespaceURI = "http://llnl.gov";
    String qualifiedName = "foo:bar";
    String value = "1";
    DomBuilder instance = newInstance();
    DomBuilder result = instance.attrNS(namespaceURI, qualifiedName, value);
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root xmlns:foo=\"http://llnl.gov\" foo:bar=\"1\"/>";
    String strResult = DomUtilities.printXmlToString(result.toElement().getOwnerDocument()).replaceAll(System.lineSeparator(), "");
    assertEquals(strResult, expResult);
  }

  /**
   * Test of elementNS method, of class DomBuilder.
   */
  @Test
  public void testElementNS()
  {
    PackageResource pkg = UtilityPackage.SELF;
    String name = "test";
    DomBuilder instance = newInstance();
    DomBuilder result = instance.elementNS(pkg, name);
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root>  <util:test xmlns:util=\"http://utility.llnl.gov\"/></root>";
    String strResult = DomUtilities.printXmlToString(result.toElement().getOwnerDocument()).replaceAll(System.lineSeparator(), "");
    assertEquals(strResult, expResult);
  }

  /**
   * Test of element method, of class DomBuilder.
   */
  @Test
  public void testElement_String()
  {
    String name = "foo";
    DomBuilder instance = newInstance();
    DomBuilder result = instance.element(name);
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root>  <foo/></root>";
    String strResult = DomUtilities.printXmlToString(result.toElement().getOwnerDocument()).replaceAll(System.lineSeparator(), "");
    assertEquals(strResult, expResult);
  }

  /**
   * Test of element method, of class DomBuilder.
   */
  @Test
  public void testElement_String_boolean()
  {
    String name = "bar";
    boolean first = true;
    DomBuilder instance = newInstance();
    instance.element("foo");
    DomBuilder result = instance.element(name, first);
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root>  <bar/>  <foo/></root>";
    String strResult = DomUtilities.printXmlToString(result.toElement().getOwnerDocument()).replaceAll(System.lineSeparator(), "");
    assertEquals(strResult, expResult);
  }

  /**
   * Test of comment method, of class DomBuilder.
   */
  @Test
  public void testComment()
  {
    String string = "text";
    DomBuilder instance = newInstance();
    DomBuilder result = instance.comment(string);
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root>  <!--text--></root>";
    String strResult = DomUtilities.printXmlToString(result.toElement().getOwnerDocument()).replaceAll(System.lineSeparator(), "");
    assertEquals(strResult, expResult);
  }

  /**
   * Test of text method, of class DomBuilder.
   */
  @Test
  public void testText()
  {
    String value = "text";
    DomBuilder instance = newInstance();
    DomBuilder result = instance.text(value);
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><root>text</root>";
    String strResult = DomUtilities.printXmlToString(result.toElement().getOwnerDocument()).replaceAll(System.lineSeparator(), "");
    assertEquals(strResult, expResult);
  }

  /**
   * Test of toElement method, of class DomBuilder.
   */
  @Test
  public void testToElement()
  {
    DomBuilder instance = newInstance();
    Element result = instance.toElement();
    assertNotNull(result);
  }

}
