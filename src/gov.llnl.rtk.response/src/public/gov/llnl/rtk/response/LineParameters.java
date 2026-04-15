// --- file: gov/llnl/rtk/response/LineParameters.java ---
package gov.llnl.rtk.response;

/**
 *
 * @author nelson85
 */
public class LineParameters
{
  double amplitude;
  double center;
  double width;

  /**
   * Interpolate between two peak entries.
   *
   * @param fraction
   * @param peak1
   * @param peak2
   */
  void computeLineParameters(double fraction,
          SplineResponseLine peak1, SplineResponseLine peak2)
  {
    // Use estimates from pick points to compute peak parameters
    this.amplitude = 0.0;
    this.width = 0.0;
    this.center = 0.0;

    // If not define in pick points there is nothing to add
    if (peak1 == null && peak2 == null)
      return;

    double amplitude = 0;
    double width = 0;
    double center = 0;

    if (peak1 != null)
    {
      amplitude += (1 - fraction) * peak1.amplitudePow;
      // Width and center must be use directly if peak2 is null.
      if (peak2 == null)
        fraction = 0;
      width += (1 - fraction) * peak1.widthPow;
      center += (1 - fraction) * peak1.center;
    }
    if (peak2 != null)
    {
      amplitude += fraction * peak2.amplitudePow;
      // Width and center must be use directly if peak1 is null.
      if (peak1 == null)
        fraction = 1;
      width += fraction * peak2.widthPow;
      center += fraction * peak2.center;
    }
    // Normalize from quad root
    amplitude *= amplitude;
    amplitude *= amplitude;
    amplitude *= amplitude;
    amplitude *= amplitude;

    // Normalize from quad root
    width *= width;
    width *= width;
    width *= width;
    width *= width;

    this.amplitude = amplitude;
    this.center = center;
    this.width = width;

    if (Double.isNaN(amplitude))
      throw new ArithmeticException("NaN amplitude");
    if (Double.isNaN(width))
      throw new ArithmeticException("NaN width");
    if (Double.isNaN(center))
      throw new ArithmeticException("NaN center");
  }

}
