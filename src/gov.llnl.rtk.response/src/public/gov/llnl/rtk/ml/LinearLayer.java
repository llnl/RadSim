// --- file: gov/llnl/rtk/ml/LinearLayer.java ---
package gov.llnl.rtk.ml;
// Provide setters for weights/biases for each layer, e.g.:
// encodeInput.setWeights(...), predictLayer2.setBias(...), etc.

import java.util.Arrays;
import java.util.List;

// --- LinearLayer inner class ---
public class LinearLayer implements NeuralModule
{

  private final int inputSize;
  private final int outputSize;
  private double[][] weights; // [out][in]
  private double[] bias; // [out]

  public LinearLayer(int in, int out)
  {
    this.inputSize = in;
    this.outputSize = out;
    this.weights = new double[out][in];
    this.bias = new double[out];
  }

  /**
   * Forward pass through the linear layer with activation.
   * @param state
   * @param input
   * @return 
   */
  @Override
  public double[] forward(ModuleState state, double[] input)
  {
    if (input.length != inputSize)
      throw new IllegalArgumentException("Input size mismatch");
    ArrayState astate = (ArrayState) state;
    astate.ensure(outputSize);
    double[] output = astate.state;
    for (int j = 0; j < outputSize; ++j)
    {
      double sum = bias[j];
      for (int i = 0; i < inputSize; ++i)
        sum += weights[j][i] * input[i];
      output[j] = sum;
    }
    return output;
  }

  // --- Setters for weights and bias (for loader) ---
  public void setWeights(double[][] weights)
  {
    if (weights.length != outputSize || weights[0].length != inputSize)
      throw new IllegalArgumentException("Weights shape mismatch");
    this.weights = weights;
  }

  public void setBias(double[] bias)
  {
    if (bias.length != outputSize)
      throw new IllegalArgumentException("Bias shape mismatch");
    this.bias = bias;
  }

  double[][] getWeights()
  {
    return this.weights;
  }

  double[] getBias()
  {
    return this.bias;
  }

  @Override
  public int getInputSize()
  {
    return this.inputSize;
  }

  @Override
  public int getOutputSize()
  {
    return this.outputSize;
  }

  @Override
  public ModuleState newState()
  {
    return new ArrayState();
  }

    @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 67 * hash + this.inputSize;
    hash = 67 * hash + this.outputSize;
    hash = 67 * hash + Arrays.deepHashCode(this.weights);
    hash = 67 * hash + Arrays.hashCode(this.bias);
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
    final LinearLayer other = (LinearLayer) obj;
    if (this.inputSize != other.inputSize)
      return false;
    if (this.outputSize != other.outputSize)
      return false;
    if (!Arrays.deepEquals(this.weights, other.weights))
      return false;
    return Arrays.equals(this.bias, other.bias);
  }
  
  @Override
  public List<NeuralModule> getComponents()
  {
    return Arrays.asList(this);
  }
}
