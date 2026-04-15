// --- file: gov/llnl/rtk/response/CompoundEvaluator.java ---
package gov.llnl.rtk.response;

import gov.llnl.math.interp.SingleInterpolator;

/**
 *
 * @author nelson85
 */
class CompoundEvaluator implements SingleInterpolator.Evaluator
{
  SingleInterpolator.Evaluator base1;
  SingleInterpolator.Evaluator base2;

  CompoundEvaluator(SingleInterpolator.Evaluator base1, SingleInterpolator.Evaluator base2)
  {
    this.base1 = base1;
    this.base2 = base2;
  }

  @Override
  public void seek(double x)
  {
    base1.seek(x);
    base2.seek(x);
  }

  @Override
  public double evaluate()
  {
    return base1.evaluate() * base2.evaluate();
  }

}
