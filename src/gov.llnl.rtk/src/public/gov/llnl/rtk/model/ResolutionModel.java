// --- file: gov/llnl/rtk/model/ResolutionModel.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.math.MathConstants;
import gov.llnl.math.function.Function;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public interface ResolutionModel extends Function.Differentiable, Serializable
{
  /**
   * Compute the width of the peak in standard deviations for a given energy.
   *
   * @param energy is the energy in keV.
   * @return the width in standard deviations.
   */
  @Override
  public double applyAsDouble(double energy);

  /**
   * Compute the width of the peak in keV at FWHM for a given energy.
   *
   * @param energy is the energy in keV.
   * @return FWHM at specified energy
   */
  default double fwhm(double energy)
  {
    return MathConstants.GAUSSIAN_FWHM * this.applyAsDouble(energy);
  }
}
