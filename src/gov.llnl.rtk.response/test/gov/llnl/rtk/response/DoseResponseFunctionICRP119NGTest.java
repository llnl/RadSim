/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class DoseResponseFunctionICRP119NGTest
{

  @Test
  public void testGetInstance()
  {
    DoseResponseFunctionICRP119 result = DoseResponseFunctionICRP119.getInstance();
    assertNotNull(result);
  }

  @Test
  public void testGetModel()
  {
    DoseResponseFunctionICRP119 instance = DoseResponseFunctionICRP119.getInstance();
    String expResult = "119 - Compendium of Dose Coefficients based on ICRP Publication 60";
    String result = instance.getModel();
    assertEquals(result, expResult);
  }

  @Test
  public void testGetVendor()
  {
    DoseResponseFunctionICRP119 instance = DoseResponseFunctionICRP119.getInstance();
    String expResult = "ICRP";
    String result = instance.getVendor();
    assertEquals(result, expResult);
  }

  @Test
  public void testNewEvaluator()
  {
    DoseResponseFunctionICRP119 instance = DoseResponseFunctionICRP119.getInstance();
    DoseEvaluatorICRP119 result = instance.newEvaluator();
    assertNotNull(result);
  }

}
