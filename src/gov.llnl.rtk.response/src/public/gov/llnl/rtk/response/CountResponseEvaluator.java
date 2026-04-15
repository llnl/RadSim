// --- file: gov/llnl/rtk/response/CountResponseEvaluator.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.flux.Flux;

/**
 *
 * @author nelson85
 */
public interface CountResponseEvaluator extends ResponseEvaluator
{

  CountResponseFunction getResponseFunction();

  /**
   * Convert a flux into counts.
   *
   * @param flux
   * @return
   */
  double apply(Flux flux);

}
