/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nelson85
 */
public class ElectronShellBuilder {
  List<ElectronShell> shells = new ArrayList<>();
  Units units = Units.get("eV");

  public ElectronShellBuilder units(Units units)
  {
    this.units = units;
    return this;
  }

  public ElectronShellBuilder add(int count, double energy)
  {
    shells.add(new ElectronShell(count, Quantity.of(energy, units)));
    return this;
  }

  public List<ElectronShell> build()
  {
    return shells;
  }
    
}
