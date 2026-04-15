// --- file: gov/llnl/rtk/data/RadiationMeasurement.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.impl.RadiationMeasurementImpl;

/**
 * Base class for anything that required both an instrument and a measurement.
 *
 * @author nelson85
 * @param <DataType>
 */
public interface RadiationMeasurement<DataType extends RadiationData>
{
  RadiationSensor getSensor();

  DataType getSample();

  static <T extends RadiationData> RadiationMeasurement<T> of(RadiationSensor sensor, T sample)
  {
    return new RadiationMeasurementImpl<>(sensor, sample);
  }
}
