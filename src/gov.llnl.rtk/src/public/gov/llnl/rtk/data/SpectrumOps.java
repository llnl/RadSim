// --- file: gov/llnl/rtk/data/SpectrumOps.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.math.DoubleArray;

/**
 *
 * @author nelson85
 */
public class SpectrumOps
{
  static public Spectrum<?> addAssign(Spectrum<?> a, Spectrum<?> b)
  {
    if (a instanceof IntegerSpectrum && b instanceof IntegerSpectrum)
    {
      return ((IntegerSpectrum) a).addAssign((IntegerSpectrum) b);
    }
    if (a instanceof DoubleSpectrum)
    {
      return ((DoubleSpectrum) a).addAssign(b);
    }
    throw new UnsupportedOperationException("Unable to handle unknown spectrum type");
  }
  
  static public Spectrum scaleCounts(Spectrum spectrum, double s)
  {
    Object u = spectrum.toArray();
    if (u instanceof double[])
    {
      DoubleArray.multiplyAssign((double[]) u, s);
    }
    else
      throw new UnsupportedOperationException("Can not scale "+u.getClass()+ " spectra");
    return spectrum;
  }
 
}
