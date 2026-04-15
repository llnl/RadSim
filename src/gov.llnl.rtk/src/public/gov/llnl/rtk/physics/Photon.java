// --- file: gov/llnl/rtk/physics/Photon.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Do we need to differentiate between Gamma and Xray or should they all just be
 * photon emissions?
 *
 * @author nelson85
 */
public interface Photon extends EnergyEmission
{

  /**
   * Get the intensity of this decay per decay that follows this branch.
   *
   * Note that this intensity is different that used in ENSDF. Those are stored
   * as intensity per 100 decays of the parent, rather than the number that
   * follow the branch.
   *
   * @return
   */
  @Override
  Quantity getIntensity();

  // FIXME consider adding the inherent width of the line.  
  // For reactions this would be based on the reaction.
  // For decay gammas the doppler broadening and the relative mass of the emmited 
  //   particle and times since would be relevant.
  // This may depend on the state of matter (solid, liquid, gas, in vacuum)
}
