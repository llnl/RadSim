// --- file: gov/llnl/rtk/data/DoubleSpectrum.java ---
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
import gov.llnl.math.RebinUtilities;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.Serializable;
import java.util.Arrays;

/**
 * THis class is used to store spectral shapes used to draw random instances.
 *
 * @author nelson85
 */
@ReaderInfo(DoubleSpectrumReader.class)
public class DoubleSpectrum extends SpectrumBase<double[]> implements Serializable
{
  private static final long serialVersionUID = UUIDUtilities.createLong("DoubleSpectrum-v2");
  private double[] data;
  private double overRangeCounts;
  private double underRangeCounts;
  // Cache for efficiency
  private double counts = Double.NEGATIVE_INFINITY;

  public DoubleSpectrum()
  {
  }

  public DoubleSpectrum(double[] data)
  {
    this(data,1,1);
  }

  public DoubleSpectrum(double[] data, double livetime, double realtime)
  {
    //FIXME Using raw data, should create copy first
    super(livetime, realtime);
    this.data = data;
    this.setValidRange(0, data.length);
  }

  /**
   * Copy constructor.
   *
   * This generates a deep copy of the data.
   *
   * @param in
   */
  public <T> DoubleSpectrum(Spectrum<T> in)
  {
    super(in);
    data = DoubleArray.copyOf(in.toDoubles());
    this.setValidRange(0, data.length);
    overRangeCounts = in.getOverRangeCounts();
    underRangeCounts = in.getUnderRangeCounts();
  }

  /**
   * Set the gamma data to a fixed array. Clears underrange, overrange, and
   * valid range data.
   *
   * @param values
   */
  public void setGammaData(double[] values)
  {
    data = DoubleArray.copyOf(values);
    this.setValidRange(0, data.length);
    this.underRangeCounts = 0;
    this.overRangeCounts = 0;
  }

//<editor-fold desc="math">
  @Override
  public void assign(Spectrum obj)
  {
    super.assign(obj);
    data = DoubleArray.copyOf(obj.toDoubles());
    this.underRangeCounts = obj.getUnderRangeCounts();
    this.overRangeCounts = obj.getOverRangeCounts();
  }

  public <T> DoubleSpectrum addAssignCounts(Spectrum<T> obj)
  {
    if (obj == null)
      return this;
    double[] objdata = obj.toDoubles();
    if (objdata == null)
      return this;
    if (data == null)
      data = DoubleArray.copyOf(objdata);
    else
      DoubleArray.addAssign(data, objdata);
    this.overRangeCounts += obj.getOverRangeCounts();
    this.underRangeCounts += obj.getUnderRangeCounts();
    return this;
  }

  @Override
  public <T> DoubleSpectrum addAssign(Spectrum<T> obj)
          throws MathExceptions.SizeException
  {
    if (obj == null)
      return this;
    double[] objdata = obj.toDoubles();
    if (objdata == null)
      return this;
    if (data == null)
      data = DoubleArray.copyOf(objdata);
    else
      DoubleArray.addAssign(data, objdata);
    super.addAssignBase(obj);
    return this;
  }

  public <T> DoubleSpectrum subtractAssign(Spectrum<T> obj)
  {
    if (obj == null)
      return this;
    double[] objData = obj.toDoubles();
    super.subtractAssignBase(obj);
    if (data == null)
      data = new double[objData.length];
    DoubleArray.subtractAssign(data, objData);
    return this;
  }

  public DoubleSpectrum multiplyAssign(double d)
  {
    DoubleArray.multiplyAssign(data, d);
    this.setLiveTime(getLiveTime() * d);
    this.setRealTime(getRealTime() * d);
    this.overRangeCounts *= d;
    this.underRangeCounts *= d;
    this.clearCache();
    return this;
  }

//</editor-fold>
  @Override
  public double getCounts()
  {
    if (counts == Double.NEGATIVE_INFINITY)
    {
      counts = DoubleArray.sumRange(data,
              this.getMinimumValidChannel(),
              this.getMaximumValidChannel());
    }
    return counts;
  }

  @Override
  public double getRate()
  {
    double livetime = getLiveTime();
    if (livetime <= 0)
      return 0;
    return getCounts() / livetime;
  }

  @Override
  public int size()
  {
    if (data == null)
      return 0;
    return data.length;
  }

  @Override
  public void resize(int size)
  {
    if (size <= 0)
      throw new RuntimeException("bad size " + size);
    data = new double[size];
    this.setValidRange(0, size);
    clearCache();
  }

  @Override
  public double[] toArray()
  {
    return data;
  }

  @Override
  public void clearCache()
  {
    counts = Double.NEGATIVE_INFINITY;
  }

  @Override
  public void clear()
  {
    super.clear();
    DoubleArray.fill(data, 0);
    this.underRangeCounts = 0;
    this.overRangeCounts = 0;
  }

  @Override
  public double[] toDoubles()
  {
    return this.data;
  }

  @Override
  public Class getCountClass()
  {
    return double[].class;
  }

  @Override
  protected void assignData(Object obj)
  {
    if (obj instanceof double[])
      this.data = (double[]) obj;
    else if (obj instanceof int[])
      this.data = IntegerArray.promoteToDoubles((int[]) obj);
    else
      throw new UnsupportedOperationException("Unable to assign data from " + obj.getClass());
    this.setValidRange(0, this.data.length);
  }

