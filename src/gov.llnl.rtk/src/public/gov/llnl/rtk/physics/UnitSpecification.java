// --- file: gov/llnl/rtk/physics/UnitSpecification.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import static gov.llnl.rtk.physics.PhysicalProperty.DENSITY;

/**
 * User specified unit system.  
 * 
 * Any unspecified units are derived from the fundamental units.
 *
 * @author nelson85
 */
final public class UnitSpecification implements UnitSystem
{
  Units lengthUnit = Units.get("m");
  Units massUnit = Units.get("kg");
  Units timeUnit = Units.get("s");
  Units areaUnit = null;
  Units volumeUnit = null;
  Units densityUnit = null;
  Units arealDensityUnit = null;

  public void setUnits(Units units)
  {
    PhysicalProperty type = (PhysicalProperty) units.getType();
    switch (type)
    {
      case LENGTH:
        lengthUnit = units;
        break;
      case MASS:
        massUnit = units;
        break;
      case TIME:
        timeUnit = units;
        break;
      case VOLUME:
        volumeUnit = units;
        break;
      case AREA:
        areaUnit = units;
        break;
      case DENSITY:
        densityUnit = units;
        break;
      case AREAL_DENSITY:
        arealDensityUnit = units;
        break;
      default:
        throw new UnsupportedOperationException("Unknown unit type " + units.getType());
    }
  }

  @Override
  public Units getLengthUnit()
  {
    return this.lengthUnit;
  }

  @Override
  public Units getMassUnit()
  {
    return this.massUnit;
  }

  @Override
  public Units getTimeUnit()
  {
    return this.timeUnit;
  }

  @Override
  public Units getDensityUnit()
  {
    if (this.densityUnit == null)
      setUnits(Units.get(String.format("%s/%s^3", massUnit.getSymbol(), lengthUnit.getSymbol())));
    return this.densityUnit;
  }

  @Override
  public Units getAreaUnit()
  {
    if (this.areaUnit == null)
      setUnits(Units.get(String.format("%s^2", lengthUnit.getSymbol())));
    return this.areaUnit;
  }

  @Override
  public Units getVolumeUnit()
  {
    if (this.volumeUnit == null)
      setUnits(Units.get(String.format("%s^3", lengthUnit.getSymbol())));
    return this.volumeUnit;
  }

  @Override
  public Units getArealDensityUnit()
  {
    if (this.arealDensityUnit == null)
      setUnits(Units.get(String.format("%s/%s^3", massUnit.getSymbol(), lengthUnit.getSymbol())));
    return this.arealDensityUnit;
  }

}
