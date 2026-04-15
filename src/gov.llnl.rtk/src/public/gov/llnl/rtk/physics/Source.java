// --- file: gov/llnl/rtk/physics/Source.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 *
 * FIXME what is the difference between a MaterialComponent and a Source?
 * 
 * @author nelson85
 */
public interface Source
{
  Nuclide getNuclide();

  double getActivity(Units activityUnits);

  Quantity getActivityQuantity();  
  
  default double getAtoms()
  {
    return getActivity(PhysicalProperty.ACTIVITY) / getNuclide().getDecayConstant();
  }

  /**
   * Create a source from a nuclide with an activity of 1 Bq.
   *
   * @param nuclide
   * @return 
   */
  public static Source of(Nuclide nuclide)
  {
    SourceImpl source = new SourceImpl(nuclide);
    source.activity = 1;
    source.atoms = source.activity / nuclide.getDecayConstant();
    return source;
  }

  public static Source fromActivity(Nuclide nuclide, Quantity activity)
  {
    activity.require(PhysicalProperty.ACTIVITY);
    if (nuclide.getDecayConstant() == 0)
      throw new IllegalArgumentException("Nuclide is stable");
    SourceImpl source = new SourceImpl(nuclide);
    source.activity = activity.as("Bq");
    source.atoms = source.activity / nuclide.getDecayConstant();
    return source;
  }

  public static Source fromAtoms(Nuclide nuclide, double atoms)
  {
    SourceImpl source = new SourceImpl(nuclide);
    source.activity = nuclide.getDecayConstant() * atoms;
    source.atoms = atoms;
    return source;
  }

}
