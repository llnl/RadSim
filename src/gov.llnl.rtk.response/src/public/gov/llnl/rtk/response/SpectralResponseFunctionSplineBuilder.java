// --- file: gov/llnl/rtk/response/SpectralResponseFunctionSplineBuilder.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.IntegerArray;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.response.support.CubicSplineBoundary;
import gov.llnl.rtk.response.support.CubicSplineBuilder;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Builder used when defining new detectors or loading from disk.
 */
public class SpectralResponseFunctionSplineBuilder
{
  final SpectralResponseFunctionSpline rf = new SpectralResponseFunctionSpline();
  ArrayList<SplineResponseEntry> entries = new ArrayList<>();

  /**
   * Define the vendor name to associate with this detector.
   *
   * @param vendor
   * @return
   */
  public SpectralResponseFunctionSplineBuilder vendor(String vendor)
  {
    rf.vendor = vendor;
    return this;
  }

  public SpectralResponseFunctionSplineBuilder lldEnergy(double[] energies)
  {
    rf.lld.energy = energies;
    return this;
  }

  public SpectralResponseFunctionSplineBuilder lldAttenuation(double[] values)
  {
    rf.lld.attenuation = values;
    return this;
  }

  /**
   * Define the model name to associate with this detector.
   *
   * @param value
   * @return
   */
  public SpectralResponseFunctionSplineBuilder model(String value)
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
  public SpectralResponseFunctionSplineBuilder energyScale(EnergyScale value)
  {
    rf.energyScale = value;
    return this;
  }

  public SpectralResponseFunctionSplineBuilder internal(double[] internal)
  {
    rf.internal = internal;
    return this;
  }

  /**
   * Define the mixing fraction.
   *
   * @param value
   * @return
   */
  public SpectralResponseFunctionSplineBuilder theta(double value)
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
  public SpectralResponseFunctionSplineBuilder negativeTail(double value)
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
  public SpectralResponseFunctionSplineBuilder positiveTail(double value)
  {
    rf.peakShapeParameters.positiveTail = value;
    return this;
  }

  public SpectralResponseFunctionSplineBuilder incompleteIntensity(double value)
  {
    rf.incomplete.intensity = value;
    return this;
  }

  public SpectralResponseFunctionSplineBuilder incompleteCenter(double value)
  {
    rf.incomplete.center = value;
    return this;
  }

  public SpectralResponseFunctionSplineBuilder incompleteVariance(double value)
  {
    rf.incomplete.variance = value;
    return this;
  }

  /**
   * Allocate a a new entry.
   *
   * @return the builder for the entry.
   */
  public EntryBuilder photon()
  {
    return new EntryBuilder(this);
  }

