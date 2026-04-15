// --- file: gov/llnl/rtk/calibration/DetectorCalibratorAttributes.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;

/**
 *
 * @author seilhan3
 */
public class DetectorCalibratorAttributes
{
  public static final String URN = RtkPackage.getInstance().getNamespaceURI();

// Common
  public static final String ENERGY_SCALE_MAPPER = URN + "#energyScaleMapper"; // String
  public static final String DEFAULT_ENERGY_SCALE = URN + "#defaultEnergyScale"; // String
}
