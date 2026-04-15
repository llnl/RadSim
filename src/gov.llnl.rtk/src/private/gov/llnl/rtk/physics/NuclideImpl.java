// --- file: gov/llnl/rtk/physics/NuclideImpl.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.annotation.Internal;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author nelson85
 */
@Internal
public class NuclideImpl implements Nuclide, Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("NuclideImpl");
  final String name;
  final Element element;
  int atomicNumber;
  int massNumber;
  double halfLife;
  double atomicMass;
  int isomerNumber;
  int id;

  NuclideImpl(String name, Element element)
  {
    this.name = name;
    this.element = element;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Element getElement()
  {
    return this.element;
  }

  @Override
  public String toString()
  {
    return getName();
  }

  @Override
  public int getAtomicNumber()
  {
    return atomicNumber;
  }

  @Override
  public int getMassNumber()
  {
    return massNumber;
  }

  @Override
  public int getIsomerNumber()
  {
    return this.isomerNumber;
  }

  @Override
  public double getAtomicMass()
  {
    return this.atomicMass;
  }

  @Override
  public double getHalfLife()
  {
    return this.halfLife;
  }

  @Override
  public int compareTo(Nuclide t)
  {
    return this.getName().compareTo(t.getName());
  }

  //<editor-fold desc="loader">
//  void setHalfLife(double v)
//  {
//    this.halfLife = v;
//  }
//
//  void setAtomicMass(double v)
//  {
//    this.atomicMass = v;
//  }
//
//  void setIsomerNumber(int v)
//  {
//    this.isomerNumber = v;
//  }
//
//  void setMassNumber(int v)
//  {
//    this.massNumber = v;
//  }
//
//  void setAtomicNumber(int v)
//  {
//    this.atomicNumber = v;
//  }
//
//  void setId(int id)
//  {
//    this.id = id;
//  }
  //</editor-fold>
  @Override
  public boolean equals(Object o)
  {
    if (!Nuclide.class.isInstance(o))
      return false;
    return this.hashCode() == o.hashCode();
  }

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(this.name);
    return hash;
  }

  @Override
  public int getId()
  {
    return id;
  }

}
