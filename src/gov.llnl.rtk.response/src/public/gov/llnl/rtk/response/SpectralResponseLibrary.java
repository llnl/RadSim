// --- file: gov/llnl/rtk/response/SpectralResponseLibrary.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

/**
 *
 * @author nelson85
 */
public interface SpectralResponseLibrary
{

  /**
   * Get the detector response function.
   *
   * @param descriptor is a string describing the response function.
   * @return the response function or null if not available.
   */
  SpectralResponseFunction getDetector(String descriptor);
}
