/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xcom;

import gov.llnl.rtk.physics.Material;
import gov.llnl.rtk.physics.PhotonCrossSections;
import gov.llnl.rtk.physics.PhotonCrossSectionsEvaluator;
import gov.llnl.rtk.physics.PhysicalProperty;
import gov.llnl.rtk.physics.Units;

/**
 * Implementation of the PhotonCrossSections for XCOM.
 *
 * Because this class is stateful to deal with units, we will store the data in
 * CrossSectionsTable.
 *
 * @author nelson85
 */
class CrossSectionsImpl implements PhotonCrossSections
{

  static private final Units DEFAULT_INPUT_UNITS = Units.get("energy:MeV");
  static private final Units DEFAULT_OUTPUT_UNITS = Units.get("cross_section:cm2/g");
  final CrossSectionsTable table;

  CrossSectionsImpl(CrossSectionsTable table)
  {
    this.table = table;
  }

  /**
   * @return the material
   */
  @Override
  public Material getMaterial()
  {
    return table.material;
  }

  @Override
  public PhotonCrossSectionsEvaluator newEvaluator()
  {
    return new CrossSectionsEvaluatorImpl(this.table, this.DEFAULT_INPUT_UNITS, this.DEFAULT_OUTPUT_UNITS);
  }

}
