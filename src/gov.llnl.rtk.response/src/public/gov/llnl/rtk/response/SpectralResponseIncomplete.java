// --- file: gov/llnl/rtk/response/SpectralResponseIncomplete.java ---
package gov.llnl.rtk.response;

import gov.llnl.math.SpecialFunctions;

/**
 * Used to represent the incomplete captures in detectors such as CZT.
 * 
 * @author nelson85
 */
class SpectralResponseIncomplete
{
  // Incomplete capture (CZT)
  double alpha;
  double beta;
  double center;
  double intensity;
  double K;
  double variance;

    /**
   * Routine to add a peak to channel.
   *
   * FIXME This assumes the center value of the group is the density rather than
   * integrating across the edges.
   *
   * @param target
   * @param edges
   * @param intensity
   */
  void render(SpectralBuffer buffer, FunctionResponseEvaluator eval,
          double intensity,
          double amplitude,
          double center)
  {
    // Scale the intensity by the detector efficiency
    intensity *= amplitude;
    intensity *= this.intensity;
    if (intensity == 0)
      return;

    // Find the end points
    int index0 = eval.edgesCursor.seek(center - this.center - 4 * Math.sqrt(variance));
    int index1 = eval.edgesCursor.seek(center);

    double[] edges = buffer.edges;
    for (int i = index0; i < index1; ++i)
    {
      double w = edges[i + 1] - edges[i];
      double c = center - (edges[i + 1] + edges[i]) / 2;
      if (c <= 0)
        continue;
      double v = w * intensity * Math.exp((alpha - 1) * Math.log(c) - beta * c + K);
      buffer.target[i] += v;
    }
  }

   @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 79 * hash + (int) (Double.doubleToLongBits(this.center) ^ (Double.doubleToLongBits(this.center) >>> 32));
    hash = 79 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
    hash = 79 * hash + (int) (Double.doubleToLongBits(this.variance) ^ (Double.doubleToLongBits(this.variance) >>> 32));
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
    final SpectralResponseIncomplete other = (SpectralResponseIncomplete) obj;
    if (Double.doubleToLongBits(this.center) != Double.doubleToLongBits(other.center))
      return false;
    if (Double.doubleToLongBits(this.intensity) != Double.doubleToLongBits(other.intensity))
      return false;
    return Double.doubleToLongBits(this.variance) == Double.doubleToLongBits(other.variance);
  }

  /**
   * Convert human parameters into those needed for calculations.
   */
  void cache()
  {
    if (intensity > 0)
    {
      beta = center / variance;
      alpha = center * beta + 1;
      K = alpha * Math.log(beta) - SpecialFunctions.gammaln(alpha);
    }
  }
}
