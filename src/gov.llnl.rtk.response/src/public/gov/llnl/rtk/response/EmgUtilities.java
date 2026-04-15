// --- file: gov/llnl/rtk/response/EmgUtilities.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import static gov.llnl.math.SpecialFunctions.erfc;
import static gov.llnl.math.SpecialFunctions.erfcx;
import static java.lang.Math.exp;

/**
 * Exponentially Modified Gaussian for gamma ray spectrometry.
 *
 * These describe the peak shapes for scintillators and semiconductor detectors.
 * Please note these are not standard equations. They are modified from standard
 * literature to give properties that we need.
 *
 * This is the support class. The front end is used for calculations.
 *
 * @author nelson85
 */
public class EmgUtilities
{

  static final double SQRT2 = Math.sqrt(2);
  static final double GAUSS_AMP = 1 / Math.sqrt(2 * Math.PI);
  static final double GAUSSIAN_FWHM = 2.3548200450309493;

  /**
   * Compute the mode for an EMG.
   *
   * This is needed because the mode of the distribution changes with the tail
   * parameters. Thus the center and the tail parameters become coupled during
   * fitting operations.
   *
   * This is just an approximation used to minimize the coupling.
   *
   * @param tail is either the positive or negative tail factor.
   * @return a scalar used to keep the peak at the desired location.
   */
  static double computeMode(double tail)
  {
    double[] table = EMG_MEAN[2];
    if (tail < 1)
      table = EMG_MEAN[0];
    else if (tail < 2)
      table = EMG_MEAN[1];
    return tail * (table[0] + tail * (table[1] + tail * (table[2] + tail * table[3])))
            / (1 + table[4] * tail);
  }

  /**
   * Compute the FWHM for an EMG passed on the tail parameters.
   *
   * @param theta is the mixture parameter between the tails.
   * @param negativeTail is the positive tail factor.
   * @param positiveTail is the negative tail factor.
   * @return a scalar used to compute the effective width.
   */
  static double computeFwhm(double theta, double negativeTail, double positiveTail)
  {
//    double mx = Math.max(positiveTail, negativeTail);
    double[] table = EMG_FWHM;
    if (negativeTail > 4)
      negativeTail = 4;
    if (positiveTail > 4)
      positiveTail = 4;
    double u0 = negativeTail * (1 - theta), u = u0;
    double v0 = positiveTail * theta, v = v0;
    double t1 = theta;
    double t2 = theta * theta;
    double t3 = theta * theta * theta;
    double h00 = 2 * t3 - 3 * t2 + 1;
    double h01 = t3 - 2 * t2 + t1;
    double h10 = -2 * t3 + 3 * t2;
    double h11 = t3 - t2;
    double correction_fwhm = GAUSSIAN_FWHM + table[12] * u * v;
    correction_fwhm += table[0] * (h00 * u + h10 * v) + table[1] * (h01 * u - h11 * v)
            + table[2] * (h10 * u + h00 * v) + table[3] * (h11 * u - h01 * v);
    u *= u0;
    v *= v0;
    correction_fwhm += table[4] * (h00 * u + h10 * v) + table[5] * (h01 * u - h11 * v)
            + table[6] * (h10 * u + h00 * v) + table[7] * (h11 * u - h01 * v);
    u *= u0;
    v *= v0;
    correction_fwhm += table[8] * (h00 * u + h10 * v) + table[9] * (h01 * u - h11 * v)
            + table[10] * (h10 * u + h00 * v) + table[11] * (h11 * u - h01 * v);
    return correction_fwhm;
  }

//<editor-fold desc="primitives" defaultstate="collapsed">
  /**
   * (Internal) Standard equations for emg11.
   *
   * @param x
   * @param mu
   * @param sigma
   * @param theta
   * @param tau_m
   * @param tau_p
   * @return
   */
  public static double emg11(double x, double mu, double sigma, double theta, double tau_m, double tau_p)
  {
    double hm = 0, hp = 0;
    double xmu = x - mu;
    double sigma2 = sigma * sigma;

    // No tails, then revert to Gaussian
    if (tau_m + tau_p == 0)
      return GAUSS_AMP / sigma * exp(-xmu * xmu / 2 / sigma2);

    // Compute the negative tail unless mixing fraction is all positive tail.
    if (theta < 1)
    {
      if (tau_m <= 0)
        hm = GAUSS_AMP / sigma * exp(-xmu * xmu / 2 / sigma2);
      else
      {
        double erfcarg = ((sigma2 / tau_m + xmu) / sigma / SQRT2);
        if (erfcarg < 0)
          hm = 0.5 / tau_m * exp((sigma2 / tau_m / 2 + xmu) / tau_m)
                  * erfc(erfcarg);
        else
          hm = 0.5 / tau_m * exp(-xmu * xmu / sigma2 / 2) * erfcx(erfcarg);
      }
    }

    // Compute the positive tail unless mixing fraction is all positive tail.
    if (theta > 0)
    {
      // Only difference here is the argument is subtracted
      if (tau_p <= 0)
        hp = GAUSS_AMP / sigma * exp(-xmu * xmu / 2 / sigma2);
      else
      {
        double erfcarg = ((sigma2 / tau_p - xmu) / sigma / SQRT2);
        if (erfcarg < 0)
          hp = 0.5 / tau_p * exp((sigma2 / tau_p / 2 - xmu) / tau_p)
                  * erfc(erfcarg);
        else
          hp = 0.5 / tau_p * exp(-xmu * xmu / sigma2 / 2) * erfcx(erfcarg);
      }
    }

    // Mix the negative and positive tails at the requested ratio
    return ((1 - theta) * hm + theta * hp);
  }

