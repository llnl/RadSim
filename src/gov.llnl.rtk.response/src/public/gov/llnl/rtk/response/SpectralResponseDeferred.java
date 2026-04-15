// --- file: gov/llnl/rtk/response/SpectralResponseDeferred.java ---
package gov.llnl.rtk.response;

import java.util.HashMap;

/**
 *
 * @author nelson85
 */
class SpectralResponseDeferred implements SpectralBufferDeferred
{
  HashMap<Integer, double[]> deferred = new HashMap<>();

  /**
   * Defer rendering a line until later.
   *
   * This is needed for annihilation and scattering x-rays in which the same
   * line will be rendered many times with differing intensities.
   *
   * @param energy
   * @param intensity
   * @param width
   */
  void add(double energy, double intensity, double width)
  {
    int key = (int)(energy*100);
    if (deferred.containsKey(key))
    {
      deferred.get(key)[1] += intensity;
    }
    else
    {
      deferred.put(key, new double[]
      {
        energy, intensity, width
      });
    }
  }

  /**
   * Add the deferred lines into the buffer.
   *
   * @param buffer
   * @param eval
   */
  void apply(SpectralBuffer buffer, FunctionResponseEvaluator eval)
  {
    LineParameters lineParameters = eval.lineParameters;
    for (double[] values : this.deferred.values())
    {
      // Copy the values from deferred
      lineParameters.center = values[0];
      lineParameters.amplitude = values[1];
      lineParameters.width = values[2];

      // Add the line
      SplineResponseLine.render(buffer, eval, 1.0, lineParameters);

      // Add in the incomplete
      if (eval.incomplete.intensity > 0)
        eval.incomplete.render(buffer, eval, 1.0, lineParameters.amplitude, lineParameters.center);
    }
    deferred.clear();
  }

  @Override
  public void clear()
  {
    deferred.clear();
  }

}
