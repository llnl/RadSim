// --- file: gov/llnl/rtk/ml/LeakyReluActivation.java ---
package gov.llnl.rtk.ml;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class LeakyReluActivation extends ActivationFunction
{

  public final double slope;

  public LeakyReluActivation(double slope)
  {
    this.slope = slope;
  }

  @Override
  public double applyAsDouble(double x)
  {
    return x > 0 ? x : slope * x;
  }

    @Override
  public int hashCode()
  {
    int hash = 3;
    hash = 29 * hash + (int) (Double.doubleToLongBits(this.slope) ^ (Double.doubleToLongBits(this.slope) >>> 32));
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
    final LeakyReluActivation other = (LeakyReluActivation) obj;
    return Double.doubleToLongBits(this.slope) == Double.doubleToLongBits(other.slope);
  }
  
  
  @Override
  public List<NeuralModule> getComponents()
  {
    return Arrays.asList(this);
  }
}
