// --- file: gov/llnl/rtk/geo/CoordinateGeoReader.java ---
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.llnl.rtk.geo;

import gov.llnl.rtk.geo.CoordinateGeoImpl;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.geo.CoordinateGeo;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author seilhan3
 */
@Internal

@Reader.Declaration(pkg = RtkPackage.class, name = "coordinate",
        cls = CoordinateGeo.class,
        order = Reader.Order.ALL,
        referenceable = true)
@Reader.AnyAttribute(processContents = Reader.ProcessContents.Skip)
public class CoordinateGeoReader extends ObjectReader<CoordinateGeo>
{

  @Override
  public CoordinateGeo start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    String hs = attributes.getValue("height");
    String lon = attributes.getValue("longitude");
    String lat = attributes.getValue("latitude");

    double height = 0;
    double latitude = 0;
    double longitude = 0;
    if (hs != null)
      height = Double.parseDouble(hs.trim());
    if (lat != null)
      latitude = Double.parseDouble(lat.trim());
    if (lat != null)
      longitude = Double.parseDouble(lon.trim());
    return new CoordinateGeoImpl(latitude, longitude, height);
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }


}
