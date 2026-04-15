// --- file: gov/llnl/rtk/data/EnergyBinUtilities.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.RebinUtilities;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnTable;
import java.util.Arrays;

/**
 *
 * @author nelson85
 */
public class EnergyBinUtilities
{

  /**
   * Converts an energy to a edge. Either gives the floor or the ceiling
   * depending on the ceiling parameter.
   *
   * @param energy
   * @param ceiling is true to get the upper edge of the channel and false to
   * get the lower edge.
   * @return the channel number that contains this energy.
   */
  public static int convertToEdge(double[] scale, double energy, boolean ceiling)
  {
    if (scale == null)
    {
      throw new NullPointerException("scale is null");
    }
    if (energy > scale[scale.length - 1])
      return scale.length - 1;
    int i = Arrays.binarySearch(scale, energy);
    if (i >= 0)
      return i;
    i = -i - 1;
    if (!ceiling)
      i--;
    if (i < 0)
      i = 0;
    if (i > scale.length)
      i = scale.length;
    if (i == scale.length)
      i--;
    return i;
  }

  public static int[] convertToChannels(double[] scale, double[] energyEdges)
  {
    int n = energyEdges.length;
    int[] channelEdges = new int[n];
    for (int i = 0; i < n - 1; i++)
    {
      channelEdges[i] = convertToEdge(scale, energyEdges[i], false);
    }
    channelEdges[n - 1] = convertToEdge(scale, energyEdges[n - 1], true);
    return channelEdges;
  }

  /**
   * Get a vector of data rebinned into a stucture. This may not produce a copy
   * of the data, so the data should produced should not be modified.
   *
   * @param spectrum
   * @param edges
   * @return
   * @throws gov.llnl.math.RebinUtilities.RebinException
   */
  public static double[] rebin(Spectrum spectrum, EnergyScale edges)
          throws RebinUtilities.RebinException
  {
    // FIXME this code does not deal with the valid range of the spectrum.

    // if we are requesting no change in structure then just return the data
    if (edges == spectrum.getEnergyScale() || edges == null)
      return DoubleArray.copyOf(spectrum.toDoubles());

    // if we are requesting a new structure, see if the 
    if (edges instanceof Rebinner)
    {
      Rebinner rebinner = (Rebinner) edges;
      if (spectrum.getEnergyScale() != rebinner.getInputBins())
        throw new RebinUtilities.RebinException("Input structure mismatch");
      return rebinner.rebinArray(spectrum.toDoubles());
    }

    // Otherwise apply the rebin to the data
    double[] b1 = spectrum.getEnergyScale().getEdges();
    double[] b2 = edges.getEdges();
    return RebinUtilities.rebin(spectrum.toDoubles(), b1, b2);
  }

  public static Matrix.ColumnAccess rebin(Matrix.ColumnAccess in, EnergyScale inBins, EnergyScale outBins) throws RebinUtilities.RebinException
  {
    if (inBins == outBins)
      return in;

    MatrixColumnTable out = new MatrixColumnTable(outBins.getEdges().length - 1, in.columns());
    if (outBins instanceof Rebinner)
    {
      Rebinner rebinner = (Rebinner) outBins;
      if (inBins != rebinner.getInputBins())
        throw new RebinUtilities.RebinException("Input structure mismatch");
      for (int i = 0; i < in.columns(); ++i)
      {
        out.assignColumn(rebinner.rebinArray(in.accessColumn(i)), i);
      }
      return out;
    }

    // Fall back to plain old rebin
    double[] b1 = inBins.getEdges();
    double[] b2 = outBins.getEdges();
    for (int i = 0; i < in.columns(); ++i)
    {
      out.assignColumn(RebinUtilities.rebin(in.accessColumn(i), b1, b2), i);
    }
    return out;
  }

}
