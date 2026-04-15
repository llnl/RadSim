/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.Cursor;
import gov.llnl.math.DoubleArray;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxEvaluator;
import gov.llnl.rtk.flux.FluxGroup;
import gov.llnl.rtk.flux.FluxItem;
import gov.llnl.rtk.flux.FluxLine;
import gov.llnl.rtk.flux.FluxSpectrum;
import gov.llnl.rtk.physics.ScatteringDistribution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnvironmentalScatteringCalculator extends ScatteringCalculator
{
  private double[] angularWeights;
  double clutter = 0.0;

  public EnvironmentalScatteringCalculator(ScatteringDistribution dist)
  {
    this.scatteringDistribution = dist;
    this.scatteringEvaluator = dist.newEvaluator();
    this.scatteringEvaluator.setInputUnits(energyUnits);
  }

  /**
   * Set a custom angular distribution (e.g., from a GADRAS .geo file or an
   * analytic ground-plane model).
   *
   * @param weights Array where index maps to cos(theta) from -1 to 1.
   */
  public void setAngularWeights(double[] weights)
  {
    this.angularWeights = weights;
  }

  /**
   * Analytic Ground-Plane Model. Creates a distribution where the intensity
   * follows a 1/cos(theta) or a simple Lambertian reflection for soil.
   */
  public void setupUniformGround(int bins)
  {
    this.angularWeights = new double[bins];
    for (int i = 0; i < bins; ++i)
    {
      double cosTheta = 2.0 * (i + 0.5) / bins - 1.0;
      // Simple logic: Only backscatter from the ground (cosTheta < 0)
      // or a specific angular window.
      this.angularWeights[i] = (cosTheta < 0) ? 1.0 : 0.0;
    }
  }

  public void setAngularProfile(double p0, double p45, double p90, double p135, double p180, double pow)
  {
    int bins = 128;
    double[] weights = new double[bins];
    if (pow < 0.2)
      pow = 0.2;

    // Anchor points in Cosine Space: [1.0, 0.707, 0.0, -0.707, -1.0]
    double[] anchorsX =
    {
      1.0, 0.707, 0.0, -0.707, -1.0
    };
    double[] anchorsY =
    {
      Math.pow(p0, pow), Math.pow(p45, pow), Math.pow(p90, pow), Math.pow(p135, pow), Math.pow(p180, pow)
    };

    for (int i = 0; i < bins; i++)
    {
      double cosTheta = 2.0 * (i + 0.5) / bins - 1.0;
      // Interpolate cosTheta against the anchors to get the weight
      weights[i] = Math.pow(interpolate(cosTheta, anchorsX, anchorsY), 1 / pow);
    }
    this.setAngularWeights(weights);
  }

  public void setRoomGeometry(double surfaceArea, double volume)
  {
    // 1. Calculate the 'Characteristic Length' of the room (Mean Chord)
    // For a cube of side L: 4(L^3) / 6(L^2) = (2/3)L
    double meanChord = 4.0 * volume / surfaceArea;

    // 2. The Space Factor (Inverse of the mean chord)
    // Smaller rooms have higher 'Geometric Density'
    double geometricDensity = 1.0 / meanChord;

    // 3. The Coupling Equation
    // We use a saturation function (1 - e^-x) to ensure clutter never exceeds 
    // the material's Albedo, preventing a 'runaway' recursive loop.
    // The 'k' constant (0.5 to 1.0) calibrates the 'Openness' of the geometry.
    double k = 0.8;
    this.clutter = (1.0 - Math.exp(-k * geometricDensity));
  }

  private double interpolate(double cosTheta, double[] x, double[] y)
  {
    // Standard piecewise linear interpolation
    // Since our x-anchors (cosines) go from 1.0 down to -1.0, 
    // we iterate backwards or sort them.
    for (int i = 0; i < x.length - 1; i++)
    {
      // Find which segment the current bin's cosTheta falls into
      // Note: x[i] is the higher cosine (smaller angle)
      if (cosTheta <= x[i] && cosTheta >= x[i + 1])
      {
        double range = x[i] - x[i + 1];
        double weight = (cosTheta - x[i + 1]) / range;
        return (1 - weight) * y[i + 1] + weight * y[i];
      }
    }
    // Fallback for floating point edge cases at -1.0
    return y[y.length - 1];
  }

  public Flux apply(Flux flux)
  {
    if (flux == null || this.energyScale == null)
      return null;

    if (this.materialCrossSections != null)
    {
      this.materialEval1 = this.materialCrossSections.newEvaluator();
      this.materialEval2 = this.materialCrossSections.newEvaluator();
      this.materialEval1.setInputUnits(energyUnits);
      this.materialEval2.setInputUnits(energyUnits);
    }

    // In environmental models, the scatterRatio (cosI/cosE) is often 
    // approximated as 1.0 (isotropic assumption) or a fixed geometric constant.
    this.scatterRatio = 1.0;

    Cursor energyCursor = new Cursor(this.energyScale.getEdges());
    double[] result = new double[this.energyScale.getChannels()];

    if (flux instanceof FluxSpectrum)
    {
      FluxEvaluator eval = flux.newPhotonEvaluator();
      double[] edges = this.energyScale.getEdges();
      for (int i = edges.length - 2; i >= 0; --i)
      {
        double e0 = edges[i];
        double e1 = edges[i + 1];
        double d = eval.getIntegral(e0, e1, FluxItem.ALL);
        double returned = 0;
        if (clutter > 0)
        {
          energyCursor.seek(e0);
          int i1 = energyCursor.getIndex();
          double f1 = energyCursor.getFraction();
          energyCursor.seek(e1);
          double f2 = energyCursor.getFraction();
          int i2 = energyCursor.getIndex();
          this.materialEval1.seek((e0 + e1) / 2);
          double albedo = materialEval1.getIncoherent() / materialEval1.getTotal();
          returned = albedo * (DoubleArray.sumRange(result, i1 + 1, i2) + (1 - f1) * result[i1] + f2 * result[i2]);
        }
        renderGroup(result, energyCursor, e0, e1, d + clutter * returned, this.angularWeights);
      }
    }
    else
    {
      for (FluxLine line : flux.getPhotonLines())
      {
        renderLine(result, energyCursor, line.getEnergy(), line.getIntensity(), this.angularWeights);
      }

      // FIXME we need to move from highest to lowest
      // Tally groups first
      List<FluxGroup> groups = new ArrayList<>(flux.getPhotonGroups());
      Collections.reverse(groups);
      for (FluxGroup group : groups)
      {
        double e0 = group.getEnergyLower();
        double e1 = group.getEnergyUpper();
        double d = group.getCounts();

        double returned = 0;
        if (clutter > 0)
        {
          energyCursor.seek(e0);
          int i1 = energyCursor.getIndex();
          double f1 = energyCursor.getFraction();
          energyCursor.seek(e1);
          double f2 = energyCursor.getFraction();
          int i2 = energyCursor.getIndex();
          this.materialEval1.seek((e0 + e1) / 2);
          double albedo = materialEval1.getIncoherent() / materialEval1.getTotal();
          returned = albedo * (DoubleArray.sumRange(result, i1 + 1, i2) + (1 - f1) * result[i1] + f2 * result[i2]);
        }
        renderGroup(result, energyCursor, e0, e1, d + clutter * returned, angularWeights);
      }
    }

    return new FluxSpectrum(this.energyScale, result, null, null);
  }

}
