/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.random;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for GammaRandom.
 */
public class GammaRandomNGTest
{
  
  public GammaRandomNGTest()
  {
  }
  

  /**
   * Test of draw method, of class GammaRandom.
   */
  @Test
  public void testDraw()
  {
    GammaRandom instance = new GammaRandom();
    instance.setSeed(1);
    instance.setGenerator(SequenceGenerator.ofDoubles(0.5,0.1, 0.2,0.6,0.8));
    assertEquals(instance.draw(0.5, 1.0), 0.4232072840934888, 0.000000001);
    assertEquals(instance.draw(1.0, 0.5), 1.180558271318159,   0.000000001);
  }

  /**
   * Test of newVariable method, of class GammaRandom.
   */
  @Test
  public void testNewVariable()
  {
//    double alpha = 0.0;
//    double beta = 0.0;
//    GammaRandom instance = new GammaRandom();
//    RandomVariable expResult = null;
//    RandomVariable result = instance.newVariable(alpha, beta);
//    assertEquals(result, expResult);
  }
  
}