  /**
   * Generalized EMG PDF.
   *
   * @param x
   * @param center
   * @param sigma
   * @param coef
   * @return
   */
  public static double emgPdf(double x, double center, double sigma, double[][] coef)
  {
    double total = 0;
    double y = 0;
    double sigma2 = sigma * sigma;
    for (double[] v : coef)
    {
      double weight = v[0];
      if (weight <= 0)
        continue;
      double xmu = x - center - sigma * v[1];
      double tau = v[2] * sigma;
      if (tau == 0)
        y = weight * GAUSS_AMP / sigma * exp(-xmu * xmu / 2 / sigma2);
      else if (tau < 0)
      {
        // negative tail
        tau = -tau;
        double erfcarg = ((sigma2 / tau + xmu) / sigma / SQRT2);
        if (erfcarg < 0)
          y += weight * 0.5 / tau * exp((sigma2 / tau / 2 + xmu) / tau)
                  * erfc(erfcarg);
        else
          y += weight * 0.5 / tau * exp(-xmu * xmu / sigma2 / 2) * erfcx(erfcarg);
      }
      else
      {
        // positive tail
        double erfcarg = ((sigma2 / tau - xmu) / sigma / SQRT2);
        if (erfcarg < 0)
          y += weight * 0.5 / tau * exp((sigma2 / tau / 2 - xmu) / tau)
                  * erfc(erfcarg);
        else
          y += weight * 0.5 / tau * exp(-xmu * xmu / sigma2 / 2) * erfcx(erfcarg);
      }
      total += weight;
    }
    return y / total;
  }

  /**
   * Generalized EMG CDF.
   *
   * @param x
   * @param center
   * @param sigma
   * @param coef
   * @return
   */
  public static double emgCdf(double x, double center, double sigma, double[][] coef)
  {
    double t = 0;
    double y = 0;
    double sigma2 = sigma * sigma;
    for (double[] v : coef)
    {
      double weight = v[0];
      if (weight <= 0)
        continue;
      double xmu = x - center - sigma * v[1];
      double tau = v[2] * sigma;
      double k = weight * gauscdf(xmu / sigma);
      if (tau == 0)
        y += k;
      else if (tau < 0)
      {
        tau = -tau;
        double h = (xmu + sigma2 / tau) / SQRT2 / sigma;
        double g = (xmu + 0.5 * sigma2 / tau) / tau;
        if (h < 0)
          y += k + weight * 0.5 * exp(g) * erfc(h);
        else
          y += k + weight * 0.5 * exp(-h * h + g) * erfcx(h);
      }
      else
      {
        double h = (-xmu + sigma2 / tau) / SQRT2 / sigma;
        double g = (-xmu + 0.5 * sigma2 / tau) / tau;
        if (h < 0)
          y += k - weight * 0.5 * exp(g) * erfc(h);
        else
          y += k - weight * 0.5 * exp(-h * h + g) * erfcx(h);
      }
      t += weight;
    }
    return y / t;
  }

