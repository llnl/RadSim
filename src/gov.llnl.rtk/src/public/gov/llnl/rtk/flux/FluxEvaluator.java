// --- file: gov/llnl/rtk/flux/FluxEvaluator.java ---
/* 
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import java.util.List;
import java.util.Set;

/**
 * Cursor for extracting information from a flux.
 *
 * A calculator used to extract values from a flux. This acts as a cursor to
 * improve speed when converting from one flux format to another. This code is
 * not reentrant, so each thread will need to use its own calculator.
 *
 * @author nelson85
 * @param <L> is the type of line stored in the flux.
 * @param <G> is the type of group stored in the flux.
 */
public interface FluxEvaluator<L extends FluxLine, G extends FluxGroup>
{

  /**
   * Get the total integrated flux between energies including lines and groups.
   *
   * @param energy0 is the lower bound for the integral.
   * @param energy1 is the upper bound for the integral.
   * @param items
   * @return
   */
  double getIntegral(double energy0, double energy1, Set<FluxItem> items);

  /**
   * Get the lines within an interval.
   *
   * This is only defined if the flux representation has lines defined.
   *
   * @param energy0 is the lower bound for the selection.
   * @param energy1 is the upper bound for the selection.
   * @return
   */
  List<L> getLines(double energy0, double energy1);

}
