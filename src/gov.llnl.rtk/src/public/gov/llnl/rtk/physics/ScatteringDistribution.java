// --- file: gov/llnl/rtk/physics/ScatteringDistribution.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * @author nelson85
 */
public interface ScatteringDistribution
{
  /**
   * Get the cross section for a specific energy.
   *
   * @param energyIncident is the energy of the incident photon.
   * @param energyEmitted is the energy of the emitted photon.
   * @return in SI units.
   */
  public double getCrossSection(Quantity energyIncident, Quantity energyEmitted);

  public double getCosAngle(Quantity energyIncident, Quantity energyEmitted);

  public Evaluator newEvaluator();

  /**
   * <b>Evaluator for fast, unit-configurable scattering calculations.</b>
   * <p>
   * This interface provides a high-performance API for evaluating scattering
   * cross sections and related quantities with explicit control over input
   * energy units.
   * </p>
   *
   * <h3>Input Units</h3>
   * <ul>
   * <li>
   * All energy arguments to this evaluator (<code>ei</code>, <code>ep</code>)
   * must be supplied in the units set by {@link #setInputUnits(Units)}.
   * </li>
   * <li>
   * By default, the input unit is SI energy (joules), but you may set any
   * consistent energy unit (e.g., keV, MeV) using
   * {@link #setInputUnits(Units)}.
   * </li>
   * <li>
   * <b>All energy arguments must use the same unit as set by this method.</b>
   * </li>
   * </ul>
   *
   * <h3>Output Units</h3>
   * <ul>
   * <li>
   * The output of {@link #getCrossSection(double, double)} is always in SI area
   * units (square meters, m²), regardless of input units.
   * </li>
   * <li>
   * The output of {@link #getCosAngle(double, double)} is dimensionless (cosine
   * of the scattering angle).
   * </li>
   * </ul>
   *
   * <h3>Usage Example</h3>
   * <pre>
   * ScatteringDistribution.Evaluator eval = ...;
   * eval.setInputUnits(Units.get("keV")); // set input units to keV
   * double cs = eval.getCrossSection(500, 200); // energies in keV, result in m²
   * double cosT = eval.getCosAngle(500, 200);   // energies in keV, result is dimensionless
   * </pre>
   *
   * <h3>Notes</h3>
   * <ul>
   * <li>
   * For batch or vectorized calculations, this evaluator is optimized for
   * speed.
   * </li>
   * <li>
   * For full unit safety and flexibility, use the
   * {@link ScatteringDistribution#getCrossSection(Quantity, Quantity)} method
   * in the parent interface.
   * </li>
   * </ul>
   */
  public interface Evaluator
  {
    /**
     * Sets the input units for all subsequent energy arguments.
     *
     * @param energy Units for input energies (must be of type ENERGY)
     */
    public void setInputUnits(Units energy);

    /**
     * Gets the current input units for energy arguments.
     *
     * @return Units for input energies
     */
    public Units getInputUnits();

    /**
     * Computes the Doppler-broadened Klein-Nishina cross section for Compton
     * scattering.
     *
     * @param ei Incident photon energy (in units set by
     * {@link #setInputUnits(Units)})
     * @param ep Emitted photon energy (same units as {@code ei})
     * @return Cross section in SI area units (square meters, m²)
     */
    public double getCrossSection(double ei, double ep);

    /**
     * Computes the cosine of the Compton scattering angle.
     *
     * @param ei Incident photon energy (in units set by
     * {@link #setInputUnits(Units)})
     * @param ep Emitted photon energy (same units as {@code ei})
     * @return Cosine of the scattering angle (dimensionless)
     */
    public double getCosAngle(double ei, double ep);

  }
}
