// --- file: gov/llnl/rtk/physics/MaterialComponentImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.UUIDUtilities;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
class MaterialComponentImpl implements MaterialComponent, Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("ComponentImpl");
  Nuclide nuclide;
  double doseFraction;
  double atomFraction;
  double massFraction;
  Quantity activity = null;

  MaterialComponentImpl()
  {
  }

  MaterialComponentImpl(Nuclide nuclide, double massFraction, double atomFraction, Quantity activity)
  {
    this.nuclide = nuclide;
    this.massFraction = massFraction;
    this.atomFraction = atomFraction;
    this.activity = activity;
  }

  @Override
  public Nuclide getNuclide()
  {
    return nuclide;
  }

  @Override
  public double getDoseFraction()
  {
    return doseFraction;
  }

  @Override
  public Quantity getActivity()
  {
    return activity;
  }

  public void setNuclide(Nuclide nuclide)
  {
    this.nuclide = nuclide;
  }

  /**
   * @return the massFraction
   */
  @Override
  public double getMassFraction()
  {
    return massFraction;
  }

  @Override
  public double getAtomFraction()
  {
    return atomFraction;
  }

  public void setDoseFraction(double doseFraction)
  {
    this.doseFraction = doseFraction;
  }

  public void setActivity(Quantity activity)
  {
    this.activity = activity;
  }

  public void setMassFraction(double fraction)
  {
    this.massFraction = fraction;
  }

  public void setAtomFraction(double fraction)
  {
    this.atomFraction = fraction;
  }
  
  @Override
  public String toString()
  {
    return String.format("%s %.4f", this.getNuclide().toString(), this.massFraction);
  }
}
