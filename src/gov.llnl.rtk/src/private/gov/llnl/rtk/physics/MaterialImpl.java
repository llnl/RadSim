// --- file: gov/llnl/rtk/physics/MaterialImpl.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.annotation.Internal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author nelson85
 */
public class MaterialImpl implements Material
{
  // Unspecified default quantities
  static final Quantity DEFAULT_DENSITY = Quantity.of(0.001, PhysicalProperty.DENSITY, 0, false).immutable();
  static final Quantity DEFAULT_AGE = Quantity.of(0, PhysicalProperty.TIME, 0, false).immutable();
  
  public String label;
  public String description;
  public String comment;

  public ArrayList<MaterialComponent> entries = new ArrayList<>();
  public Quantity density = DEFAULT_DENSITY;
  public Quantity age = DEFAULT_AGE;

  @Override
  public Iterator<MaterialComponent> iterator()
  {
    return this.entries.iterator();
  }

  public MaterialImpl()
  {
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Material(");
    if (label != null)
      sb.append("title=").append(label);
    if (description != null)
      sb.append(" description=").append(description);

    for (MaterialComponent c : this)
    {
      sb.append("(").append(c.getNuclide()).append(",").append(c.getMassFraction()).append(")");
    }
    sb.append(" density=").append(density);
    if (age.getValue() != 0)
      sb.append(" age=").append(age);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public int size()
  {
    return entries.size();
  }

  /**
   * Get the age of the material.
   *
   * @return age in seconds.
   */
  @Override
  public Quantity getAge()
  {
    return age;
  }

  /**
   * Set the age of the material.
   *
   * Age is used for decaying radioactive material.
   *
   * @param age in seconds.
   */
  public void setAge(Quantity age)
  {
    this.age = age;
  }

  /**
   * Get the density of the material.
   *
   * @return density in k/m^3.
   */
  @Override
  public Quantity getDensity()
  {
    return density;
  }

  /**
   * Set the density of the material.
   *
   * @param density to be applied as a quantity.
   */
  public void setDensity(Quantity density)
  {
    this.density = density;
  }

  public void addEntry(MaterialComponent entry)
  {
    entries.add(entry);
  }

//<editor-fold desc="normalize" defaultstate="collapsed">
  /**
   * Make sure that this is a proper material.
   *
   * This may change the components during the process.
   *
   */
  @Internal
  public void normalize()
  {
    double mass = 0;
    double atoms = 0;
    boolean specifiedMass = true;
    boolean specifiedAtom = true;

    boolean consistent = true;
    double ratio = 0;

    HashSet<Nuclide> set = new HashSet<>();
    boolean duplicates = false;

    // Check to see the current state of the material.
    // May be specified by mass or by atoms
    for (MaterialComponent iter : this)
    {
      Nuclide nuclide = iter.getNuclide();
      if (nuclide == null)
        throw new NullPointerException("Nuclide is null");
      if (set.contains(nuclide))
        duplicates = true;
      set.add(nuclide);

      Quantity act = iter.getActivity();
      double m1 = iter.getMassFraction();
      double m2 = iter.getAtomFraction() * nuclide.getAtomicMass();

      if (m1 == 0 && m2 == 0 && act != null)
      {
        // Assumed to be trace source
        continue;
      }
      if (m1 == 0)
      {
        specifiedMass = false;
        consistent = false;
      }
      else if (m2 == 0)
      {
        specifiedAtom = false;
        consistent = false;
      }

      // If both are specified check the ratio
      else
      {
        double r = m1 / m2;
        if (ratio != 0)
        {
          if (Math.abs(r - ratio) > 1e-6 * ratio)
            consistent = false;
        }
        else
          ratio = r;
      }

      mass += iter.getMassFraction();
      atoms += iter.getAtomFraction();
    }

    // Combine duplicates
    if (duplicates)
      removeDuplicates();

    // Fully specified.  Great, nothing to be done.
    boolean normalizedMass = Math.abs(mass - 1.0) < 1e-7;
    boolean normalizedAtoms = Math.abs(atoms - 1.0) < 1e-7;
    if (consistent && normalizedMass && normalizedAtoms)
      return;

    if (specifiedMass && !specifiedAtom)
    {
      normalizeMass(mass);
      return;
    }

    // Specified by atom
    if (specifiedAtom && !specifiedMass)
    {
      normalizeAtom(atoms);
      return;
    }

    // Mixed?
    ArrayList<MaterialComponent> previous = this.entries;
    this.entries = new ArrayList<>();
    for (MaterialComponent c : previous)
    {
      if (c.getMassFraction() > 0 && c.getActivity() != null)
        this.entries.add(c);
      this.entries.add(new MaterialComponentImpl(c.getNuclide(), c.getMassFraction() / mass, c.getAtomFraction() / atoms, c.getActivity()));
    }

  }

  private void normalizeMass(double mass)
  {
    ArrayList<MaterialComponent> previous = this.entries;
    this.entries = new ArrayList<>();
    double m4 = 0;
    for (MaterialComponent c : previous)
    {
      if (c.getMassFraction() > 0 && c.getActivity() != null)
        continue;
      double fr = c.getMassFraction();
      double am = c.getNuclide().getAtomicMass();
      m4 += fr / am;
    }
    for (MaterialComponent c : previous)
    {
      if (c.getMassFraction() > 0 && c.getActivity() != null)
        this.entries.add(c);
      double fr = c.getMassFraction();
      double am = c.getNuclide().getAtomicMass();
      this.entries.add(new MaterialComponentImpl(c.getNuclide(), fr / mass, fr / am / m4, c.getActivity()));
    }
  }

  private void normalizeAtom(double atoms)
  {
    ArrayList<MaterialComponent> previous = this.entries;
    this.entries = new ArrayList<>();
    double m4 = 0;
    for (MaterialComponent c : previous)
    {
      if (c.getMassFraction() > 0 && c.getActivity() != null)
        continue;
      double fr = c.getAtomFraction();
      double am = c.getNuclide().getAtomicMass();
      m4 += fr * am;
    }
    for (MaterialComponent c : previous)
    {
      if (c.getMassFraction() > 0 && c.getActivity() != null)
        this.entries.add(c);
      double fr = c.getAtomFraction();
      double am = c.getNuclide().getAtomicMass();
      this.entries.add(new MaterialComponentImpl(c.getNuclide(), fr * am / m4, fr / atoms, c.getActivity()));
    }
  }

  private void removeDuplicates()
  {
    ArrayList<MaterialComponent> previous = this.entries;
    HashMap<Nuclide, MaterialComponentImpl> map = new HashMap<>();
    this.entries = new ArrayList<>();
    for (MaterialComponent c : previous)
    {
      Nuclide n = c.getNuclide();
      double af = c.getAtomFraction();
      double mf = c.getMassFraction();
      Quantity act = c.getActivity();
   
      // We won't try to combine trace sources as they are special
      if (act == null && map.containsKey(n))
      {
        MaterialComponentImpl entry = map.get(n);
        entry.atomFraction += af;
        entry.massFraction += mf;
        continue;
      }

      // Make sure everything is a MaterialComponentImpl so that we can add up the fractions
      MaterialComponentImpl entry = new MaterialComponentImpl(n, mf, af, act);
      map.put(n, entry);
      this.entries.add(entry);
    }
  }
//</editor-fold>
}
