/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for UtilityPackage. Implicitly cover PackageResource
 */
public class UtilityPackageNGTest
{
  public UtilityPackageNGTest()
  {
  }

  /**
   * Test of getInstance method, of class UtilityPackage.
   */
  @Test
  public void testGetInstance()
  {
    assertEquals(UtilityPackage.getInstance(), UtilityPackage.SELF);
  }
}
