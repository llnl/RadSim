// --- file: gov/llnl/rtk/physics/EmissionsImpl.java ---
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.llnl.rtk.physics;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nelson85
 */
class EmissionsImpl implements Emissions
{
  final List<Emission> emissions = new LinkedList<>();
  List<EmissionCorrelation> coincidence = new LinkedList<>();

  @Override
  public List<Emission> getEmissions()
  {
    return emissions;
  }

  @Override
  public List<EmissionCorrelation> getCorrelations()
  {
    return this.coincidence;
  }

}
