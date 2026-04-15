// --- file: gov/llnl/rtk/flux/FluxSpectrum.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.utility.ExpandableObject;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Representation for a histogram based flux.
 *
 * A flux held as a histogram. This representation does not have energy lines.
 * For speed, the groups are reused and cannot be held when the list iterator is
 * moved. Using the calculator avoids going through the groups.
 *
 * @author nelson85
 */
public class FluxSpectrum extends ExpandableObject implements Flux, Serializable 
{
  final static EnergyScale EMPTY_SCALE = EnergyScaleFactory.newScale(new double[0]);

  final EnergyScale photonScale;
  final double[] photonCounts;
  final EnergyScale neutronScale;
  final double[] neutronCounts;
  final List<FluxGroup> photonGroups;
  final List<FluxGroup> neutronGroups;

  /**
   * Create a new FluxSpectrum.
   *
   * @param photonScale is the energy scale for the groups.
   * @param photonCounts is the counts in each energy group.
   * @param neutronScale
   * @param neutronCounts
   */
  public FluxSpectrum(EnergyScale photonScale, double[] photonCounts,
          EnergyScale neutronScale, double[] neutronCounts)
  {
    // Sanity checks
    if (photonScale != null && photonScale.getChannels() != photonCounts.length)
      throw new IllegalArgumentException(String.format("Size mismatch between scale and counts %d!=%d", photonScale.getChannels(), photonCounts.length));
    if (neutronScale != null && neutronScale.getChannels() != neutronCounts.length)
      throw new IllegalArgumentException();
    if (photonScale == null)
      photonScale = EMPTY_SCALE;
    if (neutronScale == null)
      neutronScale = EMPTY_SCALE;

    this.photonScale = photonScale;
    this.photonCounts = photonCounts;
    this.neutronScale = neutronScale;
    this.neutronCounts = neutronCounts;
    this.photonGroups = new SpectrumGroupList(photonScale, photonCounts);
    this.neutronGroups = new SpectrumGroupList(neutronScale, neutronCounts);
  }

  public static FluxSpectrum createGamma(EnergyScale gammaScale, double[] gammaCounts)
  {
    return new FluxSpectrum(gammaScale, gammaCounts, null, null);
  }

  public static FluxSpectrum createNeutron(EnergyScale neutronScale, double[] neutronCounts)
  {
    return new FluxSpectrum(null, null, neutronScale, neutronCounts);
  }

  @Override
  public List<FluxLine> getPhotonLines()
  {
    return Collections.EMPTY_LIST;
  }

  @Override
  public List<FluxGroup> getPhotonGroups()
  {
    return photonGroups;
  }

  @Override
  public List<FluxGroup> getNeutronGroups()
  {
    return this.neutronGroups;
  }

  @Override
  public FluxEvaluator newPhotonEvaluator()
  {
    return new FluxEvaluatorSpectrum(this.photonScale, this.photonCounts);
  }

  @Override
  public FluxEvaluator newNeutronEvaluator()
  {
    return new FluxEvaluatorSpectrum(this.neutronScale, this.neutronCounts);
  }

  /**
   * @return the gammaScale
   */
  public EnergyScale getPhotonScale()
  {
    return photonScale;
  }

  /**
   * @return the gammaCounts
   */
  public double[] getPhotonCounts()
  {
    return photonCounts;
  }

  /**
   * @return the gammaScale
   */
  public EnergyScale getNeutronScale()
  {
    return neutronScale;
  }

  /**
   * @return the gammaCounts
   */
  public double[] getNeutronCounts()
  {
    return neutronCounts;
  }

  @Override
  public String toString()
  {
    return "FluxSpectrum()";
  }

  @Override
  public boolean equals(Object obj)
  {
    if (!(obj instanceof FluxSpectrum))
      return false;
    FluxSpectrum flux = (FluxSpectrum) obj;
    return Objects.equals(this.photonScale, flux.photonScale)
            && Arrays.equals(this.photonCounts, flux.photonCounts)
            && Objects.equals(this.neutronScale, flux.neutronScale)
            && Arrays.equals(this.neutronCounts, flux.neutronCounts);
  }

//<editor-fold desc="internal" defaultstate="collapsed">
// This section contains implementations that are specialized to Java.
// C# implementations will need to be different.
  static class SpectrumGroup implements FluxGroup
  {

