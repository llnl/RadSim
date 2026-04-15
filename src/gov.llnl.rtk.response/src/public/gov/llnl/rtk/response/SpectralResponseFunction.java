// --- file: gov/llnl/rtk/response/SpectralResponseFunction.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import java.util.Map;

/**
 * Generic detector response function for a gamma sensor.
 *
 * @author nelson85
 */
public interface SpectralResponseFunction extends ResponseFunction
{
  public static String EMG_THETA = "emg.theta";
  public static String EMG_NEGATIVE_TAIL = "emg.negative_tail";
  public static String EMG_POSITIVE_TAIL = "emg.positive_tail";
  
  /**
   * Get the default energy scale for this response.
   *
   * @return the energy scale.
   */
  EnergyScale getEnergyScale();

  /**
   * Create a new evaluator for this response function.
   *
   * @return a new evaluator.
   */
  SpectralResponseEvaluator newEvaluator();
  
  Map<String, Double> getParameters();
}
