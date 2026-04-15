// --- file: gov/llnl/rtk/flux/Flux.java ---
/* 
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.utility.Expandable;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.util.List;

/**
 * Flux represents the emissions from a source over some area.
 *
 * Typically we use total flux into 4 pi. But flux may also be defined emissions
 * per area.
 *
 * @author nelson85
 */
@ReaderInfo(FluxReader.class)
@WriterInfo(FluxWriter.class)
public interface Flux extends Expandable
{

  // FIXME figure out how to represent time and flux geometry.
  /**
   * Gamma lines defined for this flux.
   *
   * The lines should not be modified.
   *
   * @return lines for this flux or an empty list if not defined.
   */
  public List<? extends FluxLine> getPhotonLines();

  /**
   * Gamma groups for this flux.
   *
   * @return a list holding groups.
   */
  public List<? extends FluxGroup> getPhotonGroups();

  /**
   * Neutron groups for this flux.
   *
   * @return a list holding groups.
   */
  public List<? extends FluxGroup> getNeutronGroups();

  /**
   * Accelerated calculator for gamma integrals.
   *
   * @return
   */
  public FluxEvaluator newPhotonEvaluator();

  /**
   * Accelerated calculator for neutron integrals.
   *
   * @return
   */
  public FluxEvaluator newNeutronEvaluator();
  
  default public FluxGroup findPhotonGroup(double energy)
  {
    return FluxUtilities.findGroup(this.getPhotonGroups(), energy);
  }

  default public EnergyScale getPhotonGroupsScale()
  {
    return toScale(this.getPhotonGroups());
  }

  default public EnergyScale getNeutronGroupsScale()
  {
    return toScale(this.getNeutronGroups());
  }

  private static EnergyScale toScale(List<? extends FluxGroup> grp)
  {
    double[] scale = new double[grp.size() + 1];
    int i = 0;
    for (FluxGroup group : grp)
    {
      scale[i] = group.getEnergyLower();
      scale[++i] = group.getEnergyUpper();
    }
    return EnergyScaleFactory.newScale(scale);

  }
}