  /**
   * Probability Density Function (pdf) for exponentially modified Gaussian.
   *
   * @param x is the position.
   * @param mu_m is the center for the negative tail.
   * @param mu_p is the center for positive tail.
   * @param sigma is the width parameter.
   * @param theta is the mixing fraction.
   * @param tau_m is the negative tail coefficient.
   * @param tau_p is the positive tail coefficient.
   * @return
   */
  public static double emg11pdf(double x, double mu_m, double mu_p, double sigma, double theta, double tau_m, double tau_p)
  {
    double hm = 0, hp = 0;
    double xmu_m = x - mu_m;
    double xmu_p = x - mu_p;
    double sigma2 = sigma * sigma;

    // No tails, then revert to Gaussian
    if (tau_m + tau_p == 0)
      return GAUSS_AMP / sigma * exp(-xmu_m * xmu_m / 2 / sigma2);

    // Compute the negative tail unless mixing fraction is all positive tail.
    if (theta < 1)
    {
      if (tau_m <= 0)
        hm = GAUSS_AMP / sigma * exp(-xmu_m * xmu_m / 2 / sigma2);
      else
      {
        double erfcarg = ((sigma2 / tau_m + xmu_m) / sigma / SQRT2);
        if (erfcarg < 0)
          hm = 0.5 / tau_m * exp((sigma2 / tau_m / 2 + xmu_m) / tau_m)
                  * erfc(erfcarg);
        else
          hm = 0.5 / tau_m * exp(-xmu_m * xmu_m / sigma2 / 2) * erfcx(erfcarg);
      }
    }

    // Compute the positive tail unless mixing fraction is all positive tail.
    if (theta > 0)
    {
      // Only difference here is the argument is subtracted
      if (tau_p <= 0)
        hp = GAUSS_AMP / sigma * exp(-xmu_p * xmu_p / 2 / sigma2);
      else
      {
        double erfcarg = ((sigma2 / tau_p - xmu_p) / sigma / SQRT2);
        if (erfcarg < 0)
          hp = 0.5 / tau_p * exp((sigma2 / tau_p / 2 - xmu_p) / tau_p)
                  * erfc(erfcarg);
        else
          hp = 0.5 / tau_p * exp(-xmu_p * xmu_p / sigma2 / 2) * erfcx(erfcarg);
      }
    }

    // Mix the negative and positive tails at the requested ratio
    return ((1 - theta) * hm + theta * hp);
  }

  public static double gauscdf(double x)
  {
    return 0.5 * erfc(-x / SQRT2);
  }

  /**
   * Cumulative Density Function (cdf) for exponentially modified Gaussian.
   *
   * @param x is the position.
   * @param mu_m is the center for the negative tail.
   * @param mu_p is the center for positive tail.
   * @param sigma is the width parameter.
   * @param theta is the mixing fraction.
   * @param tau_m is the negative tail coefficient.
   * @param tau_p is the positive tail coefficient.
   * @return
   */
  public static double emg11cdf(double x,
          double mu_m, double mu_p,
          double sigma, double theta,
          double tau_m, double tau_p)
  {
    double hm = 0, hp = 0;
    double xmu_m = x - mu_m;
    double xmu_p = x - mu_p;
    double sigma2 = sigma * sigma;

    // No tails, then revert to Gaussian
    if (tau_m + tau_p == 0)
      return gauscdf(xmu_m / sigma);

    // Compute the negative tail unless mixing fraction is all positive tail.
    if (theta < 1)
    {
      if (tau_m <= 0)
        hm = gauscdf(xmu_m / sigma);
      else
      {
        double h = (xmu_m + sigma2 / tau_m) / SQRT2 / sigma;
        double g = (xmu_m + 0.5 * sigma2 / tau_m) / tau_m;
        if (h < 0)
          hm = gauscdf(xmu_m / sigma) + 0.5 * exp(g) * erfc(h);
        else
          hm = gauscdf(xmu_m / sigma) + 0.5 * exp(-h * h + g) * erfcx(h);
      }
    }

    // Compute the positive tail unless mixing fraction is all positive tail.
    if (theta > 0)
    {
      // Only difference here is the argument is subtracted
      if (tau_p <= 0)
        hp = gauscdf(xmu_p / sigma);
      else
      {
        double h = (-xmu_p + sigma2 / tau_p) / SQRT2 / sigma;
        double g = (-xmu_p + 0.5 * sigma2 / tau_p) / tau_p;
        if (h < 0)
          hp = gauscdf(xmu_p / sigma) - 0.5 * exp(g) * erfc(h);
        else
          hp = gauscdf(xmu_p / sigma) - 0.5 * exp(-h * h + g) * erfcx(h);
      }
    }

    // Mix the negative and positive tails at the requested ratio
    return ((1 - theta) * hm + theta * hp);
  }

