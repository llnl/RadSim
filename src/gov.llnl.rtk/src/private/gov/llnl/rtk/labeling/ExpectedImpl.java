// --- file: gov/llnl/rtk/labeling/ExpectedImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.labeling;

import gov.llnl.rtk.geo.CoordinateGeo;
import gov.llnl.rtk.labeling.Expected;
import gov.llnl.utility.UUIDUtilities;
import gov.llnl.utility.xml.bind.Reader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 *
 * @author nelson85
 */
public class ExpectedImpl implements Expected
{
  private static final long serialVersionUID = UUIDUtilities.createLong("ExpectedImpl-v1");

  String classification;
  private Instant startTime;
  private Instant endTime;
  private CoordinateGeo coord;
  private double range;
  private boolean optional;
  private String note;
  private double doca;
  private UUID detectorID = null;

  public ExpectedImpl()
  {

  }

  public ExpectedImpl(Expected e)
  {
    this.classification = e.getClassification();
    this.startTime = e.getStartTime();
    this.endTime = e.getEndTime();
    this.coord = e.getCoordinate();
    this.range = e.getRange();
    this.optional = e.isOptional();
    this.note = e.getNote();
    this.doca = e.getDOCA();
    this.detectorID = e.getDetectorID();
  }

  public ExpectedImpl(Instant startTime, Instant endTime, String note)
  {
    this.startTime = startTime;
    this.endTime = endTime;
    this.note = note;
    if (startTime.isAfter(endTime))
      throw new RuntimeException("startTime cannot be after endTime");
  }

  @Override
  public Instant getStartTime()
  {
    return startTime;
  }

  @Override
  public Instant getEndTime()
  {
    return endTime;
  }

  @Override
  public CoordinateGeo getCoordinate()
  {
    return coord;
  }

  @Override
  public double getRange()
  {
    return range;
  }

  @Override
  public String getClassification()
  {
    return classification;
  }

  @Override
  public boolean isOptional()
  {
    return optional;
  }

  @Override
  public String getNote()
  {
    return note;
  }

  @Override
  public double getDOCA()
  {
    return doca;
  }

  @Override
  public UUID getDetectorID()
  {
    return detectorID;
  }

//<editor-fold desc="loader">
  @Reader.Element(name = "classification")
  public void setClassification(String label)
  {
    this.classification = label;
  }

  @Reader.Element(name = "startTime")
  public void setStartTime(Instant startTime)
  {
    this.startTime = startTime;
  }

  @Reader.Element(name = "endTime")
  public void setEndTime(Instant endTime)
  {
    this.endTime = endTime;
  }

  @Reader.Element(name = "coordinate")
  public void setCoord(CoordinateGeo coord)
  {
    this.coord = coord;
  }

  @Reader.Attribute(name = "range")
  public void setRange(double range)
  {
    this.range = range;
  }

  @Reader.Attribute(name = "optional")
  public void setOptional(boolean optional)
  {
    this.optional = optional;
  }

  @Reader.Element(name = "note")
  public void setNote(String note)
  {
    this.note = note;
  }

  @Reader.Element(name = "closestApproach")
  public void setDOCA(double doca)
  {
    this.doca = doca;
  }

  @Reader.Element(name = "detectorID")
  public void setDetectorID(UUID uuid)
  {
    this.detectorID = uuid;

  }

//</editor-fold>
  public void addTimeSlop(long pre, long post)
  {
    startTime = startTime.minus(pre, ChronoUnit.SECONDS);
    endTime = endTime.plus(post, ChronoUnit.SECONDS);
  }

}
