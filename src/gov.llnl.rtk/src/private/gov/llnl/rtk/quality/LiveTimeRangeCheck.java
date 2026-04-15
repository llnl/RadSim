// --- file: gov/llnl/rtk/quality/LiveTimeRangeCheck.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.RadiationData;
import gov.llnl.rtk.data.RadiationMeasurement;
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
@Reader.Declaration(pkg = RtkPackage.class, name = "liveTimeRangeCheck",
        referenceable = true)
public class LiveTimeRangeCheck implements QualityCheck<RadiationMeasurement>
{
  double minLiveTime = 0;
  double maxLiveTime = Double.MAX_VALUE;

  @Override
  public boolean execute(FaultSet faultSet, RadiationMeasurement measurement)
  {
    RadiationData sample = measurement.getSample();
    if (sample == null)
      return true;
    double liveTime = sample.getLiveTime();
    boolean rc = true;
    if (liveTime > maxLiveTime)
    {
      faultSet.add(new FaultImpl(FaultLevel.ERROR, FaultCategory.INVALID_LIVETIME, "LiveTime Too Large " + liveTime, this, true));
      rc = false;
    }
    if (liveTime < minLiveTime)
    {
      faultSet.add(new FaultImpl(FaultLevel.ERROR, FaultCategory.INVALID_LIVETIME, "LiveTime Too Small " + liveTime, this, true));
      rc = false;
    }
    return rc;
  }

  public double getMinLiveTime()
  {
    return minLiveTime;
  }

  @Reader.Element(name = "minimumLiveTime")
  public void setMinLiveTime(double minLiveTime)
  {
    this.minLiveTime = minLiveTime;
  }

  public double getMaxLiveTime()
  {
    return maxLiveTime;
  }

  @Reader.Element(name = "maximumLiveTime")
  public void setMaxLiveTime(double maxLiveTime)
  {
    this.maxLiveTime = maxLiveTime;
  }

}