  @Override
  public double getCounts(RegionOfInterest roi)
  {
    // if there is no roi set we just want total counts
    if (roi == null)
      return this.getCounts();

    int minimumValidChannel = this.getMinimumValidChannel();
    int maximumValidChannel = this.getMaximumValidChannel();

    // otherwise convert the roi to channels
    int[] channels = roi.getChannels(this.getEnergyScale());
    int lower = channels[0];
    if (lower < minimumValidChannel)
      lower = minimumValidChannel;

    int upper = channels[1];
    if (upper > maximumValidChannel)
      upper = maximumValidChannel;

    return DoubleArray.sumRange(this.data, lower, upper);
  }

  @Override
  public double getRate(RegionOfInterest roi)
  {
    double livetime = this.getLiveTime();
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
    return this.underRangeCounts;
  }

  @Override
  public void setUnderRange(double value)
  {
    this.underRangeCounts = value;
  }

  @Override
  public double getOverRangeCounts()
  {
    return this.overRangeCounts;
  }

  @Override
  public void setOverRange(double value)
  {
    this.overRangeCounts = value;
  }

  /**
   * Normalize the spectrum to a shape.
   *
   * Divides the counts by the total in the valid range. Clears data out of the
   * valid range. Sets the collection times to 1.
   */
  public void normalize()
  {
    double counts = this.getCounts();
    if (counts > 0)
      DoubleArray.multiplyAssign(data, 1.0 / counts);
    this.underRangeCounts = 0;
    this.overRangeCounts = 0;
    DoubleArray.fillRange(data, 0, this.getMinimumValidChannel(), 0);
    DoubleArray.fillRange(data, this.getMaximumValidChannel(), data.length, 0);
    this.setLiveTime(1.0);
    this.setRealTime(1.0);
    this.clearCache();
  }

//</editor-fold>

  /**
   * Rebin a spectrum to a new structure. 
   * 
   * Must have an existing bin structure.
   * Alters the spectrum data contents.
   *
   * FIXME this method is a mutator that leads to dangerous side effects. Often
   * something gets passed in and we want to get a rebinned version, but if one
   * forgets to copy the data then the original is mutated. We should analyze
   * the code paths and determine if there is an use cases where this was the
   * intended behavior.
   *
   * @param bins is the new energy structure.
   * @return the spectrum after modification.
   * @throws RebinUtilities.RebinException
   */
  public DoubleSpectrum rebinAssign(EnergyScale bins) throws RebinUtilities.RebinException
  {
    if (bins == null)
      throw new RebinUtilities.RebinException("Null bins");

    // Short cut if the bins are the same
    if (this.getEnergyScale() == bins)
      return this;
    if (this.getEnergyScale() == null)
      throw new RuntimeException("Attempt to rebin spectrum without bins");

    // FIXME over and underrange treatement as well as valid channels is not
    // being handled properly here
    double[] b1 = this.getEnergyScale().getEdges();
    double[] b2 = bins.getEdges();
    data = RebinUtilities.rebin(data, b1, b2);
    double oldEMin = this.getEnergyScale().getEnergyOfEdge(this.getMinimumValidChannel());
    double oldEMax = this.getEnergyScale().getEnergyOfEdge(this.getMaximumValidChannel());
    
    int newChannelMin = bins.findEdgeFloor(oldEMin);
    int newChannelMax = bins.findEdgeFloor(oldEMax);
    if (newChannelMin > bins.getChannels() || newChannelMax <= 0)
      throw new RuntimeException("Non overlapping energy scales");
    if (newChannelMin < 0)
      newChannelMin = 0;
    if (newChannelMax > bins.getChannels())
      newChannelMax = bins.getChannels();
    this.setEnergyScale(bins);
    this.setMaximumValidChannel(newChannelMax);
    this.setMinimumValidChannel(newChannelMin);
    return this;
  }
  
  
  @Override
  public int hashCode()
  {
    int hash = super.hashCode();
    hash = 59 * hash + Arrays.hashCode(this.data);
    hash = 59 * hash + (int) (Double.doubleToLongBits(this.overRangeCounts) ^ (Double.doubleToLongBits(this.overRangeCounts) >>> 32));
    hash = 59 * hash + (int) (Double.doubleToLongBits(this.underRangeCounts) ^ (Double.doubleToLongBits(this.underRangeCounts) >>> 32));
    hash = 59 * hash + (int) (Double.doubleToLongBits(this.counts) ^ (Double.doubleToLongBits(this.counts) >>> 32));
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
    if (!super.equals(obj))
     return false;
    final DoubleSpectrum other = (DoubleSpectrum) obj;
    if (Double.doubleToLongBits(this.overRangeCounts) != Double.doubleToLongBits(other.overRangeCounts))
      return false;
    if (Double.doubleToLongBits(this.underRangeCounts) != Double.doubleToLongBits(other.underRangeCounts))
      return false;
    if (Double.doubleToLongBits(this.counts) != Double.doubleToLongBits(other.counts))
      return false;
    if (!Arrays.equals(this.data, other.data))
      return false;
    return true;
  }
}
