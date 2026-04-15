// --- file: gov/llnl/rtk/data/RadiationProcessorInputImpl.java ---
package gov.llnl.rtk.data;

/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */


import gov.llnl.rtk.data.Navigation;
import gov.llnl.utility.UUIDUtilities;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import gov.llnl.rtk.data.RadiationMeasurement;
import gov.llnl.rtk.data.RadiationProcessorInput;

/**
 * Implementation of DataProcessor.Input for use with DataProcessor.
 *
 * @author nelson85
 */
public class RadiationProcessorInputImpl implements RadiationProcessorInput
{
  private static final long serialVersionUID = UUIDUtilities.createLong("DataProcessorInputImpl-v1");
  final Instant timestamp;
  final Navigation navigation;
  final List<RadiationMeasurement> measurements;


  /**
   * Create a new data processor input.
   *
   * @param timestamp
   * @param measurements
   * @param navigation
   */
  public RadiationProcessorInputImpl(
          Instant timestamp,
          Navigation navigation,
          Collection<? extends RadiationMeasurement> measurements)
  {
    this.timestamp = timestamp;
    if (measurements != null)
      this.measurements = new ArrayList<>(measurements);
    else
      this.measurements = new ArrayList<>();
    this.navigation = navigation;

  }

  @Override
  public Instant getTimestamp()
  {
    return timestamp;
  }

  /**
   * @return the navigation
   */
  @Override
  public Navigation getNavigation()
  {
    return navigation;
  }

  @Override
  public List<RadiationMeasurement> getMeasurements()
  {
    return Collections.unmodifiableList(measurements);
  }

}
