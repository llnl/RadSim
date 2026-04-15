// --- file: gov/llnl/rtk/flux/FluxEvaluatorSpectrum.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.math.Cursor;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Specialization for FluxEvaluator for FluxSpectrum.
 */
class FluxEvaluatorSpectrum implements FluxEvaluator<FluxLine, FluxGroup>
{
  final double[] edges;
  final double[] counts;
  final Cursor cursor;

  FluxEvaluatorSpectrum(EnergyScale edges, double[] counts)
  {
    if (edges != null)
      this.edges = edges.getEdges();
    else
      this.edges = null;
    this.counts = counts;
    this.cursor = new Cursor(this.edges, 0, this.edges.length);
  }

  /**
   * Compute the integral over a region.
   *
   * FluxSpectrum is not aware of lines so the total is the same regardless of
   * the integration method.
   *
   * @param energy0 is the lower energy for the region.
   * @param energy1 is the upper energy for the region.
   * @param method is the integration method.
   * @return the total in the group.
   */
  @Override
  public double getIntegral(double energy0, double energy1, Set<FluxItem> items)
  {
    // Handle the case were counts are not available.
    if (counts == null)
      return 0;

    // Reposition the cursor
    int index = cursor.seek(energy0);

    // Tally over the region
    double total = 0;
    while (energy0 < energy1 && index + 1 < edges.length)
    {
      double e0 = edges[index];
      double e1 = edges[index + 1];
      double density = counts[index] / (e1 - e0);

      // Watch for partial bins.
      if (energy0 >= e0)
        e0 = energy0;
      if (e1 >= energy1)
        e1 = energy1;
      // Increment the total
      total += density * (e1 - e0);

      // Move to the next bin
      energy0 = e1;
      index += 1;
    }
    return total;
  }

  /**
   * Get the lines in the region.
   *
   * FluxSpectrum does not support lines so the lines are always empty.
   *
   * @param energy0 is the lower energy for the region.
   * @param energy1 is the upper energy for the region.
   * @return an empty list.
   */
  @Override
  public List getLines(double energy0, double energy1)
  {
    return Collections.EMPTY_LIST;
  }

}
