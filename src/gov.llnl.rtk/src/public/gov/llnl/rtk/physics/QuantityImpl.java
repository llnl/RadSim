// --- file: gov/llnl/rtk/physics/QuantityImpl.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.Objects;

/**
 *
 * @author nelson85
 */
public final class QuantityImpl implements Quantity
{
  private static final long serialVersionUID = 0xA341_0000_0000_0000L;

  Units units;
  double scalar;
  double value;
  boolean specified = true;
  double uncertainty = 0;

  /**
   * Utility function to create a mutable quantity with a specified units.
   *
   * @param units
   */
  public QuantityImpl(Units units)
  {
    this(0, units, 0, true);
  }

  QuantityImpl(double value, Units units, double uncertainty, boolean specified)
  {
    this.units = units;
    if (units != null)
      this.scalar = units.getValue();
    else
      this.scalar = 1.0;

    this.value = value * this.scalar;
    this.uncertainty = uncertainty * this.scalar;
    this.specified = specified;
  }

  /**
   * Get the value in SI units.
   *
   * @return
   */
  @Override
  public double get()
  {
    return value;
  }

  /**
   * Update the SI value.
   *
   * @param d
   */
  public void set(double d)
  {
    this.value = d;
  }

  @Override
  public double as(Units desired)
  {
    if (desired == null)
      throw new UnitsException("Null units requested");
    if (this.units == null)
      throw new UnitsException("Can't convert scalar to units");
    this.units.require(desired.getType());
    return get() / desired.getValue();
  }

  @Override
  public QuantityImpl to(Units desired)
  {
    if (desired == null)
      throw new UnitsException("Null units requested");
    if (this.units == null || desired.getType() != this.units.getType())
      throw new UnitsException("Can't convert scalar to units");
    double change = 1 / desired.getValue();
    return new QuantityImpl(value * change, desired, uncertainty * change, specified);
  }

  /**
   * Get the units associated with this quantity.
   *
   * @return
   */
  @Override
  public Units getUnits()
  {
    return units;
  }

  /**
   * Get the number portion of the quantity without conversion.
   *
   * @return
   */
  @Override
  public double getValue()
  {
    return value / scalar;
  }

  /**
   * Get the uncertainty of the quantity without conversion.
   *
   * @return
   */
  @Override
  public double getUncertainty()
  {
    return uncertainty / scalar;
  }

  /**
   * Check if the value is specified.
   *
   * @return
   */
  @Override
  public boolean isSpecified()
  {
    return specified;
  }

  /**
   * Get the uncertainty of the quantity without conversion.
   *
   * @return
   */
  @Override
  public boolean hasUncertainty()
  {
    return uncertainty != 0;
  }

  @Override
  public String toString()
  {
    return String.format("%f %s", value / scalar, units);
  }

  /**
   * Require a specific unit.
   *
   * If no unit type is given then it will always pass.
   *
   * @param type
   */
  @Override
  public void require(UnitType type)
  {
    Units current = this.getUnits();
    if (current == null)
      throw new IllegalArgumentException("Scaler quantity");
    if (type == null)
      return;
    current.require(type);
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.units);
    hash = 53 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
    hash = 53 * hash + (this.specified ? 1 : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final QuantityImpl other = (QuantityImpl) obj;
    if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value))
      return false;
    if (this.specified != other.specified)
      return false;
    if (Double.doubleToLongBits(this.uncertainty) != Double.doubleToLongBits(other.uncertainty))
      return false;
    return Objects.equals(this.units, other.units);
  }

  static QuantityImpl from(Quantity quantity)
  {
    if (quantity == null)
      return null;
    if (quantity instanceof QuantityImpl)
      return (QuantityImpl) quantity;
    return new QuantityImpl(quantity.getValue(), quantity.getUnits(), quantity.getUncertainty(), quantity.isSpecified());
  }

  void assign(Quantity value)
  {
    if (value == null)
      throw new NullPointerException();

    if (value instanceof QuantityImpl)
    {
      // Fast path
      QuantityImpl vi = (QuantityImpl) value;
      this.units = vi.units;
      this.scalar = vi.scalar;
      this.value = vi.value;
      this.uncertainty = vi.uncertainty;
      this.specified = vi.specified;
      return;
    }

    this.setUnits(value.getUnits());
    this.value = value.getValue();
    this.uncertainty = value.getUncertainty() * this.scalar;
    this.specified = value.isSpecified();
  }

  void setUnits(Units units)
  {
    this.units = units;
    this.scalar = units != null ? units.getValue() : 1.0;
  }

  void assign(double value, Units units)
  {
    setUnits(units);
    this.value = value * this.scalar;
  }

}
