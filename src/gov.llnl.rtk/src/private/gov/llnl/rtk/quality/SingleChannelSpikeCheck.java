// --- file: gov/llnl/rtk/quality/SingleChannelSpikeCheck.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.GammaMeasurement;
import gov.llnl.rtk.data.Spectrum;
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
@Reader.Declaration(pkg = RtkPackage.class, name = "singleChannelSpikeCheck",
        referenceable=true)
public class SingleChannelSpikeCheck implements QualityCheck<GammaMeasurement>
{
  FaultImpl SPIKE_FAULT = new FaultImpl(FaultLevel.ERROR, FaultCategory.INVALID_CHANNEL_COUNT_SPIKE, "Single Channel Spike", this, true);
  double singleChannelFraction = Double.MAX_VALUE;

  @Override
  public boolean execute(FaultSet fault, GammaMeasurement measurement)
  {
    Spectrum spectrum = measurement.getSample();
    int counts = (int) spectrum.getCounts();
    for (double d : spectrum.toDoubles())
    {
      if (d / counts > singleChannelFraction)
      {
        fault.add(SPIKE_FAULT);
        return false;
      }
    }
    return true;
  }

  public double getSingleChannelFraction()
  {
    return singleChannelFraction;
  }

  @Reader.Element(name = "singleChannelFraction")
  public void setSingleChannelFraction(double singleChannelFraction)
  {
    this.singleChannelFraction = singleChannelFraction;
  }

//  @Override
//  public Class<GammaMeasurement> getObjectType()
//  {
//    return GammaMeasurement.class;
//  }
}
