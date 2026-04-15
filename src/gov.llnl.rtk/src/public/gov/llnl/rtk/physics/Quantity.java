// --- file: gov/llnl/rtk/physics/Quantity.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.io.Serializable;

/**
 * Represents a physical quantity with an associated unit.
 *
 * The {@code Quantity} interface provides a flexible mechanism for representing
 * physical quantities, performing unit-aware operations, and converting between
 * different units. Quantities can carry their original units while supporting
 * backend operations in SI units for consistency.
 *
 * <p>
 * <b>Key Features:</b></p>
 * <ul>
 * <li><b>Unit Association:</b> Each quantity is associated with a {@code Units}
 * object, which defines its type, symbol, and conversion factor relative to
 * SI.</li>
 * <li><b>Unit Conversion:</b> Provides methods to retrieve values in specific
 * units or convert quantities to new units.</li>
 * <li><b>Arithmetic Operations:</b> Supports unit-aware addition and
 * scaling.</li>
 * <li><b>Uncertainty Handling:</b> Represents and propagates measurement
 * uncertainty during arithmetic operations.</li>
 * <li><b>Mutability and Immutability:</b> Allows dynamic updates to values and
 * units, with an option to create immutable quantities.</li>
 * </ul>
 *
 * <p>
 * <b>Usage Example:</b></p> null {@code
 * <pre>
 * Quantity length = Quantity.of(100, "length:cm");
 * double lengthInMeters = length.as("length:m");
 * Quantity lengthInKilometers = length.to("length:km");
 * Quantity scaledLength = length.scaled(2.0);
 * Quantity sum = length.plus(Quantity.of(50, "length:cm"));
 * </pre>
 * }
 *
 * @author nelson85
 */
@ReaderInfo(QuantityReader.class)
@WriterInfo(QuantityWriter.class)
public interface Quantity extends Serializable
{

  /**
   * Represents an unspecified quantity.
   */
  public static Quantity UNSPECIFIED = new QuantityImpl(0, null, 0, false);

  /**
   * Create a scalar quantity without units.
   *
   * @param value the scalar value
   * @return a scalar quantity
   */
  public static Quantity scalar(double value)
  {
    return new QuantityImpl(value, null, 0, true);
  }

  /**
   * Create a quantity with specified units.
   *
   * @param value the value of the quantity
   * @param units the units associated with the quantity (as a string)
   * @return a quantity with the specified units
   */
  static public Quantity of(double value, String units)
  {
    return new QuantityImpl(value, Units.get(units), 0, true);
  }

  /**
   * Create a quantity with specified units.
   *
   * @param value the value of the quantity
   * @param units the units associated with the quantity
   * @return a quantity with the specified units
   */
  static public Quantity of(double value, Units units)
  {
    return new QuantityImpl(value, units, 0, true);
  }

  /**
   * Create a fully specified quantity with units and uncertainty.
   *
   * @param value the value of the quantity
   * @param units the units associated with the quantity
   * @param uncertainty the uncertainty of the quantity
   * @param specified whether the quantity is explicitly specified
   * @return a fully specified quantity
   */
  static public Quantity of(double value, Units units, double uncertainty, boolean specified)
  {
    return new QuantityImpl(value, units, uncertainty, specified);
  }

  /**
   * Get the value in SI units.
   *
   * @return the value in SI units
   */
  double get();

  /**
   * Get the units associated with this quantity.
   *
   * @return the units of the quantity
   */
  Units getUnits();

  /**
   * Get the raw value of the quantity without conversion.
   *
   * @return the raw value
   */
  double getValue();

  /**
   * Get the uncertainty of the quantity without conversion.
   *
   * @return the raw uncertainty
   */
  double getUncertainty();

  /**
   * Check if the value is explicitly specified.
   *
   * @return {@code true} if the value is specified, {@code false} otherwise
   */
  public boolean isSpecified();

  /**
   * Check if the quantity has uncertainty.
   *
   * @return {@code true} if the quantity has uncertainty, {@code false}
   * otherwise
   */
  public boolean hasUncertainty();

//<editor-fold desc="default" defaultstate="collapsed">
  /**
   * Convert the quantity to the requested units.
   *
   * @param desired the requested units
   * @return the value of the quantity in the requested units
   * @throws UnitsException if the conversion is invalid
   */
  default double as(Units desired)
  {
    if (desired == null)
    {
      throw new UnitsException("Null units requested");
    }
    Units currentUnits = this.getUnits();
    if (currentUnits == null)
    {
      throw new UnitsException("Can't convert scalar to units");
    }
    currentUnits.require(desired.getType());
    return getValue() / desired.getValue();
  }

