// --- file: gov/llnl/rtk/calibration/EventPeakTracker.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.InitializeException;

/**
 *
 * Class designed to take a background spectrum from an event and extract
 * calibration parameters.
 * @author mcferran4
 */
public class EventPeakTracker
{

  public int total = 0;
  public double peakLocation = 0;
  double location;
  double gain;
  public boolean locked = false;

  
  StabilizerTarget target = null;
  PeakFinder finder = null;
  PeakTester tester = null;
  PeakFitter fitter = null;
  
    // Constructor
  public EventPeakTracker( ){
  
  }
          
  
  // Constructor
  public EventPeakTracker(StabilizerTarget target,
                        PeakFinder finder,
                        PeakTester tester,
                        PeakFitter fitter
                        )
  {
    this.target = target;
    this.finder = finder;
    this.tester = tester;
    this.fitter = fitter;
  }
  
      /**
   * @return the target
   */
  public StabilizerTarget getTarget()
  {
    return target;
  }

  /**
   * @param finder the finder to set
   */
  public void setFinder(PeakFinder finder)
  {
    this.finder = finder;
  }

  /**
   * @param tester the tester to set
   */
  public void setTester(PeakTester tester)
  {
    this.tester = tester;
  }

  /**
   * @param fitter the fitter to set
   */
  public void setFitter(PeakFitter fitter)
  {
    this.fitter = fitter;
  }

  
  public void initialize() throws InitializeException
  {
    peakLocation = 0;
    if (target == null)
      throw new InitializeException("Target not set.");

    if (tester == null)
      throw new InitializeException("Tester not set.");

    if (fitter == null)
      throw new InitializeException("Fitter not set.");
    
    if (finder == null)
    {
      if (fitter instanceof PeakFinder)
        this.setFinder((PeakFinder) fitter);
      else
      {
        throw new InitializeException("Peak Finder is not set.");
      }
    }
  }
  
  /**
   * Check if peak tracking fails are below a threshold
   * @return 
   */
  public boolean isLocked()
  {
    return locked;
  }

  /**
   * Incorporate the Peak Tracker
   * 1. Find the initial Peak 
   * 2. Lock onto peak using a fitter.
   * 3. Check fitted peak intensity. 
   * 
   * @param spectrum 
   */
  public void evaluate(IntegerSpectrum spectrum)
  {
    locked = false;

    // find the initial peak estimate
    getFinder().setExpectedChannel(target.getChannel());
    PeakFinder.PeakFinderResult peakFinderResult = getFinder().initialSearch(spectrum);
    if (peakFinderResult == null)
      return;
    
    
    // Fit the peak
    double foundPeakLocation = peakFinderResult.getPeakLocation();
    getFitter().setExpectedChannel(foundPeakLocation);
    PeakFitter.PeakFitterResult peakFitterResult = getFitter().fitPeak(spectrum);
    if (peakFitterResult == null)
        return;
    
     // Test if fit peak is a given amount above background
    test(spectrum, peakFitterResult);
    return;
    
  }


  public double getPeakLocation()
  {
    return getLocation();
  }

  public PeakFinder getFinder()
  {
    return finder;
  }

  public PeakFitter getFitter()
  {
    return fitter;
  }

  public PeakTester getTester()
  {
    return tester;
  }
  
    /**
   * @return the location
   */
  public double getLocation()
  {
    return location;
  }

  /**
   * @return the gain
   */
  public double getGain()
  {
    return gain;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(double location)
  {
    this.location = location;
  }

  /**
   * @param gain the gain to set
   */
  public void setGain(double gain)
  {
    this.gain = gain;
  }

  public void setTarget(StabilizerTarget target)
  {
    this.target = target;
  }

  /**
   * Test to make sure fit peak is a certain fraction of total counts
   * @param spectrum
   * @param peakFitterResult 
   */
  private void test(Spectrum spectrum, PeakFitter.PeakFitterResult peakFitterResult)
  {

    PeakTester.PeakTesterResult peakTesterResult = getTester().testPeakFit(spectrum, peakFitterResult);
    locked = peakTesterResult.isPassed();

    if (locked == true)
    {
      setLocation(peakFitterResult.getPeakLocation());
      setGain(getLocation() / getTarget().getChannel());
    }
  }


}
