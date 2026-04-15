/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import gov.llnl.utility.xml.bind.Schema;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for AnnotationUtilities.
 */
public class AnnotationUtilitiesNGTest
{

  public AnnotationUtilitiesNGTest()
  {
  }

  static class Case1
  {
  }

  @Schema.Using(UtilityPackage.class)
  static class Case2
  {
  }

  @Schema.Using(UtilityPackage.class)
  @Schema.Using(UtilityPackage.class)
  static class Case3
  {
  }

  /**
   * Test of getRepeatingAnnotation method, of class AnnotationUtilities.
   */
  @Test
  public void testGetRepeatingAnnotation()
  {
    assertEquals(AnnotationUtilities.getRepeatingAnnotation(Case1.class, Schema.Using.class).size(), 0);
    assertEquals(AnnotationUtilities.getRepeatingAnnotation(Case2.class, Schema.Using.class).size(), 1);
    assertEquals(AnnotationUtilities.getRepeatingAnnotation(Case3.class, Schema.Using.class).size(), 2);
  }

}
