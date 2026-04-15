// --- file: gov/llnl/rtk/data/IntegerSpectraList.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleArray;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 *
 * @author nelson85
 */
@WriterInfo(SpectraListWriter.class)
public class IntegerSpectraList extends ArrayList<IntegerSpectrum> implements SpectraList<IntegerSpectrum>, Serializable
{
  public IntegerSpectraList()
  {
  }

  public IntegerSpectraList(Collection<? extends Spectrum> measuredSpectra)
  {
    super(measuredSpectra.size());
    for (Spectrum spec:measuredSpectra)
    {
      if (spec instanceof IntegerSpectrum)
        this.add((IntegerSpectrum) spec);
      else
        this.add(new IntegerSpectrum(spec));
    }
  }

  public double[] getGrossCounts()
  {
    return DoubleArray.toPrimitives(this.stream()
            .map(IntegerSpectrum::getCounts)
            .collect(Collectors.toList()));
  }

  public double[] getCountRate()
  {
    return DoubleArray.toPrimitives(this.stream()
            .map(IntegerSpectrum::getRate)
            .collect(Collectors.toList()));
  }

  @Override
  public int[][] toArray()
  {
    int[][] out = new int[this.size()][];
    for (int i = 0; i < size(); ++i)
      out[i] = get(i).toArray();
    return out;
  }

  @Override
  public Class<IntegerSpectrum> getSpectrumClass()
  {
    return IntegerSpectrum.class;
  }

  public IntegerSpectraList decimate(int samples)
  {
    IntegerSpectraList output = new IntegerSpectraList();
    int count = samples;
    IntegerSpectrum current = null;
    for (IntegerSpectrum spectrum : this)
    {
      if (current == null)
        current = new IntegerSpectrum(spectrum);
      else
        current.addAssign(spectrum);
      count--;
      if (count == 0)
      {
        output.add(current);
        count = samples;
        current = null;
      }
    }
    return output;
  }
}
