// --- file: gov/llnl/rtk/physics/MaterialBuilder.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Utility for constructing a Material properly.
 *
 * @author nelson85
 */
public class MaterialBuilder
{
  // FIXME immutable quantities are not yet supported.
  final static Quantity NO_AGE = Quantity.of(0, "s").immutable();
  MaterialImpl material = new MaterialImpl();
  final UnitSystem units;

  public MaterialBuilder(UnitSystem units)
  {
    material.density = null;
    material.age = NO_AGE;
    this.units = units;
  }

  public MaterialBuilder label(String value)
  {
    material.label = value;
    return this;
  }

  public MaterialBuilder description(String value)
  {
    material.description = value;
    return this;
  }

  public MaterialBuilder comment(String value)
  {
    material.comment = value;
    return this;
  }

  /**
   * Add a nuclide to the material.
   *
   * FIXME is this atom fraction or mass fraction?
   *
   * @param nuclide
   * @param fraction
   * @return
   */
  public MaterialBuilder add(Nuclide nuclide, double fraction)
  {
    if (nuclide == null)
      throw new NullPointerException("Null nuclide");
    material.entries.add(new MaterialComponentImpl(nuclide, fraction, 0, null));
    return this;
  }

  /**
   * Add an element to the material.
   *
   * FIXME is this atom fraction or mass fraction?
   *
   * @param element
   * @param fraction
   * @return
   */
  public MaterialBuilder add(Element element, double fraction)
  {
    Nuclide nuclide = Nuclides.natural(element);
    return this.add(nuclide, fraction);
  }

  public void add(MaterialComponent v)
  {
    material.entries.add(v);
  }

  /**
   * Add a nuclide to the material.
   *
   * FIXME is this atom fraction or mass fraction?
   *
   * @param nuclide
   * @param fraction
   * @return
   */
  public MaterialBuilder atoms(Nuclide nuclide, double fraction)
  {
    if (nuclide == null)
      throw new NullPointerException("Null nuclide");
    material.entries.add(new MaterialComponentImpl(nuclide, 0, fraction, null));
    return this;
  }

  /**
   * Add an element to the material.
   *
   * FIXME is this atom fraction or mass fraction?
   *
   * @param element
   * @param fraction
   * @return
   */
  public MaterialBuilder atoms(Element element, double fraction)
  {
    Nuclide nuclide = Nuclides.natural(element);
    return atoms(nuclide, fraction);
  }

  public MaterialBuilder trace(Nuclide nuclide, Quantity activity)
  {
    if (nuclide == null)
      throw new NullPointerException("Null nuclide");
    activity.require(PhysicalProperty.ACTIVITY);
    material.entries.add(new MaterialComponentImpl(nuclide, 0, 0, activity));
    return this;
  }

  public MaterialBuilder density(double density)
  {
    this.material.density = Quantity.of(density, units.getDensityUnit());
    return this;
  }

  public MaterialBuilder density(Quantity value)
  {
    value.require(PhysicalProperty.DENSITY);
    this.material.density = value;
    return this;
  }

  public MaterialBuilder age(double value)
  {
    this.material.age = Quantity.of(value, units.getTimeUnit());
    return this;
  }

  public MaterialBuilder age(Quantity value)
  {
    value.require(PhysicalProperty.TIME);
    this.material.age = value;
    return this;
  }

  public Material build()
  {
    MaterialImpl out = this.material;
    this.material = new MaterialImpl();
    // VERIFY the material is complete
    if (out.density == null)
      throw new IllegalStateException("Density is not set");
    out.normalize();
    return out;
  }

}
