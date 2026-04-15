/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind.readers;

import gov.llnl.utility.xml.bind.readers.FlagReader;
import gov.llnl.utility.xml.bind.Reader;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for FlagReader.
 */
public class FlagReaderNGTest
{

  public FlagReaderNGTest()
  {
  }

  /**
   * Test of contents method, of class FlagReader.
   */
  @Test
  public void testContents() throws Exception
  {
    FlagReader fr = new FlagReader();
    assertTrue(fr.contents(null, ""));
    assertTrue(fr.contents(null, "true"));
    assertTrue(fr.contents(null, "Truth") == false);
  }

  /**
   * Test of getTextContents method, of class FlagReader.
   */
  @Test
  public void testGetTextContents()
  {
    FlagReader fr = new FlagReader();
    Reader.TextContents tc = fr.getTextContents();
    assertEquals(tc.base(), "util:boolean-optional");
  }

}
