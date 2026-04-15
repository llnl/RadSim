// --- file: gov/llnl/rtk/response/SpectralResponseFunctionBase.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;

/**
 *
 * @author nelson85
 */
public abstract class SpectralResponseFunctionBase implements SpectralResponseFunction
{
  protected String vendor;
  protected String model;
  protected EnergyScale energyScale;

  @Override
  public String getVendor()
  {
    return vendor;
  }

  @Override
  public String getModel()
  {
    return model;
  }

  @Override
  public EnergyScale getEnergyScale()
  {
    return this.energyScale;
  }

}
