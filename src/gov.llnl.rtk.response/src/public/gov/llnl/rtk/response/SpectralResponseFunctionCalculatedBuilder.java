// --- file: gov/llnl/rtk/response/SpectralResponseFunctionCalculatedBuilder.java ---
/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.physics.ElectronShell;
import gov.llnl.rtk.physics.Material;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.response.support.CubicSplineBoundary;
import gov.llnl.rtk.response.support.CubicSplineBuilder;
import gov.llnl.rtk.response.support.CubicSplineExtrapolation;
import gov.llnl.rtk.response.support.TransformedInterpolator;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 * Builder used when defining new detectors or loading from disk.
 */
public class SpectralResponseFunctionCalculatedBuilder
{
  final SpectralResponseFunctionCalculated rf = new SpectralResponseFunctionCalculated();

  /**
   * Define the vendor name to associate with this detector.
   *
   * @param vendor
   * @return
   */
  public SpectralResponseFunctionCalculatedBuilder vendor(String vendor)
  {
    rf.vendor = vendor;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder lldEnergy(double[] energies)
  {
    rf.lld.energy = energies;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder lldAttenuation(double[] values)
  {
    rf.lld.attenuation = values;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder resolutionEnergy(double[] energyKeV)
  {
    rf.resolutionEnergy = energyKeV.clone();
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder resolutionFwhm(double[] fwhmKeV)
  {
    // We work in Pow space which is squared.  Interpolate then convert back to width with sqrt.
    rf.resolutionWidth2 = DoubleStream.of(fwhmKeV).map(x -> x * x).toArray();
    return this;
  }

  /**
   * Define the model name to associate with this detector.
   *
   * @param value
   * @return
   */
  public SpectralResponseFunctionCalculatedBuilder model(String value)
  {
    rf.model = value;
    return this;
  }

  /**
   * (optional) Set the default energy scale for the spectrum.
   *
   * @param value
   * @return
   */
  public SpectralResponseFunctionCalculatedBuilder energyScale(EnergyScale value)
  {
    rf.energyScale = value;
    return this;
  }

  /**
   * Define the mixing fraction.
   *
   * @param value
   * @return
   */
  public SpectralResponseFunctionCalculatedBuilder theta(double value)
  {
    rf.peakShapeParameters.theta = value;
    return this;
  }

  /**
   * Define the negative tail coefficient.
   *
   * @param value
   * @return
   */
  public SpectralResponseFunctionCalculatedBuilder negativeTail(double value)
  {
    rf.peakShapeParameters.negativeTail = value;
    return this;
  }

  /**
   * Define the positive tail coefficient.
   *
   * @param value
   * @return
   */
  public SpectralResponseFunctionCalculatedBuilder positiveTail(double value)
  {
    rf.peakShapeParameters.positiveTail = value;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder incompleteIntensity(double value)
  {
    rf.incomplete.intensity = value;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder incompleteCenter(double value)
  {
    rf.incomplete.center = value;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder incompleteVariance(double value)
  {
    rf.incomplete.variance = value;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder shells(List<ElectronShell> v)
  {
    rf.shells = v;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder material(Material v)
  {
    rf.material = v;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder length(Quantity v)
  {
    rf.length = v;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder width(Quantity v)
  {
    rf.width = v;
    return this;
  }

  public SpectralResponseFunctionCalculatedBuilder height(Quantity v)
  {
    rf.height = v;
    return this;
  }

  /**
   * Create the detector response function from the builder.
   *
   * @return the detector response function.
   */
  public SpectralResponseFunctionCalculated create()
  {
    // Using the spacing required for HPGe for now
    int nIntervals = 0;
    double[] intervals = new double[280];
    for (int i = 4; i < 177; i += 4)
      intervals[nIntervals++] = i;
    for (int i = 176; i < 513; i += 8)
      intervals[nIntervals++] = i;
    for (int i = 512; i < 1137; i += 16)
      intervals[nIntervals++] = i;
    for (int i = 1136; i <= 6000; i += 32)
      intervals[nIntervals++] = i;

    // Extract intervals
    rf.intervals = intervals;

    if (rf.resolutionEnergy == null || rf.resolutionWidth2 == null)
      throw new IllegalStateException("resolution not defined");
    if (rf.resolutionEnergy.length != rf.resolutionWidth2.length)
      throw new IllegalStateException("resolution size mismatch");

    if (rf.width == null)
      throw new IllegalStateException("width not set");
    if (rf.height == null)
      throw new IllegalStateException("height not set");
    if (rf.length == null)
      throw new IllegalStateException("length not set");

    // sort resolution pairs by energy
    int n = rf.resolutionEnergy.length;
    int[] idx = java.util.stream.IntStream.range(0, n)
            .boxed()
            .sorted(java.util.Comparator.comparingDouble(i -> rf.resolutionEnergy[i]))
            .mapToInt(Integer::intValue)
            .toArray();

    double[] e = new double[n];
    double[] w2 = new double[n];
    for (int i = 0; i < n; i++)
    {
      int k = idx[i];
      e[i] = rf.resolutionEnergy[k];
      w2[i] = rf.resolutionWidth2[k];
    }
    rf.resolutionEnergy = e;
    rf.resolutionWidth2 = w2;

    var baseModel = new CubicSplineBuilder(rf.resolutionEnergy, rf.resolutionWidth2)
            .end(CubicSplineBoundary.NATURAL)
            .extrapolation(CubicSplineExtrapolation.LINEAR).create();
    rf.resolutionModel = new TransformedInterpolator(baseModel, x -> Math.sqrt(Math.max(x, 0.0)));

    // Verify if we have an lld function
    if (rf.lld.energy != null)
    {
      if (rf.lld.energy.length != rf.lld.attenuation.length)
        throw new IllegalStateException("lld size mismatch");
    }

    rf.initialize();
    // Support for incomplete captures (CZT)
    return rf;
  }

}
