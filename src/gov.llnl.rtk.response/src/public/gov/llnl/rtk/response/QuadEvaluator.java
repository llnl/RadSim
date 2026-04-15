// --- file: gov/llnl/rtk/response/QuadEvaluator.java ---
package gov.llnl.rtk.response;

import gov.llnl.math.interp.SingleInterpolator;

/**
 *
 * @author nelson85
 */
class QuadEvaluator implements SingleInterpolator.Evaluator
{
  SingleInterpolator.Evaluator base;

  QuadEvaluator(SingleInterpolator.Evaluator base)
  {
    this.base = base;
  }

  @Override
  public void seek(double x)
  {
    base.seek(x);
  }

  @Override
  public double evaluate()
  {
    double v = base.evaluate();
    v = v * v;
    v = v * v;
    return v;
  }

}
