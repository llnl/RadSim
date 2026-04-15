// --- file: gov/llnl/rtk/test/SampleGeneratorActionScaleAll.java ---
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
 * Scale the counts in the spectrum and the time.
 */
class SampleGeneratorActionScaleAll implements SampleGenerator.Action
{
  Number factor;

  public SampleGeneratorActionScaleAll(Number factor)
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
    accumulator.setLiveTime(accumulator.getLiveTime() * factor.doubleValue());
    accumulator.setRealTime(accumulator.getRealTime() * factor.doubleValue());
    return accumulator;
  }

}
