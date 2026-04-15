// --- file: gov/llnl/rtk/response/deposition/MultiScatterLinearPointwise.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.rtk.physics.PhotonCrossSections;
import gov.llnl.rtk.physics.PhotonCrossSectionsEvaluator;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.ScatteringDistribution;
import gov.llnl.rtk.physics.Units;
import static gov.llnl.rtk.response.SplineUtilities.createPoints;
import static gov.llnl.rtk.response.deposition.DepositionUtility.addAssignConvolution;
import static gov.llnl.rtk.response.deposition.DepositionUtility.computeChordDistribution;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

/**
 * Implements a multi-scatter radiation transport calculator using linear
 * interpolation and precomputed fate tables. This class:
 *
 * 1. Precomputes photon interaction probabilities and energy deposition spectra
 * 2. Handles escape, photoelectric absorption, and pair production events 3.
 * Models multiple Compton scattering through recursive fate lookup 4. Computes
 * pair production outcomes (single/double escape peaks)
 *
 * All energies are in keV, cross-sections in m²/kg, density in kg/m³, and
 * macroscopic cross-sections (attenuation coefficients) in 1/m. Chord lengths
 * are in meters.
 */
public class MultiScatterLinearPointwise implements MultiScatterCalculator
{

  int nChord = 101;

  LinearInterpolant interpolant = new LinearInterpolant();

  final double[] buffer1;
  final double[] buffer2;
  final double[] buffer3;

  // Parameters
  private final int nBins;
  public final PhotonFate[] fates;
  private PhotonFate pairFate;

  // Physics objects (assume these are your Java classes)
  private final PhotonCrossSectionsEvaluator crossSections;
  private final ScatteringDistribution.Evaluator dbkn;
  private final double density;
  public final double[] chord;

  private DoubleUnaryOperator efficiency = (double d) -> 1.0;

  /**
   * Creates a new MultiScatterLinearPointwise calculator for computing photon
   * fates.
   *
   * @param material PhotonCrossSections object for the detector material
   * @param isotropic Isotropic chord quantile function for the detector
   * geometry
   * @param dbkn Scattering distribution (typically Klein-Nishina)
   * @param density Material density in kg/m³ (SI units)
   * @param maxEnergy Maximum energy in keV to consider
   */
  public MultiScatterLinearPointwise(
          PhotonCrossSections material,
          IsotropicChordQF isotropic,
          ScatteringDistribution dbkn,
          Quantity density,
          double maxEnergy)
  {
    this.crossSections = material.newEvaluator();
    this.crossSections.setInputUnits(Units.get("keV"));
    this.crossSections.setOutputUnits(Units.get("m2/kg")); // Standardize to SI units
    this.dbkn = dbkn.newEvaluator();
    this.dbkn.setInputUnits(Units.get("keV"));
    this.density = density.get();

    chord = new double[nChord]; // Chord lengths in meters
    computeChordDistribution(chord, isotropic);

    // Energy scale carries all these things (in keV)
    this.nBins = (int) maxEnergy;
    this.fates = new PhotonFate[nBins];

    this.buffer1 = new double[nBins + 1];
    this.buffer2 = new double[nBins + 1];
    this.buffer3 = new double[nBins + 1];
  }
  
  public void setEfficiency(DoubleUnaryOperator function)
  {
    this.efficiency = function;
  }

  /**
   * Precomputes photon fates for all energies up to nBins keV. Creates a lookup
   * table of interaction probabilities and spectra that can be queried
   * efficiently during runtime calculations.
   */
  @Override
  public void computeResponse()
  {
    // 1. Build scatterFirst-scatter matrix R
    for (int i = 0; i < nBins; i++)
    {
      double E = i + 1;
      EnergyScale scale;

      double mec2 = 511.0; // keV
      double comptonEdge = E * (1 - 1 / (1 + 2 * E / mec2));

      // Linear for the low energy range
      if (E < 100)
        scale = EnergyScaleFactory.newLinearScale(0, E, i + 2);
      else
      {
        // Convolution cost starts exploding at this point
        double[] ref = new double[]
        {
          0, comptonEdge, E
        };
        int[] meshNodes = new int[]
        {
          70, 30
        }; // Example: 40 bins below edge, 20 above
        double[] mesh = new double[1 + meshNodes[0] + meshNodes[1]];
        createPoints(mesh, ref, meshNodes);
        scale = EnergyScaleFactory.newScale(mesh);
      }

      PhotonFate fate0 = compute1(E, scale);
      fates[i] = compute2(fate0);
    }

    // 2. Now use it for pair production
    this.pairFate = this.computePairFate();
  }

  @Override
  public PhotonFate getPair()
  {
    return this.pairFate;
  }

