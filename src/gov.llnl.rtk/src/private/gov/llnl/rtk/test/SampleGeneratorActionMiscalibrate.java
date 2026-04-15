// --- file: gov/llnl/rtk/test/SampleGeneratorActionMiscalibrate.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.RebinUtilities;
import gov.llnl.math.random.NormalRandom;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;

/**
 *
 * @author nelson85
 */
 class SampleGeneratorActionMiscalibrate implements SampleGenerator.Action
{
  double offsetStd;
  double gainStd;

  SampleGeneratorActionMiscalibrate(double offsetStd, double gainStd)
  {
    this.offsetStd = offsetStd;
    this.gainStd = gainStd;
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
  {
    try
    {
      DoubleSpectrum accumulator = generator.getAccumulator();
      if (accumulator.getEnergyScale() == null)
      {
        throw new SampleGenerator.SampleException("Attempt to rebin spectrum with unknown bins");
      }
      EnergyScale bins = accumulator.getEnergyScale();
      EnergyScale bins2 = EnergyScaleFactory.newScale(bins.getEdges());
      double offsetError = new NormalRandom(generator.getRandomGenerator()).newVariable(0, offsetStd).next();
      double gainError = new NormalRandom(generator.getRandomGenerator()).newVariable(1, gainStd).next();
      double[] v1 = bins2.getEdges();
      for (int i = 0; i < v1.length; ++i)
      {
        v1[i] = gainError * v1[i] + offsetError;
      }
      // Rebin the existing data to fit.
      accumulator.rebinAssign(bins2);
      accumulator.setEnergyScale(bins);
      return accumulator;
    }
    catch (RebinUtilities.RebinException ex)
    {
      throw new SampleGenerator.SampleException(ex);
    }
  }

}
