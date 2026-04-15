// --- file: gov/llnl/rtk/test/SampleGeneratorActionSetEnergyScale.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.RebinUtilities;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.EnergyScale;

/**
 * Apply energy bins to this sample. FIXME this was replaced with
 * model.GammaDetector
 */
 class SampleGeneratorActionSetEnergyScale implements SampleGenerator.Action
{
  EnergyScale bins;

  public SampleGeneratorActionSetEnergyScale(EnergyScale bins)
  {
    this.bins = bins;
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
  {
    try
    {
      DoubleSpectrum accumulator = generator.getAccumulator();
      if (accumulator.getEnergyScale() == null)
      {
        if (accumulator.toArray() != null)
        {
          throw new SampleGenerator.SampleException("Attempt to rebin spectrum with unknown bins");
        }
        accumulator.setEnergyScale(bins);
        return accumulator;
      }
      // Rebin the existing data to fit.
      accumulator.rebinAssign(bins);
      return accumulator;
    }
    catch (RebinUtilities.RebinException ex)
    {
      throw new SampleGenerator.SampleException(ex);
    }
  }

}