  /**
   * Returns the precomputed photon fate for a given energy.
   *
   * @param energy Incident photon energy in keV
   * @return PhotonFate object with interaction probabilities and spectra
   */
  @Override
  public PhotonFate getFate(double energy)
  {
    // Clamp energy to valid range [1, nBins]
    int i = (int) Math.round(energy) - 1;
    if (i < 0)
      i = 0;
    if (i >= nBins)
      i = nBins - 1;
    return fates[i];
  }

  /**
   * Computes escape probabilities for 511 keV annihilation photons.
   *
   * @return Array containing [doubleEscape, singleEscape, photoElectric]
   * probabilities
   */
  public double[] computePairEscapeProbabilities()
  {
    crossSections.seek(511.0); // 511 keV annihilation photon
    // Cross-sections in m²/kg multiplied by density in kg/m³ gives attenuation coefficient in 1/m
    double cs_pe = crossSections.getPhotoelectric() * density;
    double cs_pp = (crossSections.getPairElectron() + crossSections.getPairNuclear()) * density;
    double cs_sc = crossSections.getIncoherent() * density;
    double cs_total = cs_pe + cs_pp + cs_sc; // Macroscopic cross-section in 1/m

    /*
     * <b>Important:</b> This method incorporates the correct chord length 
     * weighting as required by Bertrand's Paradox.  The probability distribution
     * of random chords in a convex body is not uniform in chord length.
     * The quantile grid and integration reflect this, ensuring physically correct 
     * results for escape probabilities.
     */
    double x = chord[nChord - 1];
    double expTerm = Math.exp(-cs_total * x);
    double f0 = x * expTerm;
    double f1 = 2.0 * (1 - expTerm) / cs_total - x * 2.0 * expTerm;
    double sum_f0 = f0, sum_f1 = f1, sum_norm = x;
    for (int i = 1; i < nChord - 1; i++)
    {
      x = chord[i];
      expTerm = Math.exp(-cs_total * x);
      f0 = x * expTerm;
      f1 = 2.0 * (1 - expTerm) / cs_total - x * 2.0 * expTerm;
      double weight = (i % 2 == 0 ? 2 : 4);
      sum_f0 += weight * f0;
      sum_f1 += weight * f1;
      sum_norm += weight * x;
    }

    double P0 = sum_f0 / sum_norm;
    double P1 = sum_f1 / sum_norm;
    double P2 = 1.0 - P0 - P1;
    return new double[]
    {
      P0, P1, P2
    };
  }

  /**
   * Calculates the probability that a photon escapes from the detector without
   * interacting, using Simpson's rule integration over chord lengths.
   *
   * @param totalXS Macroscopic cross-section (attenuation coefficient) in 1/m
   * @return Probability of escape (0-1)
   */
  private double computeEscapeProbability(double totalXS)
  {
    // Simpson's rule: requires odd number of points (nChord should be odd, e.g., 101)
    double du = 1.0 / (nChord - 1); // nChord-1 intervals
    double p_esc = Math.exp(-totalXS * chord[0])
            + Math.exp(-totalXS * chord[nChord - 1]);
    for (int i = 1; i < nChord - 1; i += 2)
      p_esc += 4.0 * Math.exp(-totalXS * chord[i]);
    for (int i = 2; i < nChord - 1; i += 2)
      p_esc += 2.0 * Math.exp(-totalXS * chord[i]);
    p_esc *= du / 3.0;
    return p_esc;
  }

  /**
   * Computes first-interaction photon fate for a given incident energy.
   *
   * @param E Incident photon energy in keV
   * @param scatterEnergies Energy grid for scatter spectrum
   * @return PhotonFate object with interaction probabilities and spectra
   */
  private PhotonFate compute1(double E, EnergyScale scatterEnergies)
  {
    // Allocate scatter spectrum on the supplied grid
    int n = scatterEnergies.getChannels();
    double[] R = new double[n + 1];

    // Get the cross sections for this energy (m²/kg)
    crossSections.seek(E);
    // Convert to macroscopic cross-sections (1/m) by multiplying by density (kg/m³)
    double cs_pe = crossSections.getPhotoelectric() * density;
    double cs_pp = (crossSections.getPairElectron() + crossSections.getPairNuclear()) * density;
    double cs_sc = crossSections.getIncoherent() * density;
    double total = cs_pe + cs_pp + cs_sc;
    
    if (Double.isNaN(total))
      throw new ArithmeticException("NaN in multi "+E);
    
    // Compute escape probability (integral over chord PDF)
    double p_esc = computeEscapeProbability(total);

    // Branching ratios
    double p_pe = (1 - p_esc) * cs_pe / total;
    double p_pp = (1 - p_esc) * cs_pp / total;
    double p_sc = (1 - p_esc) * cs_sc / total;

    // Fill Compton scatter spectrum using dbkn on the supplied grid
    // R[j] = dσ/dE at energyScale[j]
    for (int j = 0; j < n; ++j)
    {
      double outE = scatterEnergies.getEdge(j);
      double dE = E - outE;
      if (dE < 0)
        continue;
      R[j] = dbkn.getCrossSection(E, dE);
    }

    // Normalize R[] using numerical integration over the possibly non-uniform grid
    double sumCs = 0.0;
    for (int j = 0; j < n - 1; ++j)
    {
      double dx = scatterEnergies.getEdge(j + 1) - scatterEnergies.getEdge(j);
      // Trapezoidal rule
      sumCs += 0.5 * (R[j] + R[j + 1]) * dx;
    }

    // Avoid division by zero
    if (sumCs > 0)
      DoubleArray.multiplyAssign(R, (p_sc / sumCs));
    else
      DoubleArray.fill(R, 0);
    
    if (Double.isNaN(p_pe))
      throw new ArithmeticException("NaN in multi "+E);

    // M is still zero here, will be filled in compute2
    return new PhotonFate(E, p_esc, p_pe, p_pp, p_sc, scatterEnergies, R, null, 1, 0);
  }

