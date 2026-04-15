// --- file: gov/llnl/rtk/data/EnergyScaleFactoryImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleUtilities;
import gov.llnl.rtk.calibration.ChannelEnergyPair;
import gov.llnl.rtk.calibration.ChannelEnergyUtilities;
import gov.llnl.rtk.data.EnergyPairsScale;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.utility.annotation.Internal;
import java.util.ArrayList;
import java.util.Collection;

@Internal
public class EnergyScaleFactoryImpl extends EnergyScaleFactory
{
  @Override
  protected EnergyScale createNewScale(double[] bins)
  {
    return new EnergyBinsImpl(bins);
  }

  @Override
  protected EnergyPairsScale createNewScale(int channels, Collection<ChannelEnergyPair> pairs)
  {
    return new EnergyPairsScaleImpl(channels, pairs);
  }

  @Override
  protected EnergyScale createNewSqrtScale(double begin, double end, int channels)
  {
    double[] values = new double[channels + 1];
    double begin2 = Math.sqrt(begin);
    double end2 = Math.sqrt(end);
    for (int i = 0; i < channels + 1; i++)
    {
      values[i] = DoubleUtilities.sqr(begin2 + (end2 - begin2) / channels * i);
    }
    return EnergyScaleFactory.newScale(values);
  }

  @Override
  protected EnergyScale createNewLinearScale(double begin, double end, int channels)
  {
    double[] values = new double[channels + 1];
    for (int i = 0; i < channels + 1; i++)
    {
      values[i] = begin + (end - begin) / channels * i;
    }
    return EnergyScaleFactory.newScale(values);
  }

  @Override
  protected EnergyScale createNewScaledScale(EnergyScale energyScale, double scaleFactor)
  {
    if (energyScale instanceof EnergyPairsScale)
    {
      EnergyPairsScale pairsScale = (EnergyPairsScale) energyScale;
      int channels = pairsScale.getChannels();
      ArrayList<ChannelEnergyPair> tmp = new ArrayList<>(pairsScale);
      ChannelEnergyUtilities.scaleChannels(tmp, scaleFactor);
      return new EnergyPairsScaleImpl(channels, tmp);
    }
    double[] values = new double[energyScale.getChannels() + 1];
    for (int i = 0; i < values.length; i++)
    {
      values[i] = energyScale.getEnergyOfEdge(scaleFactor * i);
    }
    return EnergyScaleFactory.newScale(values);
  }

}
