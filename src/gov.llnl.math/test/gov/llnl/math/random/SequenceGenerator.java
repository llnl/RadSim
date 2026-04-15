/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.random;

/**
 * Test harness for making random number generators deterministic.
 * 
 * @author nelson85
 */
public class SequenceGenerator implements RandomGenerator
{
  int indexDouble;
  double[] doubleValues;
  int indexInt;
  int[] intValues;

  @Override
  public void setSeed(long seed)
  {
  }

  @Override
  public int nextInt()
  {
    int out = this.intValues[indexInt++];
    if (indexInt >= this.intValues.length)
      indexInt = 0;
    return out;
  }

  @Override
  public double nextDouble()
  {
    double out = this.doubleValues[indexDouble++];
    if (indexDouble >= this.doubleValues.length)
      indexDouble = 0;
    return out;
  }

  static RandomGenerator ofInts(int... v)
  {
    SequenceGenerator out = new SequenceGenerator();
    out.intValues = v;
    return out;
  }

  static RandomGenerator ofDoubles(double... v)
  {
    SequenceGenerator out = new SequenceGenerator();
    out.doubleValues = v;
    return out;
  }
  
}
