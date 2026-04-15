// --- file: gov/llnl/rtk/model/GammaSensorModelBuilderImpl.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.model.GammaPileupModel;
import gov.llnl.rtk.model.GammaSensorModel;

/**
 *
 * @author nelson85
 */
public class GammaSensorModelBuilderImpl implements GammaSensorModel.Builder
{
  private EnergyScale scale;
  private GammaPileupModel pileupModel;

  @Override
  public GammaSensorModel.Builder energyScale(EnergyScale scale)
  {
    this.scale = scale;
    return this;
  }

  @Override
  public GammaSensorModel.Builder pileup(GammaPileupModel model)
  {
    this.pileupModel = model;
    return this;
  }

  @Override
  public GammaSensorModel create()
  {
    return new GammaSensorModelImpl(this.scale, this.pileupModel);
  }
  
}
