// --- file: gov/llnl/rtk/data/RadiationSensorAttributes.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;

/**
 *
 * @author seilhan3
 */
public class RadiationSensorAttributes
{
  public static final String URN = RtkPackage.getInstance().getNamespaceURI();
  // Common
  public static final String NAME = URN + "#name"; // String
  public static final String UUID = URN + "#uuid";  // Instant

}
