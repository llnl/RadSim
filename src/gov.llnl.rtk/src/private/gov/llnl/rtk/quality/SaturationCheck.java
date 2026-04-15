// --- file: gov/llnl/rtk/quality/SaturationCheck.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.GammaMeasurement;
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
@Reader.Declaration(pkg = RtkPackage.class, name = "saturationCheck",
        referenceable = true)
public class SaturationCheck implements QualityCheck<GammaMeasurement>
{
  double maximumCountRate = Double.MAX_VALUE;

  @Override
  public boolean execute(FaultSet fault, GammaMeasurement measurement)
  {
    if (measurement.getSample().getRate() < maximumCountRate)
      return true;
    fault.add(new FaultImpl(FaultLevel.ERROR, FaultCategory.SATURATION, 
            "Count Rate in Satuation Region " + measurement.getSample().getRate(), 
            measurement, true));
    return false;
  }

  public double getMaximumCountRate()
  {
    return maximumCountRate;
  }

  @Reader.Element(name = "maximumCountRate")
  public void setMaximumCountRate(double maximumCountRate)
  {
    this.maximumCountRate = maximumCountRate;
  }

}
