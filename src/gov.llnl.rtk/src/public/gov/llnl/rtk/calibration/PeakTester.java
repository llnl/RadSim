// --- file: gov/llnl/rtk/calibration/PeakTester.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.calibration.PeakFitter.PeakFitterResult;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.Serializable;

/**
 *
 * @author seilhan3
 */
@ReaderInfo(PeakTesterReader.class)
public interface PeakTester extends Serializable
{
  /**
   * Returns test results, cannot be null.
   *
   * @param spectrum
   * @param result
   * @return
   */
  PeakTesterResult testPeakFit(Spectrum spectrum, PeakFitterResult result);

  public interface PeakTesterResult extends Serializable
  {
    boolean isPassed();
  }
}
