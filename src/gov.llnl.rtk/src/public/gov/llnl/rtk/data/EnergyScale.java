// --- file: gov/llnl/rtk/data/EnergyScale.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.Expandable;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.io.Serializable;

/**
 *
 * FIXME this should be renamed to spectrum scale. We have both energy scale and
 * charge scales that will be using it. Thus the generic concept of a spectrum
 * scale is required.
 *
 * @author nelson85
 */
@ReaderInfo(EnergyScaleReader.class)
@WriterInfo(EnergyScaleWriter.class)
public interface EnergyScale extends Expandable, Serializable
{
  /**
   * Get the number of channels in the spectrum.
   *
   * @return
   */
  int getChannels();

  /**
   * Get the energy corresponding to an edge.
   *
   * @param edge
   * @return the energy boundary for this edge.
   */
  double getEdge(int edge);
  
  default double getCenter(int channel)
  {
    return (getEdge(channel)+getEdge(channel+1))/2;
  }

  /**
   * Get the energy associated with the fractional bin.
   *
   * @param edge
   * @return
   */
  double getEnergyOfEdge(double edge);

  /**
   * Convert the energy scale to bin centers. This is primarily used for
   * plotting purpose.
   *
   * @return the centers for each channel.
   */
  double[] getCenters();

  /**
   * Get the energy edges. There is one more edge than channel.
   *
   * @return the edge for each channel.
   */
  double[] getEdges();

  /**
   * Find the bin which contains this energy. Bins are defined as being
   * inclusive of their lower edge.
   *
   * @param energy
   * @return the edge number for the first edge equal to or below this energy.
   */
  int findEdgeFloor(double energy);

  /**
   * Find the bin which is above this energy.
   *
   * @param energy
   * @return the edge number for the first bin equal to or above this energy.
   */
  int findEdgeCeiling(double energy);

  /**
   * Find the fractional bin associated with this energy with. The returned bin
   * has an edge origin, if you need a center origin, subtract 0.5
   *
   * @param energy
   * @return fractional bins
   */
  double findBin(double energy);
  
  /** 
   * Get the highest energy that is in scale.
   * 
   * @return 
   */
  default double getMaximumEnergy()
  {
    return this.getEdge(this.getChannels());
  }

}
