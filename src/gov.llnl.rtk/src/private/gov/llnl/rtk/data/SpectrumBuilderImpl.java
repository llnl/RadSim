// --- file: gov/llnl/rtk/data/SpectrumBuilderImpl.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.IntegerArray;
import java.time.Instant;
import java.util.stream.DoubleStream;

public class SpectrumBuilderImpl implements SpectrumBuilder
{
  double[] countsDouble;
  int[] countsInteger;
  EnergyScale energyScale;
  double livetime;
  double realtime;
  Instant timestamp;
  String title;
  int underRangeChannels = 0;
  int overRangeChannels = 0;

  @Override
  public SpectrumBuilder counts(double[] d)
  {
    if (countsInteger != null)
      throw new IllegalStateException("counts already set");
    countsDouble = d;
    return this;
  }

  @Override
  public SpectrumBuilder counts(int[] d)
  {
    if (countsDouble != null)
      throw new IllegalStateException("counts already set");
    countsInteger = d;
    return this;
  }

  @Override
  public SpectrumBuilder scale(EnergyScale scale)
  {
    this.energyScale = scale;
    return this;
  }

  @Override
  public SpectrumBuilder time(double time)
  {
    this.livetime = time;
    this.realtime = time;
    return this;
  }

  @Override
  public SpectrumBuilder time(double livetime, double realtime)
  {
    this.livetime = livetime;
    this.realtime = realtime;
    return this;
  }

  @Override
  public SpectrumBuilder timestamp(Instant timestamp)
  {
    this.timestamp = timestamp;
    return this;
  }

  @Override
  public SpectrumBuilder title(String title)
  {
    this.title = title;
    return this;
  }

  @Override
  public DoubleSpectrum asDouble()
  {
    DoubleSpectrum out;
    if (countsInteger != null)
    {
      countsDouble = IntegerArray.promoteToDoubles(countsInteger);
    }
    else if (countsDouble == null)
    {
      throw new IllegalStateException("Counts field is missing");
    }

    if (((overRangeChannels > 0) || (underRangeChannels > 0)) && countsInteger != null)
    {
      countsDouble = countsDouble.clone();
    }
    out = new DoubleSpectrum(this.countsDouble, this.livetime, this.realtime);
    double[] counts = out.toArray();
    if (energyScale != null && energyScale.getChannels() != counts.length)
      throw new RuntimeException("EnergyScale mismatch");

    if (overRangeChannels > 0)
    {
      out.setOverRange(DoubleArray.sumRange(counts, counts.length - overRangeChannels, counts.length));
      DoubleArray.fillRange(counts, counts.length - overRangeChannels, counts.length, 0);
    }

    if (underRangeChannels > 0)
    {
      out.setUnderRange(DoubleArray.sumRange(counts, 0, underRangeChannels));
      DoubleArray.fillRange(counts, 0, underRangeChannels, 0);
    }

    out.setValidRange(underRangeChannels, out.size() - overRangeChannels);

    setAttr(out);
    return out;
  }

  @Override
  public IntegerSpectrum asInteger()
  {
    IntegerSpectrum out;

    if (countsDouble != null)
    {
      countsInteger = DoubleStream.of(countsDouble).mapToInt(p -> (int) p).toArray();
    }
    else if (countsInteger == null)
    {
      throw new IllegalStateException("Counts field is missing");
    }

    if (((overRangeChannels > 0) || (underRangeChannels > 0)) && countsDouble != null)
    {
      countsInteger = countsInteger.clone();
    }

    out = new IntegerSpectrum(countsInteger, this.livetime, this.realtime);
    int[] counts = out.toArray();
    if (energyScale != null && energyScale.getChannels() != counts.length)
      throw new RuntimeException("EnergyScale mismatch");

    if (overRangeChannels > 0)
    {
      out.setOverRange(IntegerArray.sumRange(counts, counts.length - overRangeChannels, counts.length));
      IntegerArray.fillRange(counts, counts.length - overRangeChannels, counts.length, 0);
    }

    if (underRangeChannels > 0)
    {
      out.setUnderRange(IntegerArray.sumRange(counts, 0, underRangeChannels));
      IntegerArray.fillRange(counts, 0, underRangeChannels, 0);
    }

    out.setValidRange(underRangeChannels, out.size() - overRangeChannels);
    setAttr(out);
    return out;
  }

  private void setAttr(Spectrum out)
  {
    if (timestamp != null)
      out.setAttribute(SpectrumAttributes.TIMESTAMP, this.timestamp);
    out.setEnergyScale(energyScale);
  }

  @Override
  public SpectrumBuilder overRange(int channels)
  {
    if (channels < 0)
    {
      throw new IllegalArgumentException("Number of over range channels cannot be negative");
    }
    this.overRangeChannels = channels;
    return this;
  }

  @Override
  public SpectrumBuilder underRange(int channels)
  {
    if (channels < 0)
    {
      throw new IllegalArgumentException("Number of under range channels cannot be negative");
    }
    this.underRangeChannels = channels;
    return this;
  }

}
