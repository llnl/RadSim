/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.Arrays;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ProcessBuilderUtilities.
 */
public class ProcessBuilderUtilitiesNGTest
{

  @Test
  public void testConstructor()
  {
    ProcessBuilderUtilities instance = new ProcessBuilderUtilities();
  }

  /**
   * Test of parseExecutable method, of class ProcessBuilderUtilities.
   */
  @Test
  public void testParseExecutable()
  {
    String str = "/foo/bar/test \"this value\" argument";
    List expResult = Arrays.asList("/foo/bar/test", "this value", "argument", "");
    List result = ProcessBuilderUtilities.parseExecutable(str);
    assertEquals(result, expResult);
    // FIXME.  The current implementation has an extra argument.  
  }

}
