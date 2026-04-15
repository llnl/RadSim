// --- file: gov/llnl/rtk/data/Decimation.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnTable;
import gov.llnl.rtk.EnergyScaleClient;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.io.Serializable;

/**
 * FIXME Decimation needs to be a factory so that it is safe for threading. It
 * should produce an object that rebins to the proper structure.
 *
 * @author nelson85
 */
@ReaderInfo(DecimationReader.class)
@WriterInfo(DecimationWriter.class)
public class Decimation extends ExpandableObject implements Serializable, Rebinner, EnergyScale, EnergyScaleClient
{
  EnergyScale inputScale;
  double[] targetEnergyEdges;
  double[] actualEnergyBins;
  int[] channelEdges;

  @Override
  public void applyEnergyScale(EnergyScale scale)
  {
    if (scale == null)
      throw new NullPointerException("energy scale is null");
    if (targetEnergyEdges == null)
      throw new NullPointerException("decimation energy edges is null");
    if (inputScale == scale)
      return;
    this.inputScale = scale;
    double[] edges = scale.getEdges();
    channelEdges = EnergyBinUtilities.convertToChannels(edges, targetEnergyEdges);
    int n = channelEdges.length;
    actualEnergyBins = new double[channelEdges.length];
    for (int i = 0; i < n; ++i)
      actualEnergyBins[i] = edges[channelEdges[i]];
  }

//<editor-fold desc="rebin">  
  @Override
  public double[] rebinArray(double[] input)
  {
    double[] out = new double[channelEdges.length - 1];
    for (int i = 0; i < channelEdges.length - 1; ++i)
    {
      for (int j = channelEdges[i]; j < channelEdges[i + 1]; ++j)
      {
        out[i] += input[j];
      }
    }
    return out;
  }

  @Override
  public int[] rebinArray(int[] input)
  {
    int[] out = new int[channelEdges.length - 1];
    for (int i = 0; i < channelEdges.length - 1; ++i)
    {
      for (int j = channelEdges[i]; j < channelEdges[i + 1]; ++j)
      {
        out[i] += input[j];
      }
    }
    return out;
  }

  public MatrixColumnTable rebinMatrix(Matrix input)
  {
    if (channelEdges == null)
      throw new RuntimeException("Decimation used without apply energy scale.");
    MatrixColumnTable out = new MatrixColumnTable(channelEdges.length - 1, input.columns());
    for (int k = 0; k < input.columns(); ++k)
    {
      for (int i = 0; i < channelEdges.length - 1; ++i)
      {
        double sum = 0;
        for (int j = channelEdges[i]; j < channelEdges[i + 1]; ++j)
        {
          sum += input.get(j, k);
        }
        out.set(i, k, sum);
      }
    }
    return out;
  }
//</editor-fold>
//<editor-fold desc="getters/setters">

  /**
   * Get the input structure that decimation will process from.
   *
   * @return
   */
  @Override
  public EnergyScale getInputBins()
  {
    return inputScale;
  }

  /**
   * Get the actual energy inputScale for the decimation. This is computed from
   * the bin structure for the spectrum and the target bin structure.
   *
   * @return the actual energy inputScale after decimation.
   */
  @Override
  public double[] getEdges()
  {
    return this.actualEnergyBins;
  }

  public void setTargetEnergyEdges(double[] fromString)
  {
    this.targetEnergyEdges = fromString;
  }

  public int[] getChannelEdges()
  {
    return this.channelEdges;
  }
  
  public void setChannelEdges(int[] fromString)
  {
    this.channelEdges = fromString;
  }

  /**
   * Get the number of energy edges used by this decimation.
   *
   * @return
   */
  public int size()
  {
    return targetEnergyEdges.length;
  }
//</editor-fold>

  @Override
  public int getChannels()
  {
    return targetEnergyEdges.length - 1;
  }

  @Override
  public double getEdge(int edge)
  {
    return this.actualEnergyBins[edge];
  }

  @Override
  public double[] getCenters()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int findEdgeFloor(double energy)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int findEdgeCeiling(double energy)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double findBin(double energy)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double getEnergyOfEdge(double edge)
  {
    throw new UnsupportedOperationException("Not supported yet."); 
  }

}
