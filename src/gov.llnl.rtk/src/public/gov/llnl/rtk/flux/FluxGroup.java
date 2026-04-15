// --- file: gov/llnl/rtk/flux/FluxGroup.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

/**
 * Base class for groups.
 *
 * Groups may be used to represent either gamma or neutron.
 *
 * @author nelson85
 */
public interface FluxGroup
{
    /**
   * Get the group lower energy.
   *
   * @return the lower edge of this group.
   */
  double getEnergyLower();

  /**
   * Get the group upper energy.
   *
   * @return the upper edge of this group.
   */
  double getEnergyUpper();

  default double getEnergyAverage()
  {
    return (getEnergyLower() + getEnergyUpper())/2;
  }
  
  default double getEnergyWidth()
  {
    return getEnergyUpper()-getEnergyLower();
  }

  /**
   * Get the total counts in the group
   *
   * @return is the total counts in the group.
   */
  double getCounts();

  double getDensity();

  /**
   * Get the intensity in the group between two energies.
   *
   * @param energy0 is the lower edge for the integral.
   * @param energy1 is the upper edge for the integral.
   * @return the total counts in the interval.
   */
  double getIntegral(double energy0, double energy1);


}
