// --- file: gov/llnl/rtk/calibration/ChannelEnergyPairAttributes.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.SpectrumAttributes;

/**
 *
 * @author seilhan3
 */
public class ChannelEnergyPairAttributes
{
  public static final String URN = RtkPackage.getInstance().getNamespaceURI();

// Common
  public static final String LABEL = URN + "#label"; // String
  public final static String TEMPERATURE = SpectrumAttributes.TEMPERATURE;
  public final static String CHANNEL_UNCERTAINTY = URN + "#channelUncertainty";
  public final static String RESOLUTION = URN + "#resolution";
  public final static String RESOLUTION_UNCERTAINTY = URN + "#resolutionUncertainty";

}
