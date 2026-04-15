// --- file: gov/llnl/rtk/calibration/NonlinearRebin.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.random.BinomialRandom;
import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.utility.annotation.Debug;

/**
 *
 * @author nelson85
 */
public class NonlinearRebin
{
  transient BinomialRandom brand = new BinomialRandom();

  /**
   * Specialized integer rebin for converting from nonlinearly distorted.
   *
   * @param spectrum
   * @param inputEdges
   * @return a new spectrum with the requested energy bins.
   */
  public IntegerSpectrum rebin(IntegerSpectrum spectrum, double[] inputEdges)
  {
    int N = inputEdges.length;
    int inChannelMin = spectrum.getMinimumValidChannel();
    int inChannelMax = spectrum.getMaximumValidChannel();

    IntegerSpectrum output = new IntegerSpectrum(spectrum);
    int outputChannels = N - 1;

    int[] outputCounts = new int[outputChannels];
    int underrange = 0;
    int overrange = 0;

    // Okay so input channels are the predicted edges based on the observables
    // we need to transform the data into original laboratory referencce frame
    // so that the templates which have been mapped from energy to channel 
    // space can be directly compared.
    // Integer wise probabalistic rebining
    int[] inputCounts = spectrum.toArray();
    for (int i = 0; i < N - 1; i++)
    {
      double channelStart = inputEdges[i];
      double channelEnd = inputEdges[i + 1];
      int counts = inputCounts[i];

      // if we have no counts, no need to consider anything
      if (counts == 0)
      {
        continue;
      }

      if (channelStart >= outputCounts.length || i >= inChannelMax)
      {
        overrange += counts;
        continue;
      }

      if (channelEnd <= 0 || i < inChannelMin)
      {
        underrange += counts;
        continue;
      }

      // Okay we need to push those counts over the specified range of channels.
      // Case 1: all of the counts belong in the same channel
      if ((int) channelStart == (int) channelEnd)
      {
        int bin = (int) channelStart;
        if (bin < 0)
        {
          underrange += counts;
        }
        else
        {
          outputCounts[(int) channelStart] += counts;
        }
        continue;
      }

      // Case 2: split the counts between to channels
      if ((int) channelStart + 1 == (int) channelEnd)
      {
        int split = (int) channelEnd;
        double p = (split - channelStart) / (channelEnd - channelStart);
        // Take Binomial draw with probability of P to go in the first bin
        int c0 = (int) brand.newVariable(counts, p).next();
        int c1 = counts - c0;

        if (split - 1 < 0)
        {
          underrange += c0;
        }
        else
        {
          outputCounts[split - 1] += c0;
        }

        if (split >= outputCounts.length)
        {
          overrange += c1;
        }
        else
        {
          outputCounts[split] += c1;
        }
        continue;
      }

      // Case 3, we are splitting over more than 2 channels.  
      // This is a very hard case to do efficienctly
      // Drawing repeated binomials biases the distribution.
      // Method 1 (small number of counts)
      if (counts < 10)
      {
        // For small number of counts, we can just draw from a uniform 
        // distribution to place the counts
        double range = channelEnd - channelStart;
        for (int j = 0; j < counts; ++j)
        {
          double d = channelStart + range * brand.getGenerator().nextDouble();
          int c = (int) d;
          if (d < 0)
          {
            underrange++;
          }
          else if (d >= outputCounts.length)
          {
            overrange++;
          }
          else
          {
            outputCounts[c]++;
          }
        }
        continue;
      }

      // We will just use the binomial method 
      double q1 = channelStart;
      int q2 = (int) channelStart + 1;
      while (q2 <= channelEnd)
      {
        int c = (int) q1;
        double p = (q2 - q1) / (channelEnd - channelStart);
        int u = (int) brand.newVariable(counts, p).next();
        counts -= u;
        if (u < 0)
        {
          underrange += u;
        }
        else if (c >= outputCounts.length)
        {
          break;
        }
        else
        {
          outputCounts[c] += u;
        }
        q1 = q2;
        q2++;
      }

      int c = (int) channelEnd;
      if (c < outputCounts.length)
      {
        outputCounts[c] += counts;
      }
      else
      {
        overrange += counts;
      }
    }

    // Copy the counts into the spectrum.
    output.assign(outputCounts);

    // Bookkeeping to manage valid ranges
    output.setUnderRange(overrange + (int) spectrum.getOverRangeCounts());
    output.setOverRange(underrange + (int) spectrum.getUnderRangeCounts());
    int c1 = spectrum.getMinimumValidChannel();
    int c2 = spectrum.getMaximumValidChannel();
    output.setMinimumValidChannel((int) inputEdges[c1]);
    output.setMaximumValidChannel((int) inputEdges[c2]);

    // Deal with computing the valid range in the resulting spectrum
    return output;
  }

  public void setGenerator(RandomGenerator rg)
  {
    this.brand.setGenerator(rg);
  }
}
