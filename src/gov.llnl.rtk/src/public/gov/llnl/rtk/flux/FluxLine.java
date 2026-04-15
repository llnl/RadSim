// --- file: gov/llnl/rtk/flux/FluxLine.java ---
/* 
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

/**
 * Base class for line.
 *
 * Represents a physical process in which a sharp transition is produced
 * typically a gamma ray or x-ray. In most cases this is assumed to be very
 * narrow relative to the detector resolution, but in some cases Doppler
 * broadening may widen the line. Currently, we do not capture broadening.
 *
 * @author nelson85
 */
public interface FluxLine
{

  /**
   * Get the energy of the line.
   *
   * In some cases this is taken from physics tables, in other cases it is
   * extracted from a spectrum. Thus, the energy many not be exact.
   *
   * @return the energy of the line in keV.
   */
  double getEnergy();

  /**
   * Get the total counts in the line.
   *
   * @return the intensity of the line.
   */
  double getIntensity();

  /**
   * Get the associated step in density around this line.
   *
   * This is represents the low angle scatter. It is measured by the density
   * below the line minus the density above the line. It should not be negative
   * as photons always scatter down. Not all flux representations have a step.
   *
   * @return is the step associated with low angle scatter or 0 if no low angle
   * scatter is computed or the scatter is not known.
   */
  double getStep();

}
