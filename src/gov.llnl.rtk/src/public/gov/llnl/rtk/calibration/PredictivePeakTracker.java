// --- file: gov/llnl/rtk/calibration/PredictivePeakTracker.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import static gov.llnl.math.TimeUtilities.convertTimeConstant;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.InitializeException;
import gov.llnl.utility.annotation.Debug;
import gov.llnl.utility.xml.bind.ReaderInfo;

/**
 *
 * @author nelson85
 */
@ReaderInfo(PredictivePeakTrackerReader.class)
public class PredictivePeakTracker implements PeakTracker
{
  // Parameters
  int decimation = 30;
  double meanTimeConstant = 60.0;
  double biasTimeConstant = 300.0;
  double spectralTimeConstant1 = 60; // time window short
  double spectralTimeConstant2 = 1200;  // time window long
  PeakFitter fitter = null;
  StabilizerTarget target = null;
  PeakFinder finder = null;
  // State variables
  int total = 0;
  double mean = 0;
  double bias = 0;
  double alpha;
  double beta;
  Accumulator accumulator0;
  Smoothing accumulator1;
  Smoothing accumulator2;
  boolean locked = false;
  double biasFactor;
  double trust = 15;
  PeakTester tester;
  int maxFailCount = 4;
  int currentFailCount;
  double location;

  @Override
  public void clearHistory()
  {
    double expectedChannel = this.target.getChannel();

    alpha = convertTimeConstant(biasTimeConstant, decimation);
    beta = convertTimeConstant(meanTimeConstant, decimation);
    accumulator0 = new Accumulator(decimation);
    accumulator1 = new Smoothing(convertTimeConstant(spectralTimeConstant1, decimation));
    accumulator2 = new Smoothing(convertTimeConstant(spectralTimeConstant2, decimation));
    fitter.setExpectedChannel(expectedChannel);
    mean = expectedChannel;
    bias = 0;
    total = 0;
    locked = false;
    biasFactor = 1 / (1 - spectralTimeConstant1 / spectralTimeConstant2);
    peaks1 = expectedChannel;
    peaks2 = expectedChannel;
    currentFailCount = maxFailCount;
    location = expectedChannel;
  }

  @Override
  public double getPeakLocation()
  {
    return location;
  }

  @Override
  public void incorporate(IntegerSpectrum sample)
  {
    // Initialize structures
    if (total == 0)
    {
      // this will push the target channel into the fitter and finder
      clearHistory();
    }
    if (total < spectralTimeConstant2)
      total++;

    // Warmup period
    if (total < spectralTimeConstant1)
    {
      accumulator0.add(sample);
      // determining if time to update
      if (decimation > 0 && total % decimation != 0)
        return;

      accumulator1.initialize(accumulator0.data);
      accumulator2.initialize(accumulator0.data);

      // find
      finder.setExpectedChannel(this.target.getChannel());
      PeakFinder.PeakFinderResult peakFinderResult = finder.initialSearch(accumulator0.data);
      if (peakFinderResult == null)
        return;

      // fit
      fitter.setExpectedChannel(peakFinderResult.getPeakLocation());
      PeakFitter.PeakFitterResult peakFitterResult = fitter.fitPeak(accumulator0.data);
      if (peakFitterResult == null)
        return;

      bias = 0;
      mean = peakFitterResult.getPeakLocation();

      // test
      test(accumulator0.data, peakFitterResult);
      return;
    }

    // Decimate if needed
    if (decimation > 1)
    {
      if (accumulator0.add(sample) == true)
      {
        sample = accumulator0.get();
      }
      else
      {
        return;
      }
    }

    // update the short term buffers - accumulator1 is looking for short term activity
    accumulator1.add(sample);
    // update the remaining buffers - accumulator2 is looking for long term activity

    accumulator2.add(sample);

    PeakFitter.PeakFitterResult peaks1result = fitter.fitPeak(accumulator1.data);
    PeakFitter.PeakFitterResult peaks2result = fitter.fitPeak(accumulator2.data);

    if (peaks1result == null || peaks2result == null)
    {
      total = 0;
      return;
    }

    peaks1 = peaks1result.getPeakLocation();
    peaks2 = peaks2result.getPeakLocation();

    //double exponential smoothing filter
    if (total < spectralTimeConstant2 / 2)
      mean = peaks2;
    else
      mean = (1 - beta) * mean + beta * peaks2;
    double err = biasFactor * (mean - peaks1);
    if (Math.abs(err) < trust)
      bias = (1 - alpha) * bias + alpha * err;

    test(accumulator2.data, peaks2result);

    // Copy back to speed up tracking if the buffer is full
    if (locked && total > spectralTimeConstant1)
      fitter.setExpectedChannel(peaks2);
  }

