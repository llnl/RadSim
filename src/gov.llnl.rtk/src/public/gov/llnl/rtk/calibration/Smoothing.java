// --- file: gov/llnl/rtk/calibration/Smoothing.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.Spectrum;

/**
 *
 * @author nelson85
 */
public class Smoothing
{
  double factor;
  public DoubleSpectrum data = null;

  public Smoothing(double alpha)
  {
    factor = 1.0 - alpha;
  }

  public void initialize(Spectrum<?> spectrum)
  {
    data = new DoubleSpectrum(spectrum);
  }

  public void add(Spectrum<?> spectrum)
  {
    if (data == null)
    {
      data = new DoubleSpectrum();
      data.resize(spectrum.size());
    }

    if (data.size() != spectrum.size())
      throw new RuntimeException("Bad length " + data.size() + "!=" + spectrum.size());

    data.multiplyAssign(factor);
    data.addAssign(spectrum);
//    double[] sample = spectrum.toDoubles();
//    double[] dataArray = data.toDoubles();

//    // Add new sample
//    for (int i = 0; i < sample.length; i++)
//    {
//      dataArray[i] = factor * dataArray[i] + sample[i];
//    }
  }

  public void clear()
  {
    data = null;
  }
}
