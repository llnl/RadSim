// --- file: gov/llnl/rtk/calibration/GaussianFitter.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.MathConstants;
import gov.llnl.math.MathExceptions.ConvergenceException;
import gov.llnl.math.SpecialFunctions;
import gov.llnl.math.function.LinearFunction;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.math.matrix.MatrixColumnArray;
import gov.llnl.math.matrix.MatrixFactory;
import gov.llnl.math.matrix.MatrixOps;
import gov.llnl.math.matrix.MatrixViews;
import gov.llnl.math.optimize.LinearRegression;
import gov.llnl.rtk.calibration.PeakFitter.PeakFitterResult;
import gov.llnl.rtk.data.ChannelRegionOfInterestImpl;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.PropertyUtilities;
import gov.llnl.utility.Serializer;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.annotation.Debug;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * This is used to estimate the parameters for a Gaussian peak on a slope.
 *
 * @author nelson85
 */
@ReaderInfo(GaussianFitterReader.class)
public class GaussianFitter implements PeakFitter
{
  private static final long serialVersionUID = UUIDUtilities.createLong("GaussianFitter-v1");
  
  // Parameters
  double alpha = 0.2; // used to control the descent rate
  int iterations = 100;
  int regionStart;
  int regionEnd;
  boolean limitRange;
  double startChannelFraction;
  double endChannelFraction;
  // Internal
  double[] input;

  /**
   * Explicitely set the region of interest to analyze.
   *
   * @param startChannel
   * @param endChannel
   */
  public void setRegionOfInterest(int startChannel, int endChannel)
  {
    regionStart = startChannel;
    regionEnd = endChannel;
  }

  /**
   * Set the region of interest based on an expected peak location. Based on the
   * start and end channel fraction.
   *
   * @param channel
   */
  @Override
  public void setExpectedChannel(double channel)
  {
    regionStart = (int) Math.floor(startChannelFraction * channel);
    regionEnd = (int) Math.ceil(endChannelFraction * channel + 1);
  }

