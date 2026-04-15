/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import gov.llnl.math.RebinUtilities.ArrayBinEdges;
import gov.llnl.math.RebinUtilities.RebinException;
import gov.llnl.math.RebinUtilities.ScaledArrayBinEdges;
import gov.llnl.math.RebinUtilities.StepBinEdges;
import gov.llnl.math.matrix.Matrix;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for RebinUtilities.
 */
public class RebinUtilitiesNGTest
{

  public RebinUtilitiesNGTest()
  {
  }

  /**
   * Test of ArrayBinEdges methods.
   */
  @Test
  public void testArrayBinEdges() throws RebinException
  {
    double[] edges = {0.0, 1.0, 2.0, 3.0, 4.0};
    ArrayBinEdges instance = new ArrayBinEdges(edges);

    // Test size method
    assertEquals(instance.size(), 5);

    // Test get method
    assertEquals(instance.get(0), 0.0, 1e-12);
    assertEquals(instance.get(2), 2.0, 1e-12);
    assertEquals(instance.get(4), 4.0, 1e-12);

    // Test verifyEdges method (should not throw exception)
    instance.verifyEdges();
  }

  /**
   * Test of ArrayBinEdges verifyEdges with non-monotonic edges.
   */
  @Test(expectedExceptions = RebinException.class)
  public void testArrayBinEdges_NonMonotonicEdges() throws RebinException
  {
    double[] edges = {0.0, 1.0, 0.5, 3.0, 4.0}; // non-monotonic at index 2
    ArrayBinEdges instance = new ArrayBinEdges(edges);
    instance.verifyEdges(); // should throw RebinException
  }

  /**
   * Test of ScaledArrayBinEdges methods.
   */
  @Test
  public void testScaledArrayBinEdges() throws RebinException
  {
    double[] edges = {0.0, 1.0, 2.0, 3.0, 4.0};
    double scale = 2.5;
    ScaledArrayBinEdges instance = new ScaledArrayBinEdges(edges, scale);

    // Test size method
    assertEquals(instance.size(), 5);

    // Test get method (should return scaled values)
    assertEquals(instance.get(0), 0.0, 1e-12);
    assertEquals(instance.get(1), 2.5, 1e-12);
    assertEquals(instance.get(4), 10.0, 1e-12);

    // Test verifyEdges method (should not throw exception)
    instance.verifyEdges();
  }

  /**
   * Test of StepBinEdges methods.
   */
  @Test
  public void testStepBinEdges() throws RebinException
  {
    double start = 5.0;
    double step = 2.0;
    int length = 4;
    StepBinEdges instance = new StepBinEdges(start, step, length);

    // Test size method (should return length + 1)
    assertEquals(instance.size(), 5);

    // Test get method
    assertEquals(instance.get(0), 5.0, 1e-12);
    assertEquals(instance.get(1), 7.0, 1e-12);
    assertEquals(instance.get(4), 13.0, 1e-12);

    // Test verifyEdges method (should not throw exception)
    instance.verifyEdges();
  }

  /**
   * Test of createLinear method for StepBinEdges.
   */
  @Test
  public void testCreateLinear() throws RebinException
  {
    double begin = 0.0;
    double end = 10.0;
    int length = 5;
    StepBinEdges instance = StepBinEdges.createLinear(begin, end, length);

    // Test size
    assertEquals(instance.size(), 6);

    // Test beginning and ending edges
    assertEquals(instance.get(0), 0.0, 1e-12);
    assertEquals(instance.get(5), 10.0, 1e-12);

    // Test step size (should be (end-begin)/length = 2.0)
    assertEquals(instance.get(1) - instance.get(0), 2.0, 1e-12);
  }

  /**
   * Test of rebin method for double arrays.
   */
  @Test
  public void testRebin_doubleArr() throws RebinException
  {
    // Create input data: 5 values representing counts in bins
    double[] input = {10.0, 20.0, 30.0, 20.0, 10.0};

    // Input bins: 6 edges defining 5 bins
    double[] inputBins = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0};

