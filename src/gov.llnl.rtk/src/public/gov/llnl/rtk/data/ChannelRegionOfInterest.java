// --- file: gov/llnl/rtk/data/ChannelRegionOfInterest.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.xml.bind.ReaderInfo;

/**
 *
 * @author nelson85
 */
@ReaderInfo(ChannelRegionOfInterestReader.class)
public interface ChannelRegionOfInterest extends RegionOfInterest
{
  /**
   * Get the lower channel of this region of interest.
   *
   * @return
   */
  double getLowerChannel();

  /**
   * Get the upper channel for this region of interest.
   *
   * @return
   */
  double getUpperChannel();

  /**
   * Get the starting channel which contains valid data.
   * @return
   */
  default int getStart()
  {
    return (int)Math.ceil(getLowerChannel());
  }

  /**
   * Get one past the ending channel containing valid data.
   * @return
   */
  default int getEnd()
  {
    return (int)Math.ceil(getUpperChannel());
  }

  /**
   * Creates an instance of a ChannelRegionOfInterest
   *
   * @param lower
   * @param upper
   * @return
   */
  public static ChannelRegionOfInterest of(double lower, double upper)
  {
    return new ChannelRegionOfInterestImpl(lower, upper);
  }

}
