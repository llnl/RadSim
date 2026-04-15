// --- file: gov/llnl/rtk/ml/IdentityActivation.java ---
package gov.llnl.rtk.ml;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class IdentityActivation extends ActivationFunction
{
  @Override
  public double applyAsDouble(double x)
  {
    return x;
  }

  @Override
  public double[] forward(ModuleState state, double[] input)
  {
    // We are going to short cut here as there is no need to waste time. 
    // We will copy the vector just in case there is some connection that required lack of mutation.
    ArrayState vstate = (ArrayState) state;
    vstate.ensure(input.length);
    for (int i = 0; i < input.length; ++i)
      vstate.state[i] = input[i];
    return vstate.state;
  }

  @Override
  public List<NeuralModule> getComponents()
  {
    return Arrays.asList(this);
  }

  @Override
  public boolean equals(Object o)
  {
    return (o instanceof IdentityActivation);
  }
}
