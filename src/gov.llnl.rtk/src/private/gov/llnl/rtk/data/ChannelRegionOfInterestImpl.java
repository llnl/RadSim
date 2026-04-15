// --- file: gov/llnl/rtk/data/ChannelRegionOfInterestImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public class ChannelRegionOfInterestImpl implements Serializable, ChannelRegionOfInterest
{
  private final double lowerChannel;
  private final double upperChannel;
  private final int start;
  private final int end;


  public ChannelRegionOfInterestImpl(double lower, double upper)
  {
    this.lowerChannel = lower;
    this.upperChannel = upper;
    this.start = (int)Math.ceil(lower);
    this.end = (int)Math.ceil(upper);
  }

  @Override
  public double getLowerChannel()
  {
    return lowerChannel;
  }

  @Override
  public double getUpperChannel()
  {
    return upperChannel;
  }

  @Override
  public int[] getChannels(EnergyScale energyScale)
  {
    return new int[]
    {
      (int) Math.floor(lowerChannel), (int) Math.floor(upperChannel)
    };
  }

  @Override
  public int getStart()
  {
    return this.start;
  }

  @Override
  public int getEnd()
  {
    return this.end;
  }

}
