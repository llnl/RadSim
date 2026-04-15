// --- file: gov/llnl/rtk/physics/UnitSystemImpl.java ---
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
public class UnitSystemImpl implements UnitSystem
{
  private final Units lengthUnit;
  private final Units massUnit;
  private final Units timeUnit;
  private final Units densityUnit;
  private final Units areaUnit;
  private final Units volumeUnit;
  private final Units arealDensityUnit;

  public UnitSystemImpl(String length, String mass, String time)
  {
    this.lengthUnit = Units.get(length);
    this.timeUnit = Units.get(time);
    this.massUnit = Units.get(mass);
    this.densityUnit = Units.get(String.format("%s/%s^3", mass, length));
    this.areaUnit = Units.get(String.format("%s^2", length));
    this.volumeUnit = Units.get(String.format("%s^3", length));
    this.arealDensityUnit = Units.get(String.format("%s/%s^2", mass, length));
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
    return this.densityUnit;
  }

  @Override
  public Units getAreaUnit()
  {
    return this.areaUnit;
  }

  @Override
  public Units getVolumeUnit()
  {
    return this.volumeUnit;
  }

  @Override
  public Units getArealDensityUnit()
  {
    return this.arealDensityUnit;
  }

}
