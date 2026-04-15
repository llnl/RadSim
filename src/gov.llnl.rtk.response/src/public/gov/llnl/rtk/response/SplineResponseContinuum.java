// --- file: gov/llnl/rtk/response/SplineResponseContinuum.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.math.interp.SingleInterpolator;
import java.util.Arrays;

/**
 *
 * @author nelson85
 */
public class SplineResponseContinuum
{
  double energy = -1;
  double widthFactor = 1.5;
  
  // Important note: for interpolation purposes we store this as the 4th root
  double[] values = null;
  int[] meshPoints = new int[4];

  /**
   * Construct the reference points for the curve.
   *
   * This must match the corresponding code in extractor.py.
   *
   * @param eval
   * @param targetEnergy
   * @param width
   * @return
   */
  double constructCurve(FunctionResponseEvaluator eval, double targetEnergy)
  {
    eval.resizeContinuumSpline(this.values.length);
    double[] x = eval.continuumX;
    double eend = createMesh(x, targetEnergy);
    // Convert to a spline
    eval.spline.update(x, this.values, this.values.length);
    return eend;
  }

  public double createMesh(double[] x, double targetEnergy)
  {

    // Set up the reference points
    double epp = targetEnergy;
    double erf0;
    double erf1;
    double eend;
    if (this.energy >= 520.0)
    {
      double ebs = targetEnergy / (1 + 2 * targetEnergy / 511) + 2;
      double ece = targetEnergy - ebs;
      erf0 = ebs;
      erf1 = ece;
    }
    else if (this.energy < 75)
    {
      erf1 = epp * 2.0 / 3.0;
      erf0 = erf1 / 2.0;
    }
    else
    {
      double ebs = targetEnergy / (1 + 2 * targetEnergy / 511) + 2;
      double ece = targetEnergy - ebs;
      erf1 = Math.max(ebs, ece);
      erf0 = erf1 / 2;
    }
    double f = Math.sqrt(targetEnergy / energy);
    eend = epp + f * widthFactor;
    // Set up temporary space
    double[] energyRef = new double[]
    {
      0, erf0, erf1, epp, eend
    };

    // Expand reference points
    SplineUtilities.createPoints(x, energyRef, meshPoints);
    return eend;
  }

  /**
   * (private) Render the continuum on the target.
   *
   * Continuum is stored as the fourth root for the density function. The
   * meshing equations are governed by the physics of deposition in the
   * detector.
   *
   * This function is add the continuum to the SpectralBuffer extrapolated from
   * a defined energy point.
   *
   * @param target is the accumulator to add the counts to.
   * @param eval is the evaluator for the target.
   * @param targetEnergy is the actual energy to be simulated.
   * @param intensity is the intensity to apply to the values.
   */
  void render(SpectralBuffer buffer, FunctionResponseEvaluator eval,
          double targetEnergy, double intensity)
  {
    if (intensity == 0)
      return;

    // Get the edges
    double[] edges = eval.energyScale.getEdges();

    // Update spline for the target energy
    double x1 = constructCurve(eval, targetEnergy);

    // Create an evaluator
    SingleInterpolator.Evaluator ceval = new QuadEvaluator(eval.spline.get());

    // Correction for scaling
    intensity *= energy / targetEnergy;

    // Numerical integration of density curve
    int index = 0;
    int end = buffer.target.length;
    if (eval.lldEval.lldChannel > 0)
    {
      SingleInterpolator.Evaluator leval = new CompoundEvaluator(ceval, eval.lldEval.lldEvaluator);
      index = integrateSimpsons(buffer, leval, edges, intensity, x1, 0, eval.lldEval.lldChannel, eval.lldEval.lldSampling);
      if (index == eval.lldEval.lldChannel)
        index = integrateSimpsons(buffer, ceval, edges, intensity, x1, index, end, 1);
    }
    else
    {
      index = integrateSimpsons(buffer, ceval, edges, intensity, x1, index, end, 1);
    }

    // index and ulast are set for the tail extension
    // Execute tail extension
    if (index > 0 && index < buffer.target.length)
    {
      extendTail(buffer.target, eval, edges, index, intensity);
    }
  }

  private int integrateSimpsons(SpectralBuffer buffer,
          SingleInterpolator.Evaluator ceval,
          double[] edges,
          double intensity,
          double endEnergy,
          int start,
          int end,
          int sampling)
  {
    // Fill out the curve from the reference points
    int index = start;
    double e0;
    double e1 = edges[index];
    double u1, u2, u3;
    u3 = ceval.applyAsDouble(e1);
    for (; index < end; index++)
    {
      // Set up for next segment
      e0 = e1;
      e1 = edges[index + 1];

      // Compute at the requested number of samples
      double e2;
      double e3 = e0;
      double total = 0;
      for (int i = 0; i < sampling; ++i)
      {
        // Compute the end point
        e2 = e3;
        u1 = u3;

        // Compute using subsampling
        double f = ((double) (i + 1)) / sampling;
        e3 = e0 * (1 - f) + e1 * f;

        // If we run out of curve during this bin then truncate for tail extension
        if (endEnergy < e3)
          e3 = endEnergy;

        // Compute the center energy
        double ec = (e2 + e3) / 2;

        // Compute the density across the group
        u2 = ceval.applyAsDouble(ec); // compute the value in the middle of the group
        u3 = ceval.applyAsDouble(e3); // compute the value at the end of the group

        // Apply Simpson's rule
        double u = (u1 + 4 * u2 + u3) / 6;
        total += u * (e3 - e2);

        // Watch for end of curve
        if (e3 == endEnergy)
          break;
      }

      total *= intensity;
      buffer.target[index] += total;
      if (e3 == endEnergy)
        break;
    }
    return index;
  }

