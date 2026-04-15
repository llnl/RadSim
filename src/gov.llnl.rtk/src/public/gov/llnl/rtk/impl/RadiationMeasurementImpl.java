// --- file: gov/llnl/rtk/impl/RadiationMeasurementImpl.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.impl;

import gov.llnl.rtk.data.RadiationData;
import gov.llnl.rtk.data.RadiationMeasurement;
import gov.llnl.rtk.data.RadiationSensor;

/**
 *
 * @author nelson85
 * @param <DataType>
 */
public class RadiationMeasurementImpl<DataType extends RadiationData> implements RadiationMeasurement<DataType>
{
  private final RadiationSensor sensor;
  private final DataType sample;

  public RadiationMeasurementImpl(RadiationSensor sensor, DataType sample)
  {
    this.sensor = sensor;
    this.sample = sample;
  }

  @Override
  public RadiationSensor getSensor()
  {
    return this.sensor;
  }

  @Override
  public DataType getSample()
  {
    return this.sample;
  }

}
