// --- file: gov/llnl/rtk/physics/EmissionCorrelationImpl.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

public class EmissionCorrelationImpl implements EmissionCorrelation
{
  private Emission cause;
  private Emission effect;
  private double probability;

  public EmissionCorrelationImpl(Emission cause, Emission effect, double probability)
  {
    this.cause = cause;
    this.effect = effect;
    this.probability = probability;
  }

  @Override
  public Emission getPrimary()
  {
    return this.cause;
  }

  @Override
  public Emission getSecondary()
  {
    return this.effect;
  }

  @Override
  public double getProbability()
  {
    return this.probability;
  }

}
