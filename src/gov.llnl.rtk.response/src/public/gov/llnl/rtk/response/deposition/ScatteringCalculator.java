/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.Cursor;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.physics.Constants;
import gov.llnl.rtk.physics.KleinNishinaDistribution;
import gov.llnl.rtk.physics.PhotonCrossSections;
import gov.llnl.rtk.physics.PhotonCrossSectionsEvaluator;
import gov.llnl.rtk.physics.ScatteringDistribution;
import gov.llnl.rtk.physics.Units;

/**
 *
 * @author nelson85
 */
public class ScatteringCalculator
{

  // Parameters
  EnergyScale energyScale;
  PhotonCrossSections materialCrossSections;
  
  // State
  protected PhotonCrossSectionsEvaluator materialEval1;
  protected PhotonCrossSectionsEvaluator materialEval2;

  Units energyUnits = Units.get("keV");
  // Replace hard-coded physics with an injectable distributor
  ScatteringDistribution scatteringDistribution = new KleinNishinaDistribution();
  ScatteringDistribution.Evaluator scatteringEvaluator;
  double scatterRatio = 1.0;

  public EnergyScale getEnergyScale()
  {
    return this.energyScale;
  }

  // Energy scale always in keV
  public void setEnergyScale(EnergyScale scale)
  {
    this.energyScale = scale;
  }

  //<editor-fold desc="internal" defaultstate="closed">
  /**
   * Add
   *
   * @param result
   * @param energyCursor
   * @param energy
   * @param intensity
   * @param angularWeight
   */
  protected void renderLine(double[] result, Cursor energyCursor, double energy, double intensity, double[] angularWeight)
  {
    double ep0 = 0;
    double ep1;
    double valuePrevious = 0;
    double value;
    // Note: We use the evaluator's cross-section directly now,
    // scaling by the materialCrossSections's incoherent ratio.
    double mr = this.getMaterialElectronFactor(energy);
    if (intensity <= 0)
      return;
    for (int i = 0; i < angularWeight.length; ++i)
    {
      if (angularWeight[i] == 0 && valuePrevious == 0)
      {
        ep0 = 0;
        continue;
      }
      // Get the edges of the angular bin
      double cosTheta0 = 2.0 * (i) / (angularWeight.length) - 1.0;
      double cosTheta1 = 2.0 * (i + 1) / (angularWeight.length) - 1.0;
      // Correct starting point if needed
      if (ep0 == 0)
      {
        // Simple Compton shift for the boundary
        ep0 = energy / (1 + energy / Constants.MEC2.get() * (1 - cosTheta0));
      }
      // Compute the next point
      ep1 = energy / (1 + energy / Constants.MEC2.get() * (1 - cosTheta1));
      // Physics evaluation via the injected evaluator
      double f1 = mr * scatteringEvaluator.getCrossSection(energy, ep1);
      // Attenuation logic
      double af = this.getAbsorptionFactor(energy, ep1);
      // Average weighting over the segment
      double Wn = (i + 1 < angularWeight.length) ? angularWeight[i + 1] : angularWeight[i];
      value = af * intensity * (angularWeight[i] + Wn) / 2.0 * f1;
      // The Painter's Tally
      tally(result, energyCursor, ep0, valuePrevious, ep1, value);
      ep0 = ep1;
      valuePrevious = value;
    }
  }

