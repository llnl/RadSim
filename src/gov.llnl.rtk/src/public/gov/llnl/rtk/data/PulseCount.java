// --- file: gov/llnl/rtk/data/PulseCount.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.annotation.Internal;
import java.time.Instant;

/**
 *
 * @author seilhan3
 * @param <Type>
 */
public abstract class PulseCount<Type> extends ExpandableObject implements RadiationData<Type>
{
  private static final long serialVersionUID = UUIDUtilities.createLong("PulseCountBase-v1");

  double liveTime = 0;
  double realTime = 0;

  PulseCount()
  {
  }

  PulseCount(PulseCount pc)
  {
    super(pc);
    this.liveTime = pc.liveTime;
    this.realTime = pc.realTime;
  }

  PulseCount(double liveTime, double realTime)
  {
    this.liveTime = liveTime;
    this.realTime = realTime;
  }

  @Override
  final public double getLiveTime()
  {
    return liveTime;
  }

  @Override
  final public double getRealTime()
  {
    return realTime;
  }
  
  //<editor-fold desc="loader">  
  /**
   * Set the real time for the spectrum. Real time should be in seconds.
   *
   * @param toDouble
   */
  final public void setRealTime(double toDouble)
  {
    this.realTime = toDouble;
  }

  /**
   * Set the live time for the spectrum. Live time should be in seconds.
   *
   * @param toDouble
   */
  final public void setLiveTime(double toDouble)
  {
    this.liveTime = toDouble;
  }
  //</editor-fold>

  //<editor-fold desc="timestamp">
  @Override
  final public Instant getStartTime()
  {
    return this.getAttribute(SpectrumAttributes.TIMESTAMP, Instant.class);
  }

  @Override
  final public Instant getEndTime()
  {
    Instant date = this.getStartTime();
    if (date == null)
      return null;
    long ts = (long) (date.toEpochMilli() + this.realTime * 1000.0);
    return Instant.ofEpochMilli(ts);
  }

  /**
   * Set the timestamp for this spectrum.
   *
   * @param timestamp
   */
  final public void setStartTime(Instant timestamp)
  {
    this.setAttribute(SpectrumAttributes.TIMESTAMP, timestamp);
  }
  //</editor-fold>

  //<editor-fold desc="title">
  /**
   * Get the title of the spectrum.
   *
   * @return the title or null if not defined.
   */
  @Override
  public String getTitle()
  {
    return this.getAttribute(SpectrumAttributes.TITLE, String.class);
  }

  /**
   * Set the title of this spectrum. Title is stored as an attribute.
   *
   * @param title the title to set
   */
  public void setTitle(String title)
  {
    this.setAttribute(SpectrumAttributes.TITLE, title);
  }
//</editor-fold>

  public abstract PulseCount addAssign(PulseCount obj);

  @Internal
  protected void addAssignBase(PulseCount obj)
  {
    this.liveTime += obj.getLiveTime();
    this.realTime += obj.getRealTime();
  }

  @Internal
  protected void subtractAssignBase(PulseCount obj)
  {
    this.liveTime -= obj.liveTime;
    this.realTime -= obj.realTime;
  }
  
  @Internal
  protected void multiplyAssignBase(double d)
  {
    this.liveTime = this.liveTime*d;
    this.realTime = this.realTime*d;
  }

}
