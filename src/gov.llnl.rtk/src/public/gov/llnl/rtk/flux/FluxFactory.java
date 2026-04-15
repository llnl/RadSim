// --- file: gov/llnl/rtk/flux/FluxFactory.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

/**
 * Utilities for testing.
 *
 * @author nelson85
 */
public class FluxFactory
{

  /**
   * Used in testing.
   *
   * @param energy
   * @param intensity
   * @return
   */
  public static Flux monoenergetic(double energy, double intensity)
  {
    FluxBinned out = new FluxBinned();
    out.addPhotonLine(new FluxLineStep(energy, intensity, 0));
    return out;
  }

  /**
   * Used in testing.
   *
   * @param energy0
   * @param energy1
   * @param intensity
   * @return
   */
  public static Flux group(double energy0, double energy1, double intensity)
  {
    FluxBinned out = new FluxBinned();
    out.addPhotonGroup(new FluxGroupBin(energy0, energy1, intensity));
    return out;
  }

}
