/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Test code for Transform.
 */
public class TransformNGTest
{

  /**
   * Test of transform method, of class Transform.
   */
  @Test
  public void testTransform() throws Exception
  {
    // Not used in production.
//    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//    DocumentBuilder db = dbf.newDocumentBuilder();
//    Document doc = db.parse(new InputSource(new ByteArrayInputStream("<hello><world/></hello>".getBytes())));
//
//    Result result_2 = new DOMResult();
//    InputSource datafile = new InputSource(new ByteArrayInputStream("<hello><world/></hello>".getBytes()));
//    Path xslt = null;
//    Transform.transform(result_2, datafile, xslt);
  }

  /**
   * Test of write method, of class Transform.
   */
  @Test
  public void testWrite() throws Exception
  {
    // Not used in production code.
  }
}
