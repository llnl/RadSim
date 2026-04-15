// --- file: gov/llnl/rtk/calibration/ChannelEnergySet.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.MathAssert;
import gov.llnl.rtk.data.ChannelEnergySetImpl;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Representation of a set of channel energy pairs. This class is produced as an
 * output. It should not be used as an input as that would restrict its
 * functionality. Most of its member functions are in ChannelEnergyUtilities.
 *
 * @author nelson85
 */
public interface ChannelEnergySet extends Serializable, Set<ChannelEnergyPair>
{
  static ChannelEnergySet newSet(ChannelEnergyPair.Origin type, double[] channels, double[] energies)
  {
    MathAssert.assertEqualLength(channels, energies);
    ChannelEnergyPair[] pairs = new ChannelEnergyPair[channels.length];
    for (int i = 0; i < channels.length; ++i)
      pairs[i] = new ChannelEnergyPair(channels[i], energies[i], type);
    return new ChannelEnergySetImpl(Arrays.asList(pairs));
  }

  static ChannelEnergySet newSet(Collection<ChannelEnergyPair> pairs)
  {
    return new ChannelEnergySetImpl(pairs);
  }

}
