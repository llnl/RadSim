// --- file: gov/llnl/rtk/data/NeutronMeasurement.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.impl.NeutronMeasurementImpl;

/**
 *
 * @author nelson85
 */
public interface NeutronMeasurement
        extends RadiationMeasurement<PulseCount>
{
  static NeutronMeasurement of(RadiationSensor sensor, PulseCount sample)
  {
    return new NeutronMeasurementImpl(sensor, sample);
  }
}
