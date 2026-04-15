// --- file: gov/llnl/rtk/physics/EmissionCalculator.java ---
/*
 * Copyright 2023, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class EmissionCalculator
{
  private static final long serialVersionUID = 0xA341_0000_0004_0000L;
  
  DecayLibrary decayLibrary;

  /**
   * Set the physics library to use for this process.
   *
   * @param library
   */
  public void setDecayLibrary(DecayLibrary library)
  {
    this.decayLibrary = library;
  }

  public DecayLibrary getDecayLibrary()
  {
    return this.decayLibrary;
  }

  /**
   *
   * @param sl
   * @return
   */
  public Emissions apply(List<Source> sl)
  {
    EmissionsImpl out = new EmissionsImpl();
    for (Source entry : sl)
    {
      List<DecayTransition> transitions = decayLibrary.getTransitionsFrom(entry.getNuclide());
      for (DecayTransition t : transitions)
      {
        add(out, t, t.getBranchingRatio() * entry.getActivity(PhysicalProperty.ACTIVITY)); //Bq -> particles
      }
    }

    Collections.sort(out.emissions, EmissionCalculator::compareEmission);

    // FIXME do we need to convert electron capture to xrays?
    return out;

  }

  /**
   * Used to ensure the transitions are in energy order.
   *
   * Not all emissions have energy so we sort secondarily by intensity.
   *
   * @param e1
   * @param e2
   * @return
   */
  static int compareEmission(Emission e1, Emission e2)
  {
    boolean b1 = e1 instanceof EnergyEmission;
    boolean b2 = e2 instanceof EnergyEmission;
    if (b1 && b2)
    {
      return Double.compare(
              ((EnergyEmission) e1).getEnergy().get(),
              ((EnergyEmission) e2).getEnergy().get());
    }
    if (!b1 && !b2)
    {
      return Double.compare(
              ((EnergyEmission) e1).getIntensity().get(),
              ((EnergyEmission) e2).getIntensity().get());
    }
    if (!b1)
      return -1;
    return 1;
  }

  static void add(EmissionsImpl out, Emissions el, double f)
  {
    // FIXME as the coincidence is tied to the original we will need to clone
    // all of the items with the scaling factor, then connect new coincidence records
    HashMap<Emission, Emission> map = new HashMap<>();
    for (Emission e : el.getEmissions())
    {
      Emission e2 = EmissionScaled.scaled(e, f);
      out.emissions.add(e2);
      map.put(e, e2);
    }

    if (el.getCorrelations() != null)
    {
      for (EmissionCorrelation c : el.getCorrelations())
      {
        out.coincidence.add(new EmissionCorrelationImpl(
                map.get(c.getPrimary()),
                map.get(c.getSecondary()),
                c.getProbability()));
      }
    }

    // now link coincident data
  }

}
