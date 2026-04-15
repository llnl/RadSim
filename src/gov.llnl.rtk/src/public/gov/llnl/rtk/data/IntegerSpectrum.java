// --- file: gov/llnl/rtk/data/IntegerSpectrum.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.IntegerArray;
import gov.llnl.math.MathExceptions;
import gov.llnl.math.MathExceptions.MathException;
import gov.llnl.utility.UUIDUtilities;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public class IntegerSpectrum extends SpectrumBase<int[]> implements Cloneable, Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("IntegerSpectrum-v1");
  // It is important that all of the types in here be integer because we
  // can not have rounding errors in rolling window calculations.
  private int[] data = null;
  private int underrange;  // figure out what the units are
  private int overrange;
  // Cache
  private int counts = Integer.MIN_VALUE;

  public IntegerSpectrum()
  {
    super();
  }

  /**
   *
   * @param data
   * @param lt is the livetime in seconds.
   * @param rt is the realtime in seconds.
   */
  public IntegerSpectrum(int[] data, double lt, double rt)
  {
    super(lt, rt);
    this.data = data;
    this.setValidRange(0, data.length);
  }

  public IntegerSpectrum(IntegerSpectrum obj)
  {
    super(obj);
    data = IntegerArray.copyOf(obj.data);
    this.underrange = (int) obj.getUnderRangeCounts();
    this.overrange = (int) obj.getOverRangeCounts();
  }

  public IntegerSpectrum(Spectrum obj)
  {
    super(obj);
    double[] values = obj.toDoubles();
    try
    {
      DoubleArray.assertIntegerArray(values);
    }
    catch (MathException ex)
    {
      throw new RuntimeException("Cannot create integer spectrum from double array", ex);
    }

    data = new int[values.length];
    int i = 0;
    for (double v : values)
    {
      data[i++] = (int) v;
    }
    this.underrange = (int) obj.getUnderRangeCounts();
    this.overrange = (int) obj.getOverRangeCounts();
  }

  /**
   * Copy values and associated data into spectrum. Live and real time are
   * copied. The valid ranges are also preserved.
   *
   * @param obj
   */
  public void assign(IntegerSpectrum obj)
  {
    super.assign(obj);
    data = IntegerArray.copyOf(obj.data);
    this.underrange = (int) obj.getUnderRangeCounts();
    this.overrange = (int) obj.getOverRangeCounts();
  }

  /**
   * Copy values into the spectrum.
   *
   * @param obj
   */
  public void assign(int[] obj)
  {
    data = IntegerArray.copyOf(obj);
  }

  /**
   * Add an integer spectrum to the this spectrum. Increases the livetime and
   * realtime accordingly.
   *
   * @param obj
   * @return
   * @throws MathExceptions.SizeException
   */
  @Override
  public <T> IntegerSpectrum addAssign(Spectrum<T> obj)
          throws MathExceptions.SizeException
  {
    if (obj == null)
      return this;
    if (!(obj instanceof IntegerSpectrum))
    {
      throw new UnsupportedOperationException("Unable to handle non-IntegerSpectrum types");
    }
    IntegerSpectrum base = (IntegerSpectrum) obj;
    if (data == null)
      data = new int[base.data.length];
    super.addAssignBase(obj);
    IntegerArray.addAssign(data, base.data);
    return this;
  }

  public <T> IntegerSpectrum subtractAssign(Spectrum<T> obj)
  {
    if (obj == null)
      return this;
    if (!(obj instanceof IntegerSpectrum))
    {
      throw new UnsupportedOperationException("Unable to handle non-IntegerSpectrum types");
    }
    IntegerSpectrum base = (IntegerSpectrum) obj;
    if (data == null)
      data = new int[base.data.length];
    super.subtractAssignBase(obj);
    IntegerArray.subtractAssign(data, base.data);
    return this;
  }

  /**
   * Compute the total counts in the valid region of the spectrum.
   *
   * @return the total counts.
   */
  @Override
  public double getCounts()
  {
    if (counts == Integer.MIN_VALUE)
      counts = IntegerArray.sumRange(data,
              this.getMinimumValidChannel(),
              this.getMaximumValidChannel());
    return counts;
  }

  /**
   * Get the count rate for the measurement. Only includes the counts in the
   * valid region.
   *
   * @return the total count rate.
   */
  @Override
  public double getRate()
  {
    double livetime = this.getLiveTime();
    if (livetime <= 0)
      return 0;
    return getCounts() / livetime;
  }

  /**
   * Clear the spectrum. Clears the spectrum data and zeros the live and real
   * time. Does not alter the attributes or the energy bins.
   */
  @Override
  public void clear()
  {
    super.clear();
    IntegerArray.fill(data, 0);
    this.underrange = 0;
    this.overrange = 0;
  }

  /**
   * Get the size of the spectrum.
   *
   * @return the number of channels in the spectrum.
   */
  @Override
  public int size()
  {
    if (data == null)
      return 0;
    return data.length;
  }

  /**
   * Alters the size of the spectrum. The current counts in the spectrum are
   * cleared. Clears the valid range.
   *
   * @param size
   */
  @Override
  public void resize(int size)
  {
    if (size <= 0)
      throw new RuntimeException("bad size " + size);
    data = new int[size];
    this.setValidRange(0, size);
    clearCache();
  }

  /**
   * Get the spectrum data. This is not a copy of the data. Alterations to the
   * values will change the spectrum. {@code clearCache} should be called prior
   * to modifying the contents.
   *
   * @return the underlying array holding the data.
   */
  @Override
  public int[] toArray()
  {
    return this.data;
  }

  public void clearOverRange()
  {
    this.data[data.length - 1] = 0;
  }

  public void clearOverRange(int channels)
  {
    for (int i = 0; i < channels; i++)
    {
      this.overrange += this.data[data.length - 1 - i];
      this.data[data.length - 1 - i] = 0;
    }
    this.setMaximumValidChannel(data.length - 1 - channels);
  }

  public void clearUnderRange(int channels)
  {
    for (int i = 0; i < channels; i++)
    {
      this.underrange += this.data[i];
      this.data[i] = 0;
    }
    this.setMinimumValidChannel(channels);
  }

  @Override
  public void clearCache()
  {
    this.counts = Integer.MIN_VALUE;
  }

  @Override
  public double[] toDoubles()
  {
    return IntegerArray.promoteToDoubles(data);
  }

  @Override
  public Class getCountClass()
  {
    return int[].class;
  }

  @Override
  protected void assignData(Object obj)
  {
    if (obj instanceof int[])
      this.data = (int[]) obj;
    else
      throw new UnsupportedOperationException("Unable to assign data from " + obj.getClass());
  }

  @Override
  public double getCounts(RegionOfInterest roi)
  {
    // if there is no roi set we just want total counts
    if (roi == null)
      return this.getCounts();

    int minimumValidChannel = this.getMinimumValidChannel();
    int maximumValidChannel = this.getMaximumValidChannel();
    EnergyScale bins = this.getEnergyScale();

    int[] channels = roi.getChannels(bins);
    // otherwise convert the roi to channels
    int lower = channels[0];
    if (lower < minimumValidChannel)
      lower = minimumValidChannel;

    int upper = channels[1];
    if (upper > maximumValidChannel)
      upper = maximumValidChannel;

    return IntegerArray.sumRange(this.data, lower, upper);
  }

  @Override
  public double getRate(RegionOfInterest roi)
  {
    double livetime = getLiveTime();
    if (roi == null)
      return this.getRate();
    if (livetime <= 0)
      return 0;
    return this.getCounts(roi) / livetime;
  }

//<editor-fold desc="out-of-range">
  @Override
  public double getUnderRangeCounts()
  {
    return this.underrange;
  }

  @Override
  public void setUnderRange(double value)
  {
    this.underrange = (int) value;
  }

  @Override
  public double getOverRangeCounts()
  {
    return this.overrange;
  }

  @Override
  public void setOverRange(double value)
  {
    this.overrange = (int) value;
  }
//</editor-fold>
}
