/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import gov.llnl.math.interp.SingleInterpolator;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class SingleLinearInterpNGTest
{

  public SingleLinearInterpNGTest()
  {
  }

  @Test
  public void testGet()
  {
    SingleInterpolator instance = SingleInterpolator.createLinear(
            new double[]
            {
              1, 2, 3
            },
            new double[]
            {
              1, 4, 9
            });
    SingleInterpolator.Evaluator result = instance.get();
    assertNotNull(result);
    assertEquals(result.applyAsDouble(1), 1.0, 0.0);
    assertEquals(result.applyAsDouble(2), 4.0, 0.0);
    assertEquals(result.applyAsDouble(3), 9.0, 0.0);
  }

}
