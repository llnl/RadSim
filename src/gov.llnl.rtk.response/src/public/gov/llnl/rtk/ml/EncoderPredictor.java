// --- file: gov/llnl/rtk/ml/EncoderPredictor.java ---
package gov.llnl.rtk.ml;

/**
 *
 * @author nelson85
 */
public interface EncoderPredictor extends NeuralModule
{

  /**
   * Return a new state to use for evaluation.
   *
   * The state is use during evaluation to hold all intermediate results so that
   * ML can be stateless.
   *
   * @return
   */
  @Override
  ModuleState newState();

  EncoderPredictor encode(ModuleState eval, double[] xe);

  double[] predict(ModuleState eval, double[] xp);

  int getEncoderInputSize();

  int getPredictorInputSize();

  int getPredictorOutputSize();

}
