// --- file: gov/llnl/rtk/physics/DecayLibrary.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.List;

/**
 *
 * @author nelson85
 */
public interface DecayLibrary
{

  /**
   * Get all the decay modes for a specified nuclide.
   *
   * This can include all the competing processes by which a nuclide can decay.
   * <ul>
   * <li> Alpha decay
   * <li> beta decay/electron capture
   * <li> cluster decay
   * <li> fission
   * </ul>
   *
   * @param nuclide
   * @return
   */
  public List<DecayTransition> getTransitionsFrom(Nuclide nuclide);

  /**
   * Get all decays that produce a specified nuclide.
   *
   * @param nuclide
   * @return
   */
  public List<DecayTransition> getTransitionsTo(Nuclide nuclide);
}
