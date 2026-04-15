// --- file: gov/llnl/rtk/calibration/NonlinearStabilizerUtilities.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.spline.Spline;
import gov.llnl.rtk.data.Spectrum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class NonlinearStabilizerUtilities
{
  public static double computeIntegratedPower(Spectrum spectrum)
  {
    double power = 0;
    int n1 = spectrum.getMinimumValidChannel();
    int n2 = spectrum.getMaximumValidChannel();
    double[] counts = spectrum.toDoubles();
    for (int i = n1; i < n2; i++)
    {
      power += counts[i] * i;
    }
    power /= spectrum.getLiveTime() / spectrum.size();
    return power;
  }

  // FIXME move to utilities
  public static List<ChannelEnergyPair> correctPairs(Collection<ChannelEnergyPair> pairs, Spline correction)
  {
    ArrayList<ChannelEnergyPair> tmp = new ArrayList<>(pairs.size());
    for (ChannelEnergyPair pair : pairs)
    {
      ChannelEnergyPair pair2 = new ChannelEnergyPair(correction.applyAsDouble(pair.getChannelEdge()), pair.getEnergy());
      pair2.copyAttributes(pair);
      tmp.add(pair2);
    }
    return tmp;
  }

  // FIXME move to utilities
  public static List<ChannelEnergyPair> scalePairs(Collection<ChannelEnergyPair> pairs, double scale)
  {
    ArrayList<ChannelEnergyPair> tmp = new ArrayList<>(pairs.size());
    for (ChannelEnergyPair pair : pairs)
    {
      ChannelEnergyPair pair2 = new ChannelEnergyPair(scale * pair.getChannelEdge(), pair.getEnergy());
      pair2.copyAttributes(pair);
      tmp.add(pair2);
    }
    return tmp;
  }

}
