/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.stream;

import java.util.stream.Stream;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for DoubleArrayCollectors.
 */
public class DoubleArrayCollectorsNGTest
{

  public DoubleArrayCollectorsNGTest()
  {
  }

  /**
   * Test of sum method, of class DoubleArrayCollectors.
   */
  @Test
  public void testSum()
  {
    double[][] input = new double[][]
    {
      {
        1, 2, 3
      }, 
      {
        3, 2, 1
      }, 
      {
        0, 0, 0
      }, 
      {
        6, 6, 6
      }
    };
    double[] expResult = new double[]
    {
      10, 10, 10
    };
    double[] result = Stream.of(input).collect(DoubleArrayCollectors.sum());
    assertEquals(result, expResult);
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testSumMismatch1()
  {
    double[][] input = new double[][]
    {
      {
        1, 2
      }, 
      {
        3, 2, 1
      }
    };
    Stream.of(input).collect(DoubleArrayCollectors.sum());
  }

  @Test(expectedExceptions = IndexOutOfBoundsException.class)
  public void testSumMismatch2()
  {
    double[][] input = new double[][]
    {
      {
        1, 2, 3
      }, 
      {
        2, 1
      }
    };
    Stream.of(input).collect(DoubleArrayCollectors.sum());
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void testSumNull2()
  {
    double[][] input = new double[][]
    {
      {
        1, 2
      }, null
    };
    Stream.of(input).collect(DoubleArrayCollectors.sum());
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void testSumNull1()
  {
    double[][] input = new double[][]
    {
      null, 
      {
        1, 2
      }
    };
    Stream.of(input).collect(DoubleArrayCollectors.sum());
  }

}
