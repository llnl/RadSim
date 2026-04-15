// --- file: gov/llnl/rtk/model/GammaSensorModelImpl.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
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
public class GammaSensorModelImpl implements GammaSensorModel
{
  private GammaPileupModel pileupModel;
  private EnergyScale energyScale;

  public GammaSensorModelImpl(
          EnergyScale energyScale,
          GammaPileupModel pileupModel)
  {
    this.energyScale = energyScale;
    this.pileupModel = pileupModel;
  }

  @Override
  public EnergyScale getEnergyScale()
  {
    return energyScale;
  }

  @Override
  public GammaPileupModel getPileupModel()
  {
    return pileupModel;
  }


}
