// --- file: gov/llnl/rtk/quality/FaultSet.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import java.io.Serializable;
import java.util.Collection;

/**
 * A collection of faults that occurred while processing a measurement.
 *
 * @author nelson85
 */
public interface FaultSet extends Collection<Fault>, Serializable
{
//  boolean getChanged();

  boolean hasFault();

  boolean isFatal();

  boolean resetRequested();

  void clearRecoverableFaults();

  void clearAllFaults();

  @Override
  public String toString();

//  public void clearChanged();
}
