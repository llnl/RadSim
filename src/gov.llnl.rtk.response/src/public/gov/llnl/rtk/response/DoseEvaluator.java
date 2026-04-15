// --- file: gov/llnl/rtk/response/DoseEvaluator.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;


import gov.llnl.rtk.flux.Flux;
import java.time.Duration;

/**
 * DoseEvaluator is used to estimate the dose of a flux.
 *
 * A Dose evaluator converts a flux into a dose. This may take into account the
 * viewing angle of the observer or tissue dependent factors. Generally as high
 * resolution is not required, the transport of flux estimates for a dose
 * calculation should use faster transport methods rather then the high
 * resolution evaluators. Though there is nothing preventing a dose estimate
 * from being made using any flux.
 *
 * @author nelson85
 */
public interface DoseEvaluator
{
//<editor-fold desc="conditions" defaultstate="collapsed">
  /**
   * Set the distance for the dose calculation.
   *
   * @param distance in meters.
   */
  void setDistance(double distance);

  /**
   * Set the duration for the dose.
   *
   * Typically this is set to hours so the doses are reported in per hour
   * quantities. But any duration can be used.
   *
   * @param duration the duration to set
   */
  void setDuration(Duration duration);
//</editor-fold>
//<editor-fold desc="apply" defaultstate="collapsed">

  /**
   * Compute the absorbed dose.
   *
   * @param flux is the flux to be absorbed.
   * @return is dose in Gy per duration.
   */
  double getAbsorbedDose(Flux flux);

  /**
   * Get the equivalent dose.
   *
   * This is used to get the biological equivalent dose for gamma rays. Before
   * this issued, the view must be set to define the orientation of target
   * relative to the incoming flux.
   *
   * @param flux is the flux to be absorbed.
   * @return the equivalent dose in Sv per duration.
   */
  double getEquivalentDose(Flux flux);
//</editor-fold>
//<editor-fold desc="getters" defaultstate="collapsed">  

  /**
   * Get the response function that created this evaluator.
   *
   * @return a response function.
   */
  DoseResponseFunction getResponseFunction();

  /**
   * Get the distance that was applied to this evaluator.
   *
   * @return the distance to be used for the next apply.
   */
  double getDistance();

  /**
   * Get the duration that was applied to this evaluator.
   *
   * @return the duration to be used for the next apply.
   */
  Duration getDuration();

//</editor-fold>
}
