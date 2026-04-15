// --- file: gov/llnl/rtk/response/deposition/LinearInterpolant.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.rtk.data.EnergyScale;

/**
 * Efficient monotonic-grid interpolator for mapping spectra between energy
 * grids.
 */
public class LinearInterpolant
{
  private EnergyScale energyScale;
  private double[] values;
  private int index = 0;

  /**
   * Assigns the energy scale and values to interpolate over.
   * @param scale
   * @param values
   */
  public void assign(EnergyScale scale, double[] values)
  {
    this.energyScale = scale;
    this.values = values;
    this.index = 0;
  }

  /**
   * Returns the interpolated value at energy v. Uses local bin width to guess
   * index for fast access. Falls back to binary search for large jumps or
   * non-uniform grids.
   */
  public double compute(double v)
  {
    // Assume: index is current bin, n = values.length

    double eL = energyScale.getEdge(index);
    double eR = energyScale.getEdge(index + 1);

    // 1) Decide direction
    if (v < eL)
    {
      // 2) Going down: check if we are already at the bottom
      if (index == 0)
        return values[0];

      // 3) Guess how many bins down using local width
      double w = (eR - eL)+1e-6;
      int guess = (int) ((eL - v) / w + 0.5);
      int target = index - guess;

      // 4) If guess is large or out of range, binary search using current as upper
      if (guess > 4 || target < 0)
        index = binarySearch(v, 0, index);
      else
        // 5) Else, linear search down
        while (index > 0 && v < energyScale.getEdge(index))
          --index;

    }
    else if (v >= eR)
    {
      // 2a) Going up: check if we are already at the top
      if (index >= values.length - 2)
        return values[values.length - 1];

      // 3a) Guess how many bins up using local width
      double w = (eR - eL)+1e-6;
      int guess = (int) ((v - eR) / w + 0.5);
      int target = index + 1 + guess;

      // 4a) If guess is large or out of range, binary search using current as lower
      if (guess > 4 || target >= values.length - 1)
        index = binarySearch(v, index, values.length - 2);
      else
        // 5a) Else, linear search up
        while (index < values.length - 2 && v >= energyScale.getEdge(index + 1))
          ++index;
    }
    // else: already in bin

    // Interpolate
    double cL = energyScale.getEdge(index);
    double cR = energyScale.getEdge(index+1);
    double f = (v - cL) / (cR - cL);
    if (f < 0)
      f = 0;
    if (f > 1)
      f = 1;
    return values[index] * (1 - f) + values[index + 1] * f;
  }

  /**
   * Binary search to find the bin containing v. Returns the index i such that
   * edge[i] <= v < edge[i+1]
   */
  private int binarySearch(double v, int low, int high)
  {
    while (low <= high)
    {
      int mid = (low + high) / 2;
      double e1 = energyScale.getEdge(mid);
      double e2 = energyScale.getEdge(mid + 1);
      if (v < e1)
        high = mid - 1;
      else if (v >= e2)
        low = mid + 1;
      else
        return mid;
    }
    // Should not happen if v is in range
    return Math.max(0, Math.min(values.length - 2, low));
  }
}
