// --- file: gov/llnl/rtk/ml/SequentialModule.java ---
package gov.llnl.rtk.ml;

import java.util.Arrays;
import java.util.List;

public class SequentialModule implements NeuralModule
{

  final NeuralModule[] modules;
  private final int inputSize;
  private final int outputSize;

  public SequentialModule(NeuralModule... modules)
  {
    this.modules = modules;
    this.inputSize = findFirstInputSize(modules);
    this.outputSize = findLastOutputSize(modules);

    // Validate chain compatibility
    int lastOutSize = -1;
    for (int i = 0; i < modules.length; ++i)
    {
      int inSize = modules[i].getInputSize();
      // If lastOutSize and inSize are both known, check compatibility
      if (lastOutSize != -1 && inSize != -1 && lastOutSize != inSize)
      {
        throw new IllegalArgumentException(
                "SequentialModule chain mismatch: output size of module " + (i - 1)
                + " (" + lastOutSize + ") does not match input size of module " + i
                + " (" + inSize + ")"
        );
      }
      int outSize = modules[i].getOutputSize();
      // Update lastOutSize only if outSize is known
      if (outSize != -1)
        lastOutSize = outSize;
    }
  }

  @Override
  public double[] forward(ModuleState state, double[] input)
  {
    CompositeState s = (CompositeState) state;
    double[] x = input;
    for (int i = 0; i < modules.length; ++i)
      x = modules[i].forward(s.state[i], x);
    return x;
  }

  @Override
  public ModuleState newState()
  {
    CompositeState s = new CompositeState(modules.length);
    for (int i = 0; i < modules.length; ++i)
      s.state[i] = modules[i].newState();
    return s;
  }

  private static int findFirstInputSize(NeuralModule[] modules)
  {
    for (NeuralModule m : modules)
    {
      int s = m.getInputSize();
      if (s != -1)
        return s;
    }
    throw new IllegalArgumentException("No module with known input size");
  }

  private static int findLastOutputSize(NeuralModule[] modules)
  {
    for (int i = modules.length - 1; i >= 0; --i)
    {
      int s = modules[i].getOutputSize();
      if (s != -1)
        return s;
    }
    throw new IllegalArgumentException("No module with known output size");
  }

  @Override
  public int getInputSize()
  {
    return inputSize;
  }

  @Override
  public int getOutputSize()
  {
    return outputSize;
  }

  @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 61 * hash + Arrays.deepHashCode(this.modules);
    hash = 61 * hash + this.inputSize;
    hash = 61 * hash + this.outputSize;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final SequentialModule other = (SequentialModule) obj;
    if (this.inputSize != other.inputSize)
      return false;
    if (this.outputSize != other.outputSize)
      return false;
    return Arrays.deepEquals(this.modules, other.modules);
  }

  @Override
  public List<NeuralModule> getComponents()
  {
    return Arrays.asList(this.modules);
  }
}
