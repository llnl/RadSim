// --- file: gov/llnl/rtk/response/deposition/PhotonFate.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

// Outcome for an isotropically traveling photon in the detector.
import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.EnergyScale;

/**
 * Represents the fate (final outcome) of a photon or positron in a detector,
 * including probabilities for escape, photoelectric absorption, pair
 * production, and energy-deposition spectra from single and multiple Compton
 * scatters.
 * <p>
 * Used for both photon and pair events; some fields are exclusive to one case.
 * The scatter spectra are defined on an adaptive energy-loss grid and can be
 * mapped to arbitrary output energy grids for deposition calculations.
 * </p>
 *
 * <ul>
 * <li>For photon events: use
 * {@link #PhotonFate(double, double, double, double, double[], double[], double[])}.</li>
 * <li>For pair events: use
 * {@link #PhotonFate(double, double, double, double[], double[])}.</li>
 * </ul>
 *
 * <p>
 * Provides normalization and robust mapping of fate spectra to output grids via
 * {@link #addScatter}.
 * </p>
 */
public class PhotonFate
{
  public final double energy; // Center energy (keV or MeV)

  public final double escape; // Escape probability
  public final double photoElectric; // Photoelectric full absorption
  public final double pair; // Pair production (unattributed)

  public final double singleEscape; // Pair production, one 511 keV escapes (single escape peak)
  public final double doubleEscape; // Pair production, both 511 keV escape (double escape peak)

  public final double[] scatterSingle; // Scatter single deposition (this may become important if we want to consider depostion effects)
  public final double[] scatterTotal; // Multi-scatter spectrum (per output bin)
  public final EnergyScale energyScale;
  public double efficiency;
  public double variance;
  double scatter;

  /**
   * Photon fate representation (single/multi-scatter, escape, photo, pair).
   *
   * @param energy Center energy (keV or MeV)
   * @param escape Escape probability
   * @param photo Photoelectric absorption probability
   * @param pair Pair production probability
   * @param energyScale Energy-loss grid (same units as energy)
   * @param scatterSingle Single-scatter deposition spectrum (energy-loss grid)
   * @param scatterTotal Multi-scatter spectrum (energy-loss grid)
   * @param efficiency
   * @param variance
   */
  public PhotonFate(double energy, double escape, double photo, double pair, double scatter, EnergyScale energyScale,
          double[] scatterTotal, double[] scatterSingle, double efficiency, double variance)
  {
    // Normalize (this is mostly a hedge against minor bookkeeping errors)
    double sumM = 1; // normalize(energyScale, scatterTotal, scatterSingle, photo + escape + pair);

    // Copy the fields
    this.energy = energy;
    this.escape = escape / sumM;
    this.photoElectric = photo / sumM;
    this.pair = pair / sumM;

    this.scatterSingle = scatterSingle;
    this.scatter = scatter;
    this.energyScale = energyScale;
    this.scatterTotal = scatterTotal;
    this.singleEscape = 0;
    this.doubleEscape = 0;
    this.efficiency = efficiency;
    this.variance = variance;
  }

  /**
   * Pair fate representation (single/double escape, photo, multi-scatter).
   *
   * @param photo Photoelectric absorption probability
   * @param singleEscape Probability of one 511 keV escaping
   * @param doubleEscape Probability of both 511 keV escaping
   * @param scatterEnergies Energy-loss grid (keV)
   * @param scatterTotal Multi-scatter spectrum (energy-loss grid)
   */
  public PhotonFate(double photo, double singleEscape, double doubleEscape,
          EnergyScale scatterEnergies, // what energies to these represent
          double[] scatterTotal)
  {
    this.energy = 1022;
    this.photoElectric = photo;
    this.escape = 0;
    this.pair = 0;
    this.scatterSingle = null;
    this.singleEscape = singleEscape;
    this.doubleEscape = doubleEscape;
    this.energyScale = scatterEnergies;
    this.scatterTotal = scatterTotal;
  }

  /**
   * Numerically integrates y(x) using the trapezoidal rule, where x-values are
   * the bin centers provided by the EnergyScale.
   *
   * <p>
   * The integration is performed over y.length samples, using
   * scale.getCenter(i) for each i. There is no requirement that y.length
   * matches scale.getChannels().
   * </p>
   *
   * @param scale The EnergyScale providing bin centers.
   * @param y Array of y-values (length N), evaluated at scale.getCenter(i).
   * @return Approximate integral of y(x) over the range of x.
   */
  static double integrateTrapezoidal(EnergyScale scale, double[] y)
  {
    int N = y.length;
    if (N < 2)
      throw new IllegalArgumentException("Need at least two points for trapezoidal integration.");
    if (y.length > scale.getChannels())
      throw new IllegalArgumentException("Energy scale must cover the samples "
              + y.length + " " + scale.getChannels());

    double sum = 0.0;
    for (int i = 0; i < N - 1; ++i)
    {
      double x0 = scale.getCenter(i);
      double x1 = scale.getCenter(i + 1);
      double dx = x1 - x0;
      sum += 0.5 * (y[i] + y[i + 1]) * dx;
    }
    return sum;
  }

