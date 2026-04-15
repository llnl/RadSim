// --- file: gov/llnl/rtk/flux/FluxTrapezoid.java ---
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
 * Representation of a flux using trapezoids and lines with steps.
 *
 * This format is renderable with the response function.
 *
 * @author nelson85
 */
public class FluxTrapezoid extends ExpandableObject implements Flux, Serializable
{

  final List<FluxLineStep> photonLines = new ArrayList<>();
  final List<FluxGroupTrapezoid> photonGroups = new ArrayList<>();
  final List<FluxGroupTrapezoid> neutronGroups = new ArrayList<>();

  @Override
  public List<FluxLineStep> getPhotonLines()
  {
    return Collections.unmodifiableList(photonLines);
  }

  @Override
  public List<FluxGroupTrapezoid> getPhotonGroups()
  {
    return Collections.unmodifiableList(photonGroups);
  }

  @Override
  public List<FluxGroupTrapezoid> getNeutronGroups()
  {
    return Collections.unmodifiableList(neutronGroups);
  }

  @Override
  public FluxEvaluator<FluxLineStep, FluxGroupTrapezoid> newPhotonEvaluator()
  {
    return new FluxEvaluatorSorted<>(this.getPhotonLines(), this.getPhotonGroups());
  }

  @Override
  public FluxEvaluator newNeutronEvaluator()
  {
    return new FluxEvaluatorSorted<>(Collections.EMPTY_LIST, this.getNeutronGroups());
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FluxTrapezoid))
      return false;
    FluxTrapezoid flux = (FluxTrapezoid) obj;
    return Objects.equals(flux.photonLines, this.photonLines)
            && Objects.equals(flux.photonGroups, this.photonGroups)
            && Objects.equals(this.neutronGroups, flux.neutronGroups);
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  public void addPhotonLine(FluxLine line)
  {
    if (line == null)
      return;
    FluxLineStep nline;
    if (line instanceof FluxLineStep)
      nline = ((FluxLineStep) line);
    else
      nline = new FluxLineStep(line.getEnergy(), line.getIntensity(), 0);
    FluxUtilities.insertLine(photonLines, nline);
  }

  public void addPhotonGroup(FluxGroupTrapezoid group)
  {
    FluxUtilities.insertGroup(photonGroups, group);
  }

  public void addNeutronGroup(FluxGroupTrapezoid group)
  {
    FluxUtilities.insertGroup(neutronGroups, group);
  }
//</editor-fold>
}
