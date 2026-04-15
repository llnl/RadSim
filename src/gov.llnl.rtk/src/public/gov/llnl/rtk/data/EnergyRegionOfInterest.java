// --- file: gov/llnl/rtk/data/EnergyRegionOfInterest.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;

/**
 *
 * @author nelson85
 */
@ReaderInfo(EnergyRegionOfInterestReader.class)
@WriterInfo(EnergyRegionOfInterestWriter.class)
public interface EnergyRegionOfInterest extends RegionOfInterest
{

  /** 
   * Get the lower energy of the region of interest.
   * 
   * This energy is considered inclusive.
   * 
   * @return 
   */
  double getLowerEnergy();

  /** 
   * Get the upper energy of the region of interest.
   * 
   * This energy is considered exclusive.
   * 
   * @return 
   */
  double getUpperEnergy();

  /**
   * Returns true if the energy is within the region of interest.
   *
   * @param energy
   * @return
   */
  default boolean contains(double energy)
  {
    if (energy >= this.getUpperEnergy())
      return false;
    return energy >= this.getLowerEnergy();
  }

  /** 
   * Construct a new energy window using the default implementation.
   * 
   * @param lowerEnergy
   * @param upperEnergy
   * @return 
   */
  public static EnergyRegionOfInterest of(double lowerEnergy, double upperEnergy)
  {
    return new EnergyRegionOfInterestImpl(lowerEnergy, upperEnergy);
  }

  /**
   * Convert an energy region to channels.
   *
   * @param es
   * @return
   */
  public default ChannelRegionOfInterest toChannels(EnergyScale es)
  {
    if (es == null)
      throw new NullPointerException("EnergyScale is null");
    double c1 = es.findBin(getLowerEnergy());
    double c2 = es.findBin(getUpperEnergy());
    if (c1 < 0)
      c1 = 0;
    if (c2 > es.getChannels())
      c2 = es.getChannels();
    return ChannelRegionOfInterest.of(c1, c2);
  }
}
