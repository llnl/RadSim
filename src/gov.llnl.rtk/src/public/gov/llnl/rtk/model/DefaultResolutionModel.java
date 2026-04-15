// --- file: gov/llnl/rtk/model/DefaultResolutionModel.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.math.MathConstants;
import gov.llnl.math.function.Function;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;

/**
 * Used to represent the map the Gaussian standard deviations as a function of
 * energy. FWHM == 2.355 deviation F:X-&gt;Y | y = (A + B x)^C (assumes that the
 * low energy noise is basically quadrature with energy effects)
 *
 * @author nelson85
 */
@ReaderInfo(DefaultResolutionModelReader.class)
@WriterInfo(DefaultResolutionModelWriter.class)
public class DefaultResolutionModel
        implements ResolutionModel, Function.Invertable, Function.Parameterized
{
  double k[] = new double[3];

  public DefaultResolutionModel(double A, double B, double C)
  {
    k[0] = A;
    k[1] = B;
    k[2] = C;
  }

  /**
   *
   * @param x Energy
   * @return standard deviation at specified energy
   */
  @Override
  public double applyAsDouble(double x)
  {
    return Math.pow((double) (k[0] + k[1] * x), (double) (k[2]));
  }

  @Override
  public double inverse(double y)
  {
    return (Math.pow(y, 1 / k[2]) - k[0]) / k[1];
  }

  @Override
  public double derivative(double x)
  {
    return k[1] * k[2] * Math.pow(k[0] + k[1] * x, k[2] - 1);
  }

  /*
  @Override
  public double deriv_dxdy(double y)
  {
    return Math.pow(y, 1 / k[2]) / k[1] / k[2];
  }
   */
  /**
   * Creates a default resolution model based on measurements.This requires
   * three values.
   *
   * The energy resolution at zero keV specifics the inherent electric width.
   * This can be determined by random sampling, triggering off a pulser or
   * inferred from a low energy peak. The width at energy is the fwhm measured
   * for a specified energy.
   *
   * The energy power is the expected increase in the width of the peak as a
   * function of energy. For most scintillators, the energy power should be
   * between 0.5 and 0.7.
   * <p>
   * Typical use:
   * <pre>
   * {@code
   *    // 8% energy resolution detector with poor electonic noise
   *    model=DefaultResolutionModel.createFromMeasurement(10, 0.08*662, 662, 0.55);
   * }
   * </pre>
   *
   * @param fwhm0keV is the fwhm measured for a peak at 0 keV representing
   * electronic noise.
   * @param fwhmEkeV is the fwhm at energy
   * @param energy
   * @param energyPower is the expected widening.
   * @return a new energy resolution model.
   */
  public static DefaultResolutionModel createFromMeasurement(
          double fwhm0keV,
          double fwhmEkeV,
          double energy,
          double energyPower
  )
  {
    fwhm0keV /= MathConstants.GAUSSIAN_FWHM;
    fwhmEkeV /= MathConstants.GAUSSIAN_FWHM;
    double C = energyPower;
    double A = Math.pow(fwhm0keV, (1. / C));
    double B = (Math.pow(fwhmEkeV, (1. / C)) - A) / energy;

    if (A < 0)
      A = 0;

    return new DefaultResolutionModel(A, B, C);
  }

  // Convert to measurement representation
  public double getEnergy()
  {
    return 662;
  }

  public double getPower()
  {
    return this.k[2];
  }

  public double getFWHM0()
  {
    return Math.pow(k[0], k[2])*MathConstants.GAUSSIAN_FWHM;
  }

  public double getFWHM()
  {
    return Math.pow(k[1] * getEnergy() + k[0], k[2])*MathConstants.GAUSSIAN_FWHM;
  }

  @Override
  public double[] toArray()
  {
    return k.clone();
  }

  @Override
  public DefaultResolutionModel clone() throws CloneNotSupportedException
  {

    DefaultResolutionModel out = (DefaultResolutionModel) super.clone();
    return out;
  }
}
