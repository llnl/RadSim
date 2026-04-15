// --- file: gov/llnl/rtk/geo/CoordinateProjection.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

import static gov.llnl.math.DoubleUtilities.sqr;
import java.io.Serializable;

/**
 * Simple class for
 *
 * @author nelson85
 */
public class CoordinateProjection implements Serializable
{
  public double easting;
  public double northing;
  public int zone;  // 0=use current, 1-60 N, 61-120 S

  public CoordinateProjection(CoordinateProjection o)
  {
    if (o == null)
      throw new NullPointerException("cannot copy null coordinate projection");
    this.easting = o.easting;
    this.northing = o.northing;
    this.zone = o.zone;
  }

  public CoordinateProjection(int zoneId, double easting, double northing)
  {
    this.easting = easting;
    this.northing = northing;
    this.zone = zoneId;
  }

  public CoordinateProjection()
  {
  }

//  public void setPosition(double easting, double northing)
//  {
//    this.easting = easting;
//    this.northing = northing;
//  }
  public int getZone()
  {
    return zone;
  }

  public double getNorthing()
  {
    return northing;
  }

  public double getEasting()
  {
    return easting;
  }

  public void assign(int zone, double easting, double northing)
  {
    this.zone = zone;
    this.easting = easting;
    this.northing = northing;
  }

  public double measureDistanceSquared(CoordinateProjection coord)
  {
    if (coord.zone != this.zone)
    {
      // FIXME this should be an exception as distances can only be 
      // calculated in a zone.
    }
    return sqr(this.easting - coord.easting) + sqr(this.northing - coord.northing);
  }

}
