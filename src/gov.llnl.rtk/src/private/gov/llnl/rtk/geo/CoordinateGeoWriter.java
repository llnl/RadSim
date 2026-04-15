// --- file: gov/llnl/rtk/geo/CoordinateGeoWriter.java ---
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.llnl.rtk.geo;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.geo.CoordinateGeo;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author seilhan3
 */
@Internal
public class CoordinateGeoWriter extends ObjectWriter<CoordinateGeo>
{

  public CoordinateGeoWriter()
  {
    super(Options.NONE, "coordinate", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, CoordinateGeo object) throws WriterException
  {
    double height = object.getHeight();
    if (!Double.isNaN(height))
      attributes.add("height", object.getHeight());
    attributes.add("latitude", object.getLatitude()).set(ObjectWriter.DOUBLE_FORMAT, "%+.6f");
    attributes.add("longitude", object.getLongitude()).set(ObjectWriter.DOUBLE_FORMAT, "%+.6f");
  }

  @Override
  public void contents(CoordinateGeo object) throws WriterException
  {
  }

}
