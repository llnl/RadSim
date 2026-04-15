// --- file: gov/llnl/rtk/geo/SurfaceGRS80.java ---
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
public class SurfaceGRS80 extends GeodeticBase
{
  public SurfaceGRS80()
  {
    super(GeodeticGRS80.ELLIPSOID, true);
  }
}
