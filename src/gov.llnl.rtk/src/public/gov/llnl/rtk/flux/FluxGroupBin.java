// --- file: gov/llnl/rtk/flux/FluxGroupBin.java ---
/* 
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import java.io.Serializable;
import java.util.stream.DoubleStream;

/**
 * Representation of group with undefined distribution.
 *
 * This can be used for gamma or neutrons.
 *
 * A FluxGroupBinned is a group in which only the counts and energy limits are
 * known, but not the distribution of those counts within the group. Typically
 * transport engines only concern themselves with the total flux within the
 * group and not how that flux is distributed within the group. This group
 * represents that result. However, this form is usually not renderable with a
 * spectral detector as some estimates of the total flux within the group is
 * required. Converting from a binned representation to trapezoid representation
 * is required to render a flux through a SpectralResponseFunction.
 *
 * @author nelson85
 */
public class FluxGroupBin implements FluxGroup, Serializable
{

  final double energyLower;
  final double energyUpper;
  final double counts;
  final double uncertainty;

  /**
   * Create a new FluxGroupBin.
   *
   * @param energyLower is the starting energy for the group.
   * @param energyUpper is the ending energy for the group.
   * @param counts is the total integrated counts.
   */
  public FluxGroupBin(double energyLower, double energyUpper, double counts, double uncertainty)
  {
    this.energyLower = energyLower;
    this.energyUpper = energyUpper;
    this.counts = counts;
    this.uncertainty = uncertainty;
    if (this.energyLower >= this.energyUpper)
      throw new RuntimeException("Invalid energy bin");
  }

  public FluxGroupBin(double energyLower, double energyUpper, double counts) {
    this(energyLower, energyUpper, counts, 0.0);
  }

  @Override
  public double getEnergyLower()
  {
    return energyLower;
  }

  @Override
  public double getEnergyUpper()
  {
    return energyUpper;
  }

  @Override
  public double getCounts()
  {
    return counts;
  }

  public double getUncertainty() {
    return uncertainty;
  }

  @Override
  public double getDensity()
  {
    return counts / (energyUpper - energyLower);
  }

  public double getDensityLower()
  {
    return getDensity();
  }

  public double getDensityUpper()
  {
    return getDensity();
  }

  public double getEnergyCenter()
  {
    return (this.energyLower + this.energyUpper) / 2;
  }

  /**
   * Compute the integral of counts over a region.
   *
   * This method is not very accurate as without the neighboring groups, all we
   * can do is assume a flat distribution.
   *
   * @param lower is the lower energy for the region.
   * @param upper is the upper energy for the region.
   * @return the computed integral.
   */
  @Override
  public double getIntegral(double lower, double upper)
  {
    // If outside the limits return 0
    if ((upper <= this.energyLower) || (lower >= this.energyUpper))
      return 0;

    // Compute the density
    double density = counts / (this.energyUpper - this.energyLower);

    // Keep within limits range with the limits of this group
    if (lower < this.energyLower)
      lower = this.energyLower;
    if (upper > this.energyUpper)
      upper = this.energyUpper;
    return density * (upper - lower);
  }

  @Override
  public String toString()
  {
    return String.format("GroupBin(e0=%f,e1=%f,c=%f)", this.energyLower, this.energyUpper, this.counts);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FluxGroupBin))
      return false;
    FluxGroupBin flux = (FluxGroupBin) obj;
    return this.counts == flux.counts
            && this.energyLower == flux.energyLower
            && this.energyUpper == flux.energyUpper;
  }
   
  @Override
  public double getEnergyAverage()
  {
    return (this.energyUpper + this.energyLower)/2;
  }
  
  @Override
  public double getEnergyWidth()
  {
    return this.energyUpper - this.energyLower;
  }


}
