// --- file: gov/llnl/rtk/test/SampleGeneratorActionInclude.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.MathExceptions;
import gov.llnl.math.RebinUtilities;
import gov.llnl.rtk.data.DoubleSpectrum;

/**
 * Adds a spectrum to the sample adjusting the rate by time.
 */
 class SampleGeneratorActionInclude implements SampleGenerator.Action
{
  final SampleGenerator.Action action;
  final SampleGenerator.ScalingRule scalingRule;

  SampleGeneratorActionInclude(SampleGenerator.Producer action, SampleGenerator.ScalingRule scalingRule)
  {
    this.action = action;
    this.scalingRule = scalingRule;
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
  {
    try
    {
      DoubleSpectrum accumulator = generator.getAccumulator();
      if (accumulator == null)
        throw new SampleGenerator.SampleException("Must have a base spectrum");
      DoubleSpectrum sample = action.evaluate(generator);
      sample.rebinAssign(accumulator.getEnergyScale());
      double scaling = scalingRule.computeScale(sample, accumulator);
      sample.multiplyAssign(scaling);
      sample.setLiveTime(0);
      sample.setRealTime(0);
      return accumulator.addAssign(sample);
    }
    catch (RebinUtilities.RebinException | MathExceptions.SizeException ex)
    {
      throw new SampleGenerator.SampleException(ex);
    }
  }

}
