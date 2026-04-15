// --- file: gov/llnl/rtk/physics/KleinNishinaDistribution.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * <b>Klein-Nishina Compton Scattering Distribution</b>
 * <p>
 * Implements the Klein-Nishina differential cross section for photon (gamma)
 * scattering on free electrons, including total and angular cross section
 * calculations. Supports both {@link Quantity}-based and fast double-based
 * (unit-configurable) evaluation.
 * </p>
 * <h2>Units</h2>
 * <ul>
 * <li>All input energies must be in consistent units (see
 * {@link Evaluator#setInputUnits(Units)}).</li>
 * <li>All cross sections are returned in SI area units (square meters,
 * m²).</li>
 * <li>Angles are in radians.</li>
 * </ul>
 * <h2>References</h2>
 * <ul>
 * <li>Klein, O. and Nishina, Y. (1929). "Über die Streuung von Strahlung durch
 * freie Elektronen nach der neuen relativistischen Quantendynamik von Dirac".
 * Zeitschrift für Physik. 52 (11–12): 853–868.</li>
 * <li>J.D. Jackson, "Classical Electrodynamics", 3rd Edition, Eq. 13.70.</li>
 * </ul>
 *
 * @author nelson85
 */
public class KleinNishinaDistribution implements ScatteringDistribution
{
  private static final double RE2 = Constants.RADIUS_E.get() * Constants.RADIUS_E.get(); // [m²]
  private static final double MEC2 = Constants.MEC2.get(); // [energy]
  private static final double KCS = 2 * Math.PI * RE2 * MEC2 * MEC2; // [m² * (energy)²]

  /**
   * Returns the differential Klein-Nishina cross section for a given incident
   * and emitted photon energy. Energies must be in the same units (typically
   * Joules or keV).
   *
   * @param energyIncident Incident photon energy (Quantity, must have ENERGY
   * property)
   * @param energyEmitted Emitted photon energy (same units as incident)
   * @return Differential cross section in SI units (square meters, m²)
   */
  @Override
  public double getCrossSection(Quantity energyIncident, Quantity energyEmitted)
  {
    energyIncident.require(PhysicalProperty.ENERGY);
    energyEmitted.require(PhysicalProperty.ENERGY);
    double ei = energyIncident.get();
    double ep = energyEmitted.get();
    return _getCrossSection(ei, ep);
  }

  /**
   * Returns the cosine of the Compton scattering angle for a given incident and
   * emitted photon energy. Energies must be in the same units (typically Joules
   * or keV).
   *
   * @param energyIncident Incident photon energy (Quantity, must have ENERGY
   * property)
   * @param energyEmitted Emitted photon energy (same units as incident)
   * @return Cosine of the scattering angle (dimensionless), or NaN if out of
   * physical domain.
   */
  @Override
  public double getCosAngle(Quantity energyIncident, Quantity energyEmitted)
  {
    energyIncident.require(PhysicalProperty.ENERGY);
    energyEmitted.require(PhysicalProperty.ENERGY);
    return _getCosAngle(energyIncident.get(), energyEmitted.get());
  }

  /**
   * Returns the total Klein-Nishina cross section for a given incident photon
   * energy.
   *
   * @param energyIncident Incident photon energy (Quantity, must have ENERGY
   * property)
   * @return Total cross section in SI units (square meters, m²)
   */
  public double getTotalCrossSection(Quantity energyIncident)
  {
    double ei = energyIncident.get();
    double x = ei / MEC2;
    // See Jackson Eq. 13.71 for total cross section
    return Math.PI * RE2 * (2 * x * (2 + 8 * x + 9 * x * x + x * x * x) / Math.pow(1 + 2 * x, 2)
            + (-2 - 2 * x + x * x) * Math.log(1 + 2 * x)) / (x * x * x);
  }

  /**
   * Returns the Klein-Nishina cross section integrated between two scattering
   * angles.
   *
   * @param energyIncident Incident photon energy (Quantity, must have ENERGY
   * property)
   * @param a0 Minimum scattering angle (radians)
   * @param a1 Maximum scattering angle (radians)
   * @return Integrated cross section in SI units (square meters, m²)
   */
  public double getAngularCrossSection(Quantity energyIncident, double a0, double a1)
  {
    double ei = energyIncident.get();
    if (ei <= 0)
      return 0;
    double x = ei / MEC2;
    double c0 = x * Math.cos(a0);
    double c1 = x * Math.cos(a1);
    double v0 = ((-2 - 6 * x - 5 * x * x + 2 * (1 + 2 * x) * c0) / 2 / sqr(1 + x - c0)
            + (-2 - 2 * x + x * x) * Math.log(1 + x - c0) - c0) / (x * x * x);
    double v1 = ((-2 - 6 * x - 5 * x * x + 2 * (1 + 2 * x) * c1) / 2 / sqr(1 + x - c1)
            + (-2 - 2 * x + x * x) * Math.log(1 + x - c1) - c1) / (x * x * x);
    return Math.PI * RE2 * (v1 - v0);
  }

  /**
   * <b>Evaluator for fast, unit-configurable Klein-Nishina calculations.</b>
   * <p>
   * This evaluator allows efficient computation of cross sections and
   * scattering angles with explicit control of input energy units. All energies
   * must be supplied in the units set by {@link #setInputUnits(Units)}. Output
   * cross sections are always in SI area units (m²).
   * </p>
   * <h3>Usage Example</h3>
   * <pre>
   * KleinNishinaDistribution.Evaluator eval = new KleinNishinaDistribution().newEvaluator();
   * eval.setInputUnits(Units.get("keV")); // or Units.get("J"), Units.get("MeV"), etc.
   * double cs = eval.getCrossSection(500, 200); // energies in keV, result in m²
   * double cosT = eval.getCosAngle(500, 200);   // energies in keV, result is dimensionless
   * </pre>
   *
   * Javadoc for individual methods are in the base class.
   *
   * @return a new evaluator.
   */
  @Override
  public Evaluator newEvaluator()
  {
    return new EvaluatorImpl();
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  // --- Internal: Core Klein-Nishina formula (energies in same units) ---
  private double _getCrossSection(double ei, double ep)
  {
    // Physical domain: ep_min <= ep <= ei
    double ep_min = ei / (1 + 2 * ei / MEC2);
    if (ei < 0 || ep > ei || ep < ep_min)
      return 0;
    double cosT = -1 + MEC2 / ep - MEC2 / ei;
    double sinT2 = 1 - cosT * cosT;
    return KCS * (ei / ep + ep / ei - sinT2) / (ei * ei);
  }

  private double _getCosAngle(double ei, double ep)
  {
    double ep_min = ei / (1 + 2 * ei / MEC2);
    if (ei < 0)
      return 1.0; // Forward direction for unphysical negative energy
    if (ep > ei || ep < ep_min)
      return -1.0; // Backward direction for forbidden region
    return -1 + MEC2 / ep - MEC2 / ei;
  }

  private static double sqr(double x)
  {
    return x * x;
  }

  /* Evaluator implementation.  Javadoc is in the interface. */
  private class EvaluatorImpl implements Evaluator
  {
    private Units units = PhysicalProperty.ENERGY;
    private double scaleToSI = 1.0;

    @Override
    public void setInputUnits(Units energy)
    {
      energy.require(PhysicalProperty.ENERGY);
      this.units = energy;
      this.scaleToSI = units.getValue(); // Converts input to SI (Joules)
    }

    @Override
    public Units getInputUnits()
    {
      return this.units;
    }

    @Override
    public double getCrossSection(double ei, double ep)
    {
      double ei_SI = ei * scaleToSI;
      double ep_SI = ep * scaleToSI;
      return _getCrossSection(ei_SI, ep_SI);
    }

    @Override
    public double getCosAngle(double ei, double ep)
    {
      double ei_SI = ei * scaleToSI;
      double ep_SI = ep * scaleToSI;
      return _getCosAngle(ei_SI, ep_SI);
    }
  }
//</editor-fold>
}
