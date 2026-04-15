// --- file: gov/llnl/rtk/data/DoublePulseCount.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.UUIDUtilities;
import java.io.Serializable;

/**
 *
 * @author seilhan3
 */
public class DoublePulseCount extends PulseCount<double[]> implements Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("DoublePulseCount-v1");
  double counts;

  public DoublePulseCount()
  {
  }

  public DoublePulseCount(double counts, double liveTime, double realTime)
  {
    super(liveTime, realTime);
    this.counts = counts;
  }

  public DoublePulseCount(PulseCount other)
  {
    super(other);
    double val = other.getCounts();
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
    if (liveTime <= 0)
      return 0;
    return getCounts() / liveTime;
  }

  @Override
  public double[] toDoubles()
  {
    return new double[]
    {
      counts
    };
  }

  @Override
  public DoublePulseCount addAssign(PulseCount other)
  {
    counts += other.getCounts();
    super.addAssignBase(other);
    return this;
  }

  public DoublePulseCount subtractAssign(PulseCount other)
  {
    counts -= other.getCounts();
    super.subtractAssignBase(other);
    return this;
  }

  public DoublePulseCount mulitplyAssign(double d)
  {
    counts = counts * d;
    super.multiplyAssignBase(d);
    return this;
  }

}
