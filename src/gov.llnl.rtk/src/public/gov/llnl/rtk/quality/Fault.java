// --- file: gov/llnl/rtk/quality/Fault.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public interface Fault extends Comparable<Fault>, Serializable
{
  /**
   *
   * @return the fault level;
   */
  FaultLevel getLevel();


  /**
   * @return the name
   */
  String getName();

  /**
   * @return the source
   */
  Object getSource();

  /**
   * A recoverable fault does not remain valid during the next execution cycle.
   *
   * @return recoverable
   */
  boolean isRecoverable();

  boolean isState();

  /**
   *
   * @return the description
   */
  String getDescription();
}
