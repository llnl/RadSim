/*
 * Copyright 2025, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xcom;

import gov.llnl.rtk.physics.Material;
import java.util.Arrays;

/**
 * Storage for data and cache entries.
 *
 * @author nelson85
 */
public class CrossSectionsTable
{
  public String name;
  public String symbol;
  public double atomicNumber;
  public double molarMass;

  // Stored as log/log
  // energy in keV
  // cross sections in cm2/g
  public double[] energies;
  public double[] incoherent;
  public double[] pairElectron;
  public double[] pairNuclear;
  public double[] photoelectric;
  public double[] total;

  public Material material;

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 89 * hash + Arrays.hashCode(this.energies);
    hash = 89 * hash + Arrays.hashCode(this.photoelectric);
    hash = 89 * hash + Arrays.hashCode(this.incoherent);
    hash = 89 * hash + Arrays.hashCode(this.pairNuclear);
    hash = 89 * hash + Arrays.hashCode(this.pairElectron);
    hash = 89 * hash + Arrays.hashCode(this.total);
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final CrossSectionsTable other = (CrossSectionsTable) obj;
    if (!Arrays.equals(this.energies, other.energies))
      return false;
    if (!Arrays.equals(this.photoelectric, other.photoelectric))
      return false;
    if (!Arrays.equals(this.incoherent, other.incoherent))
      return false;
    if (!Arrays.equals(this.pairNuclear, other.pairNuclear))
      return false;
    if (!Arrays.equals(this.pairElectron, other.pairElectron))
      return false;
    return Arrays.equals(this.total, other.total);
  }

}