  /**
   * Create the detector response function from the builder.
   *
   * @return the detector response function.
   */
  public SpectralResponseFunctionSpline create()
  {
    // Sort all entries by energy
    Collections.sort(entries, (p1, p2) -> Double.compare(p1.continuum.energy, p2.continuum.energy));
    // Convert entries into an array for quick access
    rf.entries = this.entries.toArray(SplineResponseEntry[]::new);
    // Extract intervals
    rf.intervals = entries.stream().mapToDouble(p -> p.continuum.energy).toArray();

    double[] e2 = entries.stream()
            .filter(p -> p.photoelectric != null)
            .mapToDouble(p -> p.continuum.energy).toArray();

    // Do we even have photopeaks in the detector
    if (e2.length > 2)
    {
      // Construct the resolution as a function of energy
      double[] widths = entries.stream()
              .filter(p -> p.photoelectric != null)
              .mapToDouble(p -> p.photoelectric.width).toArray();
      rf.resolutionFunction = new CubicSplineBuilder(e2, widths)
              .start(CubicSplineBoundary.NATURAL)
              .end(CubicSplineBoundary.NATURAL)
              .create();

      // FIXME currently the units are relative rather than a function of the
      // absolute exposure.
      double[] amps = entries.stream()
              .filter(p -> p.photoelectric != null)
              .mapToDouble(p -> p.photoelectric.amplitude).toArray();
      rf.efficiencyFunction = new CubicSplineBuilder(e2, amps)
              .start(CubicSplineBoundary.NATURAL)
              .end(CubicSplineBoundary.NATURAL)
              .create();
    }

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

  //<editor-fold desc="builder" defaultstate="collapsed">

  public static class EntryBuilder
  {
    private final SpectralResponseFunctionSplineBuilder builder;
    private final SplineResponseEntry entry;

    // Temporary storage for each line
    ArrayList<SplineResponseLine> peaks = new ArrayList<>();

    EntryBuilder(SpectralResponseFunctionSplineBuilder builder)
    {
      this.builder = builder;
      entry = new SplineResponseEntry();
    }

    /**
     * Add a new line to this entry.
     *
     * @param type determines how the line will interact with neighbors.
     * @param amplitude is the intensity associated with the source.
     * @param center is the center location in keV.
     * @param width is the width in keV (sigmas).
     * @return the EntryBuilder for chaining.
     */
    public EntryBuilder line(RenderItem type, double amplitude, double center, double width)
    {
      // We will use dedicated slots for each interaction so that we are sure we are
      // adding the corresponding line properly.
      switch (type)
      {
        // These cases are special because we want to be able to collect them for plotting
        case PHOTOELECTRIC:
          entry.photoelectric = new SplineResponseLine(type, amplitude, center, width);
          break;
        case ANNIHILATION:
          entry.annihilation = new SplineResponseLine(type, amplitude, center, width);
          break;
        case ESCAPE_SINGLE:
          entry.singleEscape = new SplineResponseLine(type, amplitude, center, width);
          break;
        case ESCAPE_DOUBLE:
          entry.doubleEscape = new SplineResponseLine(type, amplitude, center, width);
          break;

        default:
          // All others go in the list.
          this.peaks.add(new SplineResponseLine(type, amplitude, center, width));
      }
      return this;
    }

    /**
     * Define the energy for this entry.
     *
     * @param value
     * @return the EntryBuilder for chaining.
     */
    public EntryBuilder energy(double value)
    {
      entry.continuum.energy = value;
      return this;
    }

    /**
     * Define the continuum meshing for this line.
     *
     * @param mesh are the number of meshPoints points
     * @param values is the density at the defined points (4-th root).
     * @param widthFactor defines how much above the line to extend the meshPoints.
     * @return the EntryBuilder for chaining.
     */
    public EntryBuilder continuum(double[] values, int[] mesh, double widthFactor)
    {
      if (values.length != 1 + IntegerArray.sum(mesh))
        throw new ArrayIndexOutOfBoundsException();
      entry.continuum.meshPoints = mesh.clone();
      entry.continuum.values = values.clone();
      entry.continuum.widthFactor = widthFactor;
      for (double d : entry.continuum.values)
        if (d<=0)
          throw new IllegalArgumentException("Continuum values must be positive");
      return this;
    }

    /**
     * Create a new entry.
     *
     * This is called after all of the entry values are set.
     *
     * @return the entry created.
     */
    public SplineResponseEntry create()
    {
      if (entry.continuum.values == null)
        throw new RuntimeException("Continuum was not defined for this entry");
      if (entry.continuum.energy == -1)
        throw new RuntimeException("Energy was not defined for this entry");
      if (entry.annihilation == null && (entry.singleEscape != null || entry.doubleEscape != null))
        throw new RuntimeException("Corrupt annihilation entry");
      if (entry.annihilation != null && (entry.singleEscape == null || entry.doubleEscape == null))
        throw new RuntimeException("Corrupt entry");
      entry.peaks = peaks.toArray(SplineResponseLine[]::new);
      builder.entries.add(entry);
      return entry;
    }
  }

//</editor-fold>
}
