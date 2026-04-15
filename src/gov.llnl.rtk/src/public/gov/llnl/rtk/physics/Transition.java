// --- file: gov/llnl/rtk/physics/Transition.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Represents a conversion from a nuclide to others.
 *
 * @author nelson85
 */
public interface Transition extends Emissions
{

  /**
   * The nuclide that is undergoing the decay.
   *
   * @return
   */
  Nuclide getParent();

  /**
   * The probability of this particular decay model relative to others.
   *
   * @return
   */
  double getBranchingRatio();

}
