// --- file: gov/llnl/rtk/quality/FaultLevel.java ---
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
public enum FaultLevel
{
  /**
   * crash and burn (DataProcessor Only).
   */
  FATAL(0),
  /**
   * current input has problem and is being ignored.
   */
  ERROR(1),
  /**
   * current state has problem, input incorporated but no output.
   */
  WARNING(2),
  /**
   * information.
   */
  INFO(3),
  /**
   * need to pull reset on dataprocessor.
   */
  RESET(4);

  private final int level;

  FaultLevel(int level)
  {
    this.level = level;
  }

}
