// --- file: gov/llnl/rtk/physics/PhotonCrossSections.java ---
/* 
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Interface for photon cross sections in materials.
 *
 * Currently we only support NIST XCOM cross section database.
 * If units are not specified then the defaults will be library specific.
 *
 * @author nelson85
 */
public interface PhotonCrossSections
{

  /**
   * Get the material for this cross section.
   *
   * @return the material
   */
  Material getMaterial();

  /**
   * Get an evaluator for cross section by energy.
   *
   * @return a new evaluator.
   */
  PhotonCrossSectionsEvaluator newEvaluator();

}
