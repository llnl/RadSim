// --- file: gov/llnl/rtk/response/LLDEvaluator.java ---
/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.Cursor;
import gov.llnl.math.interp.SingleInterpolator;
import gov.llnl.rtk.data.EnergyScale;

/**
 *
 * @author nelson85
 */
public class LLDEvaluator
{
 
    // Lld function
  final SingleInterpolator.Evaluator lldEvaluator;
  int lldChannel = -1;
  int lldSampling = 0;
  
  private final LLDFunction lldFunction;
  
  public LLDEvaluator(LLDFunction lld)
  {
    this.lldFunction = lld;
    if (lld!=null && lld.function!=null)
      this.lldEvaluator = lld.function.get();
    else
      this.lldEvaluator = null;
  }
  
  /**
   * Decide the level of sampling required for the LLD calculations based on the
   * requested energy structure.
   *
   */
  void update(EnergyScale energyScale, Cursor cursor)
  {
    // The LLD may be a very sharp function (sharper than the inherit peak width)
    // so we need to figure out what channel it falls in so that we know bins
    // require finer integrations.
    int lldBins = lldFunction.energy.length;
    double lldLower = lldFunction.energy[0];
    double lldUpper = lldFunction.energy[lldBins - 1];

    lldChannel = cursor.seek(lldUpper) + 1;

    // keV per channel
    double deltaE0 = (lldUpper - lldLower) / lldBins;
    double deltaE1 = (energyScale.getEdge(lldChannel) - energyScale.getEdge(0)) / lldChannel;

    // Guess how many subsamples will be required for accuracy
    lldSampling = (int) (deltaE1 / deltaE0 + 1);
  }
}
