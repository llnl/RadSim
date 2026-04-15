// --- file: gov/llnl/rtk/calibration/PeakFitter.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.calibration.PeakFinder.PeakFinderResult;
import gov.llnl.rtk.data.Spectrum;
import java.io.Serializable;

/**
 * Peak fitters extract parameters from a peak in a measured spectrum. The
 * location of the peak must be sufficiently well known such that the peak
 * tracker will not lock onto some neighboring feature. To help with this the
 * expected peak location should be set.
 *
 * Individual implementation require addition peak parameters such as the
 * expected width of the peak or the characteristics of the continuum. These
 * requirements will be defined in the peak fitter implementation.
 *
 * @author nelson85
 */
public interface PeakFitter extends Serializable
{
  /**
   * Compute the channel at the center of the peak. May also estimate the
   * intensity and the width of the peak. (optional)
   *
   * @param spectrum that contains the peak to find
   * @return the center channel of the peak.
   */
  PeakFitterResult fitPeak(Spectrum spectrum);

  /**
   * Set the channel in which the peak is expected to be located. This value is
   * used to seed the search. The actual peak location should be within a few
   * sigma of the expected for successful extraction.
   *
   * @param channel
   */
  void setExpectedChannel(double channel);

  /**
   * Structure containing the analysis to extract a peak. This will contain
   * additional fields depending on the peak fitter implementation.
   */
  public interface PeakFitterResult extends Serializable, PeakFinderResult
  {
    /**
     * Get the integrated counts under the continuum in the region of the peak.
     *
     * @return the total counts under the peak in the continuum.
     */
    double getIntegratedContinuum();

    /**
     * Get the integrated counts in the peak.
     *
     * @return
     */
    double getPeakIntensity();

    /**
     * Get the total intensity of the region analyzed. FIXME This is somewhat
     * nebulously defined as not all implementations have the same concept of a
     * region of interest for analysis.
     *
     * @return
     */
    double getIntegratedIntensity();

    /**
     * Get the location of the peak.
     *
     * FIXME is this in edge or center origin space. This should be defined.
     *
     * @return
     */
    @Override
    double getPeakLocation();
  }
}
