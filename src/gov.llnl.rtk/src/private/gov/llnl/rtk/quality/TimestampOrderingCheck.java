// --- file: gov/llnl/rtk/quality/TimestampOrderingCheck.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.RadiationMeasurement;
import gov.llnl.rtk.impl.FaultImpl;
import gov.llnl.rtk.quality.FaultCategory;
import gov.llnl.rtk.quality.FaultLevel;
import gov.llnl.rtk.quality.FaultSet;
import gov.llnl.rtk.quality.QualityCheck;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.xml.bind.Reader;
import java.time.Instant;

/**
 *
 * @author seilhan3
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "timestampOrderingCheck",
        referenceable = true)
public class TimestampOrderingCheck implements QualityCheck<RadiationMeasurement>
{
  FaultImpl OUT_OF_ORDER = new FaultImpl(FaultLevel.ERROR, FaultCategory.INVALID_TIMESTAMP, "Timestamp order problem", this, true);
  Instant lastDate = null;
  long timestampValidRange = 300;  // default 5 minutes

  @Override
  public boolean execute(FaultSet faultSet, RadiationMeasurement measurement)
  {
    if (measurement == null)
      return false;

    boolean rc = true;
    // Check for out of date conditions (Sigma requirement)
    if (lastDate != null) // Note: hot start needs to have lasttimestamp==0
    {
      if (measurement.getSample().getStartTime().isBefore(lastDate))
      {
        faultSet.add(OUT_OF_ORDER);
        rc = false;
      }

      long dt = measurement.getSample().getStartTime().toEpochMilli() - lastDate.toEpochMilli();
      if (Math.abs(dt / 1000.0) > this.timestampValidRange)
      {
        // reset is handeled elsewhere
        faultSet.add(new FaultImpl(FaultLevel.RESET, // Indicates that a reset is request
                FaultCategory.DATA_GAP,
                "Invalid timestamp range " + dt + " > " + this.timestampValidRange, this, true));
        rc = false;
      }
    }
    if (measurement.getSample() != null)
      lastDate = measurement.getSample().getStartTime();
    return rc;
  }

  public long getTimestampValidRange()
  {
    return timestampValidRange;
  }

  @Reader.Element(name = "timestampValidRange")
  public void setTimestampValidRange(long timestampValidRange)
  {
    this.timestampValidRange = timestampValidRange;
  }

}
