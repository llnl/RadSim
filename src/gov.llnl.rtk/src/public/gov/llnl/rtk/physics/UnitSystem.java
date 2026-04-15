// --- file: gov/llnl/rtk/physics/UnitSystem.java ---
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
public interface UnitSystem
{
  public static UnitSystem SI = new UnitSystemImpl("m", "kg", "s");
  public static UnitSystem MKS = SI;
  public static UnitSystem CGS = new UnitSystemImpl("cm", "g", "s");

  /** Get the fundamental unit of length for this system.
   * 
   * @return a unit of length.
   */
  Units getLengthUnit();

  /** Get the fundamental unit of mass for this system.
   * 
   * @return a unit of mass.
   */
  Units getMassUnit();

  /** Get the fundamental unit of time for this system.
   * 
   * @return a unit of time.
   */
  Units getTimeUnit();

  /** Get the derived unit of density for this system.
   * 
   * Density is mass per unit area.
   * 
   * @return a unit of density.
   */
  Units getDensityUnit();

  /** Get the derived unit of area for this system.
   * 
   * Area is a square unit of length.
   * 
   * @return a unit of area.
   */
  Units getAreaUnit();

  /** Get the derived unit of volume for this system.
   * 
   * Volume is a cube unit of length.
   * 
   * @return a unit of volume.
   */
  Units getVolumeUnit();

   /** Get the derived unit of areal density for this system.
   * 
   * Areal density is mass over area.
   * 
   * @return a unit of areal density.
   */ 
  Units getArealDensityUnit();
}
