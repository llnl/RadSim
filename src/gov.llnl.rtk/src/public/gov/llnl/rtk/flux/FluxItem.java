// --- file: gov/llnl/rtk/flux/FluxItem.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import java.util.EnumSet;

/**
 *
 * @author nelson85
 */
public enum FluxItem
{
  LINE,
  GROUP;

  public static EnumSet<FluxItem> LINES = EnumSet.of(LINE);
  public static EnumSet<FluxItem> GROUPS = EnumSet.of(GROUP);
  public static EnumSet<FluxItem> ALL = EnumSet.of(LINE, GROUP);

}
