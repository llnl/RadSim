// --- file: gov/llnl/rtk/data/RadiationProcessorInput.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public interface RadiationProcessorInput extends Serializable
{
  static public RadiationProcessorInput of(Instant timestamp, Navigation navigation, List<RadiationMeasurement> measurements)
  {
    return new RadiationProcessorInputImpl(timestamp, navigation, measurements);
  }

  /**
   * Get the timestamp associated with the detector.
   *
   * Timestamps are optional if supplied must be in order. Depending on the
   * configuration out of order data may be ignored or result in a background
   * reset.
   *
   * @return
   */
  Instant getTimestamp();

  /**
   * Navigation record where the instrument was located at each point in time.
   *
   * Navigation is optional and may not be supplied for all data types.
   *
   * @return the navigation on null if not available.
   */
  Navigation getNavigation();

  List<RadiationMeasurement> getMeasurements();

  default <T extends RadiationMeasurement> List<T> getMeasurements(Class<T> cls)
  {
    return getMeasurements().stream()
            .filter(p -> cls.isInstance(p))
            .map(p -> cls.cast(p))
            .collect(Collectors.toList());
  }

  default List<GammaMeasurement> getGammaMeasurements()
  {
    return this.getMeasurements(GammaMeasurement.class);
  }

  default List<NeutronMeasurement> getNeutronMeasurements()
  {
    return this.getMeasurements(NeutronMeasurement.class);
  }

}