    final EnergyScale scale;
    final double[] counts;
    int index;

    private SpectrumGroup(EnergyScale scale, double[] counts, int index)
    {
      this.scale = scale;
      this.counts = counts;
      this.index = index;
    }

    @Override
    public double getEnergyLower()
    {
      return scale.getEdges()[index];
    }

    @Override
    public double getEnergyUpper()
    {
      return scale.getEdges()[index + 1];
    }

    @Override
    public double getCounts()
    {
      return counts[index];
    }

    @Override
    public double getDensity()
    {
      double[] e = scale.getEdges();
      return counts[index] / (e[index + 1] - e[index]);
    }

    @Override
    public double getIntegral(double energy0, double energy1)
    {
      double re0 = this.getEnergyLower();
      double re1 = this.getEnergyUpper();
      if (energy1 < re0)
        return 0;
      if (energy0 > re1)
        return 0;
      if (energy1 > re1)
        energy1 = re1;
      if (energy0 < re0)
        energy0 = re0;
      return (energy1 - energy0) / (re1 - re0) * this.getCounts();
    }

    @Override
    public String toString()
    {
      return String.format("SpectrumGroup(%d,%d,%d)", this.getEnergyLower(), this.getEnergyUpper(), this.getCounts());
    }
  }

  static class SpectrumGroupList implements List<FluxGroup>
  {

    private final EnergyScale scale;
    private final double[] counts;

    SpectrumGroupList(EnergyScale scale, double[] counts)
    {
      this.scale = scale;
      this.counts = counts;
    }

    @Override
    public int size()
    {
      if (counts == null)
        return 0;
      return counts.length;
    }

    @Override
    public boolean isEmpty()
    {
      return counts.length == 0;
    }

    @Override
    public boolean contains(Object o)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<FluxGroup> iterator()
    {
      return new SpectrumGroupIterator(scale, counts, 0);
    }

    @Override
    public Object[] toArray()
    {
      Object[] out = new Object[counts.length];
      int i = 0;
      for (FluxGroup group : this)
      {
        out[i++] = new FluxGroupBin(group.getEnergyLower(), group.getEnergyUpper(), group.getCounts());
      }
      return out;
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
      if (a.length < counts.length)
        a = (T[]) Array.newInstance(a.getClass().getComponentType(), counts.length);

      int i = 0;
      Object[] result = (Object[]) a;
      for (FluxGroup group : this)
        result[i++] = new FluxGroupBin(group.getEnergyLower(), group.getEnergyUpper(), group.getCounts());

      while (i < a.length)
        result[i++] = null;
      return a;
    }

    @Override
    public boolean add(FluxGroup e)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends FluxGroup> c)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends FluxGroup> c)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public FluxGroup get(int index)
    {
      return new SpectrumGroup(scale, counts, index);
    }

    @Override
    public FluxGroup set(int index, FluxGroup element)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, FluxGroup element)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public FluxGroup remove(int index)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<FluxGroup> listIterator()
    {
      return new SpectrumGroupIterator(scale, counts, 0);
    }

    @Override
    public ListIterator<FluxGroup> listIterator(int index)
    {
      return new SpectrumGroupIterator(scale, counts, index);
    }

    @Override
    public List<FluxGroup> subList(int fromIndex, int toIndex)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

  }

  static class SpectrumGroupIterator implements ListIterator<FluxGroup>
  {

    SpectrumGroup group;
    int index;

    private SpectrumGroupIterator(EnergyScale scale, double[] counts, int index)
    {
      this.group = new SpectrumGroup(scale, counts, 0);
      this.index = index;
    }

    @Override
    public boolean hasNext()
    {
      if (group.counts == null)
        return false;
      return (index < group.counts.length);
    }

    @Override
    public FluxGroup next()
    {
      group.index = index++;
      return group;
    }

    @Override
    public boolean hasPrevious()
    {
      return index > 0;
    }

    @Override
    public FluxGroup previous()
    {
      index--;
      group.index = index;
      return group;
    }

    @Override
    public int nextIndex()
    {
      return index;
    }

    @Override
    public int previousIndex()
    {
      return index - 1;
    }

    @Override
    public void remove()
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(FluxGroup e)
    {
      // immutable
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(FluxGroup e)
    {
      // immutable
      throw new UnsupportedOperationException();
    }
  }
//</editor-fold>
}