  /**
   * Create a new quantity in the requested units.
   *
   * @param desired the requested units
   * @return a new quantity in the requested units
   * @throws UnitsException if the conversion is invalid
   */
  default Quantity to(Units desired)
  {
    if (desired == null)
    {
      throw new UnitsException("Null units requested");
    }
    Units currentUnits = this.getUnits();
    if (currentUnits == null || desired.getType() != currentUnits.getType())
    {
      throw new UnitsException("Can't convert scalar to units");
    }
    double change = 1 / desired.getValue();
    return Quantity.of(getValue() * change, desired, getUncertainty() * change, isSpecified());
  }


  /**
   * Get the value in a specified unit.
   *
   * @param units the target units (as a string)
   * @return the value in the specified units
   */
  default double as(String units)
  {
    return this.as(Units.get(units));
  }

  /**
   * Convert the quantity to a specific unit.
   *
   * @param units the target units (as a string)
   * @return a new quantity in the specified units
   */
  default Quantity to(String units)
  {
    return this.to(Units.get(units));
  }

  /**
   * Create a scaled copy of the quantity for transformation purposes.
   *
   * This method defers evaluation and represents a derived quantity based on
   * the original value. The scaled quantity is linked to the original value
   * such that changes in the original will be reflected in the scaled version.
   *
   * <p>
   * Use this method to create quantities that depend on scaling factors without
   * immediately performing calculations.</p>
   *
   * @param factor the scaling factor
   * @return a scaled quantity
   */
  default Quantity scaled(double factor)
  {
    return new ScaledQuantity(this, factor);
  }

  /**
   * Add another quantity to this one to produce a new quantity.
   *
   * This method performs immediate evaluation and is intended for combining
   * quantities with compatible units. Ensure that both quantities use
   * compatible units before calling this method.
   *
   * @param quantity the quantity to add
   * @return the sum of the quantities
   * @throws UnitsException if the units are incompatible
   */
  default Quantity plus(Quantity quantity)
  {
    Units units = this.getUnits();
    if (units.getType() != quantity.getUnits().getType())
      throw new UnitsException("Mixed unit addition");

    // Units must be equalant
    if (!quantity.getUnits().equals(units))
      quantity = quantity.to(units);

    // Operate in unit space
    double newValue = this.getValue() + quantity.getValue();
    double newUncertainty = Math.sqrt(
            Math.pow(this.getUncertainty(), 2) + Math.pow(quantity.getUncertainty(), 2)
    );

    return new QuantityImpl(newValue, units, newUncertainty, this.isSpecified());
  }

  /**
   * Require a specific unit type.
   *
   * @param type the expected unit type
   * @throws IllegalArgumentException if the unit type is incorrect
   */
  default public void require(UnitType type)
  {
    Units current = this.getUnits();
    if (current == null)
      throw new IllegalArgumentException("Scalar quantity");
    if (type == null)
      return;
    current.require(type);
  }

  /**
   * Create an immutable version of this quantity.
   *
   * @return an immutable quantity
   */
  default public Quantity immutable()
  {
    return new ImmutableQuantity(this);
  }

  /**
   * Apply a transformation to the quantity to produce a derived quantity.
   *
   * This method supports uncertainty propagation by computing the derivative
   * numerically using finite differences.
   *
   * @param transformer the function representing the transformation
   * @return a transformed quantity with propagated uncertainty
   * @throws IllegalArgumentException if the transformer is null
   */
  default Quantity transform(Units units, DoubleTransformer transformer)
  {
    if (transformer == null)
    {
      throw new IllegalArgumentException("Transformer cannot be null");
    }
    return new TransformedQuantity(this, units, transformer, null);
  }

  /**
   * Apply a transformation to the quantity to produce a derived quantity.
   *
   * This method supports uncertainty propagation by either using a
   * user-supplied derivative or computing it numerically using finite
   * differences.
   *
   * @param transformer the function representing the transformation
   * @param derivative the derivative of the transformation function
   * @return a transformed quantity with propagated uncertainty
   * @throws IllegalArgumentException if the transformer is null
   */
  default Quantity transform(Units units, DoubleTransformer transformer, DoubleTransformer derivative)
  {
    if (transformer == null)
    {
      throw new IllegalArgumentException("Transformer cannot be null");
    }
    return new TransformedQuantity(this, units, transformer, derivative);
  }

//</editor-fold>
}
