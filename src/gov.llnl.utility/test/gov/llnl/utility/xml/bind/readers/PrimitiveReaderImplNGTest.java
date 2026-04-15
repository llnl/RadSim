/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind.readers;

import gov.llnl.utility.xml.bind.readers.PrimitiveReaderImpl;
import gov.llnl.utility.ClassUtilities;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for PrimitiveReaderImpl.
 */
public class PrimitiveReaderImplNGTest
{

  public PrimitiveReaderImplNGTest()
  {
  }

  /**
   * Test of contents method, of class PrimitiveReaderImpl.
   */
  @Test(expectedExceptions =
  {
    ReaderException.class
  })
  public void testContents() throws Exception
  {
    PrimitiveReaderImpl pri = new PrimitiveReaderImpl(ClassUtilities.INTEGER_PRIMITIVE);
    assertEquals((Integer) pri.contents(null, "360"), Integer.valueOf(360));

    // Test ReaderException
    pri.contents(null, "ccggaag");
  }

  /**
   * Test of getObjectClass method, of class PrimitiveReaderImpl.
   */
  @Test
  public void testGetObjectClass()
  {
    PrimitiveReaderImpl pri = new PrimitiveReaderImpl(ClassUtilities.DOUBLE_PRIMITIVE);
    assertEquals(pri.getObjectClass(), Double.class);
  }

  /**
   * Test of getTextContents method, of class PrimitiveReaderImpl.
   */
  @Test
  public void testGetTextContents()
  {
    PrimitiveReaderImpl pri = new PrimitiveReaderImpl(ClassUtilities.LONG_PRIMITIVE);
    Reader.TextContents tc = pri.getTextContents();
    assertEquals(tc.base(), "xs:long");
    assertEquals(tc.annotationType(), Reader.TextContents.class);
  }

}
