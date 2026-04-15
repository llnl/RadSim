// --- file: gov/llnl/rtk/calibration/WeightedCentroidFitter.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.function.PolynomialFunction;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.xml.bind.ReaderInfo;

/**
 * Compute the location of a peak in channel space using weighted center of mass
 * method. We will need both an integer and a double version so that we can
 * handle both raw spectrum and manipulated data.
 *
 * @author nelson85
 */
@ReaderInfo(WeightedCentroidFitterReader.class)
public class WeightedCentroidFitter implements PeakFitter, PeakFinder
{
  // We can remove bias if we have precomputed the offset as a function 
  // of the expected background shape under the peak
  private double[] biasCoefficients = null;

// Controls where the search will start, peak must be within 2 sigma of the
  // initial position
  private double sigma;
  private double expected;

  // These are needed for the bias coefficient optimizer
  public WeightedCentroidFitter()
  {
  }

  @Override
  public void setExpectedChannel(double parseDouble)
  {
    this.expected = parseDouble;
  }

  public void setPeakSigma(double parseDouble)
  {
    this.sigma = parseDouble;
  }

  final public void setBiasCoefficients(double[] d)
  {
    this.biasCoefficients = DoubleArray.copyOf(d);
  }

  /**
   * Computed the centroid for a spectrum. Looks at the region about the
   * expected peak location.
   *
   * @param spectrum
   * @return the controid of the peak.
   */
  @Override
  public WeightedCentroidFitterResult fitPeak(Spectrum spectrum)
  {
    if (spectrum == null)
    {
      return null;
    }

    // Store for later
    double[] in = spectrum.toDoubles();

    double mu = expected;
    double previousMu;
    double k = 1.0 / 2.0 / sigma / sigma;

    // Iterate to find the wieghted centroid
    // Assume the expected peak location.
    // Compute the weighted centroid about that location.
    // Update the expected to match centroid
    // Iterate until converges
    for (int i = 0; i < 100; i++)
    {
      previousMu = mu;
      mu = computeWeightedCentroid(in, mu, sigma, k);
      if (Math.abs(mu - previousMu) < 1e-5)
      {
        break;
      }
    }
    double muBiased = mu;

    double del = 0;
    // Handle removal of bias
    if (biasCoefficients != null)
    {
      double s1 = computeWeightedCentroid(in, mu - sigma, sigma, k);
      double s2 = computeWeightedCentroid(in, mu + sigma, sigma, k);
      del = s2 - s1;
      double err = PolynomialFunction.polyval(s2 - s1, biasCoefficients);
      mu -= err;
    }

    // Estimate the intensity
    WeightedCentroidFitterResult result = new WeightedCentroidFitterResult(mu, sigma);
    result.input = in;
    result.del = del;
    result.muBiased = muBiased;
    return result;
  }

  public static double computeWeightedCentroid(double[] in, double mu, double sigma, double k)
  {
    int w1 = (int) Math.floor(mu - 4 * sigma);
    int w2 = (int) Math.ceil(mu + 4 * sigma + 1);
    if (w1 < 0)
    {
      w1 = 0;
    }
    if (w2 > in.length)
    {
      w2 = in.length;
    }
    double s1 = 0, s2 = 0;
    for (int j = w1; j < w2; ++j)
    {
      double w = Math.exp(-k * (j - mu) * (j - mu));
      s1 += w * in[j] * j;
      s2 += w * in[j];
    }
    if (s2 == 0)
    {
      return mu;
    }
    mu = s1 / s2;
    return mu;
  }

  public static double[] computeIntensity(double[] in, double mu, double sigma)
  {
    double k = 1.0 / 2.0 / sigma / sigma;
    int w1 = (int) Math.floor(mu - 4 * sigma);
    int w2 = (int) Math.ceil(mu + 4 * sigma + 1);
    if (w1 < 0)
    {
      w1 = 0;
    }
    if (w2 > in.length)
    {
      w2 = in.length;
    }
    double s1 = 0, g1 = 0, g2 = 0, s0 = 0;
    for (int j = w1; j < w2; ++j)
    {
      double w = Math.exp(-k * (j - mu) * (j - mu));
      s0 += in[j];
      s1 += w * in[j];
      g1 += w;
      g2 += w * w;
    }

    double det = (w2 - w1) * g2 - g1 * g1;
    double num = (w2 - w1) * s1 - g1 * s0;
    double intensity = num / det;
    double num2 = s0 * g2 - s1 * g1;
    double continuum = num2 / det;
    return new double[]
    {
      intensity, continuum
    };
  }

  @Override
  public PeakFinderResult initialSearch(Spectrum spectrum)
  {
    return fitPeak(spectrum);
  }

  public class WeightedCentroidFitterResult implements PeakFitterResult
  {

    private double[] input;
    private double mu;
    private double sigma;
    private double[] intensity = null;
    private double del;
    private double muBiased;

    private WeightedCentroidFitterResult(double mu, double sigma)
    {
      this.mu = mu;
      this.sigma = sigma;
    }
    //<editor-fold desc="internal">

//</editor-fold>
//<editor-fold desc="plotting">
    public double[] getFit()
    {
      double[] out = new double[input.length];
      double k = 0.5 / sigma / sigma;
      for (int i = 0; i < out.length; ++i)
      {
        out[i] = getPeakIntensity() * Math.exp(-k * (i - mu) * (i - mu));
      }
      return out;
    }

    public double[] getResidual()
    {
      double[] out = DoubleArray.copyOf(input);
      double k = 0.5 / sigma / sigma;
      for (int i = 0; i < out.length; ++i)
      {
        out[i] -= getPeakIntensity() * Math.exp(-k * (i - mu) * (i - mu));
      }
      return out;
    }
//</editor-fold>

    @Override
    public double getPeakIntensity()
    {
      if (this.intensity == null)
      {
        this.intensity = computeIntensity(input, mu, sigma);
      }
      return intensity[0];
    }

    @Override
    public double getIntegratedIntensity()
    {
      //sqrt(2)*sqrt(pi)*peakIntensity*abs(sigma);
      return 2.5066282741 * this.getPeakIntensity() * Math.abs(sigma);
    }

    @Override
    public double getIntegratedContinuum()
    {
      if (this.intensity == null)
      {
        this.intensity = computeIntensity(input, mu, sigma);
      }
      return intensity[1] * 8.0 * sigma;
    }

    @Override
    public double getPeakLocation()
    {
      return mu;
    }
  }
}