  static double normalize(EnergyScale scale, double[] scatter, double[] single, double other)
  {
    // scale == energyScale
    double sumM = other;
    sumM += integrateTrapezoidal(scale, scatter);

    // IMPORTANT NOTE: The single is for auditing the first scattering and not 
    // part of the total deposition as its contribution is already in the scattering
    // Now renormalize everything so total is 1
    DoubleArray.divideAssign(scatter, sumM);

    // Apply the same correction to the single if it exists for plotting 
    if (single != null)
      DoubleArray.divideAssign(single, sumM);

    return sumM;
  }
  
  public static int counter =0;
  public static int counter3 =0;
  
  /**
   * Adds this fate's scatter (energy-loss) spectrum into the output deposition
   * array, interpolating as needed to map from the fate's native energy-loss
   * grid to an arbitrary output energy grid (which may be linear, logarithmic,
   * or user-supplied).
   * <p>
   * <b>Alignment rule:</b> The fate's zero energy-loss bin is aligned with
   * {@code outEnergy[start]} in the output array. <b>Always set
   * {@code offset = outEnergy[start]};</b> this ensures correct physical
   * mapping.
   * </p>
   *
   * @param out Output deposition array to which the spectrum will be added.
   * @param start Starting index in {@code out} (aligns with
   * {@code outEnergy[start]}).
   * @param outEnergy Output energy grid (bin centers, sorted ascending).
   * @param offset Physical energy corresponding to {@code out[start]} (must be
   * {@code outEnergy[start]}).
   * @param length Number of output bins to fill (prevents overruns).
   * @param intensity Scaling factor (e.g., probability, branching ratio).
   *
   * <p>
   * <b>Example usage:</b></p>
   * <pre>
   *   // Add fate spectrum for incident energy E, starting at output bin i:
   *   fate.addScatter(deposited, i, outEnergy, outEnergy[i], n - i, scale);
   * </pre>
   */
  public void addScatter(double[] out,
          int start,
          EnergyScale outEnergy, double offset,
          int length,
          double intensity)
  {
    counter++;
    final EnergyScale x = this.energyScale;
    double[] edges = x.getEdges();
    final double[] y = this.scatterTotal;
    int n = y.length;
    int j = 0; // index for energyScale
    for (int i = 0; i < length; i++)
    {
      double e = outEnergy.getEdge(i+start) - offset;
      while (j < n - 2 && e > edges[j+1])
        j++;

      if (e < 0)
        continue;
      if (e > this.energy)
        break;
      
      // Linear interpolate between x[j] and x[j+1]
      double x0 = edges[j];
      double x1 = edges[j+1];
      double y0 = y[j];
      double y1 = y[j + 1];
      double f = (e - x0) / (x1 - x0);
      double interp = y0 * (1 - f) + y1 * f;
      out[i + start] += interp * intensity;
      counter3++;
    }
  }
  public void addScatter2(double[] out,
          EnergyScale outEnergy, double offset,
          double intensity)
  {
    counter++;
    final EnergyScale x = this.energyScale;
    double[] edges = x.getEdges();
    final double[] y = this.scatterTotal;
    int n = y.length;
    int j = 0; // index for energyScale
    
    int start = outEnergy.findEdgeFloor(offset);
    int end = outEnergy.findEdgeCeiling(energy+offset);
    for (int i = start; i < end; i++)
    {
      double e = outEnergy.getEdge(i) - offset;
      while (j < n - 2 && e > edges[j+1])
        j++;

      if (e < 0)
        continue;
      if (e > this.energy)
        break;
      
      // Linear interpolate between x[j] and x[j+1]
      double x0 = edges[j];
      double x1 = edges[j+1];
      double y0 = y[j];
      double y1 = y[j + 1];
      double f = (e - x0) / (x1 - x0);
      double interp = y0 * (1 - f) + y1 * f;
      out[i] += interp * intensity;
      counter3++;
    }
  }

  /**
   * Convenience method: returns the multi-scatter deposition spectrum mapped
   * onto the specified output energy grid.
   *
   * @param outScale Output energy scale (bin centers)
   * @return Array of length outScale.getChannels(), containing the
   * multi-scatter spectrum.
   */
  public double[] computeScatter(EnergyScale outScale)
  {
    int n = outScale.getChannels();
    double[] out = new double[n];
    // Map this fate's scatter spectrum onto the output grid
    this.addScatter(out, 0, outScale, 0, n, 1.0);
    return out;
  }
}
