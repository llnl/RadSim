/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.function;

import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.DocumentReader;
import gov.llnl.utility.xml.bind.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.xml.sax.Attributes;

/**
 * Test code for  SaturationFunctionReader.
 */
public class SaturationFunctionReaderNGTest
{
  
  public SaturationFunctionReaderNGTest()
  {
  }
  
  @Test
  public void testEndToEnd() throws ReaderException
  {
    String contents = "<saturation xmlns='http://math.llnl.gov' "
            + "offset='1.0' gain='2.0' saturation='3.0'/>";
    DocumentReader<SaturationFunction> dr = DocumentReader.create(new SaturationFunctionReader());
    assertEquals(dr.loadString(contents), new SaturationFunction(1,2,3));
  }

  /**
   * Test of start method, of class SaturationFunctionReader.
   */
  @Test
  public void testStart() throws Exception
  {
    // Test with end to end
  }

  /**
   * Test of getHandlers method, of class SaturationFunctionReader.
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    // Test with end to end
  }
  
}
