// --- file: gov/llnl/rtk/model/GammaPileupModel.java ---
/* 
 * Copyright (c) 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.data.Spectrum;

/**
 *
 * @author nelson85
 */
public interface GammaPileupModel
{
  /**
   * Compute the livetime for a spectrum given the counts in the spectrum and
   * the realtime. It does not alter the spectrum. This is best applied after
   * the sample has been realized.
   *
   * @param spectrum is the source spectrum without pileup.
   * @return the effective livetime which is the realtime minus the counts
   * times the deadtime.
   */
  double computeLiveTime(Spectrum spectrum);

  /**
   * Compute the expected fraction of counts that will be piled up.
   *
   * @param spectrum is the expected source spectrum without pileup.
   * @return the fraction of counts that are in the piled up shape.
   */
  double computePileupFraction(Spectrum spectrum);

  /**
   * Compute the probability density function for pileuped photons.
   *
   * @param spectrum
   * @return a new spectrum with unit counts containing the expected pileup
   * shape.
   */
  Spectrum computePileup(Spectrum spectrum);
  
}
