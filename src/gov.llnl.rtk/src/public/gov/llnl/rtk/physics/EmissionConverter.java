// --- file: gov/llnl/rtk/physics/EmissionConverter.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.flux.FluxGroupTrapezoid;
import gov.llnl.rtk.flux.FluxTrapezoid;
import java.util.List;

/**
 *
 * @author cheung27
 */
public class EmissionConverter
{
  /**
   *
   * @param energyEmissionList
   * @param energyScale
   * @return new FluxTrapezoid with emissions in photon list
   */
  public static FluxTrapezoid getEmissionAsFluxTrapezoid(List<? extends EnergyEmission> energyEmissionList, EnergyScale energyScale)
  {

    double[] edges = energyScale.getEdges();
    double[] densities = new double[edges.length];

    for (EnergyEmission energyEmission : energyEmissionList)
    {
      double energy = energyEmission.getEnergy().getValue();
      double intensity = energyEmission.getIntensity().getValue();

      if (energy > edges[edges.length - 1])
      {
        continue;
      }

      int lowerIndex = energyScale.findEdgeFloor(energy);
      int upperIndex = lowerIndex + 1;

      double deltaE = edges[upperIndex] - edges[lowerIndex];

      double lowerFraction = (edges[upperIndex] - energy) / deltaE;
      double upperFraction = (energy - edges[lowerIndex]) / deltaE;

      densities[lowerIndex] += intensity * lowerFraction;
      densities[upperIndex] += intensity * upperFraction;
    }

    FluxTrapezoid flux = new FluxTrapezoid();
    for (int i = 1; i < edges.length; i++)
    {
      flux.addPhotonGroup(new FluxGroupTrapezoid(
              edges[i - 1], edges[i], densities[i - 1], densities[i]
      ));
    }

    return flux;
  }
}
