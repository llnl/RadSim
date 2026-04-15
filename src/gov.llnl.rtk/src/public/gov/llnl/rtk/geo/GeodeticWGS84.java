// --- file: gov/llnl/rtk/geo/GeodeticWGS84.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

/**
 * World Geodetic System 1984 (WGS84)
 *
 * @author nelson85
 */
public class GeodeticWGS84 extends GeodeticBase
{
  static final Ellipsoid ELLIPSOID = new Ellipsoid(
          6378137.0,
          298.257223563);
  static final Geodetic INSTANCE = new GeodeticWGS84();

  public GeodeticWGS84()
  {
    super(ELLIPSOID, false);
  }

   public static Geodetic getInstance()
  {
    return INSTANCE;
  }
   
  static public void main(String[] args)
  {
    CoordinateGeo C1 = new CoordinateGeoImpl(37.6819, 121.7680, 10);
    Geodetic geod = new GeodeticWGS84();
    CoordinateECR C2 = geod.convert(C1);
    CoordinateGeo C1p = geod.convert(C2);

    System.out.println(C1);
    System.out.println(C2);
    System.out.println(C1p);
  }
}