  @Override
  public GaussianFitterResult fitPeak(Spectrum spectrum)
  {
    if (spectrum == null)
      return null;
    double[] in = spectrum.toDoubles();
    try
    {
      double mu;
      double sigma;
      double intensity;
      double linear1;
      double linear2;
      input = in;
      if (regionEnd > in.length)
        regionEnd = in.length;

      int start = regionStart;
      int end = regionEnd;

      if (end < start)
        return null;

      // Compute the minimum k4
      double mink4 = (1.0 / (end - start) / (end - start));

      // Set up limits
      double[][] limits = new double[][]
      {
        DoubleArray.newArray(-0.1, 5.0, 0.5, 0),
        DoubleArray.newArray(-0.1, 5.0, 0.5, 0),
        DoubleArray.newArray(1e-8, 2.0, 1e-3, 0),
        DoubleArray.newArray(start - 10, end + 10, (start + end) / 2, 0),
        DoubleArray.newArray(mink4, 0.1, (4 * mink4 + 0.1) / 5, 0)
      };
      int[] violation = new int[5];

      // Auto scale problem to the point we have tested
      double[] working = DoubleArray.copyOf(in);
      double norm = Math.sqrt(DoubleArray.sumSqrRange(working, start, end));
      DoubleArray.divideAssignRange(working, start, end, norm);

      {
        // Set up the initial guess.
        LinearRegression lr = new LinearRegression();
        for (int i = start; i < end; ++i)
          lr.add(i, working[i]);
        LinearFunction pf = lr.compute();
        for (int j = 0; j < 3; ++j)
        {
          lr.clear();
          for (int i = start; i < end; ++i)
          {
            lr.add(i, working[i],
                    SpecialFunctions.logistic(pf.applyAsDouble(i), working[i], 100));
          }
          pf = lr.compute();
        }

        double s0 = 0;
        double s1 = 0;
        double s2 = 0;
        intensity = 0;
        for (int i = start; i < end; ++i)
        {
          double d = working[i] - pf.applyAsDouble(i);
          if (d > intensity)
            intensity = d;
          if (d > 0)
          {
            s0 += d;
            s1 += d * i;
            s2 += d * i * i;
          }
        }
        linear1 = pf.applyAsDouble(start);
        linear2 = pf.applyAsDouble(end - 1);
        mu = s1 / s0;
        sigma = Math.sqrt(s2 / s0 - mu * mu) / 2;
      }

      double k[] = new double[]
      {
        linear1, linear2, intensity, mu, 0.5 / sigma / sigma
      };

      // Apply initial limits
      for (int i = 0; i < 5; i++)
      {
        if (k[i] < limits[i][0])
          k[i] = limits[i][0];
        if (k[i] > limits[i][1])
          k[i] = limits[i][1];
      }

      Matrix q1 = new MatrixColumnArray(5, 1);
      Matrix q2 = new MatrixColumnArray(5, 5);

      // Standard Gauss-Newton gradiant descent
      double dk = 0;
      double err = 0;
      double previousErr = Double.MAX_VALUE;
      for (int i1 = 0; i1 < iterations; ++i1)
      {
        // Fill with zeros between computation
        MatrixOps.fill(q1, 0);
        MatrixOps.fill(q2, 0);

        err = 0;
        for (int i2 = start; i2 < end; ++i2)
        {
          // Compute the fit error
          double g = Math.exp(-k[4] * (i2 - k[3]) * (i2 - k[3]));
          double h = (i2 - start) / (end - 1.0 - start);
          double f = ((k[0] * (1 - h) + k[1] * h + k[2] * g) - working[i2]);
          err += f * f;

          // Compute the derivative
          Matrix df = MatrixFactory.wrapColumnVector(new double[]
          {
            (1 - h),
            h,
            g,
            2 * g * k[2] * k[4] * (i2 - k[3]),
            -g * k[2] * (i2 - k[3]) * (i2 - k[3])
          });

          // Update the (df'*df) and (df'*f) matrix
          MatrixOps.addAssignScaled(q1, df, f);
          MatrixOps.addAssign(q2, MatrixOps.multiplyVectorOuter(df, df));
        }

//      System.out.println("err=" + err);
        if (err < previousErr && previousErr - err < 1e-5 * err)
          break;
        previousErr = err;

        for (int i = 0; i < 5; ++i)
        {
          double lambda = limits[i][3];
          double v1 = q1.get(i, 0);
          double v2 = q2.get(i, i);
          q1.set(i, 0, v1 + lambda * (k[i] - limits[i][2]));
          q2.set(i, i, v2 + lambda);
        }

        // Levenberg–Marquardt algorithm
        Matrix v = MatrixViews.diagonal(q2);
        MatrixOps.addAssignScaled(v, v, 0.01);

        // Solve for the gradiant
        Matrix update = MatrixOps.divideLeft(q2, q1);

        // Impose barrier conditions
        boolean good = false;
        int count = 0;

        while (true)
        {
          good = true;

          // Check the limits
          for (int i = 0; i < 5; i++)
          {
            if (limits[i] == null)
              continue;

            double revised = k[i] - alpha * update.get(i, 0);
            if (revised >= limits[i][0] && revised < limits[i][1])
            {
              violation[i] = 0;
              continue;
            }

            // Determine the appriate lambda
            double lambda = limits[i][3];
            if (lambda == 0)
              lambda = 1e-4;
            if (violation[i] != 0)
              lambda *= 4;
            limits[i][3] = lambda;
            if (limits[i][3] > 1000)
              limits[i][3] = 1000;

            // Apply addition constraint to stay in the limits
            good = false;
            double v1 = q1.get(i, 0);
            double v2 = q2.get(i, i);
            q1.set(i, 0, v1 + lambda * (revised - limits[i][2]) / alpha);
            q2.set(i, i, v2 + lambda);
            violation[i] = 1;
          }

          // If all values in limit range
          if (good)
            break;

          // Otherwise revise
          update = MatrixOps.divideLeft(q2, q1);

          count++;
          if (count > 40)
          {
            MatrixOps.dump(System.out, q1);
            MatrixOps.dump(System.out, q2);
            MatrixOps.dump(System.out, MatrixOps.divideLeft(q2, q1));

            throw new ConvergenceException("Failed in updating limits");
          }
        }

        // Clear the violation
        for (int i = 0; i < 5; i++)
        {
          violation[i] = 0;
        }

        // Apply the update
        dk = 0;
        for (int i3 = 0; i3 < 5; ++i3)
        {
          double delta = update.get(i3, 0);

          // Handle NaN
          if (delta != delta)
          {
            throw new ConvergenceException("NaN in operation");
          }
          dk += delta * delta;
          k[i3] -= alpha * delta;
        }

        // Check for convergence
        if (dk < 1e-6)
          break;
      }

      // Project the results back to the space we are interested in
      GaussianFitterResult parameters
              = new GaussianFitterResult(norm * k[0], norm * k[1], norm * k[2], k[3], Math.sqrt(0.5 / k[4]),
                      new ChannelRegionOfInterestImpl(start, end),
                      err / (end - start),
                      input);
      return parameters;
    }
    catch (Exception ex)
    {
      if (PropertyUtilities.get("gov.llnl.rtk.DumpCore", false))
        try
        {
          Serializer serializer = new Serializer();
          serializer.save(
                  Paths.get(String.format("gf%d.ser.gz", this.hashCode())), this);
        }
        catch (IOException ex1)
        {
        }

      throw ex;
    }
  }