  /**
   * Generates a diagnostic array of photoelectric contributions for debugging.
   *
   * @param f0 Initial photon fate
   * @param fates Array of photon fates for different energies
   * @return Array of photoelectric contributions
   */
  public double[] auditPE(PhotonFate f0, PhotonFate[] fates)
  {
    double p[] = new double[(int) (f0.energy - 1)];
    int i = (int) (f0.energy - 1);

    this.interpolant.assign(f0.energyScale, f0.scatterTotal);
    for (int j = 0; j < i; ++j)
    {
      double q = this.interpolant.compute(j + 1);
      p[j] = q * fates[i - j].photoElectric;
    }
    return p;
  }

  /**
   * Generates a diagnostic array of Compton scattering contributions for
   * debugging.
   *
   * @param fate0 Initial photon fate
   * @param fates Array of photon fates for different energies
   * @return 2D array of Compton scattering contributions
   */
  public double[][] auditCS(PhotonFate fate0, PhotonFate[] fates)
  {
    int i = (int) (fate0.energy - 1);
    double[][] out = new double[i][];
    EnergyScale scale = fate0.energyScale;
    this.interpolant.assign(scale, fate0.scatterTotal);
    for (int j = 0; j < i; j++)
    {
      double q = this.interpolant.compute(j + 1);
      PhotonFate fate1 = fates[i - j - 1];
      out[j] = new double[scale.getChannels() + 1];
      // Recursively add the scattered spectrum from lower fate onto buffer1
      fate1.addScatter2(out[j], scale, fate0.energy - fate1.energy, q);
    }
    return out;
  }

  /**
   * Computes multi-interaction photon fate from a first-scatter fate. Handles
   * recursive scattering, escape, photoelectric absorption, and pair
   * production.
   *
   * @param fate0 First-scatter fate object
   * @return PhotonFate object including all subsequent interactions
   */
  private PhotonFate compute2(PhotonFate fate0)
  {
    // Start with the initial outcome
    double energy = fate0.energy;
    double photoTotal = fate0.photoElectric;
    double pairTotal = fate0.pair;
    double escapeTotal = fate0.escape;

    double ea0 = energy * this.efficiency.applyAsDouble(energy);
    double s0 = photoTotal;
    double s1 = photoTotal * ea0;
    double s2 = photoTotal * ea0 * ea0;

    EnergyScale scale = fate0.energyScale;
    int incidentIdx = scale.getChannels() + 1;

    Arrays.fill(buffer1, 0, incidentIdx, 0);
    Arrays.fill(buffer2, 0, incidentIdx, 0);
    int i = (int) (fate0.energy - 1);

    this.interpolant.assign(scale, fate0.scatterTotal);
    for (int j = 0; j < i; ++j)
    {
      double q = this.interpolant.compute(j + 1);
      
      PhotonFate fate1 = this.fates[i - j - 1];
      photoTotal += q * fate1.photoElectric;
      pairTotal += q * fate1.pair;

      double E1 = fate1.energy;
      double A = (energy - E1) * this.efficiency.applyAsDouble(energy - E1); // deterministic part
      E1 *= fate1.efficiency;
      double var1 = E1 * E1 * fate1.variance;                              // variance remainder collected energy
      double ea_mean_branch = A + E1;
      double e2_branch = ea_mean_branch*ea_mean_branch + var1;
      
//      System.out.println("   "+ q+ " " + fate1.photoElectric);
      s0 += q * fate1.photoElectric;
      s1 += q * fate1.photoElectric * ea_mean_branch;
      s2 += q * fate1.photoElectric * e2_branch;
    }

    for (int j = 0; j < i; j++)
    {
      double q = this.interpolant.compute(j + 1);
      PhotonFate fate1 = this.fates[i - j - 1];
      // Recursively add the scattered spectrum from lower fate onto buffer1
      fate1.addScatter2(buffer2, scale, fate0.energy - fate1.energy, q);
    }

    for (int j = 0; j < fate0.scatterTotal.length; j++)
    {
      double E = fate0.energyScale.getEdge(j);
      PhotonFate fate2 = this.getFate(fate0.energy - E);
      // Special case no additional loss 
      if (fate2 == null)
        fate2 = fate0;
      buffer2[j] += fate2.escape * fate0.scatterTotal[j];
    }

    double[] singleEscapeSpectrum = Arrays.copyOfRange(buffer3, 0, incidentIdx);
    double[] multiScatterSpectrum = Arrays.copyOfRange(buffer2, 0, incidentIdx);

    // Estimate the effect collection efficiency on the photoelectric output
    double eff = 0;
    double var = 0;
    if (s0 > 0)
    {
      eff = s1 / s0; // units average energy
      var =  s2 / s0 - eff * eff;
    }
    eff /= energy; // units efficiency
    var /= energy * energy; // units
    
    // Post correction for total
    return new PhotonFate(fate0.energy,
            escapeTotal, photoTotal, pairTotal, 0, scale,
            multiScatterSpectrum, singleEscapeSpectrum, eff, var);
  }

