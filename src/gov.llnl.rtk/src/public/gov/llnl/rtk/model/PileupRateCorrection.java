// --- file: gov/llnl/rtk/model/PileupRateCorrection.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.xml.bind.WriterInfo;
import gov.llnl.utility.xml.bind.Reader;

/**
 * This method is usable if we know the maximum observed rate in a detector.
 * Rates above the maximum correction will capped.
 */
@WriterInfo(RateCorrectionWriter.class)
@Reader.Declaration(pkg = RtkPackage.class, name = "rateCorrection")
public class PileupRateCorrection implements PileupCorrection
{
  private double maxRate;

  @Override
  public double compute(Spectrum spectrum)
  {
    // This will be accurate within a factor of 2 so long as the
    // observed rate is less and one half of the assumed maximum
    // if the maximum rate is known within 5%.
    double livetime = spectrum.getLiveTime();
    double counts = spectrum.getCounts();
    double rate = counts / livetime;
    if (rate > 0.95 * maxRate)
      rate = 0.95 * maxRate;
    return 1.0 / (1.0 - rate / maxRate);
  }

  /**
   * @param maxRate the maxRate to set
   */
  @Reader.Attribute(name = "rate", required = true)
  public void setMaxRate(double maxRate)
  {
    this.maxRate = maxRate;
  }

  public double getMaxRate()
  {
    return this.maxRate;
  }
}
