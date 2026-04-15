// --- file: gov/llnl/rtk/data/DoubleSpectraList.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of SpectraList for DoubleSpectrum.
 *
 * @author nelson85
 */
@ReaderInfo(DoubleSpectraListReader.class)
@WriterInfo(SpectraListWriter.class)
public class DoubleSpectraList
        extends ArrayList<DoubleSpectrum>
        implements SpectraList<DoubleSpectrum>
{
  public final static long serialVersionUID = -152046824508101937L;

   public DoubleSpectraList()
  {
    super();
  }

  public DoubleSpectraList(Collection<DoubleSpectrum> items)
  {
    super(items);
  }

  public double[][] toDoubleArray()
  {
    double[][] out = new double[this.size()][];
    for (int i = 0; i < size(); ++i)
      out[i] = get(i).toArray();
    return out;
  }

  @Override
  public Class<DoubleSpectrum> getSpectrumClass()
  {
    return DoubleSpectrum.class;
  }

  public DoubleSpectraList decimate(int samples)
  {
    DoubleSpectraList output = new DoubleSpectraList();
    int count = samples;
    DoubleSpectrum current = null;
    for (DoubleSpectrum spectrum : this)
    {
      if (current == null)
        current = new DoubleSpectrum(spectrum);
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
