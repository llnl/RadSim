// --- file: gov/llnl/rtk/data/IntegerPulseCount.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.UUIDUtilities;

/**
 *
 * @author seilhan3
 */
public class IntegerPulseCount extends PulseCount<int[]>
{
  private static final long serialVersionUID = UUIDUtilities.createLong("IntegerPulseCount-v2");
  int counts;

  public IntegerPulseCount()
  {
  }

  public IntegerPulseCount(int counts, double liveTime, double realTime)
  {
    super(liveTime, realTime);
    this.counts = counts;
  }

  public IntegerPulseCount(PulseCount other)
  {
    super(other);
    double val = other.getCounts();
    if (val != (int) val)
      throw new RuntimeException("Incompatible pulse count");
    this.counts = (int) val;
  }

  @Override
  public double getCounts()
  {
    return counts;
  }

  @Override
  public double getRate()
  {
    return getCounts() / getLiveTime();
  }

  @Override
  public double[] toDoubles()
  {
    return new double[]
    {
      counts
    };
  }
  
  public void setCounts(int counts)
  {
    this.counts = counts;
  }

  @Override
  public IntegerPulseCount addAssign(PulseCount other)
  {
    double val = other.getCounts();
    if (val != (int) val)
    {
      throw new RuntimeException("Cannot add non double counts to IntegerPulseCount");
    }
    counts += other.getCounts();
    super.addAssignBase(other);
    return this;
  }

  public void subtractAssign(IntegerPulseCount other)
  {
    counts -= other.counts;
    super.subtractAssignBase(other);
  }
}
