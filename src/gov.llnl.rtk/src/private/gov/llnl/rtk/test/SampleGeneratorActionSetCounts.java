// --- file: gov/llnl/rtk/test/SampleGeneratorActionSetCounts.java ---
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
 class SampleGeneratorActionSetCounts implements SampleGenerator.Action
{
  Number counts;

  SampleGeneratorActionSetCounts(Number counts)
  {
    this.counts = counts;
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
  {
    DoubleSpectrum accumulator = generator.getAccumulator();
    double[] values = accumulator.toArray();
    DoubleArray.normColumns1(values);
    DoubleArray.multiplyAssign(values, counts.doubleValue());
    return accumulator;
  }

}
