// --- file: gov/llnl/rtk/pileup/PileupEvaluator.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.pileup;

import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.IntegerSpectrum;

/**
 *
 * @author nelson85
 */
public interface PileupEvaluator
{
  /**
   * Create a draw from this spectrum.Assumes that the binning is uniform.
   *
   * This implementation is good up to 30% deadtime and accuracy will fall off
   * afterwards.
   *
   * @param spectrum is the expected spectrum to draw from.
   * @param scalar is a scale factor to apply to the spectrum.
   * @return an array holding the draw. This array will be destroyed the next
   * time this method is called.
   *
   */
  int[] draw(DoubleSpectrum spectrum, double scalar, double timeScalar);

  /**
   * Convert the output to a spectrum.
   *
   * @return a new integer spectrum.
   */
  IntegerSpectrum toSpectrum();

  double getLiveTime();

  double getRealTime();

}
