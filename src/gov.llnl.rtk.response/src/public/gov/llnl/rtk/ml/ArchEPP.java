// --- file: gov/llnl/rtk/ml/ArchEPP.java ---
package gov.llnl.rtk.ml;

import static gov.llnl.rtk.ml.Utility.addAssign;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Modular implementation of ArchEPP with arrays of encoder, predictor, and
 * reducer modules. Supports flexible scientific experimentation and operational
 * safety.
 */
public class ArchEPP implements EncoderPredictor
{

  final SequentialModule encoder;
  final SequentialModule predictor;
  final NeuralModule[] reducers;
  private final int predictorsInputSize;
  private final int encodersInputSize;

  /**
   * @param encoders Array of encoder modules (length N)
   * @param predictors Array of predictor modules (length M)
   * @param reducers Array of reducer modules (length K)
   */
  public ArchEPP(SequentialModule encoders, SequentialModule predictors, NeuralModule[] reducers)
  {
    if (encoders.modules.length != 4)
      throw new IllegalArgumentException("ArchEPP requires 4 encoder modules.");
    if (predictors.modules.length != 4)
      throw new IllegalArgumentException("ArchEPP requires 4 predictor modules.");
    if (reducers.length != 3)
      throw new IllegalArgumentException("ArchEPP requires 3 reducer modules.");

    // Optionally: validate array lengths and compatibility here
    this.encoder = encoders;
    this.predictor = predictors;
    this.reducers = reducers;

    this.predictorsInputSize = this.predictor.getInputSize();
    this.encodersInputSize = this.encoder.getInputSize();
  }

  @Override
  public ModuleState newState()
  {
    CompositeState output = new CompositeState(4);
    // Passing a list of modules to a CompositeState will create the array with right size and allocate
    // all the states recursively
    output.state[0] = new CompositeState(encoder.modules);
    output.state[1] = new CompositeState(reducers);
    CompositeState glue = new CompositeState(5, ArrayState[]::new, ArrayState::new);
    output.state[2] = glue;
    output.state[3] = new CompositeState(predictor.modules);
    return output;
  }

  @Override
  public EncoderPredictor encode(ModuleState state, double[] xe)
  {
    CompositeState c1 = (CompositeState) state;
    CompositeState encState = (CompositeState) c1.state[0];
    CompositeState redState = (CompositeState) c1.state[1];
    CompositeState glueState = (CompositeState) c1.state[2];
    ArrayState glue0 = (ArrayState) glueState.state[0];
    ArrayState glue1 = (ArrayState) glueState.state[1];
    ArrayState glue2 = (ArrayState) glueState.state[2];

    // Encoder forward
    double[] x1 = encoder.modules[0].forward(encState.state[0], xe);
    double[] x2 = encoder.modules[1].forward(encState.state[1], x1);
    double[] x3 = encoder.modules[2].forward(encState.state[2], x2);
    double[] x4 = encoder.modules[3].forward(encState.state[3], x3);

    // Reductions
    glue0.assign(reducers[0].forward(redState.state[0], x2));
    glue1.assign(reducers[1].forward(redState.state[1], x3));
    glue2.assign(reducers[2].forward(redState.state[2], x4));
    return this;
  }

  @Override
  public double[] predict(ModuleState state, double[] xp)
  {
    CompositeState c1 = (CompositeState) state;
    CompositeState glueState = (CompositeState) c1.state[2];
    ArrayState glue0 = (ArrayState) glueState.state[0];
    ArrayState glue1 = (ArrayState) glueState.state[1];
    ArrayState glue2 = (ArrayState) glueState.state[2];
    CompositeState predState = (CompositeState) c1.state[3];

    // Predictor forward with parallel injection
    double[] x = predictor.modules[0].forward(predState.state[0], xp);
    addAssign(x, glue0.state, 0, x.length);
    x = predictor.modules[1].forward(predState.state[1], x);
    addAssign(x, glue1.state, 0, x.length);
    x = predictor.modules[2].forward(predState.state[2], x);
    addAssign(x, glue2.state, 0, x.length);
    return predictor.modules[3].forward(predState.state[3], x);
  }

  @Override
  public int getEncoderInputSize()
  {
    return this.encoder.getInputSize();
  }

  @Override
  public int getPredictorInputSize()
  {
    return this.predictor.getInputSize();
  }

  @Override
  public int getPredictorOutputSize()
  {
    return this.predictor.getOutputSize();
  }

  @Override
  public double[] forward(ModuleState state, double[] input)
  {
    CompositeState c1 = (CompositeState) state;
    CompositeState glueState = (CompositeState) c1.state[2];
    ArrayState glue3 = (ArrayState) glueState.state[3];
    ArrayState glue4 = (ArrayState) glueState.state[4];
    glue3.ensure(this.predictorsInputSize);
    glue4.ensure(this.encodersInputSize);
    System.arraycopy(input, 0, glue3.state, 0, this.predictorsInputSize);
    System.arraycopy(input, this.predictorsInputSize, glue4.state, 0, this.encodersInputSize);

    this.encode(state, glue4.state);
    return this.predict(state, glue3.state);
  }

  /**
   * @return Total input size (encoder + predictor input sizes).
   */
  @Override
  public int getInputSize()
  {
    return this.encoder.getInputSize() + this.predictor.getInputSize();
  }

  /**
   * @return Output size of the final predictor module.
   */
  @Override
  public int getOutputSize()
  {
    return this.predictor.getOutputSize();
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
    final ArchEPP other = (ArchEPP) obj;
    if (this.predictorsInputSize != other.predictorsInputSize)
      return false;
    if (this.encodersInputSize != other.encodersInputSize)
      return false;
    if (!Objects.equals(this.encoder, other.encoder))
      return false;
    if (!Objects.equals(this.predictor, other.predictor))
      return false;
    return Arrays.deepEquals(this.reducers, other.reducers);
  }

  @Override
  public List<NeuralModule> getComponents()
  {
    return Stream.of(
            encoder.getComponents(),
            predictor.getComponents(),
            Arrays.asList(reducers)
        )
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

}
