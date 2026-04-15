// --- file: gov/llnl/rtk/response/CountResponseFunction.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

/**
 * CountResponseFunction represents a detector which measures counts per
 * interval.
 *
 * This can be used to represent a simple gross counting device such as a GM
 * tube. Or it can be used to represent a detector model in which the current
 * displayed information is not spectral such as a rate display for a spectral
 * instrument. A DetectorResponseFunction may implement both a
 * CountResponseFunction and SpectralResponseFunction if both outputs are
 * produced.
 *
 * FIXME the time interval and distance may be relevant to the output,
 * especially if we are going to include such effect as pileup and saturation
 * effects. Thus this interface will need to be revised when we get to those
 * effects.
 *
 * @author nelson85
 */
public interface CountResponseFunction extends ResponseFunction
{

  /**
   * Create a new count evaluator.
   *
   * Evaluators are not reentrant. Thus each thread should use its own
   * evaluator.
   *
   * @return a new evaluator.
   */
  CountResponseEvaluator newEvaluator();

}
