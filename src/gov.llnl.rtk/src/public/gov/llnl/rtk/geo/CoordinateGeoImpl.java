// --- file: gov/llnl/rtk/geo/CoordinateGeoImpl.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

import gov.llnl.utility.annotation.Internal;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
@Internal
public final class CoordinateGeoImpl implements Serializable, CoordinateGeo
{
  final Geodetic geodetic;
  private final double latitude;
  private final double longitude;
  private final double height;

  public CoordinateGeoImpl(double latitude, double longitude)
  {
    this.geodetic = null;
    this.latitude = latitude;
    this.longitude = longitude;
    this.height = Double.NaN;
  }

  public CoordinateGeoImpl(double latitude, double longitude, double height)
  {
    this.geodetic = null;
    this.latitude = latitude;
    this.longitude = longitude;
    this.height = height;
  }

  public CoordinateGeoImpl(CoordinateGeo coord)
  {
    this.geodetic = null;
    this.latitude = coord.getLatitude();
    this.longitude = coord.getLongitude();
    this.height = coord.getHeight();
  }

  public CoordinateGeoImpl(Geodetic geo, double latitude, double longitude, double height)
  {
    this.geodetic = geo;
    this.latitude = latitude;
    this.longitude = longitude;
    this.height = height;
  } 
  
  CoordinateGeoImpl()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public String toString()
  {
    return String.format("(latitude=%f, longitude=%f, height=%f)",
            this.latitude, this.longitude, this.height);
  }

  @Override
  public double getLatitude()
  {
    return latitude;
  }

  @Override
  public double getLongitude()
  {
    return longitude;
  }

  @Override
  public double getHeight()
  {
    return height;
  }

  /**
   * @return the geodetic
   */
  @Override
  public Geodetic getGeodetic()
  {
    return geodetic;
  }
}
