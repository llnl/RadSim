// --- file: gov/llnl/rtk/flux/FluxLineStep.java ---
/* 
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import java.io.Serializable;

/**
 * Representation for a line with a step.
 *
 * Line is defined by the energy, intensity and step. The step is defined as the
 * total density difference below the peak minus the density above.
 *
 * @author nelson85
 */
public class FluxLineStep implements FluxLine, Serializable
{

  final double energy;
  final double intensity;
  final double step;

  /**
   * Create a new FluxLineStep.
   *
   * @param energy is the energy for the line.
   * @param intensity is the intensity for the line.
   * @param step is the density change in the continuum at this line.
   */
  public FluxLineStep(double energy, double intensity, double step)
  {
    this.energy = energy;
    this.intensity = intensity;
    this.step = step;
    if (Double.isNaN(step))
      throw new ArithmeticException("NaN in step");
  }

  @Override
  public double getEnergy()
  {
    return this.energy;
  }

  @Override
  public double getIntensity()
  {
    return this.intensity;
  }

  @Override
  public double getStep()
  {
    return this.step;
  }

  @Override
  public String toString()
  {
    return String.format("LineStep(e=%.2f,i=%.3e,s=%.3e)", this.energy, this.intensity, this.step);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FluxLineStep))
      return false;
    FluxLineStep line = (FluxLineStep) obj;
    return this.energy == line.energy
            && this.intensity == line.intensity
            && this.step == line.step;
  }

}
