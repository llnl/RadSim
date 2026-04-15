// --- file: gov/llnl/rtk/SpectrumDoseCalculator.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.RebinUtilities;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.model.PileupCorrection;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
@ReaderInfo(SpectrumDoseReader.class)
@WriterInfo(SpectrumDoseWriter.class)
public class SpectrumDoseCalculator implements Serializable
{
  double[] doseTable;
  EnergyScale energyScale;
  PileupCorrection pileupCorrection = null;

  public double computeDoseRate(Spectrum spectrum)
  {
    double livetime = spectrum.getLiveTime();
    EnergyScale energyScale = spectrum.getEnergyScale();

    if (energyScale == null)
      throw new RuntimeException("Spectrum EnergyScale not set");

    if (this.energyScale == null)
      throw new RuntimeException("Dose calculator bins not set");

    double[] channelData = spectrum.toDoubles();
    double rate;
    double[] out;
    try
    {
      // FIXME add corrections for saturation effects
      out = RebinUtilities.rebin(channelData, energyScale.getEdges(), this.energyScale.getEdges());
    }
    catch (RebinUtilities.RebinException ex)
    {
      throw new RuntimeException("unable to rebin", ex);
    }

    rate = DoubleArray.multiplyInner(out, doseTable) / livetime;

    if (pileupCorrection != null)
      rate = rate * pileupCorrection.compute(spectrum);

    return rate;
  }

  public void setEnergyScale(EnergyScale energyScale)
  {
    if (energyScale == null)
      throw new NullPointerException("Null Energy Scale");
    this.energyScale = energyScale;
  }

  public void setDoseTable(double[] d)
  {
    this.doseTable = d;
  }

  public EnergyScale getEnergyScale()
  {
    return this.energyScale;
  }

  public double[] getDoseTable()
  {
    return this.doseTable;
  }

  public void setPileupCorrection(PileupCorrection pileupCorrection)
  {
    this.pileupCorrection = pileupCorrection;
  }

  public PileupCorrection getPileupCorrection()
  {
    return pileupCorrection;
  }
}
