// --- file: gov/llnl/rtk/quality/LowEnergyCheck.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.math.IntegerArray;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.GammaMeasurement;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.impl.FaultImpl;
import gov.llnl.rtk.quality.FaultCategory;
import gov.llnl.rtk.quality.FaultLevel;
import gov.llnl.rtk.quality.FaultSet;
import gov.llnl.rtk.quality.QualityCheck;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author seilhan3
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "lowEnergyCheck",
        referenceable=true)
public class LowEnergyCheck implements QualityCheck<GammaMeasurement>
{
  private double fractionBelowLLD;
  private int lldChannel;

  @Override
  public boolean execute(FaultSet faultSet, GammaMeasurement measurement)
  {
    IntegerSpectrum spectrum = (IntegerSpectrum) measurement.getSample();
    int totalCounts = (int) spectrum.getCounts();
    int countsBelowLLD = IntegerArray.sumRange(spectrum.toArray(), 0, lldChannel);
    double frac = (double) countsBelowLLD / totalCounts;
    if (frac < fractionBelowLLD)
      return true;
    faultSet.add(new FaultImpl(FaultLevel.ERROR, FaultCategory.LOW_ENERGY_NOISE, "too many counts below lld " + frac, this, true));
    return false;
  }

  public double getFractionBelowLLD()
  {
    return fractionBelowLLD;
  }

  @Reader.Element(name = "fractionBelowLLD")
  public void setFractionBelowLLD(double fractionBelowLLD)
  {
    this.fractionBelowLLD = fractionBelowLLD;
  }

  public int getLldChannel()
  {
    return lldChannel;
  }

  @Reader.Element(name = "lldChannel")
  public void setLldChannel(int lldChannel)
  {
    this.lldChannel = lldChannel;
  }
}
