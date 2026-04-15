// --- file: gov/llnl/rtk/response/DoseResponseFunction.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

/**
 * A DoseResponseFunction is used to estimate the health dose for a flux.
 *
 * There are numerous standards that are used to compute dose and they have
 * different input conditions when converting to dose. The absorbed dose is the
 * total amount of energy that is absorbed by a target over a period of time.
 * However, not all tissue is effected equally, thus equivalent dose is used to
 * compute the effects of absorbing that dose in tissue. Typically parameters
 * regarding the tissue that was targeted must be specified in the form of a
 * view.
 *
 * For details consult the specific dose response function documentation.
 * Currently we only support ICRP-119.
 *
 * @author nelson85
 */
public interface DoseResponseFunction
{
  /**
   * Get the detector vendor.
   *
   * @return
   */
  String getVendor();

  /**
   * Get the detector model name.
   *
   * @return
   */
  String getModel();

  /**
   * Create a new dose evaluator.
   *
   * Evaluators are not reentrant. Thus each thread should use its own
   * evaluator.
   *
   * @return a new evaluator.
   */
  DoseEvaluator newEvaluator();
}
