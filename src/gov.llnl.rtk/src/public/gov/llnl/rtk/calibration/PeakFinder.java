// --- file: gov/llnl/rtk/calibration/PeakFinder.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.Serializable;

/**
 *
 * @author canion1
 */
@ReaderInfo(PeakFinderReader.class)
public interface PeakFinder
{

  /**
   * Does the finder think we have enough information to proceed based on
   * current configuration
   *
   * @param spectrum
   * @return
   */
  PeakFinderResult initialSearch(Spectrum spectrum);

//    void setFitter(PeakFitter fitter);
//    boolean testSearch(double[] spectrum, double currentEstimate);
  void setExpectedChannel(double channel);

//    public boolean testSearch(double[] data);
  public interface PeakFinderResult extends Serializable
  {
    double getPeakLocation();
  }
}
