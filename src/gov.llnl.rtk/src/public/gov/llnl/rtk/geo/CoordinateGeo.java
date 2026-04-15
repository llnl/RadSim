// --- file: gov/llnl/rtk/geo/CoordinateGeo.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;

/**
 *
 * @author nelson85
 */
@ReaderInfo(CoordinateGeoReader.class)
@WriterInfo(CoordinateGeoWriter.class)
public interface CoordinateGeo
{
  /**
   * Create a coordinate using the default Geodetic.
   *
   * @param latitude
   * @param longitude
   * @param height
   * @return
   */
  static CoordinateGeo of(double latitude, double longitude, double height)
  {
    return new CoordinateGeoImpl(GeodeticWGS84.getInstance(), latitude, longitude, height);
  }

  /**
   * Create a coordinate using a specified Geodetic.
   *
   * @param geo
   * @param latitude
   * @param longitude
   * @param height
   * @return
   */
  static CoordinateGeo of(Geodetic geo, double latitude, double longitude, double height)
  {
    return new CoordinateGeoImpl(geo, latitude, longitude, height);
  }

  /**
   * @return the geodetic
   */
  Geodetic getGeodetic();
  
  double getHeight();

  double getLatitude();

  double getLongitude();

}
