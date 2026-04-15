/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import gov.llnl.math.random.Random48;
import java.util.Arrays;
import org.testng.Assert;
import org.testng.annotations.Test;
import gov.llnl.math.random.RandomGenerator;

/**
 * Test code for Fourier.
 */
public class FourierNGTest
{
  long seed = 1L;
  RandomGenerator random = new Random48(seed);

  public FourierNGTest()
  {
  }

  /**
   * Test of fftRadix2 method, of class Fourier.
   */
  @Test
  public void testFft_FourierResult_doubleArr()
  {
  }

  /**
   * Test of fftRadix2 method, of class Fourier.
   */
  @Test
  public void testFft_doubleArr()
  {
  }

  /**
   * Test of shuffle method, of class Fourier.
   */
  @Test
  public void testShuffle()
  {
  }

  /**
   * Test of binlog method, of class Fourier.
   */
  @Test
  public void testBinlog()
  {
    for (int i = 0; i < 100; ++i)
    {
      int u = random.nextInt();
      int u1 = random.nextInt() % 32;
      u >>>= u1;
      if (u < 0)
        u = -u;
      if (u == 0)
        u = 1;
      Assert.assertEquals(FourierUtilities.binlog(u), (int) (Math.log(u) / Math.log(2)), " " + u);
    }
  }

  /**
   * Test of bitswap method, of class Fourier.
   */
  @Test
  public void testBitswap()
  {
    for (int i = 1; i < 16; ++i)
      Assert.assertEquals(FourierUtilities.bitswap(1, i), (1 << (i - 1)));
    int[] list = new int[16];
    for (int i = 0; i < 16; ++i)
      list[i] = FourierUtilities.bitswap(i, 4);
    Arrays.sort(list);
    for (int i = 0; i < 16; ++i)
      Assert.assertEquals(list[i], i);
  }

}
