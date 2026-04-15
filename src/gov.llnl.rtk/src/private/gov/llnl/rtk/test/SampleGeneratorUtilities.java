// --- file: gov/llnl/rtk/test/SampleGeneratorUtilities.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.Spectrum;

/**
 *
 * @author nelson85
 */
class SampleGeneratorUtilities
{
  // Impl
  static public IntegerSpectrum convert(Spectrum<double[]> sample) throws SampleGenerator.SampleException
  {
    // Apply the conversion
    IntegerSpectrum out = new IntegerSpectrum();
    out.copyAttributes(sample);
    out.setEnergyScale(sample.getEnergyScale());
    out.setLiveTime(sample.getLiveTime());
    out.setRealTime(sample.getRealTime());
    out.setMinimumValidChannel(sample.getMinimumValidChannel());
    out.setMaximumValidChannel(sample.getMaximumValidChannel());

    double[] v = sample.toDoubles();
    out.resize(v.length);
    int[] w = out.toArray();
    for (int i = 0; i < v.length; ++i)
    {
      if ((int) v[i] != v[i])
        throw new SampleGenerator.SampleException("Poisson not called");
      w[i] = (int) v[i];
    }
    double u1 = sample.getOverRangeCounts();
    double u2 = sample.getUnderRangeCounts();
    out.setOverRange((int) u1);
    out.setUnderRange((int) u2);
    return out;
  }

//  static public DoubleSpectrum add(DoubleSpectrum accumulator, DoubleSpectrum sample)
//          throws SampleGenerator.SampleException
//  {
//    try
//    {
//      // If we don't have an accumulator, then just use this sample
//      if (accumulator == null)
//        return sample;
//
//      // Otherwise, match bins and add it.
//      sample.rebin(accumulator.getEnergyScale());
//      accumulator.addAssign(sample);
//      return accumulator;
//    } catch (MathExceptions.SizeException | RebinUtilities.RebinException ex)
//    {
//      throw new SampleGenerator.SampleException(ex);
//    }
//  }
  /**
   * Compute the ideal weighted SNR for a sample and a background. The weighted
   * SNR is defined as the \( \displaystyle SNR_W(S,B)=\sum_i
   * \frac{s_i^2}{b_i+\lambda } \)
   *
   * @param sample
   * @param background
   * @return weighted SNR.
   */
  public static double computeWeightedSNR(Spectrum sample, Spectrum background)
  {
    double[] S = sample.toDoubles();
    double[] B = background.toDoubles();
    double sum = 0;
    for (int i = 0; i < S.length; ++i)
      sum += (S[i] * S[i]) / (B[i] + 0.1);
    return Math.sqrt(sum);
  }
}
