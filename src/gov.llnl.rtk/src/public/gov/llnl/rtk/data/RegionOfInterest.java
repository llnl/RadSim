// --- file: gov/llnl/rtk/data/RegionOfInterest.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import java.io.Serializable;

/**
 * A region of interest defines a contiguous set of channels in a spectrum. This
 * may either be defined in terms of energy or channels.
 *
 * @author nelson85
 */
public interface RegionOfInterest extends Serializable
{
  /**
   * Get the region of interest in channels for a specified scale.
   *
   * @param energyScale
   * @return
   */
  int[] getChannels(EnergyScale energyScale);
}
