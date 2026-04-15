// --- file: gov/llnl/rtk/physics/EnergyEmission.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author cheung27
 */
public interface EnergyEmission extends Emission
{
  /**
   * Get the emission energy.
   *
   * @return
   */
  Quantity getEnergy();
}