  @Debug public double peaks1;
  @Debug public double peaks2;

  /**
   * @param decimation the decimation to set
   */
  public void setDecimation(int decimation)
  {
    this.decimation = decimation;
  }

  /**
   * @param meanTimeConstant the meanTimeConstant to set
   */
  public void setMeanTimeConstant(double meanTimeConstant)
  {
    this.meanTimeConstant = meanTimeConstant;
  }

  /**
   * @param biasTimeConstant the biasTimeConstant to set
   */
  public void setBiasTimeConstant(double biasTimeConstant)
  {
    this.biasTimeConstant = biasTimeConstant;
  }

  /**
   * @param spectralTimeConstant1 the spectrumTimeConstant1 to set
   */
  public void setSpectralTimeConstant1(double spectralTimeConstant1)
  {
    this.spectralTimeConstant1 = spectralTimeConstant1;
  }

  /**
   * @param spectralTimeConstant2 the spectrumTimeConstant2 to set
   */
  public void setSpectralTimeConstant2(double spectralTimeConstant2)
  {
    this.spectralTimeConstant2 = spectralTimeConstant2;
  }

  /**
   * @param fitter the peak fitter to use
   */
  public void setFitter(PeakFitter fitter)
  {
    this.fitter = fitter;
  }

  public void setFinder(PeakFinder finder)
  {
    this.finder = finder;
  }

  public void setTester(PeakTester tester)
  {
    this.tester = tester;
  }

  @Override
  public PeakFinder getFinder()
  {
    return this.finder;
  }

  @Override
  public void initialize() throws InitializeException
  {
    if (this.spectralTimeConstant1 > this.spectralTimeConstant2)
    {
      throw new InitializeException("Time constant 1 is greater than time constant 2.");
    }
    if (this.target == null)
      throw new InitializeException("Target not set.");

    if (this.tester == null)
      throw new InitializeException("Tester not set.");

    if (this.fitter == null)
      throw new InitializeException("Fitter not set.");

    if (this.finder == null)
    {
      if (this.fitter instanceof PeakFinder)
        this.finder = (PeakFinder) this.fitter;
      else
      {
        throw new InitializeException("Peak Finder is not set.");
      }
    }

    this.currentFailCount = this.maxFailCount;

  }

  @Override
  public void setTarget(StabilizerTarget target)
  {
    this.target = target;
  }

  /**
   * Get if the system is locked. This has a hysteresis to prevent problems.
   *
   * @return
   */
  @Override
  public boolean isLocked()
  {
    return currentFailCount < maxFailCount;
  }

  private void test(Spectrum data, PeakFitter.PeakFitterResult peakFitterResult)
  {
    if (currentFailCount < maxFailCount)
      currentFailCount++;

    PeakTester.PeakTesterResult peakTesterResult = tester.testPeakFit(data, peakFitterResult);
    locked = peakTesterResult.isPassed();

    if (locked == true)
    {
      currentFailCount = 0;
      this.location = mean - bias;
    }
  }

  @Override
  public PeakFitter getFitter()
  {
    return this.fitter;
  }

  @Override
  public PeakTester getTester()
  {
    return this.tester;
  }
}
