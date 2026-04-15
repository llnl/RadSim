// --- file: gov/llnl/rtk/ml/ArrayState.java ---
package gov.llnl.rtk.ml;

import java.util.Arrays;

/**
 *
 * @author nelson85
 */
public class ArrayState implements ModuleState
{
  double[] state;

  public void ensure(int size)
  {
    if (state == null || state.length != size)
      state = new double[size];
  }

  void assign(double[] forward)
  {
    this.ensure(forward.length);
    for (int i = 0; i < forward.length; ++i)
      state[i] = forward[i];
  }
  
  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 29 * hash + Arrays.hashCode(this.state);
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
    final ArrayState other = (ArrayState) obj;
    return Arrays.equals(this.state, other.state);
  }

}
