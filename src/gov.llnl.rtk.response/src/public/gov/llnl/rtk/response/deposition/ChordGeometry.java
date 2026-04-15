// --- file: gov/llnl/rtk/response/deposition/ChordGeometry.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

/**
 * Factory to create evaluators for 
 * @author nelson85
 */
public interface ChordGeometry
{
  // Individual geometry parameters such as size appear here.
  
  SpatialChordQF newSpatial();
  
  AngularChordQF newAngular();
  
  IsotropicChordQF newIsotropic();
  
}
