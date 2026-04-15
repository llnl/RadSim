// --- file: gov/llnl/rtk/response/SpectralResponseFunctionSpline.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.interp.SingleInterpolator;
import gov.llnl.rtk.response.support.CubicSplineBuilder;
import gov.llnl.rtk.response.support.CubicSpline;
import gov.llnl.rtk.response.support.CubicSplineBoundary;
import gov.llnl.math.RebinUtilities;
import gov.llnl.rtk.data.EnergyScale;
import java.util.Arrays;
import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.euclidean.Versor;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.Spectrum;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author nelson85
 */
public class SpectralResponseFunctionSpline extends SpectralResponseFunctionBase
{

  final static double TAIL_TOLERANCE = 1e-5;

  // Global peakShapeParameters parameters
  ShapeParameters peakShapeParameters = new ShapeParameters();

  // Impulse reponses sorted by energy
  SplineResponseEntry[] entries;

  LLDFunction lld = new LLDFunction();

  // Internal source if present
  double[] internal;

  // Caches created from the entries.
  double[] intervals;              // collected from continuum.energy

  CubicSpline resolutionFunction;  // collected from photopeaks
  CubicSpline efficiencyFunction;  // collected from photopeaks
//  SingleInterpolator lldFunction;

  // Model for incomplete capture in CZT
  SpectralResponseIncomplete incomplete = new SpectralResponseIncomplete();

  void initialize()
  {
    this.incomplete.cache();
    this.lld.initialize();
  }

  @Override
  public SpectralResponseEvaluator newEvaluator()
  {
    return new SplineResponseEvaluator(this);
  }

  /**
   * @return the lld
   */
  public LLDFunction getLld()
  {
    return lld;
  }

  /**
   * Define an internal source spectrum which will be added to evaluations.
   *
   * Typical examples would be detectors with calibrations sources or detectors
   * with inherent radioactivity (LaBr).
   *
   * @param spectrum
   */
  public void setInternal(DoubleSpectrum spectrum)
  {
    try
    {
      spectrum = new DoubleSpectrum(spectrum);
      spectrum.rebinAssign(energyScale);
      double[] counts = spectrum.toDoubles();
      double[] edges = energyScale.getEdges();
      double rt = spectrum.getRealTime();
      for (int i = 0; i < counts.length; ++i)
      {
        counts[i] /= rt * (edges[i + 1] - edges[i]);
      }
      // Store the density in each bin
      this.internal = counts;
    }
    catch (RebinUtilities.RebinException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Compute an internal spectrum to be added to samples.
   *
   * @param scale
   * @return
   */
  public DoubleSpectrum getInternal(EnergyScale scale)
  {
    if (scale == null)
      scale = energyScale;
    CubicSpline internalSpline = new CubicSplineBuilder(energyScale.getCenters(), internal)
            .start(CubicSplineBoundary.NATURAL)
            .end(CubicSplineBoundary.NATURAL)
            .create();
    internalSpline.flatten();
    double[] edges = scale.getEdges();
    double[] counts = new double[scale.getChannels()];
    SingleInterpolator.Evaluator eval = internalSpline.get();
    double d0 = eval.applyAsDouble(edges[0]);
    for (int i = 0; i < counts.length; ++i)
    {
      double d1 = eval.applyAsDouble(edges[i + 1]);
      counts[i] = Math.max((d1 + d0) / 2 * (edges[i + 1] - edges[i]), 0);
      d0 = d1;
    }
    return Spectrum.builder().time(1).counts(counts).scale(scale).asDouble();
  }

  @Override
  public double getGeometryFactor(Vector3 sourceCoordinate,
          Vector3 globalCoordinate, Versor globalOrientation,
          Vector3 sensorCoordinate, Versor sensorOrientation)
  {
    Vector3 delta = Vector3Ops.subtract(sourceCoordinate, globalCoordinate);
    delta = globalOrientation.rotate(delta);
    double distance2 = Vector3Ops.sqrDistance(delta, sensorCoordinate);
    // Correct formula for near field is
    //   4 * Math.atan2(2 * distance * Math.sqrt(4 * distance * distance + w * w + h * h), w * h)/w/h;
    // But we don't have the area of the detector
    return 1 / distance2;
  }

  @Override
  public Map<String, Double> getParameters()
  {
    HashMap<String, Double> map = new HashMap<>();
    map.put(EMG_THETA, this.peakShapeParameters.theta);
    map.put(EMG_NEGATIVE_TAIL, this.peakShapeParameters.negativeTail);
    map.put(EMG_POSITIVE_TAIL, this.peakShapeParameters.positiveTail);
    return map;
  }

  /**
   * @return the entries
   */
  public SplineResponseEntry[] getEntries()
  {
    return entries;
  }

  /**
   * @return the peakShapeParameters
   */
  public ShapeParameters getPeakShapeParameters()
  {
    return peakShapeParameters;
  }

//<editor-fold desc="boilerplate" defaultstate="collapsed">
  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 79 * hash + this.peakShapeParameters.hashCode();
    hash = 79 * hash + this.incomplete.hashCode();
    hash = 79 * hash + Arrays.deepHashCode(this.entries);
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
    final SpectralResponseFunctionSpline other = (SpectralResponseFunctionSpline) obj;
    if (!Objects.equals(this.peakShapeParameters, other.peakShapeParameters))
      return false;
    if (!Objects.equals(this.incomplete, other.incomplete))
      return false;
    return Arrays.deepEquals(this.entries, other.entries);
  }
//</editor-fold>

}
