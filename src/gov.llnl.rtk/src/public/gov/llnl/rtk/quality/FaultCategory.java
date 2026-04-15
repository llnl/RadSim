// --- file: gov/llnl/rtk/quality/FaultCategory.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

/**
 *
 * @author nelson85
 */
public class FaultCategory
{
  public static final String BAD_ENERGY_SCALE = "Bad Energy Scale";
  public static final String INVALID_CHANNEL_COUNT_SPIKE = "Invalid channel count spike";
  public static final String LOW_ENERGY_NOISE = "Low Energy Noise";
  public static final String INVALID_LIVETIME = "Invalid Livetime";
  public static final String SATURATION = "Saturated Signal";
  public static final String NULL_MEASUREMENT = "Null Measurement";
  public static final String INVALID_TIMESTAMP = "Invalid Timestamp";
  public static final String CALIBRATION_PROBLEM = "Calibration Problem";
  public static final String STATE_INITIALIZING = "Initializing";
  public static final String STATE_RE_INITIALIZING = "Re Initializing";
  public static final String STATE_REDUCED_SENSITIVITY = "Reduced Sensitivity";
  public static final String DATA_GAP = "Gap in data";
  public static final String DETECTOR_DISABLED = "Detector disabled";

}
