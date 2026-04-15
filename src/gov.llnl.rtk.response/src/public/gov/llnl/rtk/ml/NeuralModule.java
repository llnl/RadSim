// --- file: gov/llnl/rtk/ml/Module.java ---
package gov.llnl.rtk.ml;

import java.util.List;

/**
 *
 * @author nelson85
 */
public interface NeuralModule
{
  public double[] forward(ModuleState state, double[] input);

  /**
   * This is called recursively to build up a workspace for each unit during
   * evaluation.
   *
   * @return
   */
  public ModuleState newState();

  public int getInputSize();

  public int getOutputSize();
  
  public List<NeuralModule> getComponents();
}
