// --- file: gov/llnl/rtk/test/SampleGeneratorActionScaleCounts.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.DoubleSpectrum;

/**
 * SampleGeneratorActionScaleCounts the counts in the spectrum. Does not adjust
 * the times.
 */
class SampleGeneratorActionScaleCounts implements SampleGenerator.Action
{
  Number factor;

  public SampleGeneratorActionScaleCounts(Number factor)
  {
    this.factor = factor;
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
  {
    DoubleSpectrum accumulator = generator.getAccumulator();
    if (accumulator == null)
    {
      throw new SampleGenerator.SampleException("Accumulator spectrum is null");
    }
    DoubleArray.multiplyAssign(accumulator.toArray(), factor.doubleValue());
    return accumulator;
  }

}
