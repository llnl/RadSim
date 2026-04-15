// --- file: gov/llnl/rtk/calibration/ChannelEnergyPair.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.ChannelEnergyPairReader;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
@ReaderInfo(ChannelEnergyPairReader.class)
final public class ChannelEnergyPair
        extends ExpandableObject
        implements Serializable, Comparable<ChannelEnergyPair>
{
  private static final long serialVersionUID = UUIDUtilities.createLong("ChannelEnergyPair-v2");

  public static enum Origin
  {
    CENTER, EDGE
  }
  private final double channel;  // Origin is always edge
  private final double energy;

  /**
   * Needed for the reader.
   */
  private ChannelEnergyPair()
  {
    channel = 0;
    energy = 0;
  }

  /**
   * Create a new pair with an center origin.
   *
   * @param channel
   * @param energy
   */
  public ChannelEnergyPair(double channel, double energy)
  {
    this.channel = channel + 0.5;
    this.energy = energy;
  }

  /**
   * Create a new pair with an undefined origin.
   *
   * @param channel
   * @param energy
   * @param origin
   */
  public ChannelEnergyPair(double channel, double energy, Origin origin)
  {
    this.channel = channel + (origin == Origin.CENTER ? 0.5 : 0);
    this.energy = energy;
  }

  /**
   * Copy constructor.
   *
   * @param pair
   */
  public ChannelEnergyPair(ChannelEnergyPair pair)
  {
    super(pair);
    this.channel = pair.channel;
    this.energy = pair.energy;
  }

  @Override
  public int compareTo(ChannelEnergyPair t)
  {
    return Double.compare(channel, t.channel);
  }

  /**
   * Get the channel referenced to the left edge of the first bin.
   *
   * @return
   */
  public double getChannelEdge()
  {
    return channel;
  }

  /**
   * Get the channel referenced to the center the first edge.
   *
   * @return
   */
  public double getChannelCenter()
  {
    return channel + 0.5;
  }

  public double getEnergy()
  {
    return energy;
  }

  public String getLabel()
  {
    return this.getAttribute(ChannelEnergyPairAttributes.LABEL, String.class);
  }

//<editor-fold desc="attributes" defaultstate="collapsed">
  /**
   *
   * @param label of this point (typically nuclide name).
   */
  @Reader.Attribute(name = "label")
  public void setLabel(String label)
  {
    this.setAttribute(ChannelEnergyPairAttributes.LABEL, label);
  }
//</editor-fold>
}
