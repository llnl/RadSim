// --- file: gov/llnl/rtk/labeling/ExpectedWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.labeling;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.geo.CoordinateGeo;
import gov.llnl.rtk.labeling.Expected;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author seilhan3
 */
public class ExpectedWriter extends ObjectWriter<Expected>
{
  public ExpectedWriter()
  {
    super(Options.NONE, "expected", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Expected object) throws WriterException
  {
    attributes.add("optional", object.isOptional());
    attributes.add("range", object.getRange()).set(DOUBLE_FORMAT, "%.2f");
  }

  @Override
  public void contents(Expected object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    if (object.getCoordinate() != null)
      wb.element("coordinate").contents(CoordinateGeo.class).put(object.getCoordinate());
    wb.element("startTime").putString(object.getStartTime().toString());
    wb.element("endTime").putString(object.getEndTime().toString());
    wb.element("classification").putContents(object.getClassification());
    wb.element("closestApproach").putDouble(object.getDOCA(), "%.2f");
    wb.element("note").putString(object.getNote());
    if (object.getDetectorID() != null)
      wb.element("detectorID").putContents(object.getDetectorID());
  }

}
