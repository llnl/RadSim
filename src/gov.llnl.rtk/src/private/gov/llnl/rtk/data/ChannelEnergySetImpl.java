// --- file: gov/llnl/rtk/data/ChannelEnergySetImpl.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.calibration.ChannelEnergyPair;
import gov.llnl.rtk.calibration.ChannelEnergySet;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.annotation.Internal;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Utility to convert a possibly incomplete energy pair list into a complete
 * specification. Should remain sorted.
 * <p>
 */
@Internal
public class ChannelEnergySetImpl extends ExpandableObject implements ChannelEnergySet
{
  private static final long serialVersionUID = UUIDUtilities.createLong("ChannelEnergySet-v2");
  TreeSet<ChannelEnergyPair> pairs = new TreeSet<>();

  public ChannelEnergySetImpl()
  {
  }

  /**
   * Copy constructor. Shallow copy.
   *
   * @param contents
   */
  public ChannelEnergySetImpl(Collection<ChannelEnergyPair> contents)
  {
    pairs.addAll(contents);
  }

  protected void clearCache()
  {
  }

//<editor-fold desc="Collection">
  /**
   * Get the number of energy pairs in this energy scale.
   *
   * @return the number of pairs.
   */
  @Override
  public int size()
  {
    return pairs.size();
  }

  @Override
  public Iterator<ChannelEnergyPair> iterator()
  {
    return new CacheIterator<>(pairs.iterator());
  }

  @Override
  public boolean add(ChannelEnergyPair pair)
  {
    clearCache();
    return this.pairs.add(new ChannelEnergyPair(pair));
  }

  /**
   * Add all pairs
   *
   * @param pairs
   * @return true if this list changed as a result of the call.
   */
  @Override
  public boolean addAll(Collection<? extends ChannelEnergyPair> pairs)
  {
    clearCache();
    boolean rc = false;
    for (ChannelEnergyPair pair : pairs)
      rc |= this.pairs.add(new ChannelEnergyPair(pair));
    return rc;
  }

  @Override
  public void clear()
  {
    clearCache();
    pairs.clear();
  }

  /**
   * Remove all pairs that fall within a specified region of interest.
   *
   * @param roi is the region of interest to remove.
   */
  public void removeFrom(EnergyRegionOfInterest roi)
  {
    clearCache();
    Iterator<ChannelEnergyPair> iter = pairs.iterator();
    while (iter.hasNext())
    {
      ChannelEnergyPair pair = iter.next();
      if (roi.contains(pair.getEnergy()))
        iter.remove();
    }
  }

  @Override
  public boolean isEmpty()
  {
    return pairs.isEmpty();
  }

  @Override
  public boolean contains(Object o)
  {
    return pairs.contains(o);
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    return pairs.toArray(a);
  }

  @Override
  public boolean remove(Object o)
  {
    return pairs.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return pairs.containsAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    return pairs.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    return pairs.retainAll(c);
  }

  @Override
  public Object[] toArray()
  {
    return pairs.toArray();
  }
//</editor-fold>
//<editor-fold desc="Iterator">

  public class CacheIterator<T> implements Iterator<T>
  {
    Iterator<T> proxy;

    private CacheIterator(Iterator<T> iterator)
    {
      proxy = iterator;
    }

    @Override
    public boolean hasNext()
    {
      return proxy.hasNext();
    }

    @Override
    public T next()
    {
      return proxy.next();
    }

    @Override
    public void remove()
    {
      clearCache();
      proxy.remove();
    }
  }
//</editor-fold>
}
