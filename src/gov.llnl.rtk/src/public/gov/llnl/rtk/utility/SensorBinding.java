// --- file: gov/llnl/rtk/utility/SensorBinding.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.utility;

import gov.llnl.rtk.data.RadiationMeasurement;
import gov.llnl.rtk.data.RadiationSensor;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * Utility used in RDAK when matching the order of measurements to sensors.
 * 
 * @author seilhan3
 * @param <Type>
 */
public class SensorBinding
{

  public SensorBinding(
          BiPredicate<RadiationSensor, RadiationSensor> comparison,
          BiFunction<RadiationSensor, RadiationMeasurement, RadiationMeasurement> producer
  )
  {
    this.producer = producer;
    this.comparison = comparison;
  }

  BiPredicate<RadiationSensor, RadiationSensor> comparison = RadiationSensor::equal;
  BiFunction<RadiationSensor, RadiationMeasurement, RadiationMeasurement> producer;

  public static final BiPredicate<RadiationSensor, RadiationSensor> byIndex
          = (RadiationSensor t, RadiationSensor u) -> t.getSensorIndex() == u.getSensorIndex();

  public RadiationMeasurement[] reorder(List<RadiationMeasurement> measurements, 
          List<? extends RadiationSensor> sensors)
  {
    RadiationMeasurement[] output = new RadiationMeasurement[sensors.size()];
    int i = 0;
    for (RadiationSensor detector : sensors)
    {
      for (RadiationMeasurement input : measurements)
      {
        if (input == null)
          continue;

        if (comparison.test(detector, input.getSensor()))
        {
          output[i] = producer.apply(detector, input);
          break;
        }
      }
      i++;
    }
    return output;
  }

  public RadiationSensor findSensorFor(RadiationSensor sensor, List<? extends RadiationSensor> sensors)
  {
    for (RadiationSensor det : sensors)
    {
      if (comparison.test(sensor, det))
      {
        return det;
      }
    }
    return null;
  }

}
