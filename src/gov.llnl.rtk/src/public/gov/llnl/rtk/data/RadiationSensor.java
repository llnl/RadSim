// --- file: gov/llnl/rtk/data/RadiationSensor.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.impl.RadiationSensorImpl;

/**
 *
 * @author seilhan3
 */
public interface RadiationSensor
{
  SensorType getSensorType();
          
  /**
   * Get the index on the detector into the outputs. Gamma and neutrons
   * detectors have independent indexing. Thus there is gamma detector 0 and a
   * neutron detector 0. This may need to be revisited if we have detectors with
   * both gamma and neutron sensitivity.
   *
   * @return
   */
  int getSensorIndex();

  /** Compare two detectors by index and type.
   *
   * This is primarily used as a BiPredicate functor when matching
   * detectors for radiation processors.
   *
   * @param obj1
   * @param obj2
   * @return
   */
  public static boolean equal(RadiationSensor obj1, RadiationSensor obj2)
  {
    if (obj1 == null || obj2 == null)
      return false;
    return obj1.getSensorIndex() == obj2.getSensorIndex() && 
            obj1.getSensorType() == obj2.getSensorType();
  }

  /**
   * Create a default implementation with a defined detector index.
   *
   * @param index
   * @return
   */
  static RadiationSensor of(SensorType type, int index)
  {
    return new RadiationSensorImpl(type, index);
  }
}
