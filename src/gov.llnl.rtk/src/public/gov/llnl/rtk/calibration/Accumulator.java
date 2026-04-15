// --- file: gov/llnl/rtk/calibration/Accumulator.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.MathExceptions;
import gov.llnl.rtk.data.IntegerSpectrum;

/**
 *
 * @author nelson85
 */
public class Accumulator
{
  int sz;
  int counter;
  IntegerSpectrum data = null;

  public Accumulator(int samples)
  {
    sz = samples;
  }

  public boolean full()
  {
    return counter >= sz;
  }

  public boolean add(IntegerSpectrum sample)
  {
    try
    {
      if (data == null)
        data = new IntegerSpectrum(sample);
      else
        data.addAssign(sample);

      counter++;
      return counter >= sz;
    }
    catch (MathExceptions.SizeException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  // empty the buffer, called when full
  public IntegerSpectrum get()
  {
    counter = 0;
    IntegerSpectrum out = this.data;
    this.data = null;
    return out;
  }
}
