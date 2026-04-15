// --- file: gov/llnl/rtk/physics/DoseTable.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.Singletons.Singleton;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;

/**
 *
 * @author nelson85
 */
@ReaderInfo(DoseTableReader.class)
@WriterInfo(DoseTableWriter.class)
public interface DoseTable extends Singleton
{
  DoseTable INSTANCE = RtkPackage.loadResource(DoseTable.class, "gov/llnl/rtk/resources/dose.xml.gz");

  static DoseTable getInstance()
  {
    return INSTANCE;
  }

  /**
   * Get the version number for the dose conversion file.
   *
   * @return the version number.
   */
  int getVersion();

  /**
   * Compute the dose conversion (Dose/Activity) in units of Sv/hr/Ci @ 1m
   *
   * @param nuclide is the name of the nuclide.
   * @param Z is the atomic number of the shielding.
   * @param AD is the areal density of the shielding.
   * @return the dose conversion or -1 if not available.
   */
  double getDoseToActivityConversion(Nuclide nuclide, double Z, double AD);

}
