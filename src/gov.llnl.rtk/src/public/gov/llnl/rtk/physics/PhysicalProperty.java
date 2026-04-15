// --- file: gov/llnl/rtk/physics/PhysicalProperty.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Used to define units that apply to a quantity.
 *
 * @author nelson85
 */
public enum PhysicalProperty implements Units, UnitType
{
  UNKNOWN("unknown"),
  // base units
  /**
   * Base unit of mass (SI kg)
   */
  MASS("mass:kg"), // M

  /**
   * Base unit of length (SI m)
   */
  LENGTH("length:m"), // L

  /**
   * Base unit of time (SI s)
   */
  TIME("time:s"), // T

  /**
   * Base unit of current (SI A)
   */
  CURRENT("current:A"),
  /**
   * Base unit of temperature (SI K)
   */
  TEMPERATURE("temperature:K"),
  /**
   * Base unit of substance (SI mol)
   */
  AMOUNT("amount:mol"),
  /**
   * Base unit of luminuous intensity (SI cd)
   */
  LUMINUOUS_INTENSITY("luminous_intensity:cd"),
  // derived units
  /**
   * Derived unit of activity (SI Bq=1/s)
   */
  ACTIVITY("activity:Bq"), // 1/T

  /**
   * Derived unit of specific activity (SI Bq/kg=1/s/kg)
   */
  SPECIFIC_ACTIVITY("specific_activity:Bq/kg"), // 1/T/M

  /**
   * Derived unit of density (SI kg/m3)
   */
  DENSITY("density:kg/m3"), // M/L^3

  /**
   * Derived unit of areal density (SI kg/m2)
   */
  AREAL_DENSITY("areal_density:kg/m2"),
  /**
   * Derived unit of area (SI m^2)
   */
  AREA("area:m2"),
  /**
   * Derived unit of volume (SI m^3)
   */
  VOLUME("volume:m3"),
  /**
   * Derived unit of illuminance (SI lx)
   */
  ILLUMINANCE("illuminance:lx"),
  /**
   * Derived unit of power (SI W=J/s)
   */
  POWER("power:W"), // J/s

  /**
   * Derived unit of frequency (SI W=1/s)
   */
  FREQUENCY("frequency:hz"), // 1/s

  /**
   * Derived unit of absorbed dose (SI W=Gy)
   */
  ABSORBED_DOSE("absorbed_dose:Gy"),
  /**
   * Derived unit of dose equivalent (SI W=Sv)
   */
  DOSE_EQUIVALENT("dose_equivalent:Sv"),
  /**
   * Derived unit of dose equivalent (SI Sv/s)
   */
  DOSE_RATE("dose_rate:Sv/s"),
  /**
   * Derived unit of pressure (SI Pa=kg/m^2)
   */
  PRESSURE("pressure:Pa"), // N/m2

  /**
   * Derived unit of capacitance (SI F=C/V)
   */
  CAPACITANCE("capacitance:F"), // C/V

  /**
   * Derived unit of conductance (SI S=1/ohm)
   */
  CONDUCTANCE("conductance:S"), // 1/ohm

  /**
   * Derived unit of resistance (SI ohm=V/A)
   */
  RESISTANCE("resistance:ohm"), // V/A

  /**
   * Derived unit of inductance (SI H=Wb/A)
   */
  INDUCTANCE("inductance:H"), // Wb/A

  /**
   * Derived unit of voltage (SI V=W/A)
   */
  VOLTAGE("voltage:V"), // W/A

  /**
   * Derived unit of charge (SI C)
   */
  CHARGE("charge:C"),
  /**
   * Derived unit of charge (SI N=kg m/s^2)
   */
  FORCE("force:N"), // kg m/s2

  /**
   * Derived unit of energy (SI J=N m)
   */
  ENERGY("energy:J"), // N m

  /**
   * Derived unit of magnetic flux (SI Wb=V s)
   */
  MAGNETIC_FLUX("magnetic_flux:Wb"), // V s

  /**
   * Derived unit of magnetic flux density (SI Wb/m^2)
   */
  MAGNETIC_FLUX_DENSITY("magnetic_flux_density:T"), // Wb/m^2

  /**
   * Derived unit of flux density (SI Bq/m^2)
   */
  FLUX("flux:c/m2/s"),
  
  
  CROSS_SECTION("cross_section:m2/kg"),
  
  /**
   * Derived unit for molar cross section (SI m^2/mol)
   */
  MOLAR_CROSS_SECTION("molar_cross_section:m2/mol"),
  
  MOLAR_MASS("molar_mass:kg/mol"),
  
  ANGLE("angle:rad");

  private final String unit;
  private final String measure;
  private final String name;

  PhysicalProperty(String unit)
  {

    String[] parts = unit.split(":");
    this.measure = parts[0];
    if (parts.length == 2)
      this.name = parts[1];
    else
      this.name = null;

    this.unit = unit;
  }

  @Override
  public String toString()
  {
    return this.getSymbol();
  }

  public String getUnit()
  {
    return unit;
  }

  @Override
  public String getSymbol()
  {
    return this.name;
  }

  @Override
  public double getValue()
  {
    return 1;
  }

  @Override
  public UnitType getType()
  {
    return this;
  }

  @Override
  public String getMeasure()
  {
    return measure;
  }

}
