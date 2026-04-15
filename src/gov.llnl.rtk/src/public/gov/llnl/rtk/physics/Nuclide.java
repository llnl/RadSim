// --- file: gov/llnl/rtk/physics/Nuclide.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Representation of a nuclide. Created from the
 * {@link gov.llnl.rtk.physics.Nuclides#get(java.lang.String) Nuclides.get}.
 *
 * @author nelson85
 */
public interface Nuclide extends Comparable<Nuclide>
{
  public static double LN2 = Math.log(2);
  static final double AVAGADROS_CONSTANT = 6.02214076e23;

  /**
   * Get the name of the nuclide.
   *
   * @return
   */
  String getName();

  Element getElement();

  /**
   * Get the atomic mass.
   *
   * @return g/mole
   */
  double getAtomicMass();

  /**
   * Get the atomic number.
   *
   * @return
   */
  int getAtomicNumber();

  /**
   * Get halflife. Units are seconds.
   *
   * @return the halflife in seconds, or Double.POSITIVE_INFINITY if stable.
   */
  double getHalfLife();

  /**
   * Get the isomer number.
   *
   * @return
   */
  int getIsomerNumber();

  /**
   * Get the mass number.
   *
   * @return
   */
  int getMassNumber();

  int getId();

  default double getDecayConstant()
  {
    return LN2 / this.getHalfLife();
  }

  /**
   * Specific activity in 1 over kg seconds.
   *
   * @return
   */
  default double getSpecificActivity()
  {
    return (LN2 * AVAGADROS_CONSTANT) / (getHalfLife() * getAtomicMass());
  }

  /**
   * Is this nuclide stable.
   *
   * @return true if is stable.
   */
  default boolean isStable()
  {
    return Double.isInfinite(getHalfLife());
  }

  default public int getZaid()
  {
    return 10000 * this.getAtomicNumber() + 10 * this.getMassNumber() + this.getIsomerNumber(); //Karl's method in int format
  }

  /**
   * Converts a microscopic cross section (per atom) to a macroscopic cross
   * section (per unit mass) for this nuclide, returning the result as a
   * {@link Quantity} in SI units (m²/kg).
   * <p>
   * <b>Physical context:</b> <br>
   * The microscopic cross section (σ, in area units such as m² or barns)
   * describes the probability of interaction per atom. The macroscopic cross
   * section (Σ, in m²/kg) describes the probability of interaction per unit
   * mass of material, and is computed as:
   * <pre>
   *     Σ = N<sub>A</sub> / M × σ
   * </pre> where:
   * <ul>
   * <li>N<sub>A</sub> = Avogadro's constant (atoms/mol)</li>
   * <li>M = molar mass of the nuclide (kg/mol)</li>
   * <li>σ = microscopic cross section (m²)</li>
   * </ul>
   * </p>
   *
   * @param microscopicCrossSection the microscopic cross section per atom, as a
   * {@link Quantity} with units of area (e.g., "m2", "barn", etc.)
   * @return the macroscopic cross section as a {@link Quantity} in SI units
   * (m²/kg)
   * @throws IllegalArgumentException if the input quantity does not have area
   * units
   *
   * <p>
   * <b>Usage Example:</b></p>
   * <pre>
   * Quantity microXS = Quantity.of(1.5, "barn");
   * Quantity macroXS = nuclide.convertToMacro(microXS); // returns in m²/kg
   * double macroXS_cmg = macroXS.as("cm2/g");           // convert to cm²/g if needed
   * </pre>
   *
   * <p>
   * <b>Notes:</b></p>
   * <ul>
   * <li>The caller is responsible for converting the result to other units if
   * desired.</li>
   * <li>This method enforces that the input is an area quantity.</li>
   * </ul>
   */
  default Quantity convertToMacro(Quantity microscopicCrossSection)
  {
    Units inputUnit = microscopicCrossSection.getUnits();
    inputUnit.require(PhysicalProperty.AREA);

    double molarMass_kg = this.getAtomicMass() / 1000.0;
    if (molarMass_kg <= 0.0)
      throw new IllegalArgumentException("Molar mass must be positive.");
   
    double atomsPerKg = Nuclide.AVAGADROS_CONSTANT / molarMass_kg;
    double sigma_si = microscopicCrossSection.get(); // Already in SI (m²)
    double value_si = atomsPerKg * sigma_si;
    return Quantity.of(value_si, PhysicalProperty.CROSS_SECTION); // SI units (m²/kg)
  }
}
