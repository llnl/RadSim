/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxEncoding;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class SpectralResponseFunctionNullNGTest
{

  public SpectralResponseFunctionNull newInstance()
  {
    return new SpectralResponseFunctionNull(EnergyScaleFactory.newLinearScale(0, 3000, 501));
  }

  @Test
  public void testGetEnergyScale()
  {
    SpectralResponseFunctionNull instance = newInstance();
    EnergyScale expResult = instance.energyScale;
    EnergyScale result = instance.getEnergyScale();
    assertEquals(result, expResult);
  }

  @Test
  public void testGetModel()
  {
    SpectralResponseFunctionNull instance = newInstance();
    String expResult = "";
    String result = instance.getModel();
    assertEquals(result, expResult);
  }

  @Test
  public void testGetVendor()
  {
    SpectralResponseFunctionNull instance = newInstance();
    String expResult = "null";
    String result = instance.getVendor();
    assertEquals(result, expResult);
  }

  @Test
  public void testNewEvaluator()
  {
    SpectralResponseFunctionNull instance = newInstance();
    SpectralResponseEvaluator result = instance.newEvaluator();
    assertNotNull(result);
  }

  @Test
  public void testEvaluateSpectral()
  {
    SpectralResponseFunctionNull instance = newInstance();
    SpectralResponseEvaluator evaluator = instance.newEvaluator();
    Flux flux = TestSupport.loadResource("gov/llnl/rtk/resources/fluxSpectrum.bin", FluxEncoding.getInstance());
    Spectrum spectrum = evaluator.apply(flux);
    assertNotNull(spectrum);
  }

  @Test
  public void testEvaluateBinned()
  {
    SpectralResponseFunctionNull instance = newInstance();
    SpectralResponseEvaluator evaluator = instance.newEvaluator();
    Flux flux = TestSupport.loadResource("gov/llnl/rtk/resources/fluxBinned.bin", FluxEncoding.getInstance());
    Spectrum spectrum = evaluator.apply(flux);
    assertNotNull(spectrum);
  }

  @Test
  public void testEvaluateTrapezoid()
  {
    SpectralResponseFunctionNull instance = newInstance();
    SpectralResponseEvaluator evaluator = instance.newEvaluator();
    Flux flux = TestSupport.loadResource("gov/llnl/rtk/resources/fluxTrapezoid.bin", FluxEncoding.getInstance());
    Spectrum spectrum = evaluator.apply(flux);
    assertNotNull(spectrum);
  }

  @Test
  public void testEvaluateSetEnergyScale()
  {
    SpectralResponseFunctionNull instance = newInstance();
    SpectralResponseEvaluator evaluator = instance.newEvaluator();
    EnergyScale scale = EnergyScaleFactory.newLinearScale(0, 20, 10);
    evaluator.setEnergyScale(scale);
    assertEquals(evaluator.getEnergyScale(), scale);
    assertNotEquals(instance.getEnergyScale(), scale);
  }

  @Test
  public void testEvaluateGetResponseFunction()
  {
    SpectralResponseFunctionNull instance = newInstance();
    SpectralResponseEvaluator evaluator = instance.newEvaluator();
    assertEquals(evaluator.getResponseFunction(), instance);
  }

}
