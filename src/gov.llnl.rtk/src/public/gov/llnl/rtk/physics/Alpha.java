// --- file: gov/llnl/rtk/physics/Alpha.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public interface Alpha extends EnergyEmission
{
  Quantity getHindrance();
}
