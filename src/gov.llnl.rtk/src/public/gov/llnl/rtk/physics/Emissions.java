// --- file: gov/llnl/rtk/physics/Emissions.java ---
/* 
 * Copyright 2023, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.List;
import java.util.stream.Collectors;

public interface Emissions
{

  /**
   * Get the all of the emissions that are produced.
   *
   * This will generally be an unmodifiable list. This will generally be a
   * polymorphic list with many different emission types.
   *
   * @return
   */
  List<Emission> getEmissions();

  /**
   * Get the relationships between the emissions.
   *
   * @return
   */
  List<EmissionCorrelation> getCorrelations();

  default List<Gamma> getGammas()
  {
    return getEmissions().stream().filter(p -> p instanceof Gamma).map(p -> (Gamma) p).collect(Collectors.toList());
  }

  default List<Beta> getBetas()
  {
    return getEmissions().stream().filter(p -> p instanceof Beta).map(p -> (Beta) p).collect(Collectors.toList());
  }

  default List<Alpha> getAlphas()
  {
    return getEmissions().stream().filter(p -> p instanceof Alpha).map(p -> (Alpha) p).collect(Collectors.toList());
  }

  default List<ElectronCapture> getElectronCaptures()
  {
    return getEmissions().stream().filter(p -> p instanceof ElectronCapture).map(p -> (ElectronCapture) p).collect(Collectors.toList());
  }

  default List<Positron> getPositrons()
  {
    return getEmissions().stream().filter(p -> p instanceof Positron).map(p -> (Positron) p).collect(Collectors.toList());
  }

  default List<Xray> getXrays()
  {
    return getEmissions().stream().filter(p -> p instanceof Xray).map(p -> (Xray) p).collect(Collectors.toList());
  }

}
