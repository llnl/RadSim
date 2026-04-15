// --- file: gov/llnl/rtk/response/RenderItem.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import java.util.EnumSet;

/**
 * Used to annotate physics and decide what features to render.
 * @author nelson85
 */
public enum RenderItem
{
  PHOTOELECTRIC, // Photoelectric peak
  // FIXME There was a bug in the model building such that numbers are off.
  //  Kludge for now.
  XRAY_SCATTER, // xray lines produced externally
  ANNIHILATION, // 511 keV from external sources
  ESCAPE_SINGLE, // partial capture from pair production
  ESCAPE_DOUBLE, // partial capture from pair production
  XRAY_ESCAPE, // xrays produced internally (move with photopeak)
  CONTINUUM, // anything that isn't line
//  INCOMPLETE, // incomplete photon capture (found in CZT detectors)
  LLD; // the cut off from the electronics

  public static EnumSet<RenderItem> LINES = EnumSet.of(PHOTOELECTRIC, ANNIHILATION, ESCAPE_SINGLE, ESCAPE_DOUBLE, XRAY_ESCAPE, XRAY_SCATTER);
  public static EnumSet<RenderItem> GROUPS = EnumSet.of(CONTINUUM, LLD);
  public static EnumSet<RenderItem> ALL = EnumSet.of(PHOTOELECTRIC, RenderItem.values());
}
