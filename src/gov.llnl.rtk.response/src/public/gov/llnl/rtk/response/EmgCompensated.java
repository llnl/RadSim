// --- file: gov/llnl/rtk/response/EmgCompensated.java ---
package gov.llnl.rtk.response;

import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author nelson85
 */
public class EmgCompensated
{
  final double correction_m;
  final double correction_p;
  final double correction_fwhm;
  final double theta;
  final double negativeTail;
  final double positiveTail;

  public EmgCompensated(double theta, double negativeTail, double positiveTail)
  {
    // Enforce limits
    if (theta < 0)
      theta = 0;
    if (theta > 1)
      theta = 1;
    if (negativeTail < 0)
      negativeTail = 0;
    if (positiveTail < 0)
      positiveTail = 0;
    this.negativeTail = negativeTail;
    this.positiveTail = positiveTail;
    this.theta = theta;
    this.correction_m = EmgUtilities.computeMode(negativeTail);
    this.correction_p = EmgUtilities.computeMode(positiveTail);
    this.correction_fwhm = EmgUtilities.computeFwhm(theta, negativeTail, positiveTail);
  } // Enforce limits

  DoubleUnaryOperator asFunction(double center, double width)
  {
    double sigma = width * EmgUtilities.GAUSSIAN_FWHM / correction_fwhm; // DISABLE the correction when creating calibration tables
    double mu_m = center + correction_m * sigma;
    double mu_p = center - correction_p * sigma;
    double tau_m = negativeTail * sigma;
    double tau_p = positiveTail * sigma;
    return (double x) -> EmgUtilities.emg11pdf(x, mu_m, mu_p, sigma, this.theta, tau_m, tau_p);
  }

  /**
   * Compute pdf for given tail parameters.
   *
   * @param x
   * @param center
   * @param width
   * @return
   */
  public double pdf(double x, double center, double width)
  {
    double sigma = width * EmgUtilities.GAUSSIAN_FWHM / correction_fwhm; // DISABLE the correction when creating calibration tables
    double mu_m = center + correction_m * sigma;
    double mu_p = center - correction_p * sigma;
    double tau_m = negativeTail * sigma;
    double tau_p = positiveTail * sigma;
    return EmgUtilities.emg11pdf(x, mu_m, mu_p, sigma, this.theta, tau_m, tau_p);
  }

  /**
   * Compute cdf for given tail parameters.
   *
   * @param x
   * @param center
   * @param width
   * @return
   */
  public double cdf(double x, double center, double width)
  {
    double sigma = width * EmgUtilities.GAUSSIAN_FWHM / correction_fwhm; // DISABLE the correction when creating calibration tables
    double mu_m = center + correction_m * sigma;
    double mu_p = center - correction_p * sigma;
    double tau_m = negativeTail * sigma;
    double tau_p = positiveTail * sigma;
    return EmgUtilities.emg11cdf(x, mu_m, mu_p, sigma, this.theta, tau_m, tau_p);
  }

  /**
   * Compute complementary cdf for given tail parameters.
   *
   * @param x
   * @param center
   * @param width
   * @return
   */
  public double ccdf(double x, double center, double width)
  {
    double sigma = width * EmgUtilities.GAUSSIAN_FWHM / correction_fwhm; // DISABLE the correction when creating calibration tables
    double mu_m = center + correction_m * sigma;
    double mu_p = center - correction_p * sigma;
    double tau_m = negativeTail * sigma;
    double tau_p = positiveTail * sigma;
    return EmgUtilities.emg11ccdf(x, mu_m, mu_p, sigma, this.theta, tau_m, tau_p);
  }
  
}
