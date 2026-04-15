// --- file: gov/llnl/rtk/flux/FluxUtilities.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.math.RebinUtilities;
import gov.llnl.rtk.data.EnergyScale;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;

/**
 * Methods to convert from one representation to another.
 *
 * These conversions may be lossy as it is often not possible to preserve the
 * counts in a group. Thus excessive conversions should be avoided.
 *
 * FIXME this class is likely to be broken up into a number of algorithm classes
 * as some of the algorithm may require parameters or produce intermediate
 * results which will require auditing. For now, we have simply collected all
 * the methods in one place.
 *
 * @author nelson85
 */
public class FluxUtilities
{

  /**
   * High to low scanning algorithm to decompose a flux spectrum into binned.
   *
   * This is a special implementation which tries to guess the line positions by
   * looking at the surrounding neighborhood.
   *
   * This is the high to low approach. It has a number of flaws, though overall
   * its accuracy is reasonable. This code will likely be replaced with low to
   * high and line list added version. But it is a reasonable starting
   * algorithm.
   *
   * @param spectrum is the spectrum to be extracted
   * @param scale is the energy scale for the desired binned spectrum.
   * @return a new binned spectrum.
   */
  public static FluxBinned toBinned(FluxSpectrum spectrum, EnergyScale scale)
  {
    try
    {
      double[] es = spectrum.getPhotonScale().getEdges();
      double[][] ex = extract(spectrum);
      double[] C = ex[0];  // Continuum
      double[] L = ex[1];  // Lines
      double[] S = ex[2];  // Step
      double[] G = RebinUtilities.rebin(C, es, scale.getEdges());
      FluxBinned out = new FluxBinned();

      // Convert the lines
      for (int i = 0; i < L.length; ++i)
      {
        if (L[i] <= 0)
          continue;
        out.addPhotonLine(new FluxLineStep((es[i] + es[i + 1]) / 2, L[i], S[i]));
      }

      // Convert the gammaGroups
      es = scale.getEdges();
      for (int i = 0; i < G.length; ++i)
      {
        out.addPhotonGroup(new FluxGroupBin(es[i], es[i + 1], G[i]));
      }

      // Copy over the neutron groups
      for (FluxGroup ngrp : spectrum.getNeutronGroups())
      {
        out.addNeutronGroup(ngrp);
      }

      return out;
    }
    catch (RebinUtilities.RebinException ex1)
    {
      throw new RuntimeException(ex1);
    }
  }

