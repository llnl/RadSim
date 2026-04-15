// --- file: gov/llnl/rtk/physics/PhotonCrossSectionLibrary.java ---
/* 
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public interface PhotonCrossSectionLibrary
{

  /**
   * Get the cross sections by energy for an element.
   *
   * @param element
   * @return
   */
  PhotonCrossSections get(Element element);

  /**
   * Get the cross sections by element for a material.
   *
   * @param material
   * @return
   */
  PhotonCrossSections get(Material material);

}
