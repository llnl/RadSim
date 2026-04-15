/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.Xray;
import gov.llnl.rtk.physics.XrayEdge;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NIST version of xray edge data.
 *
 * Collection of xray emissions associated with an edge ("K","L","M",...).
 *
 * @author nelson85
 */
class XrayEdgeImpl implements XrayEdge
{
  // FIXME need to property export this data.
  final String name;
  final  double energy; 
  final double fluorescence_yield;
  final double ratio_jump;
  final Quantity energyQuantity;
  public List<XrayImpl> lines = new ArrayList<>();
  // CosterKronig
  Map<String, Double> CK = new HashMap<>();

  XrayEdgeImpl(String name, double energy, double fluorescence_yield, double ratio_jump)
  {
    this.name = name;
    this.energy = energy;
    this.energyQuantity = Quantity.of(energy, "eV");
    this.fluorescence_yield =fluorescence_yield;
    this.ratio_jump = ratio_jump;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public double getFluorescenceYield()
  {
    return this.fluorescence_yield;
  }

  @Override
  public List<Xray> getXrays()
  {
    return Collections.unmodifiableList(lines);
  }

  @Override
  public Map<String, Double> getCosterKronig()
  {
    return this.CK;
  }

  @Override
  public double getJumpRatio()
  {
    return ratio_jump;
  }

  @Override
  public Quantity getEnergy()
  {
    return energyQuantity;
  }
}
