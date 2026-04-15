/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.wavelet;

import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for WaveletTransformFactory.
 */
public class WaveletTransformFactoryNGTest
{

  public WaveletTransformFactoryNGTest()
  {
  }

  /**
   * Test of setFamily method, of class WaveletTransformFactory.
   */
  @Test (expectedExceptions = {
    NullPointerException.class
  })
  public void testSetFamily_WaveletFamily()
  {
    WaveletFamily family = new WaveletFamilyDaubechies();
    WaveletTransformFactory instance = new WaveletTransformFactory();
    instance.setFamily(family);
    family = null;
    instance.setFamily(family);
  }

  /**
   * Test of setFamily method, of class WaveletTransformFactory.
   */
  @Test
  public void testSetFamily_String() throws Exception
  {
    String family = "daub";
    WaveletTransformFactory instance = new WaveletTransformFactory();
    instance.setFamily(family);
    assertTrue(instance.getFamily() instanceof WaveletFamilyDaubechies);
  }

  /**
   * Test of setFamily method, of class WaveletTransformFactory.
   */
  @Test(expectedExceptions = WaveletNotFoundException.class)
  public void testSetFamily_StringFail() throws Exception
  {
    String family = "xxx";
    WaveletTransformFactory instance = new WaveletTransformFactory();
    instance.setFamily(family);
  }

  /**
   * Test of getAvailableFamilies method, of class WaveletTransformFactory.
   */
  @Test
  public void testGetAvailableFamilies()
  {
    WaveletTransformFactory instance = new WaveletTransformFactory();
    Set<String> result = instance.getAvailableFamilies();
    assertTrue(result.contains("daub"));
  }

  /**
   * Test of setLength method, of class WaveletTransformFactory.
   */
  @Test
  public void testSetLength()
  {
    int length = 0;
    WaveletTransformFactory instance = new WaveletTransformFactory();
    instance.setLength(length);
  }

  /**
   * Test of create method, of class WaveletTransformFactory.
   */
  @Test
  public void testCreate() throws Exception
  {
    WaveletTransformFactory instance = new WaveletTransformFactory();
    instance.setFamily("daub");
    instance.setLength(4);
    WaveletTransform result = instance.create();
    assertTrue(result instanceof WaveletTransformImpl);
  }

  /**
   * Test of newTransform method, of class WaveletTransformFactory.
   */
  @Test
  public void testNewTransform() throws Exception
  {
    String description = "daub16";
    WaveletTransform expResult = null;
    WaveletTransform result = WaveletTransformFactory.newTransform(description);
    assertTrue(result instanceof WaveletTransformImpl);

  }

}