  /**
   * Specialized version of the extractor when the line positions and expected
   * intensities are known.
   *
   * With this version only the
   *
   * @param spectrum
   * @param scale
   * @param lines
   * @return a new binned spectrum.
   */
  public static FluxBinned toBinned(FluxSpectrum spectrum, EnergyScale scale,
          List<? extends FluxLine> lines)
  {
    try
    {
      double[] es = spectrum.getPhotonScale().getEdges();
      double[] counts = spectrum.getPhotonCounts();
      double[] C = counts.clone(); // assume all counts are continuum.
      int n = C.length;

      // Sort the lines.
      ArrayList<FluxLine> l2 = new ArrayList<>(lines);
      l2.sort((p1, p2) -> Double.compare(p1.getEnergy(), p2.getEnergy()));

      // Extract all the expecte line information
      int m = lines.size();
      double[] LE = new double[m]; // initial line energies
      double[] LI = new double[m]; // initial line intensities
      int[] LX = new int[lines.size()];
      {
        int i = 0;
        int j = 0;
        for (FluxLine line : l2)
        {
          LE[i] = line.getEnergy();
          LI[i] = line.getIntensity();
          while (j < es.length && es[j] <= LE[i])
            j++;
          LX[i] = j - 1;
          i++;
        }
      }

      // Break into line groups (i0, i1) index into line arrays and estimate line intensity and step
      double[] LIp = new double[m]; // extracted line intensities
      double[] LSp = new double[m]; // extracted line steps
      int i0 = 0;
      int i1 = 0;
      //many lines are too weak to properly compute the step so use the previous
      // step to estimate instead.
      double fract = 0;
      while (i0 < m)
      {
        while (i1 < m && LX[i1] == LX[i0])
          i1++;
        if (i1 < m && LX[i1] == LX[i0] + 1)
        {
          while (i1 < m && LX[i1 - 1] + 1 >= LX[i1])
            i1++;
        }

        // b0, b1 index into count arrays
        int b0 = LX[i0];
        int b1 = LX[i1 - 1] + 1;
        if (b1 >= n || b0 == n)
        {
          // Skip over the end of the scale for now
          for (int i = i0; i < i1; ++i)
          {
            LIp[i] = 0;
            LSp[i] = 0;
          }
          i0 = i1;
          continue;
        }

        // Compute the width of each group
        double deltaE0 = es[b0] - es[b0 - 1];
        double deltaE1 = es[b1] - es[b0];
        double deltaE2 = es[b1 + 1] - es[b1];

        // Compute the density of the groups
        double density0 = counts[b0 - 1] / deltaE0;
        double density2 = counts[b1] / deltaE2;
//        double c = 0;
//        for (int i = b0; i < b1; ++i)
//          c += counts[i];
//        double density1 = c / deltaE1;

        // Use the intensity of all lines to get weighted energy
        double q0 = 0;  // total intensity of all lines to extract
        double q1 = 0;  // weighted energy of group
        for (int i = i0; i < i1; i++)
        {
          q0 += LI[i];
          q1 += LE[i] * LI[i];
        }

        // Now expected line intensity then skip this extraction to avoid NaN
        if (q0 == 0)
        {
          i0 = i1;
          continue;
        }

        // The center of the line is centroid of the lines in the group
//        double le = q1 / q0;
        // Get the proportion of the group that the effective line is located
//        double P = (le - es[b0]) / (es[b1] - es[b0]);
        // Compute the counts in the total line
        // Use the exact values rather than guessing for now
        double TI = q0; // deltaE1 * (density1 - (density0 * P) - density2 * (1 - P));
        double expectedCounts = (density0 + density2) * deltaE1;
        if (TI > 5 * expectedCounts)
          fract = Math.max((density0 - density2) / TI, 0);

        // Compute the total step
        double TS = (density0 - density2);
        if (TS < 0)
          TS = fract * TI;
        if (TS > 0.01 * TI)
          TS = fract * TI;

        // Note this algorithm is flawed if multiple lines from different shielding
        // depths/different nuclides fall into the same line group.
        // Divide proportially to the original line intensities
        for (int i = i0; i < i1; ++i)
        {
          LIp[i] = TI * LI[i] / q0;
          LSp[i] = TS * LI[i] / q0;
          if (LIp[i] < 0.002 * expectedCounts)
          {
            LIp[i] = 0;
            LSp[i] = 0;
          }
        }

        // Advance to the next line group
        i0 = i1;
      }

      // We now have the observed line intensities which should match the expected
      // with some error.   we may want to perform some filtering here to make them
      // match better.
      // Next we remove the lines from the continuum
      for (int i = 0; i < m; ++i)
      {
        if (LX[i] < n)
        {
          C[LX[i]] -= LIp[i];

          // In some cases the line strength exceeds the computed in the bin
          // we can't have negatives so we place a lower bound.
          if (C[LX[i]] < 0)
            C[LX[i]] = 0;
        }
      }

      // Convert the remaining continuum
      double[] G = RebinUtilities.rebin(C, es, scale.getEdges());

      // Make it a binned spectrum
      FluxBinned out = new FluxBinned();
      // Convert the lines
      for (int i = 0; i < m; ++i)
      {
        if (LIp[i] <= 0)
          continue;
        out.addPhotonLine(new FluxLineStep(LE[i], LIp[i], LSp[i]));
      }

      // Convert the gammaGroups
      es = scale.getEdges();
      for (int i = 0; i < G.length; ++i)
      {
        // Watch for negative groups (whether the come from processing or from
        // the GADRA output)
        out.addPhotonGroup(new FluxGroupBin(es[i], es[i + 1], Math.max(G[i], 0)));
      }

      // Copy over the neutron groups
      for (FluxGroup ngrp : spectrum.getNeutronGroups())
      {
        out.addNeutronGroup(ngrp);
      }

      return out;
    }
    catch (RebinUtilities.RebinException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Procedure to blindly extract the lines from a flux.
   *
   * This is the low to high procedure.
   *
   * @param spectrum
   * @return
   */
  public static double[][] extract(FluxSpectrum spectrum)
  {
    double[] counts = spectrum.getPhotonCounts();
    int n = counts.length;
    double[] C = new double[n];  // continuum
    double[] L = new double[n];  // line intensity
    double[] S = new double[n];  // step
    int i1 = n - 1;
    double[] es = spectrum.getPhotonScale().getEdges();
    while (i1 > 3)
    {
      double deltaE0 = es[i1 + 1] - es[i1];
      double deltaE1 = es[i1] - es[i1 - 1];
      double deltaE2 = es[i1 - 1] - es[i1 - 2];
      double delteE3 = es[i1 - 2] - es[i1 - 3];
      double density0 = counts[i1] / deltaE0;
      double density1 = counts[i1 - 1] / deltaE1;
      double density2 = counts[i1 - 2] / deltaE2;
      double density3 = counts[i1 - 3] / delteE3;
      double mid1 = (density0 + density2) / 2;
      double mid2 = (density0 + 2 * density3) / 3;
      // Two lines
      if (density1 - mid1 > 0.01 * mid1 && density2 - mid2 > 0.01 * mid2 && density2 > 1.01 * density3)
      {
        C[i1] = density0 * deltaE0;
        mid1 = (2 * density0 + density3) / 3;
        C[i1 - 1] = mid1 * deltaE1;
        C[i1 - 2] = mid2 * deltaE2;
        C[i1 - 3] = density3 * delteE3;
        L[i1 - 1] = (density1 - mid1) * deltaE1;
        L[i1 - 2] = (density2 - mid2) * deltaE2;
        // Step is proportional to line intensity
        S[i1 - 1] = (density3 - density0) * (L[i1 - 1] / (L[i1 - 1] + L[i1 - 2]));
        S[i1 - 2] = (density3 - density0) * (L[i1 - 2] / (L[i1 - 1] + L[i1 - 2]));
        i1 -= 3;
      } // One line
      else if (density1 - mid1 > 0.01 * mid1)
      {
        C[i1] = density0 * deltaE0;
//        if (m1 < x3)
//          m1 = x3;
        C[i1 - 1] = mid1 * deltaE1;
        C[i1 - 2] = density2 * deltaE2;
        L[i1 - 1] = (density1 - mid1) * deltaE1;
        S[i1 - 1] = (density2 - density0);
        i1 -= 2;
      }
      else
      {
        C[i1] = counts[i1];
        i1 -= 1;
      }
    }

    // Assume bottom bins are continuum.
    System.arraycopy(counts, 0, C, 0, 4);

    for (int i0 = 0; i0 < S.length; ++i0)
    {
      S[i0] = Math.max(S[i0], 0);
    }
    return new double[][]
    {
      C, L, S
    };
  }

  /**
   * Convert a flux to a binned representation.
   *
   * Lines are copied from the original and the counts in the regions specified
   * are tallied. This version does not operate on flux spectrum as it lacks
   * line information.
   *
   * @param flux is the flux representation to alter.
   * @param scale is the desired group energy scale for the binned flux.
   * @return a new binned flux representation.
   */
  public static FluxBinned toBinned(Flux flux, EnergyScale scale)
  {
    FluxEvaluator<FluxLine, FluxGroup> calculator = flux.newPhotonEvaluator();
    double[] edges = scale.getEdges();
    FluxBinned out = new FluxBinned();

    for (FluxLine line : flux.getPhotonLines())
    {
      out.addPhotonLine(line);
    }

    for (int i = 0; i < edges.length - 1; ++i)
    {
      double counts = calculator.getIntegral(edges[i], edges[i + 1], FluxItem.GROUPS);
      out.addPhotonGroup(new FluxGroupBin(edges[i], edges[i + 1], counts));
    }
    return out;

  }

  /**
   * Convert a flux to a spectrum.
   *
   * This loses all line information.
   *
   * Distribution in the groups depends on the original representation.
   *
   * @param flux is the flux to be converted.
   * @param scale is the desired energy scale for the spectrum.
   * @param items
   * @return
   */
  public static FluxSpectrum toSpectrum(Flux flux, EnergyScale scale,
          Set<FluxItem> items)
  {
    FluxEvaluator calculator = flux.newPhotonEvaluator();
    double[] edges = scale.getEdges();
    double[] counts = new double[edges.length - 1];
    for (int i = 0; i < edges.length - 1; ++i)
    {
      counts[i] = calculator.getIntegral(edges[i], edges[i + 1], items);
    }
    return new FluxSpectrum(scale, counts, null, null);
  }

  /**
   * Convert a binned flux to trapezoid.
   *
   * This attempts to redistributed the flux within the groups. The groups are
   * roughly the same as the original binned groups, but may be split around
   * lines.
   *
   * @param binned
   * @return
   */
  public static FluxTrapezoid toTrapezoid(FluxBinned binned)
  {
    List<FluxLineStep> lines = binned.getPhotonLines();
    List<FluxGroupBin> groups = binned.getPhotonGroups();
    double[] upper = new double[groups.size()];
    double[] lower = new double[groups.size()];
    double[] step = new double[groups.size()];
    double[] center = new double[groups.size()];
    FluxTrapezoid out = new FluxTrapezoid();

    // Transfer the lines
    lines.stream().forEach(out::addPhotonLine);

    // Create a calculator
    FluxEvaluator<FluxLineStep, FluxGroupBin> calculator = binned.newPhotonEvaluator();

    // Precompute the required values for each group
    int i = 0;
    for (FluxGroupBin group : groups)
    {
      double e0 = group.energyLower;
      double e1 = group.energyUpper;
      double w = e1 - e0;
      double counts = group.counts;

      // Compute group center
      center[i] = (e0 + e1) / 2;

      // Compute upper and lower density estimates
      double density = counts / w;
      upper[i] = density;
      lower[i] = density;
      for (FluxLineStep line : calculator.getLines(group.energyLower, group.energyUpper))
      {
        // FIXME this method neglects the slope of the group which requires a
        // kludge to address.
        if (line.step <= 0)
          continue;
        step[i] += line.step;
        upper[i] += line.step * (e1 - line.energy) / w;
        lower[i] -= line.step * (line.energy - e0) / w;
      }

      // Watch out when the lower estimate for the group is less than 0.
      if (lower[i] < 0)
        lower[i] = 0;

      // next group
      i++;
    }

    // Distibute count within the gammaGroups.
    i = 0;
    double l = 0, u, l0;
    for (FluxGroupBin group : groups)
    {
      double e0 = group.energyLower;
      double e1 = group.energyUpper;
      double w = e1 - e0;

      // Get required values from previous and next group
      double u1 = (i == groups.size() - 1) ? 0 : upper[i + 1];
      l0 = l;
      double ec0 = (i == 0) ? e0 - w : center[i - 1];
      double ec1 = (i == groups.size() - 1) ? e1 + w : center[i + 1];

      // Get required values for the current group
      double ec = center[i];
      u = upper[i];
      l = lower[i];

      // If there is a very strong step then use the upper estimate of the
      // next group.   FIXME this could be refined.
      if (u1 < 5 * l && step[i] > 0.8 * u)
      {
        l = u1;
      }

      // Use group values to estimate corners for distibution
      double f00 = linear(e0, ec0, l0, ec, u);
      double f11 = linear(e1, ec, l, ec1, u1);
      double f10 = linear(e1, ec, u, ec1, u1 + step[i]);
      double f01 = (f10 > 0) ? f00 * (f11 / f10) : 0;
      if (f00 < 0)
        f00 = 0;
      if (f11 < 0)
        f11 = 0;

      // Find all the lines in the group
      List<FluxLineStep> contained = calculator.getLines(group.energyLower, group.energyUpper);
      if (contained.isEmpty())
      {
        // No lines then just create a trapezoid for the group assuming there
        // are gammaCounts in the group.
        if (f00 > 0 || f11 > 0)
          out.addPhotonGroup(new FluxGroupTrapezoid(e0, e1, f00, f11));
      }
      else
      {
        // Else deal with distributing within the group
        double ep = e0;
        double dp = f00;
        double el = 0;
        double s = 0;
        for (FluxLineStep line : contained)
        {
          // Skip lines that have no contibutions
          if (step[i] <= 0)
            continue;
          double e = line.energy;
          double f = s / step[i];
          if (e != ep)
          {
            double b = (1 - f) * linear(e, e0, f00, e1, f10) + f * linear(e, e0, f01, e1, f11);
            out.addPhotonGroup(new FluxGroupTrapezoid(ep, e, dp, b));
          }

          // Define the base for the next trapezoid
          s += line.step;
          f = s / step[i];
          ep = e;
          dp = (1 - f) * linear(e, e0, f00, e1, f10) + f * linear(e, e0, f01, e1, f11);
        }
        if (el != e1)
          out.addPhotonGroup(new FluxGroupTrapezoid(ep, e1, dp, f11));
      }

      i++;
    }
    return out;
  }

  /**
   * Insert a group into an existing list.
   *
   * @param <T>
   * @param groups
   * @param group
   */
  public static <T extends FluxGroup> void insertGroup(List<T> groups, T group)
  {
    // Never add a null group.
    if (group == null)
      return;

    // If the list is empty we don't need to worry about where we insertGroup.
    if (groups.isEmpty())
    {
      groups.add(group);
      return;
    }

    // Find the insertion point.
    double e0 = group.getEnergyLower();
    double e1 = group.getEnergyUpper();
    ListIterator<T> iter = groups.listIterator(groups.size());
    T prev = null;
    T last = null;
    while (iter.hasPrevious())
    {
      prev = iter.previous();
      if (e0 >= prev.getEnergyUpper())
      {
        // Insert at end
        if (last == null)
        {
          groups.add(group);
          return;
        }
        break;
      }
      last = prev;
    }

    // This should never happen
    if (prev == null)
      throw new RuntimeException();

    // Insert at the beginning.
    if (e1 <= prev.getEnergyLower())
    {
      groups.add(0, group);
      return;
    }

    if (prev.getEnergyUpper() <= e0 && e1 <= last.getEnergyLower())
    {
      iter.next();
      iter.add(group);
      return;
    }    
    // Not implemented yet.
    throw new UnsupportedOperationException("Overlapping group requested");
  }

  /**
   * Insert a line while keeping the list sorted.
   *
   * @param <T>
   * @param lines
   * @param nline
   */
  public static <T extends FluxLine> void insertLine(List<T> lines, T nline)
  {
    if (lines.isEmpty())
    {
      lines.add(nline);
      return;
    }

    double e = nline.getEnergy();
    if (lines.get(0).getEnergy() >= e)
    {
      lines.add(0, nline);
      return;
    }
    if (lines.get(lines.size() - 1).getEnergy() < e)
    {
      lines.add(nline);
      return;
    }

    // Just add the line at the end and sort.
    lines.add(nline);
    Collections.sort(lines, (T p1, T p2) -> Double.compare(p1.getEnergy(), p2.getEnergy()));
  }

  /**
   * Reduces the number of lines in a spectrum to improve rendering speed.
   *
   * Useful when processing lower resolution spectrum.
   *
   * @param flux
   * @param resolutionFunction
   * @return
   */
  public static FluxBinned simplify(FluxBinned flux, DoubleUnaryOperator resolutionFunction)
  {
    FluxBinned flux2 = new FluxBinned();
    FluxLineStep last = null;
    for (FluxLineStep line : flux.getPhotonLines())
    {
      // Nothing to merge on first line
      if (last == null)
      {
        last = line;
        continue;
      }

      // Line widely spaced, accept it
      double delta = (line.getEnergy() - last.getEnergy())
              / resolutionFunction.applyAsDouble(line.getEnergy());
      if (delta >= 0.3)
      {
        flux2.addPhotonLine(last);
        last = line;
        continue;
      }

      // Merge neighboring lines
      double e0 = last.getEnergy();
      double e1 = line.getEnergy();
      double i0 = last.getIntensity();
      double i1 = line.getIntensity();
      double s0 = last.getStep();
      double s1 = line.getStep();
      double e = (e0 * i0 + e1 * i1) / (i0 + i1);
      last = new FluxLineStep(e, i0 + i1, s0 + s1);
    }
    flux2.addPhotonLine(last);

    // Copy the gamma groups
    for (FluxGroupBin grp : flux.getPhotonGroups())
      flux2.addPhotonGroup(grp);

    // Drop insignificant lines
    double k = Math.sqrt(2 * Math.PI);
    FluxBinned flux3 = new FluxBinned();
    FluxEvaluator<FluxLineStep, FluxGroupBin> ge = flux2.newPhotonEvaluator();

    // For each group
    List<FluxGroupBin> groups = flux2.getPhotonGroups();

    // Add any lines that are below the first group
    for (FluxLineStep line : ge.getLines(-Double.MAX_VALUE, groups.get(0).getEnergyLower()))
      flux3.addPhotonLine(line);

    // Add any lines that are above the last group
    for (FluxLineStep line : ge.getLines(groups.get(groups.size() - 1).getEnergyLower(), Double.MAX_VALUE))
      flux3.addPhotonLine(line);

    // Trim lines that are within groups
    for (FluxGroupBin grp : groups)
    {
      double counts = grp.getCounts();

      // For each line in group
      for (FluxLineStep line : ge.getLines(grp.energyLower, grp.energyUpper))
      {
        double lineDensity = line.getIntensity() / k / resolutionFunction.applyAsDouble(line.getEnergy());
        double importance = lineDensity / grp.getDensity();
        if (importance < 0.1)
          // Line is not significant
          counts += line.getIntensity();
        else
          // Line is significant (keep it)
          flux3.addPhotonLine(line);
      }

      // Add the group with combined intensity
      flux3.addPhotonGroup(new FluxGroupBin(grp.getEnergyLower(), grp.getEnergyUpper(), counts));
    }
    return flux3;
  }

  /**
   * Remove lines that are insignificant.
   *
   *
   * @param binned
   * @param significance
   * @return
   */
  public static FluxBinned chop(FluxBinned binned, double significance)
  {
    FluxBinned out = new FluxBinned();
    ListIterator<FluxGroupBin> giter = binned.photonGroups.listIterator();
    ListIterator<FluxLineStep> liter = binned.photonLines.listIterator();

    while (giter.hasNext())
    {
      FluxGroupBin group = giter.next();
      double density = group.getDensity();
      double gcounts = group.getCounts();

      while (liter.hasNext())
      {
        FluxLineStep line = liter.next();
        if (line.energy < group.energyLower)
          out.addPhotonLine(line);
        if (line.energy >= group.energyUpper)
        {
          liter.previous();
          break;
        }

        double counts = line.intensity;
        if (counts < Math.sqrt(density) * significance)
        {
          gcounts += counts;
        }
        else
          out.addPhotonLine(line);
      }

      out.addPhotonGroup(new FluxGroupBin(group.energyLower, group.energyUpper, gcounts));
    }
    return out;
  }

  /**
   * Simple linear interpolation.
   *
   * @param x
   * @param x0
   * @param y0
   * @param x1
   * @param y1
   * @return
   */
  static double linear(double x, double x0, double y0, double x1, double y1)
  {
    double f = (x - x0) / (x1 - x0);
    return (1 - f) * y0 + f * y1;
  }

  static FluxGroup findGroup(List<? extends FluxGroup> groups, double energy)
  {
    for (FluxGroup g : groups)
    {
      if (g.getEnergyLower() <= energy && g.getEnergyUpper() > energy)
        return g;
    }
    return null;
  }

}
