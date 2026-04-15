// --- file: gov/llnl/rtk/physics/ElectronShell.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Represents a single electron shell, with electron count and binding energy.
 */
public class ElectronShell
{
  public int count; // Number of electrons in this shell
  public Quantity energy; // Binding energy of the shell (e.g., in eV)

  public ElectronShell(int count, Quantity energy)
  {
    this.count = count;
    this.energy = energy;
  }
  public static ElectronShellBuilder builder()
  {
    return new ElectronShellBuilder();
  }
 
}
