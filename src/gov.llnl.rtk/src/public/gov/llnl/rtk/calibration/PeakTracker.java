// --- file: gov/llnl/rtk/calibration/PeakTracker.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.utility.InitializeInterface;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
@ReaderInfo(PeakTrackerReader.class)
public interface PeakTracker extends InitializeInterface, Serializable
{
  boolean isLocked();

  /**
   * Incorporate a new measurement to update the peak estimate.
   *
   * @param spectrum is the new measurement.
   */
  void incorporate(IntegerSpectrum spectrum);

  /**
   * Clear the history and reset the state.
   */
  void clearHistory();

  /**
   * Returns the most recent estimate of the peak location.
   *
   * @return the last peak location.
   */
  double getPeakLocation();

  /**
   *
   * @return peak finder
   */
  PeakFinder getFinder();

  PeakFitter getFitter();

  PeakTester getTester();

  /**
   * Set the expected peak location
   *
   * @param target
   */
  void setTarget(StabilizerTarget target);

}