    // Output bins: 4 edges defining 3 bins
    double[] outputBins = {0.0, 3.0, 7.0, 10.0};

    // Expected output: 3 values for the rebinned data
    double[] expResult = {20.0, 50.0, 20.0}; // calculated by hand

    // Run the rebin operation
    double[] result = RebinUtilities.rebin(input, inputBins, outputBins);

    // Verify results
    assertEquals(result.length, expResult.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], expResult[i], 1e-12);
    }
  }

  /**
   * Test of scale method.
   */
  @Test
  public void testScale() throws RebinException
  {
    // Create input data: linear ramp
    double[] input = {1.0, 2.0, 3.0, 4.0};
    double value = 2.0; // Scale factor

    // Expected result: stretched by factor of 2
    double[] expResult = {0.5, 0.5, 1.0, 1.0};

    // Run the scale operation
    double[] result = RebinUtilities.scale(input, value);

    // Verify results
    assertEquals(result.length, expResult.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], expResult[i], 1e-12);
    }
  }

  /**
   * Test of rescale method to change the number of bins.
   */
  @Test
  public void testRescale_doubleArr() throws RebinException
  {
    // Create input data: linear ramp
    double[] input = {10.0, 20.0, 30.0, 40.0};

    // Target number of channels (bins)
    int channels = 2;

    // Expected result: 4 channels reduced to 2 by rebinning
    double[] expResult = {30.0, 70.0};

    // Run the rescale operation
    double[] result = RebinUtilities.rescale(input, channels);

    // Verify results
    assertEquals(result.length, expResult.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], expResult[i], 1e-12);
    }
  }

  /**
   * Test of shift method.
   */
  @Test
  public void testShift() throws RebinException
  {
    // Create input data: linear ramp
    double[] input = {10.0, 20.0, 30.0, 40.0};

    // Shift by 1 channel towards higher channels
    double shift = 1.0;
    double[] result1 = RebinUtilities.shift(input, shift);

    // Expected result: shifted right by 1 bin
    double[] expResult1 = {0.0, 10.0, 20.0, 30.0};

    // Verify results for positive shift
    assertEquals(result1.length, expResult1.length);
    for (int i = 0; i < result1.length; i++)
    {
      assertEquals(result1[i], expResult1[i], 1e-12);
    }

    // Shift by -1 channel towards lower channels
    shift = -1.0;
    double[] result2 = RebinUtilities.shift(input, shift);

    // Expected result: shifted left by 1 bin
    double[] expResult2 = {20.0, 30.0, 40.0, 0.0};

    // Verify results for negative shift
    assertEquals(result2.length, expResult2.length);
    for (int i = 0; i < result2.length; i++)
    {
      assertEquals(result2[i], expResult2[i], 1e-12);
    }
  }

  /**
   * Test of collect method for integer arrays.
   */
  @Test
  public void testCollect_intArr() {
    // Create input data: counts in 10 bins
    int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    // Define channel edges to collect into 3 groups: [0-3), [3-7), [7-10)
    int[] channelEdges = {0, 3, 7, 10};

    // Expected result: sum of counts in each group
    int[] expResult = {6, 22, 27};

    // Run the collect operation
    int[] result = RebinUtilities.collect(data, channelEdges);

    // Verify results
    assertEquals(result.length, expResult.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], expResult[i]);
    }
  }

  /**
   * Test of collect method for double arrays.
   */
  @Test
  public void testCollect_doubleArr() {
    // Create input data: values in 10 bins
    double[] data = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};

    // Define channel edges to collect into 3 groups: [0-3), [3-7), [7-10)
    int[] channelEdges = {0, 3, 7, 10};

    // Expected result: sum of values in each group
    double[] expResult = {6.0, 22.0, 27.0};

    // Run the collect operation
    double[] result = RebinUtilities.collect(data, channelEdges);

    // Verify results
    assertEquals(result.length, expResult.length);
    for (int i = 0; i < result.length; i++)
    {
      assertEquals(result[i], expResult[i], 1e-12);
    }
  }
}