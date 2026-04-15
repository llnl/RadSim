// --- file: gov/llnl/rtk/physics/Material.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.Iterator;

/**
 * Represents the composition of a material, including its components, density,
 * and age.
 *
 * Materials can consist of multiple components, each with unique properties
 * such as atomic number and mass fraction.
 *
 * @author nelson85
 */
public interface Material extends Iterable<MaterialComponent>
{

  /**
   * Get the label for this material.
   *
   * The default name is constructed from the nuclides of its components.
   *
   * @return the label as a String.
   */
  default public String getName()
  {
    StringBuilder b = new StringBuilder();
    for (MaterialComponent c : this)
    {
      b.append(c.getNuclide().toString());
    }
    return b.toString();
  }

  public static MaterialBuilder builder()
  {
    return new MaterialBuilder(UnitSystem.SI);
  }

  public static MaterialBuilder builder(UnitSystem units)
  {
    return new MaterialBuilder(units);
  }

  /**
   * Get the description of this material. Used by some material libraries.
   *
   * @return the description, or null if not available.
   */
  default public String getDescription()
  {
    return null;
  }

  /**
   * Get the comment for this material. Used by some material libraries.
   *
   * @return the comment, or null if not available.
   */
  default public String getComment()
  {
    return null;
  }

  /**
   * Get the age of this material.
   *
   * @return the age as a Quantity object.
   */
  Quantity getAge();

  /**
   * Get the density of this material.
   *
   * @return the density as a Quantity object.
   */
  Quantity getDensity();

  /**
   * Get an iterator to traverse the components of this material.
   *
   * @return an Iterator for MaterialComponent objects.
   */
  @Override
  Iterator<MaterialComponent> iterator();

  /**
   * Get the number of components in this material.
   *
   * @return the number of components as an integer.
   */
  int size();

  /**
   * Compute the average atomic number (Z) for this material.
   *
   * @return the average atomic number as a double.
   */
  default double getAverageZ()
  {
    double weight = 0;
    double Z = 0;
    for (MaterialComponent entry : this)
    {
      weight += entry.getMassFraction();
      Z += entry.getNuclide().getAtomicNumber() * entry.getMassFraction();
    }
    if (weight == 0)
      return 0;
    return Z / weight;
  }

  /**
   * Compute the effective atomic number (Z) for this material. Based on the
   * x-ray effective Z formula.
   *
   * @return the effective atomic number as a double.
   */
  default double getEffectiveZ()
  {
    double weight = 0;
    double Z = 0;
    for (MaterialComponent entry : this)
    {
      weight += entry.getMassFraction();
      Z += Math.pow(entry.getNuclide().getAtomicNumber(), 2.94) * entry.getMassFraction();
    }
    if (weight == 0)
      return 0;
    return Math.pow(Z / weight, 1 / 2.94);
  }

  /**
   * Find the component associated with a given nuclide.
   *
   * @param nuclide the Nuclide to search for.
   * @return the MaterialComponent associated with the nuclide, or null if not
   * found.
   */
  default MaterialComponent findNuclide(Nuclide nuclide)
  {
    for (MaterialComponent iter : this)
    {
      if (iter.getNuclide() == nuclide)
        return iter;
    }
    return null;
  }

}
