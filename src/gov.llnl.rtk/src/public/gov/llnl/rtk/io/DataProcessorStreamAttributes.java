// --- file: gov/llnl/rtk/io/DataProcessorStreamAttributes.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.RtkPackage;


/**
 *
 * @author forsyth2
 */
public class DataProcessorStreamAttributes
{
  public static final String URN = RtkPackage.getInstance().getNamespaceURI();

  /**
   * Requires index after. Example usage:
   * DataProcessorStreamAttributes.NEUTRON_DETECTOR + "[0]"
   */
  public static final String NEUTRON_DETECTOR = URN + "#neutronDetector"; // NeutronDetector

  /**
   * Requires index after. Example usage:
   * DataProcessorStreamAttributes.GAMMA_DETECTOR + "[0]"
   */
  public static final String GAMMA_DETECTOR = URN + "#gammaDetector"; // GammaDetector

  /**
   * Add all detectors prior to passing them off.
   */
  public static final String COMBINED = URN + "#combined"; // bool

  /**
   * Add all detectors prior to passing them off.
   */
  public static final String ENERGY_SCALE = URN + "#energyScale"; // bool

}
