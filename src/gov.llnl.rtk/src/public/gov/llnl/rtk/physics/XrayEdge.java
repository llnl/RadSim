// --- file: gov/llnl/rtk/physics/XrayEdge.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.List;
import java.util.Map;

/**
 * The Xrays associated with a specific edge.
 *
 * @author nelson85
 */
public interface XrayEdge
{
  /**
   * Get the name of the xray transition.
   *
   * @return
   */
  String getName();

  /**
   * Get the xrays associated with this edge.
   *
   * @return a list of Xrays or an empty list if not available.
   */
  List<Xray> getXrays();

  /**
   * Get the Fluorescence Yield.
   *
   * FIXME what is the units on this or is it a scalar. The API is subject to
   * change depending on the need to add units.
   *
   * @return
   */
  double getFluorescenceYield();

  /**
   * Get the Coster Kronig table for this edge.
   *
   * FIXME what is the units for this. If if not a scaler then we may have to
   * revise the API.
   *
   * @return the transition table or null if not available.
   */
  Map<String, Double> getCosterKronig();

  Quantity getEnergy();

  double getJumpRatio();
}
