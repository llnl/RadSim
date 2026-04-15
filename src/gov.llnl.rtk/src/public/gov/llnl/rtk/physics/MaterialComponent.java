// --- file: gov/llnl/rtk/physics/MaterialComponent.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;

/**
 * Component for each nuclide of a Material.
 * 
 * A Material is stored per unit of substance.
 */
@ReaderInfo(MaterialComponentReader.class)
@WriterInfo(MaterialComponentWriter.class)
public interface MaterialComponent
{
  /**
   * Get the nuclide.
   *
   * @return
   */
  Nuclide getNuclide();

  /**
   * Get the activity of the source. Units will be defined by the nuclide. Units
   * should be SI.
   *
   * FIXME this is being used for both specific activity and activity. This
   * should duality should be removed.
   *
   * @return the activity of the source if known, or 0 if not available.
   */
  Quantity getActivity();

  /**
   * Get the mass fraction of a material for the component.
   *
   * This is used for non-radioactive components of a material.
   *
   * @return
   */
  double getMassFraction();

  double getAtomFraction();

  
  /**
   * Get the fraction of dose associated with the template that comes from this
   * nuclide.
   *
   * This is used for activity estimates based on dose.
   *
   * @return the dose fraction or 0 if not available.
   */
  default double getDoseFraction()
  { 
    return 0;
  }

}
