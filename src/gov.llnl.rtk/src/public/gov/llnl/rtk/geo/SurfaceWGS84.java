// --- file: gov/llnl/rtk/geo/SurfaceWGS84.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

/**
 *
 * @author seilhan3
 */
public class SurfaceWGS84 extends GeodeticBase
{
  public SurfaceWGS84()
  {
    super(GeodeticWGS84.ELLIPSOID, true);
  }

}
