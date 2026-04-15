// --- file: gov/llnl/rtk/ml/CompositeState.java ---
package gov.llnl.rtk.ml;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 *
 * @author nelson85
 */
public class CompositeState implements ModuleState
{

  final ModuleState[] state;

  public CompositeState(int size)
  {
    state = new ModuleState[size];
  }

  public CompositeState(NeuralModule[] modules)
  {
    state = new ModuleState[modules.length];
    for (int i = 0; i < modules.length; ++i)
      this.state[i] = modules[i].newState();
  }

  public CompositeState(int size, IntFunction<ModuleState[]> allocator, Supplier<ModuleState> prod)
  {
    this.state = allocator.apply(size);
    for (int i = 0; i < state.length; ++i)
      this.state[i] = prod.get();
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 23 * hash + Arrays.deepHashCode(this.state);
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
    final CompositeState other = (CompositeState) obj;
    return Arrays.deepEquals(this.state, other.state);
  }
}
