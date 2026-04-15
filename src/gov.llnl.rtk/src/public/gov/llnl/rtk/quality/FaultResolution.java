// --- file: gov/llnl/rtk/quality/FaultResolution.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

/**
 *
 * @author seilhan3
 */
public enum FaultResolution
{
  /**
   * no hope of recovery, enter non-recoverable state.
   */
  TERMINATE,
  /**
   * Ignore current sample, do not process sample validity is unknown and not
   * used. Reset the processor back to initial state
   */
  RESET,
  /**
   * Issue reacquire command to tracking algorithm and do not process current
   * sample since the validity is unknown.
   */
  REACQUIRE,
  /**
   * Current sample is ignored, no other actions are taken.
   */
  IGNORE

}
