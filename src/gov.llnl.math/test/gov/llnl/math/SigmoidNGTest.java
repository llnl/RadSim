/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Sigmoid.
 */
public class SigmoidNGTest
{
  
  public SigmoidNGTest()
  {
  }
  
  /**
   * Test constructor of class Sigmoid.
   */
  @Test
  public void testConstructor()
  {
    Sigmoid instance = new Sigmoid();
  }

  /**
   * Test of evaluate method, of class Sigmoid.
   */
  @Test
  public void testEvaluate()
  {
    assertEquals(Sigmoid.evaluate(-1, 1, 1), 0.11920292202211755, 1e-6);
    assertEquals(Sigmoid.evaluate(0, 1, 1), 0.2689414213699951, 1e-6);
    assertEquals(Sigmoid.evaluate(1, 1, 1), 0.5, 1e-6);
    assertEquals(Sigmoid.evaluate(2, 1, 1), 0.7310585786300049, 1e-6);
    assertEquals(Sigmoid.evaluate(3, 1, 1), 0.8807970779778823, 1e-6);
  }

  /**
   * Test of create method, of class Sigmoid.
   */
  @Test
  public void testCreate()
  {
    double[] expResult = new double[]{0.11920292202211755, 0.2689414213699951, 0.5, 0.7310585786300049, 0.8807970779778823
  };
    double[] out = IntStream.range(0, 5).asDoubleStream().map(Sigmoid.create(2, 1)).toArray();
    assertEquals(out, expResult);
  }
  
}
