// --- file: gov/llnl/rtk/physics/Units.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Conversion for units.
 *
 * The {@code Units} interface provides a mechanism for handling unit
 * conversions and ensuring consistency across computations involving physical
 * quantities. It supports conversions to and from SI (International System of
 * Units) and validates unit types to prevent improper usage.
 *
 * <p>
 * <b>Design Philosophy:</b></p>
 * <ul>
 * <li><b>SI-Centric:</b> All conversions are centered around SI units to ensure
 * consistency with international standards.</li>
 * <li><b>Type Safety:</b> Units are categorized by type (e.g., length, mass,
 * time), and type validation is enforced.</li>
 * <li><b>Extensibility:</b> The static {@code get} method allows dynamic
 * retrieval of units, making it easy to add new units.</li>
 * </ul>
 *
 * <p>
 * <b>Thread Safety:</b></p>
 * <ul>
 * <li>This interface does not enforce thread safety. If units are dynamically
 * added or retrieved, ensure that the underlying registry (e.g.,
 * {@code UnitImpl}) is thread-safe.</li>
 * </ul>
 *
 * <p>
 * <b>Usage Example:</b></p> null {@code
 * <pre>
 * double processToLengthInKm(double massInGrams) {
 *     double massSI = massInGrams * Units.get("mass:g").getValue();
 *     ...
 *     // Return needs to be in kilometers
 *     return Units.get("length:km").convert(lengthSI);
 * }
 * </pre> }
 *
 * @author nelson85
 */
public interface Units
{

  /**
   * Get the symbol for this unit.
   *
   * @return the symbol for the unit (e.g., "m" for meters, "kg" for kilograms).
   */
  String getSymbol();

  /**
   * Value relative to the SI unit.
   *
   * @return the conversion factor relative to the corresponding SI unit.
   */
  double getValue();

  /**
   * Get the measure associated with this unit.
   *
   * @return the type of the unit (e.g., length, mass, time).
   */
  UnitType getType();

  /**
   * Retrieve a unit by name.
   *
   * @param name the name of the unit (e.g., "length:km").
   * @return the unit corresponding to the given name.
   */
  static Units get(String name)
  {
    return UnitImpl.get(name);
  }

  /**
   * Validate the unit type.
   *
   * @param type the expected unit type.
   * @throws IllegalArgumentException if the unit type is incorrect.
   */
  default void require(UnitType type)
  {
    if (type == null)
      return;
    if (!this.getType().equals(type))
    {
      throw new IllegalArgumentException(
              String.format("Incorrect unit type (%s != %s)", this.getType(), type)
      );
    }
  }

  /**
   * Convert a value from SI units to this unit.
   *
   * @param siValue the value in SI units
   * @return the converted value in this unit
   */
  default double fromSI(double siValue)
  {
    return siValue / getValue(); // Divide by the conversion factor
  }

  /**
   * Convert a value from this unit to SI units.
   *
   * @param unitValue the value in this unit
   * @return the converted value in SI units
   */
  default double toSI(double unitValue)
  {
    return unitValue * getValue(); // Multiply by the conversion factor
  }
}
