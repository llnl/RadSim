/*
 * Copyright 2024, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.nist.physics.xray;

import gov.llnl.rtk.physics.Transition;
import gov.llnl.rtk.physics.Xray;
import gov.llnl.rtk.physics.Quantity;

/**
 * NIST version of xray data.
 *
 * @author nelson85
 */
public class XrayImpl implements Xray
{

  String symbolIUPAC;
  public String symbolSiegbahn;
  Quantity energy;
  Quantity intensity;

  @Override
  public String getName()
  {
    return this.symbolIUPAC;
  }

  @Override
  public Quantity getEnergy()
  {
    return this.energy;
  }

  @Override
  public Quantity getIntensity()
  {
    return this.intensity;
  }

  @Override
  public Transition getOrigin()
  {
    return null;
  }
}
