// --- file: gov/llnl/rtk/physics/DoubleTransformer.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

@FunctionalInterface
public interface DoubleTransformer
{
  double apply(double value);

}