  /**
   * Computes the fate of a pair production event (two 511 keV photons). Handles
   * single escape, double escape, and full absorption cases.
   *
   * @return PhotonFate object for pair production
   */
  public PhotonFate computePairFate()
  {
    int nBins = 511; // Number of bins for single 511 keV photon
    EnergyScale energyScale = EnergyScaleFactory.newLinearScale(0, 1022, 2 * nBins);

    // Escape probabilities
    double[] pairEscapes = computePairEscapeProbabilities();
    double doubleEscape = pairEscapes[0];
    double singleEscape = pairEscapes[1];
    double photoElectric = pairEscapes[2];

    // Multi-scatter fate for 511 keV
    PhotonFate fate511 = this.getFate(511.0);

    double[] spectrum511 = new double[nBins];
    fate511.addScatter(spectrum511, 0, energyScale, 0, nBins, 1);

    // Convolution result is length 2*nBins-1 = 1021
    double[] scatter = new double[2 * nBins];
    double[] convolution = addAssignConvolution(new double[2 * nBins - 1], spectrum511, nBins, spectrum511, nBins, photoElectric);

    // Copy convolution result into scatter[0..2*nBins-2]
    System.arraycopy(convolution, 0, scatter, 0, 2 * nBins - 1);

    // Add single escape peak at 511 keV (index 510)
    DoubleArray.addAssignScaled(scatter, nBins - 1, spectrum511, 0, nBins, singleEscape);

    if (Double.isNaN(photoElectric))
      throw new ArithmeticException("NaN in pair calculation");
    
    return new PhotonFate(
            photoElectric,
            singleEscape,
            doubleEscape,
            energyScale,
            scatter
    );
  }

  /**
   * Interpolates scatter spectra from internal energy grid to output energy
   * grid.
   *
   * @param singleEscapeSpectrum Output array for single-escape spectrum
   * @param multiScatterSpectrum Output array for multi-scatter spectrum
   * @param scale Target energy scale
   * @param buffer3 Source buffer for single-escape data
   * @param buffer2 Source buffer for multi-scatter data
   */
  private void convert(double[] singleEscapeSpectrum, double[] multiScatterSpectrum, EnergyScale scale, double[] buffer3, double[] buffer2)
  {
    int channels = scale.getChannels();
    double E_min = 0.0;
    double dE = 1.0;
    int nBuffer = buffer2.length;

    for (int c = 0; c < channels; ++c)
    {
      double E = scale.getCenter(c);
      double idx_f = (E - E_min) / dE;
      int idx0 = (int) Math.floor(idx_f);
      int idx1 = idx0 + 1;

      // Clamp indices to valid range
      if (idx0 < 0)
      {
        idx0 = 0;
        idx1 = 0;
      }
      else if (idx1 >= nBuffer)
      {
        idx1 = nBuffer - 1;
        idx0 = idx1;
      }

      double E0 = E_min + idx0 * dE;
      double E1 = E_min + idx1 * dE;
      double f = (E1 == E0) ? 0.0 : (E - E0) / (E1 - E0);

      multiScatterSpectrum[c] = (1 - f) * buffer2[idx0] + f * buffer2[idx1];
      singleEscapeSpectrum[c] = (1 - f) * buffer3[idx0] + f * buffer3[idx1];
    }
  }
}
