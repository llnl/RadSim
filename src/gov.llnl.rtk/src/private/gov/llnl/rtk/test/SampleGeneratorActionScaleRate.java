// --- file: gov/llnl/rtk/test/SampleGeneratorActionScaleRate.java ---
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
 *
 * @author nelson85
 */
 class SampleGeneratorActionScaleRate implements SampleGenerator.Action
{
  Number rate;

  SampleGeneratorActionScaleRate(Number rate)
  {
    this.rate = rate;
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
  {
    DoubleSpectrum accumulator = generator.getAccumulator();
    double[] values = accumulator.toArray();
    DoubleArray.normColumns1(values);
    DoubleArray.multiplyAssign(values, rate.doubleValue() * accumulator.getLiveTime());
    return accumulator;
  }

}
