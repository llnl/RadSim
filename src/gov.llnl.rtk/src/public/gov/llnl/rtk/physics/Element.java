// --- file: gov/llnl/rtk/physics/Element.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public interface Element
{
  String getSymbol();

  int getAtomicNumber();

  @Override
  String toString();
  
  /** 
   * Get the nominal density of the element in its pure form.
   * 
   * @return 
   */
  Quantity getDensity();

  /**
   * @return the molarMass is natural occurring, otherwise 0.
   */
  double getMolarMass();
  
  // Break down the element into its isotopes
  Material toMaterial();
  
}
