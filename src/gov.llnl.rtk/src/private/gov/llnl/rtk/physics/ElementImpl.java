// --- file: gov/llnl/rtk/physics/ElementImpl.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.io.Serializable;
import java.util.ArrayList;

public class ElementImpl implements Element, Serializable
{
  private final String symbol;
  int z;
  double molarMass;
  Quantity density;
  public ArrayList<MaterialComponentImpl> abundance = new ArrayList<>();

  ElementImpl(String symbol)
  {
    this.symbol = symbol;
  }

  @Override
  public String getSymbol()
  {
    return this.symbol;
  }

  @Override
  public int getAtomicNumber()
  {
    return this.z;
  }

  @Override
  public String toString()
  {
    return getSymbol();
  }

  @Override
  public double getMolarMass()
  {
    return molarMass;
  }

  @Override
  public Quantity getDensity()
  {
    return this.density;
  }

  @Override
  public Material toMaterial()
  {
    throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
  }



}
