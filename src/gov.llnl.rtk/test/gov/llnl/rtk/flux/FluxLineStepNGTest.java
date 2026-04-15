/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class FluxLineStepNGTest
{

  FluxLineStep newInstance()
  {
    return new FluxLineStep(10, 20, 2);
  }

  @Test
  public void testGetEnergy()
  {
    FluxLineStep instance = newInstance();
    double expResult = 10.0;
    double result = instance.getEnergy();
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGetIntensity()
  {
    FluxLineStep instance = newInstance();
    double expResult = 20.0;
    double result = instance.getIntensity();
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGetStep()
  {
    FluxLineStep instance = newInstance();
    double expResult = 2.0;
    double result = instance.getStep();
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testToString()
  {
    FluxLineStep instance = newInstance();
    String expResult = "LineStep(e=10.00,i=2.000e+01,s=2.000e+00)";
    String result = instance.toString();
    assertEquals(result, expResult);
  }

  @Test
  public void testEquals()
  {
    Object obj = newInstance();
    FluxLineStep instance = newInstance();
    assertTrue(instance.equals(obj));
    assertFalse(instance.equals(new Object()));
    assertFalse(instance.equals(new FluxLineStep(11, 20, 2)));
    assertFalse(instance.equals(new FluxLineStep(10, 21, 2)));
    assertFalse(instance.equals(new FluxLineStep(10, 20, 3)));
  }

}