  /**
   * Complementary cdf for exponentially modified Gaussian.
   *
   * @param x is the position.
   * @param mu_m is the center for the negative tail.
   * @param mu_p is the center for positive tail.
   * @param sigma is the width parameter.
   * @param theta is the mixing fraction.
   * @param tau_m is the negative tail coefficient.
   * @param tau_p is the positive tail coefficient.
   * @return
   */
  public static double emg11ccdf(double x,
          double mu_m, double mu_p,
          double sigma, double theta,
          double tau_m, double tau_p)
  {
    double hm = 0, hp = 0;
    double xmu_m = x - mu_m;
    double xmu_p = x - mu_p;
    double sigma2 = sigma * sigma;

    // No tails, then revert to Gaussian
    if (tau_m + tau_p == 0)
      return gauscdf(-xmu_m / sigma);

    // Compute the negative tail unless mixing fraction is all positive tail.
    if (theta < 1)
    {
      if (tau_m <= 0)
        hm = gauscdf(-xmu_m / sigma);
      else
      {
        double h = (xmu_m + sigma2 / tau_m) / SQRT2 / sigma;
        double g = (xmu_m + 0.5 * sigma2 / tau_m) / tau_m;
        if (h < 0)
          hm = 0.5 * erfc(xmu_m / sigma / SQRT2) - 0.5 * exp(g) * erfc(h);
        else
          hm = 0.5 * erfc(xmu_m / sigma / SQRT2) - 0.5 * exp(-h * h + g) * erfcx(h);
      }
    }

    // Compute the positive tail unless mixing fraction is all positive tail.
    if (theta > 0)
    {
      // Only difference here is the argument is subtracted
      if (tau_p <= 0)
        hp = gauscdf(-xmu_p / sigma);
      else
      {
        double h = (-xmu_p + sigma2 / tau_p) / SQRT2 / sigma;
        double g = (-xmu_p + 0.5 * sigma2 / tau_p) / tau_p;
        if (h < 0)
          hp = 0.5 * erfc(xmu_p / sigma / SQRT2) + 0.5 * exp(g) * erfc(h);
        else
          hp = 0.5 * erfc(xmu_p / sigma / SQRT2) + 0.5 * exp(-h * h + g) * erfcx(h);
      }
    }

    // Mix the negative and positive tails at the requested ratio
    return ((1 - theta) * hm + theta * hp);
  }

//</editor-fold>
//<editor-fold desc="tables" defaultstate="collapsed">
  static double[][] EMG_MEAN = new double[][]
  {
    new double[]
    {
      0.98403306667494006, 6.87128260946707048, -3.59356629456900478, 0.97698992079952762, 6.50913948527094366
    },
    {
      1.11211213678761900, -0.17136747984795347, 0.05433012674502260, -0.00660670333633031, 0.41741265192937493
    },
    {
      1.16630323030957350, 0.05908179241594781, -0.00338867235260345, 0.00010550416371927, 0.74936852155925970
    }
  };

  static double[] EMG_FWHM = new double[]
  {
    0.35168175166649507, 0.19599963151612038, 0.22021348448390624, -0.47571183354995672, 0.19848578480384590, -0.42602304285692455, -0.56650603029754198, -2.48414110123217613, -0.03097780175443219, -0.04698491209035962, -0.35083016889165969, -0.65255267395508787, 0.40242977372414324
  };
//</editor-fold>
}
