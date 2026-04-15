// --- file: gov/llnl/rtk/ml/ArchEPT.java ---
package gov.llnl.rtk.ml;

import static gov.llnl.rtk.ml.Utility.addAssign;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ArchEPT: Encoder/Predictor T-style architecture for scientific ML.
 *
 * - Encoder: SequentialModule processing condition features. - Predictor:
 * SequentialModule processing dynamic variables (e.g., quantile, time). -
 * Encoder output is translated and split for injection into predictor at
 * multiple points.
 *
 * This modular pattern supports flexible experimentation, operational safety,
 * and regulatory compliance.
 *
 * @author nelson85
 */
public class ArchEPT implements EncoderPredictor
{
  final SequentialModule encoder;      // Sequential encoder chain (length 3)
  final SequentialModule predictor;    // Sequential predictor chain (length 3)
  final NeuralModule translate;              // Translation module
  private final int predictorInputSize;
  private final int encoderInputSize;
  private int predictorOutputSize;

  /**
   * Construct an ArchEPT model.
   *
   * @param encoder SequentialModule of encoder blocks (length 3)
   * @param predictor SequentialModule of predictor blocks (length 3)
   * @param translate Translation module (splits encoder output for injection)
   */
  public ArchEPT(SequentialModule encoder, SequentialModule predictor, NeuralModule translate)
  {
    if (predictor.modules.length != 3)
      throw new IllegalArgumentException("ArchEPT requires 3 predictor modules.");

    this.encoder = encoder;
    this.predictor = predictor;
    this.translate = translate;

    this.encoderInputSize = encoder.getInputSize();
    this.predictorInputSize = predictor.getInputSize();
    this.predictorOutputSize = predictor.getOutputSize();
  }

  @Override
  public ModuleState newState()
  {
    CompositeState output = new CompositeState(5);
    output.state[0] = encoder.newState();
    output.state[1] = predictor.newState();
    output.state[2] = translate.newState();
    output.state[3] = new ArrayState(); // predictor input buffer
    output.state[4] = new ArrayState(); // encoder input buffer
    return output;
  }

  @Override
  public EncoderPredictor encode(ModuleState state, double[] xe)
  {
    CompositeState c1 = (CompositeState) state;
    CompositeState encState = (CompositeState) c1.state[0];
    ArrayState transState = (ArrayState) c1.state[2];

    // Encoder forward: use sequential module for correct state handling
    double[] encoderOut = encoder.forward(encState, xe);

    // Translator forward: must use its own state object
    transState.assign(translate.forward(transState, encoderOut));
    return this;
  }

  @Override
  public double[] predict(ModuleState state, double[] xp)
  {
    CompositeState c1 = (CompositeState) state;
    CompositeState predState = (CompositeState) c1.state[1];
    ArrayState transState = (ArrayState) c1.state[2];

    // Split translated encoder output for injection
    double[] trans = transState.state;
    int n0 = predictor.modules[0].getOutputSize();
    int n1 = n0 + predictor.modules[1].getOutputSize();
    int n2 = n1 + predictor.modules[2].getOutputSize();

    // Predictor forward with injected encoder splits
    double[] p1 = predictor.modules[0].forward(predState.state[0], xp);
    addAssign(p1, trans, 0, n0);

    double[] p2 = predictor.modules[1].forward(predState.state[1], p1);
    addAssign(p2, trans, n0, n1);

    double[] p3 = predictor.modules[2].forward(predState.state[2], p2);
    addAssign(p3, trans, n1, n2);

    return p3;
  }

  @Override
  public double[] forward(ModuleState state, double[] input)
  {
    CompositeState c1 = (CompositeState) state;
    ArrayState predInput = (ArrayState) c1.state[3];
    ArrayState encoderInput = (ArrayState) c1.state[4];

    // Ensure buffers are correct size (should be already allocated, but safe to check)
    predInput.ensure(this.predictorInputSize);
    encoderInput.ensure(this.encoderInputSize);

    // Copy input into pre-allocated buffers
    System.arraycopy(input, 0, predInput.state, 0, this.predictorInputSize);
    System.arraycopy(input, this.predictorInputSize, encoderInput.state, 0, this.encoderInputSize);

    // Use buffers directly, no new allocation
    this.encode(state, encoderInput.state);
    return this.predict(state, predInput.state);
  }

  @Override
  public int getInputSize()
  {
    return this.encoderInputSize + this.predictorInputSize;
  }

  @Override
  public int getOutputSize()
  {
    return this.predictorOutputSize;
  }

  @Override
  public int getEncoderInputSize()
  {
    return this.encoder.getInputSize();
  }

  @Override
  public int getPredictorInputSize()
  {
    return this.predictorInputSize;
  }

  @Override
  public int getPredictorOutputSize()
  {
    return this.predictorOutputSize;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 23 * hash + Objects.hashCode(this.encoder);
    hash = 23 * hash + Objects.hashCode(this.predictor);
    hash = 23 * hash + Objects.hashCode(this.translate);
    hash = 23 * hash + this.predictorInputSize;
    hash = 23 * hash + this.encoderInputSize;
    hash = 23 * hash + this.predictorOutputSize;
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
    final ArchEPT other = (ArchEPT) obj;
    if (this.predictorInputSize != other.predictorInputSize)
      return false;
    if (this.encoderInputSize != other.encoderInputSize)
      return false;
    if (this.predictorOutputSize != other.predictorOutputSize)
      return false;
    if (!Objects.equals(this.encoder, other.encoder))
      return false;
    if (!Objects.equals(this.predictor, other.predictor))
      return false;
    return Objects.equals(this.translate, other.translate);
  }

  @Override
  public List<NeuralModule> getComponents()
  {
    return Stream.of(
            encoder.getComponents(),
            predictor.getComponents(),
            Arrays.asList(this.translate)
    )
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
  }
}
