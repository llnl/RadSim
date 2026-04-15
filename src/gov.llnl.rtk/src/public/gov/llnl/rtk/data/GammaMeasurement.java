// --- file: gov/llnl/rtk/data/GammaMeasurement.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.impl.GammaMeasurementImpl;

/**
 * This is a ghost class. Consider removing.
 *
 * @author nelson85
 */
public interface GammaMeasurement extends RadiationMeasurement<Spectrum>
{

  static GammaMeasurement of(RadiationSensor sensor, Spectrum sample)
  {
    return new GammaMeasurementImpl(sensor, sample);
  }
}
