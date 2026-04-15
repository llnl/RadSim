// --- file: gov/llnl/rtk/calibration/StabilizerTarget.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.EnergyScaleException;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;

/**
 * Specification for a position in the spectrum. Can be specified in terms of
 * either channels or energy. This class may be renamed if it is used outside of
 * this scope to become a general tool. For now used only by the stabilizer
 * code.
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "stabilizerTarget", 
        referenceable = true)
public class StabilizerTarget implements Serializable
{
  public enum Units
  {
    CHANNELS,
    ENERGY
  }
  private Units units = Units.ENERGY;
  private double energy;
  private double channel;

  /**
   * Converts the energy to a channel number if specified in terms of energy.
   *
   * @param scale
   * @throws EnergyScaleException
   */
  public void setTargetEnergyScale(EnergyScale scale) throws EnergyScaleException
  {
    if (units == Units.ENERGY)
      channel = scale.findEdgeFloor(energy) + 0.5; //.findChannelCenter(energy);
  }

  /**
   * Defines what units the value will be specified in.
   *
   * @param units the units to set
   */
  @Reader.Attribute(name = "units", required = true)
  public void setUnits(Units units)
  {
    this.units = units;
  }

  /**
   * Set the energy for the target directly. This should be called prior to
   * calling applyEnergyBins.
   *
   * @param energy the energy to set
   */
  public void setEnergy(double energy)
  {
    this.units = Units.ENERGY;
    this.energy = energy;
  }

  /**
   * Set the channel for the target directly. This will override any energy
   * based specification.
   *
   * @param channel the channel to set
   */
  public void setChannel(double channel)
  {
    this.units = Units.CHANNELS;
    this.channel = channel;
  }

  /**
   * Returns the channel number for this spectral features. May be based either
   * on the energy or the specified channels.
   *
   * @return the channel number.
   */
  public double getChannel()
  {
    return this.channel;
  }

  /**
   * Set the value associated with the target. The interpretation will depend on
   * the units specified. This is used primarily by the loader.
   *
   * @param value
   */
  @Reader.TextContents
  public void setValue(double value)
  {
    if (this.units == Units.ENERGY)
      setEnergy(value);
    else
      setChannel(value);
  }
}
