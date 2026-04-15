// --- file: gov/llnl/rtk/data/EnergyScaleFactory.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.calibration.ChannelEnergyPair;
import java.util.Collection;

/**
 *
 * @author nelson85
 */
public abstract class EnergyScaleFactory
{

  static final EnergyScaleFactory INSTANCE = newInstance();

  /**
   * Create a new energy scale with a fixed bin structure.
   *
   * @param bins
   * @return
   */
  public static EnergyScale newScale(double[] bins)
  {
    return INSTANCE.createNewScale(bins);
  }

  /**
   * Create a new energy scale from energy pairs.
   *
   * @param channels
   * @param pairs
   * @return
   */
  public static EnergyPairsScale newScale(int channels, Collection<ChannelEnergyPair> pairs)
  {
    return INSTANCE.createNewScale(channels, pairs);
  }

  /**
   * Create a new energy scale with square root binning.
   *
   * This is often used for analysis in which the keeping the relative peak
   * widths the same is desirable.
   * 
   * FIXME should this be named Quadratic scale rather than Sqrt?
   *
   * @param begin is the start of the energy scale in keV.
   * @param end is the end of the energy scale in keV.
   * @param channels is the number of energy channels in the energy scale.
   * @return a new energy scale.
   */
  public static EnergyScale newSqrtScale(double begin, double end, int channels)
  {
    return INSTANCE.createNewSqrtScale(begin, end, channels);
  }

  /**
   * Create a new linear scale.
   *
   * @param begin is the start of the energy scale in keV.
   * @param end is the end of the energy scale in keV.
   * @param channels is the number of energy channels in the energy scale.
   * @return a new energy scale.
   */
  public static EnergyScale newLinearScale(double begin, double end, int channels)
  {
    return INSTANCE.createNewLinearScale(begin, end, channels);
  }

  /**
   * Create an energy scale which is scaled from an existing one.
   *
   * This is used as part of the peak stabilizer routines to adjust an existing
   * scale.
   *
   * @param energyScale
   * @param ratio
   * @return
   */
  public static EnergyScale newScaledScale(EnergyScale energyScale, double ratio)
  {
    return INSTANCE.createNewScaledScale(energyScale, ratio);
  }

//<editor-fold desc="implementation">  
  /**
   * Create an instance of the energy scale factory using reflection. This hides
   * the implementation from the interface.
   *
   * @return a new instance.
   */
  static private EnergyScaleFactory newInstance()
  {
    return new gov.llnl.rtk.data.EnergyScaleFactoryImpl();
  }

  protected abstract EnergyScale createNewScale(double[] bins);

  protected abstract EnergyPairsScale createNewScale(int channels, Collection<ChannelEnergyPair> pairs);

  protected abstract EnergyScale createNewSqrtScale(double begin, double end, int channels);

  protected abstract EnergyScale createNewLinearScale(double begin, double end, int channels);

  protected abstract EnergyScale createNewScaledScale(EnergyScale energyScale, double scaleFactor);
//</editor-fold> 
}
