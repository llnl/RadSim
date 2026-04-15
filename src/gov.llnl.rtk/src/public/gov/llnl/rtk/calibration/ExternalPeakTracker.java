// --- file: gov/llnl/rtk/calibration/ExternalPeakTracker.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.utility.InitializeException;

/**
 *
 * @author nelson85
 */
public class ExternalPeakTracker implements PeakTracker
{
  double peakLocation = 0;

  @Override
  public void initialize() throws InitializeException
  {
    peakLocation = 0;
  }

  @Override
  public void incorporate(IntegerSpectrum spectrum)
  {
    // does nothing
  }

  @Override
  public void clearHistory()
  {
    peakLocation = 0;
  }

  @Override
  public boolean isLocked()
  {
    return peakLocation != 0;
  }

  @Override
  public double getPeakLocation()
  {
    return peakLocation;
  }

  public void setPeakLocation(double d)
  {
    peakLocation = d;
  }

  @Override
  public void setTarget(StabilizerTarget target)
  {
  }

  @Override
  public PeakFinder getFinder()
  {
    return null;
  }

  @Override
  public PeakFitter getFitter()
  {
    return null;
  }

  @Override
  public PeakTester getTester()
  {
    return null;
  }
}

/*
Typical usage:

Configuration:
  <calibration>
    <singlePeakGainStabilizer>
      <target units="channels">269</target>
      <externalPeakTracker id="calibration.external"/>
    </singlePeakGainStabilizer>
  </calibration>

Loading:
  DataProcessor dp=new DataProcessor();
  DataProcessorReader dpr=new DataProcessorReader();
  dpr.load(dp, new File("config.xml"));
  ExternalPeakTracker ept=dpr.getContext().get("calibration.external", ExternalPeakTracker.class);

 */
