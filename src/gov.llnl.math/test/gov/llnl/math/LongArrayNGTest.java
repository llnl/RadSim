/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import gov.llnl.math.random.Random48;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.Test;
import gov.llnl.math.random.RandomGenerator;

/**
 * Test code for LongArray.
 */
public class LongArrayNGTest
{
  long seed = 1L;
  RandomGenerator rg=new Random48(seed);
  
  public LongArrayNGTest()
  {
  }
  
  /**
   * Test constructor of class LongArray.
   */
  @Test
  public void testConstructor()
  {
    LongArray instance = new LongArray();
  }

  /**
   * Test of toPrimitives method, of class LongArray.
   */
  @Test
  public void testToPrimitives()
  {
    List<Long> input=new ArrayList<Long>();
    for (int i=0;i<100;i++)
      input.add(Double.doubleToLongBits(rg.nextDouble()));
    long[] out=LongArray.toPrimitives(input);
    for (int i=0;i<out.length;++i)
      Assert.assertEquals((long)input.get(i), out[i]);
  }
  
}
