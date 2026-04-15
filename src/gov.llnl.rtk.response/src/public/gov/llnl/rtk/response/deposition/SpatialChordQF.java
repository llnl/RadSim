// --- file: gov/llnl/rtk/response/deposition/SpatialChordQF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.rtk.physics.Quantity;

/**
 * This will be the ML quantile to chord length distribution based on position.
 *
 * The model itself will likely be a singleton and we need an evaluator. We use
 * encoder projector based models, so the evaluator gets updated with a new
 * position. We then sweep to get a chord distribution from it.
 *
 * @author nelson85
 */
public interface SpatialChordQF
{

  // Maybe this should handle permution and scale
  public void setPosition(double x, double y, double z);

  default void setPosition(Quantity x, Quantity y, Quantity z)
  {
    setPosition(x.get(), y.get(), z.get());
  }

  /** Get a chord length in SI units for the specified quantile.
   * 
   * @param quantile
   * @return 
   */
  public double getChord(double quantile);

}
