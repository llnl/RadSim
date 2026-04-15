// --- file: gov/llnl/rtk/physics/Constants.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public class Constants
{

  public static final double AVOGADRO = 6.0221408E23;
  public static final Quantity RADIUS_E = Quantity.of(2.817_940_3205e-15, "m");
  public static final Quantity MEC2 = Quantity.of(511.0, "keV");

  /**
   * K represents the macroscopic scale factor.
   */
  public static final Quantity K = Quantity.of(Math.pow(RADIUS_E.get(), 2) * AVOGADRO, PhysicalProperty.MOLAR_CROSS_SECTION);
}
