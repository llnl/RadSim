// --- file: gov/llnl/rtk/ml/ReluActivation.java ---
package gov.llnl.rtk.ml;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class ReluActivation extends ActivationFunction
{
  @Override
  public double applyAsDouble(double x)
  {
    return Math.max(0, x);
  }

  
  @Override
  public List<NeuralModule> getComponents()
  {
    return Arrays.asList(this);
  }
  
  public boolean equals(Object o)
  {
    return (o instanceof ReluActivation);
  }
}
