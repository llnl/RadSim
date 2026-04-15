// --- file: gov/llnl/rtk/flux/FluxLineZAD.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

/**
 *
 * Representation for GADRAS data.
 *
 * @author nelson85
 */
public class FluxLineZAD implements FluxLine
{
  final double energy;
  final double intensity;
  private final double z;
  private final double ad;

  /**
   * Create a new FluxLineStep.
   *
   * @param energy is the energy for the line.
   * @param intensity is the intensity for the line.
   * @param z
   * @param ad
   */
  public FluxLineZAD(double energy, double intensity, double z, double ad)
  {
    this.energy = energy;
    this.intensity = intensity;
    this.z = z;
    this.ad = ad;
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
    return 0;
  }

  @Override
  public String toString()
  {
    return String.format("LineZAD(e=%f,i=%f)", this.energy, this.intensity);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FluxLineStep))
      return false;
    FluxLineStep line = (FluxLineStep) obj;
    return this.energy == line.energy
            && this.intensity == line.intensity;
  }

  /**
   * @return the z
   */
  public double getZ()
  {
    return z;
  }

  /**
   * @return the ad
   */
  public double getAD()
  {
    return ad;
  }
}