  /**
   * Complete the tail of the continuum distribution.
   *
   * Assumes some type of exponential tail will appear in the data.
   *
   * @param target
   * @param eval
   * @param edges
   * @param index
   * @param intensity
   */
  private void extendTail(double[] target, FunctionResponseEvaluator eval,
          double[] edges, int index, double intensity)
  {
    // Use the last three points to define the extension
    int n = this.values.length;
    double e0 = eval.continuumX[n - 3];
    double e1 = eval.continuumX[n - 2];
    double e2 = eval.continuumX[n - 1];
    double d0 = 4 * Math.log(this.values[n - 3]);
    double d1 = 4 * Math.log(this.values[n - 2]);
    double d2 = 4 * Math.log(this.values[n - 1]);

    // Compute the shape of the tail from three points
    double offset = d2;
    double slope0 = (d2 - d0) / (e2 - e0);
    double slope1 = (d2 - d1) / (e2 - e1);
    double accel = (slope1 - slope0) / (e1 - e0);
    if (accel > 0)
      accel = 0;
    double vel = -accel * (e1 - e2) + slope1;

    // Define the stopping point as 5 orders down
    double dr = Math.exp(offset) / 100000.0;

    // Set up for Simpson's rule
    e0 = e2;
    d0 = Math.exp(offset + vel * (e0 - e2) + accel * (e0 - e2) * (e0 - e2));

    // Extend tail until we run out of samples
    while (index < target.length)
    {
      // FIXME if the bins are too large we may need to subsample.

      // Use Simpson's rule for the integration (to avoid need for error function)
      e1 = edges[index + 1];
      double ew = e1 - e0;
      double ec = (e1 + e0) / 2;

      d1 = Math.exp(offset + vel * (ec - e2) + accel * (ec - e2) * (ec - e2));
      d2 = Math.exp(offset + vel * (e1 - e2) + accel * (e1 - e2) * (e1 - e2));

      // Apply LLD if applicable
      double total;
      if (index < eval.lldEval.lldChannel)
      {
        double f0 = eval.lldEval.lldEvaluator.applyAsDouble(e0);
        double f1 = eval.lldEval.lldEvaluator.applyAsDouble(e1);
        double f2 = eval.lldEval.lldEvaluator.applyAsDouble(e2);
        total = ew * (f0 * d0 + 4 * f1 * d1 + f2 * d2) * intensity / 6;
      }
      else
        total = ew * (d0 + 4 * d1 + d2) * intensity / 6;
      target[index] += total;

      // Set up for next
      index++;
      e0 = e1;
      d0 = d2;
      // Check stopping point
      if (d2 < dr)
        return;
    }
  }

//<editor-fold desc="boiler plate" defaultstate="collapsed">
  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 53 * hash + (int) (Double.doubleToLongBits(this.energy) ^ (Double.doubleToLongBits(this.energy) >>> 32));
    for (int i : this.meshPoints)
      hash = 53 * hash + i;
    hash = 53 * hash + Arrays.hashCode(this.values);
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
    final SplineResponseContinuum other = (SplineResponseContinuum) obj;
    if (Double.doubleToLongBits(this.energy) != Double.doubleToLongBits(other.energy))
      return false;
    if (!Arrays.equals(this.meshPoints, other.meshPoints))
      return false;
    return Arrays.equals(this.values, other.values);
  }
//</editor-fold>

  /**
   * @return the energy
   */
  public double getEnergy()
  {
    return energy;
  }

  /**
   * @return the widthFactor
   */
  public double getWidthFactor()
  {
    return widthFactor;
  }

  /**
   * @return the values
   */
  public double[] getValues()
  {
    return values;
  }

  public double[] getEnergies()
  {
    double[] x = new double[this.values.length];
    this.createMesh(x, energy);
    return x;
  }

  /**
   * @return the meshPoints
   */
  public int[] getMeshPoints()
  {
    return meshPoints;
  }

  public double getTotal()
  {
    double[] x = getEnergies();
    double[] v = getValues();
    if (x.length != v.length)
      throw new IllegalArgumentException();
    double total = 0.0;
    for (int i = 0; i < x.length - 1; i++)
    {
      double dx = x[i + 1] - x[i];
      double y0 = Math.pow(v[i], 4);
      double y1 = Math.pow(v[i + 1], 4);
      total += 0.5 * (y0 + y1) * dx;
    }
    return total;
  }
}
