// --- file: gov/llnl/rtk/physics/Xray.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Photon emission associates with the transition of electrons in orbitals.
 *
 * @author nelson85
 */
public interface Xray extends Photon
{
  /**
   * Get the name for this xray.
   *
   * @return a string.
   */
  public String getName();
}
