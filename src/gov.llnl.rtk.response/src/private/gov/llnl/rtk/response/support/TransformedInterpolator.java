// --- file: private/gov/llnl/rtk/response/support/TransformedInterpolator.java ---
/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import gov.llnl.math.interp.SingleInterpolator;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author nelson85
 */
public class TransformedInterpolator implements SingleInterpolator
{
  SingleInterpolator base;
  DoubleUnaryOperator transform;

  public TransformedInterpolator(SingleInterpolator base, DoubleUnaryOperator transform)
  {
    this.base = base;
    this.transform = transform;
  }

  @Override
  public Evaluator get()
  {
    return new TransformedEvaluator(base.get());
  }

  private class TransformedEvaluator implements Evaluator
  {
    Evaluator evaluator;

    public TransformedEvaluator(Evaluator evaluator)
    {
      this.evaluator = evaluator;
    }

    @Override
    public void seek(double x)
    {
      this.evaluator.seek(x);
    }

    @Override
    public double evaluate()
    {
      return transform.applyAsDouble(this.evaluator.evaluate());
    }
  }
}
