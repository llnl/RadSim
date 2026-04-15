// --- file: gov/llnl/rtk/physics/Emission.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public interface Emission extends Serializable
{
  /**
   * Get the process that produced this emission.
   *
   * @return
   */
  Transition getOrigin();

  /**
   * Get the intensity of this decay per decay that follows this branch.
   *
   * Note that this intensity is different that used in ENSDF. Those are stored
   * as intensity per 100 decays of the parent, rather than the number that
   * follow the branch.
   *
   * @return
   */
  Quantity getIntensity();
}
