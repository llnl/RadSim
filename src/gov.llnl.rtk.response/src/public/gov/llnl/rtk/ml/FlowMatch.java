// --- file: gov/llnl/rtk/ml/FlowMatch.java ---
package gov.llnl.rtk.ml;

/**
 * This is not a module but rather a type of stateful evaluator.
 *
 * @author nelson85
 */
public class FlowMatch
{
  private final EncoderPredictor model;
  private final double[] stateInput;
  private final int stateSize;
  int numSteps;
  private final ModuleState state;

  public FlowMatch(EncoderPredictor model, int numSteps)
  {
    this.model = model;
    this.state = model.newState();
    this.stateSize = model.getPredictorInputSize();
    this.stateInput = new double[stateSize];
    this.numSteps = numSteps;
  }

  /**
   * Flow-match evaluation: integrates the vector field for numSteps.
   *
   * @param result
   * @param x0 Initial state vector (e.g., position)
   * @param condition Condition vector (e.g., parameters)
   * @return Final state after flow (as a new array, to avoid overwriting x0)
   */
  public double[] evaluate(double[] result, double[] x0, double[] condition)
  {

    // Copy x0 into x_t (internal buffer)
    System.arraycopy(x0, 0, stateInput, 1, stateSize);

    double dt = 1.0 / numSteps;
    double t = 0.0;

    model.encode(state, condition);
    for (int step = 0; step < numSteps; ++step)
    {
      // Prepare stateInput: [t, x1, x2, ...]
      stateInput[0] = t;

      // Model predicts the vector field: dx/dt = model.forward(condition, stateInput)
      double[] v_t = model.predict(state, stateInput);

      // FIXME the x0 can contain predictor values not just items to flow.  So we need
      // to know the size of the output here not the size of the input 
      // Euler step: x_t = x_t + v_t * dt
      for (int i = 0; i < stateSize - 1; ++i)
        stateInput[i + 1] += v_t[i] * dt;
      t += dt;
    }

    // Return a copy (to avoid exposing internal buffer)
    if (result == null || result.length != stateSize - 1)
      result = new double[stateSize - 1];
    System.arraycopy(stateInput, 1, result, 0, stateSize - 1);
    return result;
  }

  /**
   * @return the numSteps
   */
  public int getNumSteps()
  {
    return numSteps;
  }

  /**
   * @param numSteps the numSteps to set
   */
  public void setNumSteps(int numSteps)
  {
    this.numSteps = numSteps;
  }
}
