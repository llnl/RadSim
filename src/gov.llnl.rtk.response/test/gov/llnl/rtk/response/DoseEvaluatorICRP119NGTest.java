/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.flux.FluxBinned;
import gov.llnl.rtk.flux.FluxGroupBin;
import gov.llnl.rtk.flux.FluxLineStep;
import java.time.Duration;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class DoseEvaluatorICRP119NGTest
{

  @Test
  public void testGetResponseFunction()
  {
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    DoseResponseFunction expResult = DoseResponseFunctionICRP119.getInstance();
    DoseResponseFunction result = instance.getResponseFunction();
    assertEquals(result, expResult);
  }

  @Test
  public void testSetDistance()
  {
    double distance = 2.0;
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    instance.setDistance(distance);
    assertEquals(instance.getDistance(), distance, 0.0);
  }

  @Test
  public void testSetDuration()
  {
    Duration duration = Duration.ofSeconds(1);
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    instance.setDuration(duration);
    assertEquals(instance.getDuration(), duration);
  }

  @Test
  public void testSetView_5args()
  {
    double iso = 0.0;
    double ap = 0.0;
    double pa = 1.0;
    double llat = 0.0;
    double rlat = 0.0;
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    instance.setView(iso, ap, pa, llat, rlat);
  }

  @Test
  public void testGetAbsorbedDose()
  {

    FluxBinned flux = new FluxBinned();
    flux.addPhotonLine(new FluxLineStep(1000, 1000, 0));
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    double expResult = 1.28056067211739E-10;
    double result = instance.getAbsorbedDose(flux);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGetAbsorbedDoseGroup()
  {
    FluxBinned flux = new FluxBinned();
    flux.addPhotonGroup(new FluxGroupBin(1000, 1100, 1000));
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    double expResult = 1.3279093057226133E-10;
    double result = instance.getAbsorbedDose(flux);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGetEquivalentDose()
  {
    FluxBinned flux = new FluxBinned();
    flux.addPhotonLine(new FluxLineStep(1000, 1000, 0));
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    instance.setView(1, 0, 0, 0, 0);
    double expResult = 9.207231232524031E-11;
    double result = instance.getEquivalentDose(flux);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGetEquivalentDoseGroup()
  {
    FluxBinned flux = new FluxBinned();
    flux.addPhotonGroup(new FluxGroupBin(1000, 1100, 1000));
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    instance.setView(1, 0, 0, 0, 0);
    double expResult = 9.597334083205592E-11;
    double result = instance.getEquivalentDose(flux);
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGetDuration()
  {
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    Duration expResult = Duration.ofHours(1);
    Duration result = instance.getDuration();
    assertEquals(result, expResult);
  }

  @Test
  public void testGetDistance()
  {
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    double expResult = 1.0;
    double result = instance.getDistance();
    assertEquals(result, expResult, 0.0);
  }

  @Test
  public void testGetView()
  {
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    double[] expResult = new double[5];
    double[] result = instance.getView();
    assertEquals(result, expResult);
  }

  @Test
  public void testSetView_doubleArr()
  {
    double[] view = new double[]
    {
      1
    };
    DoseEvaluatorICRP119 instance = DoseResponseFunctionICRP119.getInstance().newEvaluator();
    instance.setView(view);
  }

}
