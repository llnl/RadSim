// --- file: gov/llnl/rtk/model/GammaSensorModel.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.data.EnergyScale;

/**
 * The forward model used by the sample generator.
 *
 * @author nelson85
 */
public interface GammaSensorModel
{

  /**
   * Get the energy scale to apply to samples.
   *
   * @return the energy scale to be used for samples.
   */
  EnergyScale getEnergyScale();

  /**
   * Get the pileup model to be applied to samples.
   *
   * @return the pileup model or null if not defined.
   */
  GammaPileupModel getPileupModel();

//<editor-fold desc="factory" defaultstate="collapsed">
  
  static Builder newBuilder()
  {
    return new GammaSensorModelBuilderImpl();
  }        
  
  interface Builder
  {
    public Builder energyScale(EnergyScale scale);
    public Builder pileup(GammaPileupModel model); 
    public GammaSensorModel create();
  }
  
  /**
   * Simple factory method to produce a gamma detector model with a defined
   * energy scale.
   *
   * @param es
   * @return
   */
  static GammaSensorModel withEnergyScale(EnergyScale es)
  {
    return new GammaSensorModelImpl(es, null);
  }
//</editor-fold>
}
