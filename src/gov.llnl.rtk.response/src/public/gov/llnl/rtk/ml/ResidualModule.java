// --- file: gov/llnl/rtk/ml/ResidualModule.java ---
package gov.llnl.rtk.ml;

import static gov.llnl.math.DoubleArray.addAssign;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author nelson85
 */
public class ResidualModule extends SequentialModule
{

  final NeuralModule residualLayer;

  public ResidualModule(NeuralModule residualLayer, NeuralModule... modules)
  {
    super(modules);
    this.residualLayer = residualLayer;
  }

  @Override
  public double[] forward(ModuleState state, double[] input)
  {
    double[] x = super.forward(state, input);
    CompositeState s = (CompositeState) state;

    if (residualLayer != null)
    {
      double[] residual = input;
      if (!(residualLayer instanceof IdentityActivation))
        residual = residualLayer.forward(s.state[this.modules.length], input);
      addAssign(x, residual);
    }
    return x;
  }

  @Override
  public ModuleState newState()
  {
    CompositeState s = new CompositeState(modules.length + 1);
    for (int i = 0; i < modules.length; ++i)
      s.state[i] = modules[i].newState();

    if (residualLayer != null)
      s.state[modules.length] = residualLayer.newState();
    return s;
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 79 * hash + Objects.hashCode(this.residualLayer);
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
    final ResidualModule other = (ResidualModule) obj;
    if (!Objects.equals(this.residualLayer, other.residualLayer))
      return false;
    return Arrays.deepEquals(this.modules, other.modules);
  }
  
    @Override
  public List<NeuralModule> getComponents()
  {
    return Stream.of(
            Arrays.asList(modules),
            Arrays.asList(this.residualLayer)
        )
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }
  
}