  /**
   * @param startChannelFraction the startChannelFraction to set
   */
  public void setStartChannelFraction(double startChannelFraction)
  {
    this.startChannelFraction = startChannelFraction;
  }

  /**
   * @param endChannelFraction the endChannelFraction to set
   */
  public void setEndChannelFraction(double endChannelFraction)
  {
    this.endChannelFraction = endChannelFraction;
  }

  public static class GaussianFitterResult implements PeakFitterResult
  {
    private final double center;
    private final double width;
    private final double intensity;
    private final double linear1;
    private final double linear2;
    private final double mse;
    private final double[] input;
    private final ChannelRegionOfInterestImpl region;

    public GaussianFitterResult(
            double linear1, double linear2,
            double intensity, double mu, double sigma,
            ChannelRegionOfInterestImpl region,
            double error,
            double[] input
    )
    {
      this.linear1 = linear1;
      this.linear2 = linear2;
      this.intensity = intensity;
      this.center = mu;
      this.width = sigma;
      this.region = region;
      this.mse = error;
      this.input = input;
    }

    /**
     * Get the center of the peak. Counting is in channel centers starting from
     * 0.
     *
     * @return the center of the peak.
     */
    public double getPeakLocation()
    {
      return center;
    }

    /**
     * Get the width of the peak. This will be in the same units as the peak
     * center. THe width will be as the standard deviation of a Gaussian
     * distribution. Use {@code getFWHM} for the apparent width.
     *
     * @return the width of the peak as defined by a Gaussian.
     */
    public double getWidth()
    {
      return width;
    }

    /**
     * Get the full width at half maximum.
     *
     * @return the estimated full width at half maximum.
     */
    public double getFWHM()
    {
      return width * MathConstants.GAUSSIAN_FWHM;
    }

    /**
     * @return the intensity
     */
    @Override
    public double getPeakIntensity()
    {
      return intensity;
    }

    /**
     * @return the linear1
     */
    public double getLinear1()
    {
      return linear1;
    }

    /**
     * @return the linear2
     */
    public double getLinear2()
    {
      return linear2;
    }

    public ChannelRegionOfInterestImpl getRegion()
    {
      return this.region;
    }

    public double getError()
    {
      return mse;
    }

    /**
     * Estimate the total integrated intensity of the continuum in the region of
     * interest.
     *
     * @return the counts in the continuum in the region of interest.
     */
    @Override
    public double getIntegratedContinuum()
    {
      return 0.5 * (region.getUpperChannel() - region.getLowerChannel()) * (linear1 + linear2);
    }

    /**
     * Estimate the total integerated intensity of the peak in the region of
     * interest.
     *
     * @return the total intensity of the peak in the region of interest.
     */
    @Override
    public double getIntegratedIntensity()
    {
      final double VSQRT2PI = 2.5066282741;      // sqrt(2*pi)
      final double VSQRTI2 = 0.7071067811865475; // 1/sqrt(2)

      double k = VSQRTI2 / width;
      double u1 = intensity * width / 2 * VSQRT2PI
              * (SpecialFunctions.erf(k * (region.getUpperChannel() - center))
              - SpecialFunctions.erf(k * (region.getLowerChannel() - center)));
//    double u2 = 2.5066282741 * intensity * Math.abs(width);
      return u1;
    }

    public double[] getInput()
    {
      return input;
    }

    public double[] getFit()
    {

      double[] out = new double[input.length];
      double k = 0.5 / width / width;
      int regionStart = (int) region.getLowerChannel();
      int regionEnd = (int) region.getUpperChannel();
      for (int i = regionStart; i < regionEnd; ++i)
      {
        double f = (i - regionStart) / (regionEnd - 1.0 - regionStart);
        out[i] = linear1 * (1 - f) + linear2 * f + intensity
                * Math.exp(-k * (i - center) * (i - center));
      }
      return out;
    }

    public double[] getResidual()
    {
      double[] out = DoubleArray.copyOf(input);
      double k = 0.5 / width / width;
      int regionStart = (int) region.getLowerChannel();
      int regionEnd = (int) region.getUpperChannel();
      for (int i = regionStart; i < regionEnd; ++i)
      {
//      double f = (i - regionStart) / (regionEnd - 1.0 - regionStart);
        out[i] -= intensity * Math.exp(-k * (i - center) * (i - center));
      }
      return out;
    }

  }

}
