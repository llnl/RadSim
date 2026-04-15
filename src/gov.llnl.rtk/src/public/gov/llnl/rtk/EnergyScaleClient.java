// --- file: gov/llnl/rtk/EnergyScaleClient.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk;

import gov.llnl.rtk.data.EnergyScale;

/**
 * Interface to be supported by all classes that use an energy scale to convert
 * bin structures.
 *
 * @author nelson85
 */
public interface EnergyScaleClient
{
  /**
   * Set up the energy scale for this object.
   *
   * @param scale
   * @throws EnergyScaleException
   */
  abstract public void applyEnergyScale(EnergyScale scale) throws EnergyScaleException;
}
