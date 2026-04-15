// --- file: gov/llnl/rtk/quality/FaultSetImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.quality.Fault;
import gov.llnl.rtk.quality.FaultLevel;
import gov.llnl.rtk.quality.FaultSet;
import gov.llnl.utility.annotation.Internal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author seilhan3
 */
@Internal
public class FaultSetImpl implements FaultSet
{
  List<FaultSet> children = new ArrayList<>();
  TreeSet<Fault> stateList = new TreeSet<>();

  public void addChild(FaultSet faultSet)
  {
    children.add(faultSet);
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for (FaultSet fs : children)
    {
      String name = fs.toString();
      if (name.isEmpty())
        continue;
      if (sb.length() > 0)
        sb.append("/");
      sb.append(name);
    }

    for (Fault s : stateList)
    {
      String name = s.getName();
      if (name.isEmpty())
        continue;
      if (sb.length() > 0)
        sb.append("/");
      sb.append(name);
    }

    return sb.toString();
  }

  @Override
  public boolean add(Fault fault)
  {
    boolean out = this.stateList.add(fault);
//    changed = true;
    return out;
  }

  @Override
  public boolean isEmpty()
  {
    if (!this.stateList.isEmpty())
      return false;
    for (FaultSet child : this.children)
      if (!child.isEmpty())
        return false;
    return true;
  }

  @Override
  public boolean hasFault()
  {
    return !stateList.isEmpty();
  }

  @Override
  public Iterator<Fault> iterator()
  {
    Fault[] cache = this.toArray(new Fault[0]);
    return Arrays.asList(cache).iterator();
  }

  @Override
  public boolean isFatal()
  {
    if (!stateList.isEmpty() && stateList.first().getLevel() == FaultLevel.FATAL)
    {
      return true;
    }
    for (FaultSet child : children)
    {
      if (child.isFatal())
        return true;
    }
    return false;
  }

  @Override
  public boolean resetRequested()
  {
    if (!stateList.isEmpty())
    {
      for (Fault s : stateList)
      {
        if (s.getLevel() == FaultLevel.RESET)
          return true;
      }
    }
    for (FaultSet child : children)
    {
      if (child.resetRequested())
        return true;
    }
    return false;
  }

  @Override
  public void clearRecoverableFaults()
  {
//    clearChanged();
    Iterator<Fault> it = this.stateList.iterator();
    while (it.hasNext())
    {
      Fault next = it.next();
      if (next.isRecoverable())
      {
        it.remove();
      }
    }
    for (FaultSet fs : children)
    {
      fs.clearRecoverableFaults();
    }
  }

  public void clearState()
  {
    Iterator<Fault> it = this.stateList.iterator();
    while (it.hasNext())
    {
      Fault next = it.next();
      if (!next.isRecoverable())
      {
        it.remove();
      }
    }
  }

  @Override
  public void clearAllFaults()
  {
//    clearChanged();
    this.stateList.clear();
    for (FaultSet fs : children)
    {
      fs.clearAllFaults();
    }
  }

  @Override
  public void clear()
  {
    this.clearAllFaults();
  }

  @Override
  public int size()
  {
    int sz = this.stateList.size();
    for (FaultSet child : children)
    {
      sz += child.size();
    }
    return sz;
  }

  public List<Fault> toList()
  {
    ArrayList<Fault> out = new ArrayList<>();
    out.addAll(this.stateList);
    for (FaultSet child : children)
    {
      out.addAll(child);
    }
    return out;
  }

  @Override
  public Object[] toArray()
  {
    return toList().toArray();
  }

  @Override
  public <T> T[] toArray(T[] ts)
  {
    return toList().toArray(ts);
  }

//<editor-fold desc="container">
  @Override
  public boolean contains(Object o)
  {
    if (this.stateList.contains(o))
      return true;
    for (FaultSet child : children)
    {
      if (child.contains(o))
        return true;
    }
    return false;
  }

  @Override
  public boolean remove(Object o)
  {
    return this.stateList.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> clctn)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean addAll(Collection<? extends Fault> clctn)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean removeAll(Collection<?> clctn)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public boolean retainAll(Collection<?> clctn)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
//</editor-fold>

}
