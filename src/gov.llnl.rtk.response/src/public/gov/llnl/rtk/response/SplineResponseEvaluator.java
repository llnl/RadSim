// --- file: gov/llnl/rtk/response/SplineResponseEvaluator.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.Spectrum;
import gov.llnl.math.Cursor;
import java.util.function.DoubleUnaryOperator;

class SplineResponseEvaluator extends FunctionResponseEvaluator
{
  final SpectralResponseFunctionSpline outer;
  final Cursor intervalCursor;

  final DoubleUnaryOperator resolutionEvaluator;
  final DoubleUnaryOperator efficiencyEvaluator;

  SplineResponseEvaluator(final SpectralResponseFunctionSpline outer)
  {
    super(outer, outer.energyScale, outer.peakShapeParameters, outer.lld);
    this.outer = outer;
    this.incomplete = outer.incomplete;

    // Set up a cursor to navigate response curves
    intervalCursor = new Cursor(outer.intervals, 0, outer.intervals.length);

    // Set up evaluators for resolution and efficiency
    //  (assuming it is defined for this detector)
    if (outer.resolutionFunction != null)
      this.resolutionEvaluator = outer.resolutionFunction.get();
    else
      this.resolutionEvaluator = null;

    if (outer.efficiencyFunction != null)
      this.efficiencyEvaluator = outer.efficiencyFunction.get();
    else
      this.efficiencyEvaluator = null;
  }

  @Override
  public DoubleUnaryOperator getResolutionFunction()
  {
    return resolutionEvaluator;
  }

  @Override
  public DoubleUnaryOperator getEfficiencyFunction()
  {
    return this.efficiencyEvaluator;
  }

  @Override
  public double getLower()
  {
    return 10.0;
  }

  @Override
  Cursor seek(double energy)
  {
    this.intervalCursor.seek(energy);
    return this.intervalCursor;
  }

  @Override
  public SplineResponseEntry getEntry(int i)
  {
    if (i + 1 >= outer.intervals.length)
      i = outer.intervals.length - 1;
    return outer.entries[i];
  }

  @Override
  public Spectrum getInternal()
  {
    return this.outer.getInternal(energyScale);
  }

  @Override
  public SpectralBufferDeferred deferred()
  {
    return new SpectralResponseDeferred();
  }
}
