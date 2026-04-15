// --- file: gov/llnl/rtk/physics/ScaledQuantity.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
class ScaledQuantity implements Quantity
{
  private final Quantity base;
  private final double scale;

  ScaledQuantity(Quantity base, double scale)
  {
    this.base = base;
    this.scale = scale;
  }

  @Override
  public double get()
  {
    return base.get() * scale;
  }

  @Override
  public Units getUnits()
  {
    return base.getUnits();
  }

  @Override
  public double getValue()
  {
    return base.getValue() * scale;
  }

  @Override
  public double getUncertainty()
  {
    return base.getUncertainty() * scale;
  }

  @Override
  public void require(UnitType type)
  {
    base.require(type);
  }

  @Override
  public double as(Units units)
  {
    return base.as(units) * scale;
  }

  @Override
  public Quantity to(Units units)
  {
    return new ScaledQuantity(base.to(units), scale);
  }

  @Override
  public boolean isSpecified()
  {
    return base.isSpecified();
  }

  @Override
  public boolean hasUncertainty()
  {
    return base.hasUncertainty();
  }

}