  protected void renderGroup(double[] result, Cursor energyCursor, double energy0, double energy1, double intensity, double[] angularWeight)
  {
    if (intensity <= 0)
      return;
    double em = (energy0 + energy1) / 2.0;
    double mr = this.getMaterialElectronFactor(em);
    double Wprevious = 0;
    for (int i = 0; i < angularWeight.length; ++i)
    {
      if (angularWeight[i] == 0)
      {
        Wprevious = 0;
        continue;
      }
      double Wcurrent = angularWeight[i];
      double Wnext = (i + 1 < angularWeight.length) ? angularWeight[i + 1] : Wcurrent;

      // 1. Midpoint (Central Physics)
      double cosThetaM = 2.0 * (i + 0.5) / (angularWeight.length) - 1.0;
      double epm = em / (1 + em / Constants.MEC2.get() * (1 - cosThetaM));
      double vm = intensity * Wcurrent * mr * scatteringEvaluator.getCrossSection(em, epm);
      double afM = this.getAbsorptionFactor(em, epm) / 2.0;
      afM *= getThicknessCorrection(em, epm, cosThetaM);

      // 2. Low-end boundary
      double cosTheta0 = 2.0 * (i) / (angularWeight.length) - 1.0;
      double ep0 = energy0 / (1 + energy0 / Constants.MEC2.get() * (1 - cosTheta0));
      double v0 = intensity * (Wcurrent + Wprevious) / 2.0 * mr * scatteringEvaluator.getCrossSection(energy0, ep0);

      // 3. High-end boundary
      double cosTheta1 = 2.0 * (i + 1) / (angularWeight.length) - 1.0;
      double ep1 = energy1 / (1 + energy1 / Constants.MEC2.get() * (1 - cosTheta1));
      double v1 = intensity * (Wcurrent + Wnext) / 2.0 * mr * scatteringEvaluator.getCrossSection(energy1, ep1);

      // Two-stage tally for the group trapezoid
      tally(result, energyCursor, ep0, afM * v0, epm, afM * vm);
      tally(result, energyCursor, epm, afM * vm, ep1, afM * v1);
      Wprevious = Wcurrent;
    }
  }

  protected double getThicknessCorrection(double energy, double ep, double cosTheta)
  {
    return 1.0;
  }

  protected double getMaterialElectronFactor(double energy)
  {
    if (materialCrossSections == null)
      return 1.0;
    this.materialEval1.seek(energy);
    // Total incoherent from library (e.g. m2/kg)
    double libIncoherent = this.materialEval1.getIncoherent();
    // Ideal scattering per mole from our distribution (m2/mol)
    // We use a 90-degree scatter (cos=0) as the reference point for scaling
    double ep_ref = energy / (1 + energy / Constants.MEC2.get());
    double idealIncoherent = scatteringEvaluator.getCrossSection(energy, ep_ref);
    return libIncoherent / idealIncoherent;
  }

  protected double getAbsorptionFactor(double incident, double emitted)
  {
    if (materialCrossSections == null)
      return 1.0;
    this.materialEval1.seek(incident);
    this.materialEval2.seek(emitted);
    return 1 / (this.materialEval1.getTotal() + this.materialEval2.getTotal() * this.scatterRatio);
  }

  /**
   * Conservative Tally (Trapezoidal Binning).
   *
   * Uses an antialiasing approach to distribute "paint" (counts) across energy
   * bins. This ensures integral conservation and prevents aliasing artifacts at
   * sharp features like the Compton edge.
   *
   * @param result is the location to sum onto.
   * @param cursor
   * @param x1
   * @param y1
   * @param x2
   * @param y2
   */
  protected static void tally(double[] result, Cursor cursor, double x1, double y1, double x2, double y2)
  {
    int i1 = cursor.seek(x1);
    int i2 = cursor.seek(x2);
    double v0 = y1;
    double S = x2 - x1;
    double lower = x1;
    if (S <= 0)
    {
      result[i1] += (y2 + y1) / 2;
      return;
    }
    // Paint from the bottom
    double upper;
    double v;
    for (int i = i1; i < i2; ++i)
    {
      upper = cursor.get(i + 1);
      v = ((x2 - upper) * y1 + (upper - x1) * y2) / S;
      result[i] += (v0 + v) / 2 * (upper - lower) / S;
      v0 = v;
      lower = upper;
    }
    // Add rest to upper bin
    v = y2;
    upper = x2;
    result[i2] += (v0 + v) / 2 * (upper - lower) / S;
  }
//</editor-fold>
}
