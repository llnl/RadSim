// --- file: gov/llnl/rtk/data/SpectrumBase.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.MathExceptions;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.UUIDUtilities;
import java.time.Instant;
import java.util.Objects;

/**
 *
 * @author nelson85
 * @param <Type>
 */
public abstract class SpectrumBase<Type> extends ExpandableObject implements Spectrum<Type>
{
  private static final long serialVersionUID = UUIDUtilities.createLong("SpectrumBase-v1");
  /**
   * energy bins for this data. May be null if not available.
   */
  private EnergyScale energyScale = null;
  /**
   * real time in seconds.
   */
  private double realtime = 0;
  /**
   * live time in seconds.
   */
  private double livetime = 0;
  /**
   * minimum valid channel (inclusive)
   */
  private int minimumValidChannel = 0;
  /**
   * maximum valid channel (exclusive)
   */
  private int maximumValidChannel = 0;

  protected SpectrumBase()
  {
  }

  protected SpectrumBase(double livetime, double realtime)
  {
    this.livetime = livetime;
    this.realtime = realtime;
  }

  /**
   * Copy constructor. Copy the attributes, times and energy bins.
   *
   * @param obj
   */
  protected SpectrumBase(Spectrum obj)
  {

    super(obj);
    this.realtime = obj.getRealTime();
    this.livetime = obj.getLiveTime();
    this.energyScale = obj.getEnergyScale();   // FIXME deep or shallow copy
    this.setValidRange(obj.getMinimumValidChannel(),
            obj.getMaximumValidChannel());
  }

  @Override
  final public double getLiveTime()
  {
    return livetime;
  }

  @Override
  final public double getRealTime()
  {
    return realtime;
  }

//<editor-fold desc="loader">
  /**
   * Set the real time for the spectrum. Real time should be in seconds.
   *
   * @param toDouble
   */
  final public void setRealTime(double toDouble)
  {
    this.realtime = toDouble;
  }

  /**
   * Set the live time for the spectrum. Live time should be in seconds.
   *
   * @param toDouble
   */
  final public void setLiveTime(double toDouble)
  {
    this.livetime = toDouble;
  }
//</editor-fold>
//<editor-fold desc="energy-bins">

  /**
   * Get the energy bins associated with the spectrum.
   *
   * @return the energy bins associated with the spectrum, or null if no scale
   * is set.
   */
  @Override
  final public EnergyScale getEnergyScale()
  {
    return energyScale;
  }

  /**
   * Set the energy bins associated with the spectrum.
   *
   * @param energyScale
   */
  @Override
  final public void setEnergyScale(EnergyScale energyScale)
  {
    this.energyScale = energyScale;
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
    long ts = (long) (date.toEpochMilli() + this.realtime * 1000.0);
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
//<editor-fold desc="valid-range">
  /**
   * @return the minimumValidChannel
   */
  @Override
  public int getMinimumValidChannel()
  {
    return minimumValidChannel;
  }

  /**
   * @param minimumValidChannel the minimumValidChannel to set
   */
  public void setMinimumValidChannel(int minimumValidChannel)
  {
    if (minimumValidChannel < 0)
      minimumValidChannel = 0;
    this.minimumValidChannel = minimumValidChannel;
    clearCache();
  }

  /**
   * @return the maximumValidChannel
   */
  @Override
  public int getMaximumValidChannel()
  {
    return maximumValidChannel;
  }

  /**
   * @param maximumValidChannel the maximumValidChannel to set
   */
  public void setMaximumValidChannel(int maximumValidChannel)
  {
    if (maximumValidChannel > size())
      maximumValidChannel = size();
    this.maximumValidChannel = maximumValidChannel;
    clearCache();
  }
//</editor-fold>

  /**
   * Clear the spectrum data. This clears the realtime and livetime.
   */
  public void clear()
  {
    this.realtime = 0;
    this.livetime = 0;
    clearCache();
  }

  protected void assign(Spectrum obj)
  {
    // Copy any attributes
    super.copyAttributes(obj);

    // copy the energy bins
    EnergyScale eb = obj.getEnergyScale();
    if (eb == null)
      this.energyScale = null;
    else
      this.energyScale = EnergyScaleFactory.newScale(eb.getEdges());

    this.livetime = obj.getLiveTime();
    this.realtime = obj.getRealTime();
    this.minimumValidChannel = obj.getMinimumValidChannel();
    this.maximumValidChannel = obj.getMaximumValidChannel();
    clearCache();
  }

  protected <T> void addAssignBase(Spectrum<T> obj)
  {
    this.livetime += obj.getLiveTime();
    this.realtime += obj.getRealTime();
    this.minimumValidChannel = Math.max(minimumValidChannel, obj.getMinimumValidChannel());
    this.maximumValidChannel = Math.max(maximumValidChannel, obj.getMaximumValidChannel());
    clearCache();
  }

  public abstract <S> SpectrumBase<Type> addAssign(Spectrum<S> obj)
          throws MathExceptions.SizeException;

  protected <T> void subtractAssignBase(Spectrum<T> obj)
  {
    this.livetime -= obj.getLiveTime();
    this.realtime -= obj.getRealTime();
    this.minimumValidChannel = Math.max(minimumValidChannel, obj.getMinimumValidChannel());
    this.maximumValidChannel = Math.max(maximumValidChannel, obj.getMaximumValidChannel());
    clearCache();
  }

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

//<editor-fold desc="out-of-range">
  /**
   * Set number of underrange counts.
   *
   * @param value
   */
  abstract public void setUnderRange(double value);

  /**
   * Set number of overrange counts.
   *
   * @param value
   */
  abstract public void setOverRange(double value);
//</editor-fold>

  public void setValidRange(int start, int end)
  {
    this.minimumValidChannel = start;
    this.maximumValidChannel = end;
  }

  protected abstract void assignData(Object obj);

  @Override
  public int hashCode()
  {
    int hash = 5;
    hash = 11 * hash + Objects.hashCode(this.energyScale);
    hash = 11 * hash + (int) (Double.doubleToLongBits(this.realtime) ^ (Double.doubleToLongBits(this.realtime) >>> 32));
    hash = 11 * hash + (int) (Double.doubleToLongBits(this.livetime) ^ (Double.doubleToLongBits(this.livetime) >>> 32));
    hash = 11 * hash + this.minimumValidChannel;
    hash = 11 * hash + this.maximumValidChannel;
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final SpectrumBase<?> other = (SpectrumBase<?>) obj;
    if (Double.doubleToLongBits(this.realtime) != Double.doubleToLongBits(other.realtime))
      return false;
    if (Double.doubleToLongBits(this.livetime) != Double.doubleToLongBits(other.livetime))
      return false;
    if (this.minimumValidChannel != other.minimumValidChannel)
      return false;
    if (this.maximumValidChannel != other.maximumValidChannel)
      return false;
    if (!Objects.equals(this.energyScale, other.energyScale))
      return false;
    return true;
  }

}
