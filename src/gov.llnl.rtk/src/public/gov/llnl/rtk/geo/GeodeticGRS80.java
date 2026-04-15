// --- file: gov/llnl/rtk/geo/GeodeticGRS80.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

/**
 * Geodetic Reference System 1980 (GRS80)
 *
 * @author nelson85
 */
public class GeodeticGRS80 extends GeodeticBase
{
  static final Ellipsoid ELLIPSOID = new Ellipsoid(
          6378137.0,
          298.257222101);

  public GeodeticGRS80()
  {
    super(ELLIPSOID, false);
  }
}
