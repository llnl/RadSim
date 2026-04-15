// --- file: gov/llnl/rtk/data/RadiationData.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.Expandable;
import java.time.Instant;

/**
 *
 * @author seilhan3
 * @param <Type>
 */
public interface RadiationData<Type> extends Expandable
{
  /**
   * Get the title of the spectrum.
   *
   * @return the title or null if not defined.
   */
  String getTitle();

  /**
   * Get the live time for the measurement. Units are in seconds. For most
   * systems the live time is simply given as the real time. In those cases the
   * true live time is equal to to the real time minus the counts times the dead
   * time.
   *
   * @return the live time measurement.
   */
  double getLiveTime();

  /**
   * Get the real time for the measurement. Units are in seconds.
   *
   * @return the real time of the measurement.
   */
  double getRealTime();

  /**
   * Get the starting time for this spectrum. The timestamp should be the start
   * time for this spectral data. Timestamps are optional. It will return the
   * null if there is no timestamp for this measurement.
   *
   * @return a date for the start of the measurement.
   */
  Instant getStartTime();

  /**
   * Get the end time for this measurement. This will be calculated from the
   * start time and the real time. It will return null if there is no timestamp
   * associated with this measurement.
   *
   * @return a date for the end of the measurement.
   */
  Instant getEndTime();

  /**
   * Get the count rate for the measurement.
   *
   * @return the count rate computed using the live time.
   */
  double getRate();

  /**
   * Get the total counts in the spectrum.
   *
   * @return
   */
  double getCounts();

  /** 
   * Convert counts to a list of doubles.
   * 
   * @return a new array holding the pulse counts.
   * Warning: This sometimes creates copies and sometimes passes the original
   */ 
  double[] toDoubles();
}
