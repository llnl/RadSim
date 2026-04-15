// --- file: gov/llnl/rtk/response/SplineResponseLine.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

/**
 * (Internal) Class to hold a line for SpectralResponseFunctionSpline.
 *
 * @author nelson85
 */
public class SplineResponseLine
{
  
  final RenderItem type;
  final double center;
  final double amplitude;
  final double width;

  // Caches
  final double amplitudePow;
  final double widthPow;

  SplineResponseLine(RenderItem type, double amplitude, double center, double width)
  {
    this.type = type;
    this.amplitude = amplitude;
    this.width = width;
    this.center = center;

    // For interpolation purposes we use the 4th root.
    this.amplitudePow = toValue(amplitude);
    this.widthPow = toValue(width);
  }

  private double toValue(double v)
  {
    return Math.pow(v, 0.0625);
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 29 * hash + (int) (Double.doubleToLongBits(this.amplitudePow) ^ (Double.doubleToLongBits(this.amplitudePow) >>> 32));
    hash = 29 * hash + (int) (Double.doubleToLongBits(this.center) ^ (Double.doubleToLongBits(this.center) >>> 32));
    hash = 29 * hash + (int) (Double.doubleToLongBits(this.widthPow) ^ (Double.doubleToLongBits(this.widthPow) >>> 32));
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
    final SplineResponseLine other = (SplineResponseLine) obj;
    if (Double.doubleToLongBits(this.amplitudePow) != Double.doubleToLongBits(other.amplitudePow))
      return false;
    if (Double.doubleToLongBits(this.center) != Double.doubleToLongBits(other.center))
      return false;
    return Double.doubleToLongBits(this.widthPow) == Double.doubleToLongBits(other.widthPow);
  }

  public double getAmplitude()
  {
    return amplitude;
  }

  public double getWidth()
  {
    return width;
  }
  
  public double getCenter()
  {
    return center;
  }
//<editor-fold desc="render" defaultstate="collapsed">
  /**
   * Routine to add a peakShapeParameters to channel.
   *
   * @param target
   * @param edges
   * @param intensity
   * @param f
   * @param peak1
   * @param peak2
   */
  static void render(SpectralBuffer buffer, FunctionResponseEvaluator eval, double intensity, LineParameters lineParameters)
  {
    // Scale the intensity by the detector efficiency
    intensity *= lineParameters.amplitude;
    intensity *= (1 - eval.incomplete.intensity);
    if (intensity <= 0)
      return;

    // Locate the center position
    int index = eval.edgesCursor.seek(lineParameters.center);

    // Render both sides of the peakShapeParameters
    renderRight(buffer, eval,
            index, intensity, lineParameters.center, lineParameters.width);
    renderLeft(buffer, eval,
            index, intensity, lineParameters.center, lineParameters.width);
  }

  /**
   * Use the ccdf to compute the total peakShapeParameters integral in the intervals
   *
   * @param edges
   * @param start
   * @param intensity
   * @param center
   * @param width
   */
  private static void renderRight(SpectralBuffer buffer, FunctionResponseEvaluator eval,
          int start, double intensity, double center, double width)
  {
    final double[] target = buffer.target;
    final double[] edges = buffer.edges;
    final PeakFunction shape = eval.ccdf;
    final double tail_tolerance = SpectralResponseFunctionSpline.TAIL_TOLERANCE;

    // Use ccdf to compute contributions above the peakShapeParameters center
    double counts;
    double energy0;
    double energy1 = edges[start];
    double a0;
    double a1 = shape.apply(energy1, center, width);
    for (int i = start; i < edges.length - 1; ++i)
    {
      counts = 0;
      a0 = a1;
      energy0 = energy1;
      energy1 = edges[i + 1];

      // Deal with peaks in the lld region
      if (i < eval.lldEval.lldChannel)
      {
        for (int j = 0; j < eval.lldEval.lldSampling; j++)
        {
          double f = ((double) j) / eval.lldEval.lldSampling;
          double e = (1-f) * energy0 + f * energy1;
          a1 = shape.apply(e, center, width);
          counts += eval.lldEval.lldEvaluator.applyAsDouble(e) * (a0 - a1);  
          a0 = a1;
        }
      }
      else
      {
        a1 = shape.apply(energy1, center, width);
        counts = a0 - a1;
      }

      counts *= intensity;
      target[i] += counts;

      // Truncate if we are less then the tail tolerance.
      if (counts < tail_tolerance * target[i] + 1e-9)
        break;
    }
  }

  private static void renderLeft(SpectralBuffer buffer, FunctionResponseEvaluator eval,
          int start, double intensity, double center, double width)
  {
    final double[] target = buffer.target;
    final double[] edges = buffer.edges;
    final PeakFunction shape = eval.cdf;
    final double tail_tolerance = SpectralResponseFunctionSpline.TAIL_TOLERANCE;

    // Use cdf to compute contributions below the peakShapeParameters center
    double counts;
    double energy0;
    double energy1 = edges[start];
    double a0;
    double a1 = shape.apply(energy1, center, width);

    for (int i = start - 1; i >= 0; --i)
    {
      // increment the steps
      energy0 = edges[i];
      counts = 0;
      a0 = a1;

      // Deal with peaks in the lld region
      if (i < eval.lldEval.lldChannel)
      {
        for (int j = 0; j < eval.lldEval.lldSampling; j++)
        {
          double f = ((double) j) / eval.lldEval.lldSampling;
          double e = f * energy0 + (1 - f) * energy1;
          a1 = shape.apply(e, center, width);
          counts += eval.lldEval.lldEvaluator.applyAsDouble(e) * (a0 - a1);
          a0 = a1;
        }
      }
      else
      {
        a1 = shape.apply(energy0, center, width);
        counts = (a0 - a1);
      }
      counts *= intensity;
      target[i] += counts;

      // Truncate if we are less then the tail tolerance.
      if (counts < tail_tolerance * target[i])
        break;

      energy1 = energy0;
    }
  }
//</editor-fold>
}
