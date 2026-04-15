// --- file: gov/llnl/rtk/impl/NeutronMeasurementImpl.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.impl;

import gov.llnl.rtk.data.DoublePulseCount;
import gov.llnl.rtk.data.IntegerPulseCount;
import gov.llnl.rtk.data.NeutronMeasurement;
import gov.llnl.rtk.data.PulseCount;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.rtk.data.RadiationSensor;

/**
 *
 * @author nelson85
 */
public class NeutronMeasurementImpl
        extends RadiationMeasurementImpl<PulseCount>
        implements NeutronMeasurement
{
  private static final long serialVersionUID = UUIDUtilities.createLong("NeutronMeasurement-v1");

  public NeutronMeasurementImpl(RadiationSensor sensor, PulseCount counts)
  {
    super(sensor, counts);
  }

  public NeutronMeasurementImpl(NeutronMeasurement other)
  {
    super(other.getSensor(), duplicate(other.getSample()));
  }

  private static PulseCount duplicate(PulseCount sample)
  {
    if (sample instanceof IntegerPulseCount)
      return new IntegerPulseCount((IntegerPulseCount) sample);
    if (sample instanceof DoublePulseCount)
      return new DoublePulseCount(sample.getCounts(),
              sample.getLiveTime(),
              sample.getRealTime());
    throw new RuntimeException("Unknown PulseCount type");
  }

}
