// --- file: gov/llnl/rtk/data/EnergyRegionOfInterestImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.annotation.Internal;

/**
 *
 * Default implementation for EnergyRegionOfInterest.
 * 
 * @author nelson85
 */
@Internal
public class EnergyRegionOfInterestImpl implements EnergyRegionOfInterest
{

  final double lowerEnergy;
  final double upperEnergy;
  transient EnergyScale energyScale = null;
  transient int[] cache = null;

  public EnergyRegionOfInterestImpl(double lower, double upper)
  {
    this.lowerEnergy = lower;
    this.upperEnergy = upper;
  }

  @Override
  public double getLowerEnergy()
  {
    return this.lowerEnergy;
  }

  @Override
  public double getUpperEnergy()
  {
    return this.upperEnergy;
  }

  /**
   * Convert a region of interest to channels.
   *
   * @param energyScale
   * @return an array with the lower and upper channel boundaries.
   */
  @Override
  public synchronized int[] getChannels(EnergyScale energyScale)
  {
    if (energyScale == this.energyScale)
      return cache;
    this.energyScale = energyScale;
    if (cache == null)
      cache = new int[2];
    cache[0] = EnergyBinUtilities.convertToEdge(energyScale.getEdges(), lowerEnergy, false);
    cache[1] = EnergyBinUtilities.convertToEdge(energyScale.getEdges(), upperEnergy, true);
    return cache;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("EnergyRegionOfInterest(lower=");
    sb.append(this.lowerEnergy);
    sb.append(",upper=");
    sb.append(this.upperEnergy);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 59 * hash + (int) (Double.doubleToLongBits(this.lowerEnergy) ^ (Double.doubleToLongBits(this.lowerEnergy) >>> 32));
    hash = 59 * hash + (int) (Double.doubleToLongBits(this.upperEnergy) ^ (Double.doubleToLongBits(this.upperEnergy) >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final EnergyRegionOfInterestImpl other = (EnergyRegionOfInterestImpl) obj;
    return other.lowerEnergy == this.lowerEnergy && other.upperEnergy == this.upperEnergy;
  }
}
