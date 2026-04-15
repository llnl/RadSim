/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.random;

import gov.llnl.math.MathExceptions.DomainException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for BetaRandom.
 */
public class BetaRandomNGTest
{

  public BetaRandomNGTest()
  {
  }

  BetaRandom newInstance()
  {
    BetaRandom instance = new BetaRandom();
    instance.setSeed(1);
    instance.grand = new GammaRandom()
    {
      public double draw(double alpha, double beta)
      {
        return alpha * beta;
      }
    };    
    return instance;
  }
  
  /**
   * Test of draw method, of class BetaRandom.
   */
  @Test
  public void testDraw()
  {
    BetaRandom instance = newInstance();
    instance.setSeed(1);
    assertEquals(instance.draw(1.0, 2.0), 1.0 / 3.0, 0.0);
    assertEquals(instance.draw(2.0, 1.0), 2.0 / 3.0, 0.0);
  }

  /**
   * Test of newVariable method, of class BetaRandom.
   */
  @Test (expectedExceptions = {
    DomainException.class
  })
  public void testNewVariable()
  {
    BetaRandom instance = newInstance();
    instance.setSeed(1);
    RandomVariable result = instance.newVariable(1.0, 2.0);
    assertEquals(result.next(), 1.0/3.0);
    instance.newVariable(-1.0, 2.0);
    instance.newVariable(1.0, -1.0);
    instance.newVariable(-1.0, -1.0);
  }

  /**
   * Test of newVariableFromMeanVar method, of class BetaRandom.
   */
  @Test (expectedExceptions = {
    DomainException.class
  })
  public void testNewVariableFromMeanVar()
  {
    BetaRandom instance = newInstance();
    instance.setSeed(1);
    double mean = instance.getMean(2.0, 1.0);
    double var = instance.getVariance(2.0, 1.0);
    RandomVariable result = instance.newVariableFromMeanVar(mean, var);
    assertEquals(result.next(), 2.0/3.0);
    mean = 0.0;
    var = 0.0;
    instance.newVariableFromMeanVar(mean, var);
  }

}
