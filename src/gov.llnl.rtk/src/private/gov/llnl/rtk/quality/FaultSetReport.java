// --- file: gov/llnl/rtk/quality/FaultSetReport.java ---
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author nelson85
 */
@Internal
public class FaultSetReport implements FaultSet
{
  List<Fault> faults;

  public FaultSetReport(FaultSet faultSet)
  {
    faults = Collections.unmodifiableList(Arrays.asList(faultSet.toArray(new Fault[0])));
  }

  @Override
  public boolean hasFault()
  {
    return !isEmpty();
  }

  @Override
  public boolean isEmpty()
  {
    return faults.isEmpty();
  }

  @Override
  public boolean isFatal()
  {
    for (Fault fault : this)
      if (!fault.isRecoverable())
        return true;
    return false;
  }

  @Override
  public boolean resetRequested()
  {
    for (Fault fault : this)
      if (fault.getLevel() == FaultLevel.RESET)
        return true;
    return false;
  }

  @Override
  public int size()
  {
    return faults.size();
  }

  @Override
  public boolean contains(Object o)
  {
    return faults.contains(o);
  }

  @Override
  public Iterator<Fault> iterator()
  {
    return faults.iterator();
  }

  @Override
  public Object[] toArray()
  {
    return faults.toArray();
  }

  @Override
  public <T> T[] toArray(T[] ts)
  {
    return faults.toArray(ts);
  }

//<editor-fold desc="list">
  @Override
  public boolean add(Fault e)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> clctn)
  {
    return faults.containsAll(clctn);
  }

  @Override
  public boolean addAll(Collection<? extends Fault> clctn)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> clctn)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> clctn)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clearRecoverableFaults()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clearAllFaults()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    for (Fault s : faults)
    {
      sb.append(s.getName()).append("/");
    }

    if (sb.length() > 0)
      sb.deleteCharAt(sb.length() - 1);

    return sb.toString();
  }

}
