// --- file: gov/llnl/rtk/flux/FluxGroupTrapezoid.java ---
/* 
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import java.io.Serializable;

/**
 * Representation of group with linear distribution.
 *
 * @author nelson85
 */
public class FluxGroupTrapezoid implements FluxGroup, Serializable
{

  final double energyLower;
  final double energyUpper;
  final double densityLower;
  final double densityUpper;

  /**
   * Create a new FluxGroupTrapezoid.
   *
   * @param energy0 is the energy at the start of the group.
   * @param energy1 is the energy at the end of the group.
   * @param density0 is the density at the start.
   * @param density1 is the density at the end.
   */
  public FluxGroupTrapezoid(double energy0, double energy1, double density0, double density1)
  {
    this.energyLower = energy0;
    this.energyUpper = energy1;
    this.densityLower = density0;
    this.densityUpper = density1;
    if (Double.isNaN(this.densityLower) || Double.isNaN(this.densityUpper))
      throw new ArithmeticException("density is NaN");
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
    return (densityLower + densityUpper) / 2 * (energyUpper - energyLower);
  }

  @Override
  public double getDensity()
  {
    return (densityLower + densityUpper) / 2;
  }

  @Override
  public double getIntegral(double energy0, double energy1)
  {

    if ((energy1 <= this.energyLower) || (energy0 >= this.energyUpper))
      return 0;
    if (energy0 < this.energyLower)
      energy0 = this.energyLower;
    if (energy1 > this.energyUpper)
      energy1 = this.energyUpper;
    double f0 = (energy0 - this.energyLower) / (this.energyUpper - this.energyLower);
    double f1 = (energy1 - this.energyLower) / (this.energyUpper - this.energyLower);
    double d0 = (1 - f0) * this.densityLower + f0 * this.densityUpper;
    double d1 = (1 - f1) * this.densityLower + f1 * this.densityUpper;
    return (d0 + d1) / 2 * (energy1 - energy0);
  }

  /**
   * @return the densityLower
   */
  public double getDensityLower()
  {
    return densityLower;
  }

  /**
   * @return the densityUpper
   */
  public double getDensityUpper()
  {
    return densityUpper;
  }

  @Override
  public String toString()
  {
    return String.format("GroupTrapezoid(e0=%f,e1=%f,d0=%f,d1=%f)",
            this.energyLower, this.energyUpper,
            this.densityLower, this.densityUpper);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FluxGroupTrapezoid))
      return false;
    FluxGroupTrapezoid flux = (FluxGroupTrapezoid) obj;
    return this.densityLower == flux.densityLower
            && this.densityUpper == flux.densityUpper
            && this.energyLower == flux.energyLower
            && this.energyUpper == flux.energyUpper;
  }

}
