// --- file: gov/llnl/rtk/data/SpectraList.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleArray;
import gov.llnl.utility.annotation.Matlab;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.util.List;

/**
 *
 * @author nelson85
 * @param <T>
 */
@ReaderInfo(SpectraListReader.class)
@WriterInfo(SpectraListWriter.class)
public interface SpectraList<T extends SpectrumBase> extends List<T>
{
  Class<T> getSpectrumClass();

  @Matlab
  default Object[] getAttributes(String name)
  {
    Object[] out = new Object[size()];
    int i = 0;
    for (T s : this)
    {
      out[i++] = s.getAttribute(name);
    }
    return out;
  }

  @Matlab
  default double[] getAttributesDouble(String name)
  {
    double[] out = new double[size()];
    int i = 0;
    for (T s : this)
    {
      out[i++] = (double) s.getAttribute(name);
    }
    return out;
  }

  @Matlab
  default String[] getTitles()
  {
    String[] out = new String[size()];
    int i = 0;
    for (T s : this)
    {
      out[i++] = s.getTitle();
    }
    return out;
  }

  default double[] getMean()
  {
    double[] out = null;
    double lt = 0;
    for (T s : this)
    {
      if (out == null)
        out = s.toDoubles();
      else
        DoubleArray.addAssign(out, s.toDoubles());
      lt += s.getLiveTime();
    }
    if (lt > 0)
      DoubleArray.divideAssign(out, lt);
    return out;
  }

  default double[] getSum()
  {
    double[] out = null;
    for (T s : this)
    {
      if (out == null)
        out = s.toDoubles();
      else
        DoubleArray.addAssign(out, s.toDoubles());
    }
    return out;
  }
}
