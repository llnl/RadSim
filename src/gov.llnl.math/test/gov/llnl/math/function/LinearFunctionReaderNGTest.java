/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.function;

import gov.llnl.utility.xml.bind.DocumentReader;
import gov.llnl.utility.xml.bind.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Test code for LinearFunctionReader.
 */
public class LinearFunctionReaderNGTest
{
  
  public LinearFunctionReaderNGTest()
  {
  }

  /**
   * Test of start method, of class LinearFunctionReader.
   */
  @Test
  public void testStart() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "offset", null, "1");
    attributes.addAttribute(null, null, "slope", null, "2");
    LinearFunctionReader instance = new LinearFunctionReader();
    LinearFunction expResult = new LinearFunction(1,2);
    LinearFunction result = instance.start(null, attributes);
    assertEquals(result, expResult);
  }

  /**
   * Test of getHandlers method, of class LinearFunctionReader.
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    LinearFunctionReader instance = new LinearFunctionReader();
    Reader.ElementHandlerMap expResult = null;
    Reader.ElementHandlerMap result = instance.getHandlers(null);
    assertEquals(result, expResult);
  }
  
}
