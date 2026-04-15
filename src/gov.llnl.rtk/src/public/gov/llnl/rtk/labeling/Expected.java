// --- file: gov/llnl/rtk/labeling/Expected.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.labeling;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.geo.CoordinateGeo;
import gov.llnl.utility.xml.bind.WriterInfo;
import gov.llnl.utility.xml.bind.Reader;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "expected", impl = ExpectedImpl.class)
@WriterInfo(ExpectedWriter.class)
public interface Expected extends Serializable, Comparable<Expected>
{
  /**
   * Get the earliest time this encounter is expected.
   *
   * @return
   */
  Instant getStartTime();

  /**
   * Get the latest time this encounter can be reported.
   *
   * @return
   */
  Instant getEndTime();

  /**
   * Get the coordinate for the expected source.
   *
   * @return the coordinate or null if the inject is time based.
   */
  CoordinateGeo getCoordinate();

  /**
   * Get the range from the source in which a report is required.
   *
   * @return the range to the source or 0 if time based.
   */
  double getRange();

  /**
   * Get the expected classification.
   *
   * @return the expected classification or null if any acceptable.
   */
  String getClassification();

  /**
   * Get if this is an optional report.
   *
   * @return true if a report does not count against scoring.
   */
  boolean isOptional();

  String getNote();

  double getDOCA();

  UUID getDetectorID();

  @Override
  default public int compareTo(Expected e)
  {
    // should throw null pointer exception if either is null so don't need 
    //  to check
    return getStartTime().compareTo(e.getStartTime());
  }

}
