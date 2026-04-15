// --- file: gov/llnl/rtk/physics/SphericalEscapeResult.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Result container for photon escape probability calculations.
 * <p>
 * Encapsulates arrays of referenceEscape probabilities for each energy group,
 * distinguishing between photons that referenceEscape without interaction and
 * those that referenceEscape after a single low-angle scattering event.
 * <p>
 * Instances of this class are immutable and intended for immediate consumption
 * by the caller.
 */
public class SphericalEscapeResult
{
  private final double[] energy;
  private final double[] uncollided;
  private final double[] lowAngle;
  private double[] stepRatio; // cached
  private final double thetaMax;
  private final KleinNishinaDistribution knd = new KleinNishinaDistribution();
  private final Units energyUnits;

  /**
   * Package-private constructor for {@code EscapeResult}, used internally by
   * {@link SphericalEscapeCalculator} to store photon escape probability
   * results.
   *
   * @param energy Array of photon energies. Each value must be interpreted in
   * the units specified by {@code energyUnits}.
   * @param energyUnits Units for all values in the {@code energy} array (e.g.,
   * keV, MeV, J).
   * @param uncollided Array of escape probabilities for uncollided photons (per
   * energy group). Each entry corresponds to the probability that a photon of
   * the given energy escapes without any interaction. This array is referenced
   * directly.
   * @param lowAngle Array of escape probabilities for photons escaping after a
   * single low-angle scatter (per energy group). Each entry corresponds to the
   * probability that a photon escapes after a single low-angle Compton scatter.
   * This array is referenced directly.
   * @param thetaMax The maximum scatter angle (in radians) used to define
   * "low-angle" scattering.
   *
   * <p>
   * <b>Intended Usage:</b> This constructor is not part of the public API and
   * is intended for use only by calculator classes within this package.
   * </p>
   *
   * <p>
   * <b>Array Ownership:</b> All arrays are referenced directly by this instance
   * and may be reused or modified by subsequent method calls. If the caller
   * needs to retain independent copies, they are responsible for copying the
   * arrays.
   * </p>
   *
   * <p>
   * <b>Units:</b> All arrays must use consistent units (e.g., energy in keV).
   * </p>
   */
  SphericalEscapeResult(double[] energy, Units energyUnits, double[] uncollided, double[] lowAngle, double thetaMax)
  {
    this.energy = energy;
    this.uncollided = uncollided;
    this.lowAngle = lowAngle;
    this.thetaMax = thetaMax;
    this.energyUnits = energyUnits;
  }

  /**
   * Gets the array of escape probabilities for uncollided photons. Each entry
   * corresponds to a specific energy group.
   *
   * @return the uncollided referenceEscape probabilities
   */
  public double[] getUncollided()
  {
    return uncollided;
  }

  /**
   * Gets the array of escape probabilities for photons escaping after a single
   * low-angle scatter. Each entry corresponds to a specific energy group. May
   * be {@code null} if not computed.
   *
   * @return the low-angle referenceEscape probabilities
   */
  public double[] getLowAngle()
  {
    return lowAngle;
  }

  /**
   * Returns the step ratio for each energy group, defined as:
   * <pre>
   *   stepRatio = lowAngle / (deltaE * uncollided)
   *   where deltaE = E - Emin,
   *         E     = incident photon energy,
   *         Emin  = scattered energy at angle {@code thetaMax}.
   * </pre>
   * <p>
   * <b>Array Ownership and Mutability:</b><br>
   * The returned array is owned by this {@code SphericalEscapeResult} instance
   * and may be reused or modified by subsequent calls. Callers <b>should
   * not</b>
   * modify the returned array in place. If the result must be preserved
   * independently of this object, callers are responsible for making a
   * defensive copy.
   * </p>
   * <p>
   * <b>Thread Safety:</b><br>
   * This method is <b>not thread-safe</b>. If concurrent access is required,
   * callers must provide their own synchronization and/or copy the result.
   * </p>
   * <p>
   * <b>Physical Units:</b><br>
   * The step ratio has units of 1/(energy unit) (e.g., 1/keV).
   * </p>
   *
   * @return the array of step ratios for each energy group (owned by this
   * instance)
   */
  public double[] getStepRatio()
  {
    if (stepRatio != null)
      return stepRatio;
    stepRatio = new double[energy.length];
    QuantityVectorIterator energyIter = new QuantityVectorIterator(energy, energyUnits);
    int i = 0;
    while (energyIter.hasNext())
    {
      Quantity incident = energyIter.next();
      double E = incident.as("keV");
      double mec2 = 511; // keV
      double Emin = E / (1 + (E / mec2) * (1 - Math.cos(thetaMax)));      
      double deltaE = E - Emin;

      if (uncollided[i] > 0 && deltaE > 0)
        stepRatio[i] = lowAngle[i] / (deltaE * uncollided[i]);
      else
        stepRatio[i] = 0;
      ++i;
    }
    return stepRatio;
  }

}
