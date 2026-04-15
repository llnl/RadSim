// --- file: gov/llnl/rtk/flux/FluxEvaluatorSorted.java ---
/* 
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * Flux calculator for typical Flux representations.Used for most flux
 * representation.Some flux representations will have specialized evaluators.
 *
 * This implementation has cursors for lines and groups so that traversal of the
 * calculator from low to high energy does not require searching the lists. This
 * method can only be used on flux representations that keep their groups and
 * lines in sorted form.
 *
 * @author nelson85
 * @param <L> is the type of line used in this flux.
 * @param <G> is the type of group used in this flux.
 */
class FluxEvaluatorSorted<L extends FluxLine, G extends FluxGroup>
        implements FluxEvaluator<L, G>
{

  final List<L> lines;
  final List<G> groups;
  final ListIterator<L> lineIterator;
  final ListIterator<G> groupIterator;

  /**
   * Create a new evaluator for flux.
   *
   * @param lines
   * @param groups
   */
  public FluxEvaluatorSorted(List<L> lines,
          List<G> groups)
  {
    this.lines = lines;
    this.groups = groups;
    this.lineIterator = lines.listIterator();
    this.groupIterator = groups.listIterator();

    // This code may benefit from a cache for lines and group energies.
    // Currently we are walking the list from the current position.
  }

  public void seekLine(double energy)
  {

    // Reposition the line iterator so the next line is at or above this energy.
    while (this.lineIterator.hasPrevious())
    {
      if (this.lineIterator.previous().getEnergy() < energy)
      {
        break;
      }
    }

    while (this.lineIterator.hasNext())
    {
      FluxLine next = this.lineIterator.next();
      if (next.getEnergy() >= energy)
      {
        this.lineIterator.previous();
        break;
      }
    }
  }

  /**
   * Moves the cursor for lines and groups to the reference energy.
   *
   * @param energy
   */
  public void seekGroup(double energy)
  {
    // Reposition the group iterator so that next energy 1 is above 
    while (this.groupIterator.hasPrevious())
    {
      if (this.groupIterator.previous().getEnergyUpper() < energy)
        break;
    }

    while (this.groupIterator.hasNext())
    {
      if (this.groupIterator.next().getEnergyUpper() > energy)
      {
        this.groupIterator.previous();
        break;
      }
    }

  }

  /**
   * Get the total integrated flux between energies.
   *
   * @param energy0
   * @param energy1
   * @param method
   * @return the integral.
   */
  @Override
  public double getIntegral(double energy0, double energy1, Set<FluxItem> items)
  {
    double total = 0;
    
    // Include the group portion
    if (items.contains(FluxItem.GROUP))
    {
      seekGroup(energy0);
      while (groupIterator.hasNext())
      {
        FluxGroup group = groupIterator.next();
        if (group.getEnergyLower() > energy1)
          break;
        total += group.getIntegral(energy0, energy1);
      }
    }
    
    // Include the line portion
    if (items.contains(FluxItem.LINE))
    {
      seekLine(energy0);
      while (lineIterator.hasNext())
      {
        FluxLine line = lineIterator.next();
        double energy = line.getEnergy();
        if (energy >= energy1)
          break;
        total += line.getIntensity();
      }
    }
    return total;
  }

  @Override
  public List<L> getLines(double energy0, double energy1)
  {
    seekLine(energy0);
    int i1 = lineIterator.nextIndex();
    seekLine(energy1);
    int i2 = lineIterator.nextIndex();
    return this.lines.subList(i1, i2);
  }

}
