// --- file: gov/llnl/rtk/flux/FluxBinned.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.utility.ExpandableObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * Flux representation in which the distribution in groups is not defined.
 *
 * @author nelson85
 */
public class FluxBinned extends ExpandableObject implements Flux, Serializable
{

  final List<FluxLineStep> photonLines = new ArrayList<>();
  final List<FluxGroupBin> photonGroups = new ArrayList<>();
  final List<FluxGroupBin> neutronGroups = new ArrayList<>();

  @Override
  public List<FluxLineStep> getPhotonLines()
  {
    return Collections.unmodifiableList(photonLines);
  }

  @Override
  public List<FluxGroupBin> getPhotonGroups()
  {
    return Collections.unmodifiableList(photonGroups);
  }

  @Override
  public List<FluxGroupBin> getNeutronGroups()
  {
    return Collections.unmodifiableList(neutronGroups);
  }

  @Override
  public FluxEvaluator<FluxLineStep, FluxGroupBin> newPhotonEvaluator()
  {
    return new FluxEvaluatorSorted<>(this.getPhotonLines(), this.getPhotonGroups());
  }

  @Override
  public FluxEvaluator<FluxLine, FluxGroup> newNeutronEvaluator()
  {
    return new FluxEvaluatorSorted(Collections.EMPTY_LIST, this.getNeutronGroups());
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FluxBinned))
      return false;
    FluxBinned flux = (FluxBinned) obj;
    return Objects.equals(this.photonLines, flux.photonLines)
            && Objects.equals(this.photonGroups, flux.photonGroups)
            && Objects.equals(this.neutronGroups, flux.neutronGroups);
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  public void addPhotonLine(FluxLine line)
  {
    if (line == null)
      return;
    FluxLineStep nline;
    if (line instanceof FluxLineStep)
      nline = (FluxLineStep) line;
    else
      nline = new FluxLineStep(line.getEnergy(), line.getIntensity(), 0);
    FluxUtilities.insertLine(this.photonLines, nline);
  }

  public void addPhotonGroup(FluxGroup group)
  {
    if (group == null)
      return;
    FluxGroupBin ngroup;
    if (group instanceof FluxGroupBin)
      ngroup = (FluxGroupBin) group;
    else
      ngroup = new FluxGroupBin(group.getEnergyLower(), group.getEnergyUpper(), group.getCounts());
    FluxUtilities.insertGroup(this.photonGroups, ngroup);
  }

  public void addNeutronGroup(FluxGroup group)
  {
    if (group == null)
      return;
    FluxGroupBin ngroup;
    if (group instanceof FluxGroupBin)
      ngroup = (FluxGroupBin) group;
    else
      ngroup = new FluxGroupBin(group.getEnergyLower(), group.getEnergyUpper(), group.getCounts());
    FluxUtilities.insertGroup(this.neutronGroups, ngroup);
  }
//</editor-fold>
}
