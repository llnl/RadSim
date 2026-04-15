/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.function;

import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.DocumentReader;
import gov.llnl.utility.xml.bind.ObjectReader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for FunctionReader.
 */
public class FunctionReaderNGTest
{

  public FunctionReaderNGTest()
  {
  }

  @Test
  public void testEndToEnd() throws ReaderException
  {
    DocumentReader<Function> dr;
    String s1 = "<function xmlns='http://math.llnl.gov'>"
            + "<linear offset='1' slope='2'/>"
            + "</function>";
    dr = DocumentReader.create(Function.class);
    assertEquals(dr.loadString(s1), new LinearFunction(1, 2));
    
    String s2 = "<function xmlns='http://math.llnl.gov'>"
            + "<quadratic offset='1' slope='2' accel='3'/>"
            + "</function>";
    dr = DocumentReader.create(Function.class);
    assertEquals(dr.loadString(s2), new QuadraticFunction(1, 2, 3));
  }

  /**
   * Test of getReaders method, of class FunctionReader.
   */
  @Test
  public void testGetReaders() throws Exception
  {
    FunctionReader instance = new FunctionReader();
    ObjectReader[] result = instance.getReaders();
    assertNotNull(result);
  }

}
