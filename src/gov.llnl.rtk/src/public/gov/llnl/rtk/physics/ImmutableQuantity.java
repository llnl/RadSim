// --- file: gov/llnl/rtk/physics/ImmutableQuantity.java ---
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
class ImmutableQuantity implements Quantity
{
  private final double scalar;
  private final double value;
  private final double uncertainty;
  private final boolean specified;
  private final Units units;

  public ImmutableQuantity(Quantity quantity)
  {
    this.units = quantity.getUnits();
    if (units != null)
      this.scalar = quantity.getUnits().getValue();
    else
      this.scalar = 1;
    this.value = quantity.getValue() / scalar;
    this.uncertainty = quantity.getUncertainty() / scalar;
    this.specified = quantity.isSpecified();
  }

  @Override
  public double get()
  {
    return value;
  }

  @Override
  public Units getUnits()
  {
    return units;
  }

  @Override
  public double getValue()
  {
    return value / scalar;
  }

  @Override
  public double getUncertainty()
  {
    return uncertainty / scalar;
  }

  @Override
  public boolean isSpecified()
  {
    return this.specified;
  }

  @Override
  public boolean hasUncertainty()
  {
    return this.uncertainty != 0;
  }

  @Override
  public double as(Units units)
  {
    return this.value / units.getValue();
  }

  @Override
  public Quantity to(Units desired)
  {
    if (desired == null)
      throw new UnitsException("Null units requested");
    if (this.units == null || desired.getType() != this.units.getType())
      throw new UnitsException("Can't convert scalar to units");
    double change = 1 / desired.getValue();
    return new QuantityImpl(value * change, desired, uncertainty * change, specified);
  }

}
