// --- file: gov/llnl/rtk/response/SpectralResponseRenderer.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.flux.Flux;
import gov.llnl.rtk.flux.FluxBinned;
import gov.llnl.rtk.flux.FluxGroupBin;
import gov.llnl.rtk.flux.FluxGroupTrapezoid;
import gov.llnl.rtk.flux.FluxLineStep;
import gov.llnl.rtk.flux.FluxSpectrum;
import gov.llnl.rtk.flux.FluxTrapezoid;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author nelson85
 */
public class SpectralResponseRenderer
{
  static public Spectrum render(SpectralResponseEvaluator eval, Flux flux)
  {
    // Sanity check for evaluator
    EnergyScale energyScale = eval.getEnergyScale();
    if (energyScale == null)
      throw new NullPointerException("energyScale not set");

    if (flux instanceof FluxSpectrum)
      return renderSpectrum(eval, (FluxSpectrum) flux);

    if (flux instanceof FluxBinned)
      return renderBinned(eval, (FluxBinned) flux);

    if (flux instanceof FluxTrapezoid)
      return renderTrapezoid(eval, (FluxTrapezoid) flux);

    throw new UnsupportedOperationException();
  }

  /**
   * Simple renderer for spectrum.
   *
   * @param eval
   * @param flux
   * @return
   */
  static public Spectrum renderSpectrum(SpectralResponseEvaluator eval, FluxSpectrum flux)
  {
    // Set up target 
    SpectralBuffer buffer = new SpectralBuffer();

    EnergyScale energyScale = flux.getPhotonScale();
    double[] gammaCounts = flux.getPhotonCounts();
    double[] gammaCenters = energyScale.getCenters();

    // Sanity check for rendering
    if (gammaCounts.length != gammaCenters.length)
      throw new RuntimeException("Size mismatch");

    // For efficiency it is better to render from low to high so that 
    // we can truncate the tails of the peaks.
    for (int i = gammaCounts.length - 1; i >= 0; --i)
    {
      double counts = gammaCounts[i];
      if (counts <= 0)
        continue;
      double center = gammaCenters[i];
      eval.renderLine(buffer, center, counts);
    }
    return buffer.toSpectrum();
  }

  /**
   * Simple rendering of binned data (without consideration for steps).
   *
   * @param eval
   * @param flux
   * @return
   */
  static public Spectrum renderBinned(SpectralResponseEvaluator eval, FluxBinned flux)
  {
    // Set up target 
    SpectralBuffer buffer = new SpectralBuffer();

    // Set up the groups
    List<FluxGroupBin> gammaGroups = new ArrayList<>(flux.getPhotonGroups());
    double[] width = gammaGroups.stream().mapToDouble(p -> p.getEnergyWidth()).toArray();
    double[] counts = gammaGroups.stream().mapToDouble(p -> p.getCounts()).toArray();

    // Render lines first
    
    //  It is best to render from strongest to weakest
    var lines = new ArrayList<>(flux.getPhotonLines());
    lines.sort((p1, p2) -> -Double.compare(p1.getIntensity(), p2.getIntensity()));

    renderLines(buffer, eval, lines);

    // Render groups
    ListIterator<FluxGroupBin> groupIter = gammaGroups.listIterator();

    // If we have no groups, no need for further rendering
    if (!groupIter.hasNext())
    {
      return buffer.toSpectrum();
    }

    // Deal with first bin
    FluxGroupBin last = groupIter.next();

    // FIXME  this code should be preforming on the fly to trapizod conversions
    // if the steps are available.
    // FIXME we should be able to test if a line is significant and add the line
    // contributions to the groups.   But that is not structurally possible 
    // because lines are not group aware.   
    //
    // To pull that off we need to 
    //  - generate a temporary structure so we can add counts to groups for 
    //    removed lines.
    //  - associate a line with the corresponding temporary group. 
    //  - render lines from strong to weak.
    //  - test the line against the current buffer.  If insignificant, then
    //    add it counts to the group it belongs to.
    //  (If we test against the continuum then the test may fail.)
    //  - This also should go for each of its pieces separately (but given
    //    the secondaries are down I don't see a case in which the secondary
    //    would render by the primary would not.)
    //  - render the temporary groups
    
    int index = 0;
    // Get the next group if it exists
    double density0 = counts[index] / width[index++];
    if (!groupIter.hasNext())
    {
      // Render a square group.
      eval.renderGroup(buffer,
              last.getEnergyLower(), density0,
              last.getEnergyUpper(), density0);

      // No need for anything special as we are out of groups to render.
      return buffer.toSpectrum();
    }

    // Peek at the next group
    FluxGroupBin current = groupIter.next();

    // Deal with figuring out the desired density at the start of the first group.
    double density1 = density0;
    double center0 = last.getEnergyCenter();
    double center1 = current.getEnergyCenter();
    double fLower = (last.getEnergyLower() - center0) / (center1 - center0);
    double densityLower = (1 - fLower) * density0 + fLower * density1;
    if (densityLower < 0)
      densityLower = 0;
    eval.renderGroup(buffer,
            last.getEnergyLower(), densityLower,
            center0, density0);

    // Reset the current to the first group
    groupIter.previous();
    current = last;

    // Render all groups.
    while (groupIter.hasNext())
    {
      density0 = density1;
      density1 = counts[index] / width[index++];
      last = current;
      current = groupIter.next();
      eval.renderGroup(buffer,
              last.getEnergyCenter(), density0,
              current.getEnergyCenter(), density1);
    }

    // Deal with the top for the last group
    //   Current is the last group
    center0 = last.getEnergyCenter();
    center1 = current.getEnergyCenter();
    double fUpper = (last.getEnergyUpper() - center0) / (center1 - center0);
    double densityUpper = (1 - fUpper) * density0 + fUpper * density1;
    if (densityUpper < 0)
      densityUpper = 0;
    eval.renderGroup(buffer,
            last.getEnergyLower(), density1,
            center0, densityUpper);
    return buffer.toSpectrum();
  }

  static public Spectrum renderTrapezoid(SpectralResponseEvaluator eval, FluxTrapezoid flux)
  {
    // Set up target 
    SpectralBuffer buffer = new SpectralBuffer();

    // Render the lines
    renderLines(buffer, eval, flux.getPhotonLines());

    // Render the groups
    for (FluxGroupTrapezoid grp : flux.getPhotonGroups())
    {
      eval.renderGroup(buffer,
              grp.getEnergyLower(), grp.getDensityLower(),
              grp.getEnergyUpper(), grp.getDensityUpper());
    }
    return buffer.toSpectrum();
  }

  private static void renderLines(SpectralBuffer buffer, SpectralResponseEvaluator eval,
          List<FluxLineStep> gammaLines)
  {
    // Render lines in reverse order first
    //   We process in reverse order so that we can get the benefit of trucation
    //   of the tails.
    ListIterator<FluxLineStep> lineIter = gammaLines.listIterator(gammaLines.size());
    while (lineIter.hasPrevious())
    {
      FluxLineStep item = lineIter.previous();
      eval.renderLine(buffer, item.getEnergy(), item.getIntensity());
    }
  }

}
