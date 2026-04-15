/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import gov.llnl.math.interp.MultiLogLogInterp;
import gov.llnl.math.interp.MultiInterpolator;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class MultiLogLogInterpNGTest
{

  public MultiLogLogInterpNGTest()
  {
  }

  @Test
  public void testGet()
  {
    MultiLogLogInterp instance = (MultiLogLogInterp) MultiInterpolator.createLogLog(
            new double[]
            {
              1, 2, 3
            },
            new double[]
            {
              1, 4, 9
            });
    MultiInterpolator.Evaluator result = instance.get();
    assertNotNull(result);
    result.seek(1.5);
    assertEquals(result.evaluate(0), 2.25, 0.0);
  }

}
