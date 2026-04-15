// --- file: gov/llnl/rtk/ml/ActivationFunction.java ---
package gov.llnl.rtk.ml;

import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author nelson85
 */
public abstract class ActivationFunction implements NeuralModule, DoubleUnaryOperator
{
  public static ActivationFunction IDENTITY = new IdentityActivation();
  public static ActivationFunction RELU = new ReluActivation();

  @Override
  public abstract double applyAsDouble(double x);

  @Override
  public double[] forward(ModuleState state, double[] input)
  {
    ArrayState vstate = (ArrayState) state;
    vstate.ensure(input.length);
    for (int i = 0; i < input.length; ++i)
      vstate.state[i] = this.applyAsDouble(input[i]);
    return vstate.state;
  }

  @Override
  public ModuleState newState()
  {
    return new ArrayState();
  }

  @Override
  public int getInputSize()
  {
    return -1;
  }

  @Override
  public int getOutputSize()
  {
    return -1;
  }

}
