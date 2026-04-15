// --- file: gov/llnl/rtk/test/SampleGeneratorActionSetTimes.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.rtk.data.DoubleSpectrum;

/**
 *
 * @author nelson85
 */
class SampleGeneratorActionSetTimes implements SampleGenerator.Action
{
  Number realTime;
  Number liveTime;

  SampleGeneratorActionSetTimes(Number realtime, Number livetime)
  {
    super();
    this.realTime = realtime;
    this.liveTime = livetime;
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
  {
    DoubleSpectrum accumulator = generator.getAccumulator();
    accumulator.setRealTime(realTime.doubleValue());
    accumulator.setLiveTime(liveTime.doubleValue());
    return accumulator;
  }

}
