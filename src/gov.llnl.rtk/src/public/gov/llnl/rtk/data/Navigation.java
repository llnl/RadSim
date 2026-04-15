// --- file: gov/llnl/rtk/data/Navigation.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.geo.CoordinateGeo;
import gov.llnl.rtk.geo.Geodetic;
import gov.llnl.utility.UUIDUtilities;
import java.io.Serializable;
import java.time.Instant;

/**
 *
 * @author nelson85
 */
public class Navigation implements Serializable, CoordinateGeo
{
  private static final long serialVersionUID = UUIDUtilities.createLong("Navigation-v2");

  static final public Instant NO_TIMESTAMP_AVAILABLE = null;
  static final public double NO_SPEED_AVAILABLE = -1;
  static final public double NO_ALTITUDE_AVAILABLE = -Double.MAX_VALUE;
  static final public double NO_ORIENTATION_AVAILABLE = -Double.MAX_VALUE;
  static final public double NO_BEARING_AVAILABLE = -Double.MAX_VALUE;
  static final public double NO_GPS_AVAILABLE = -Double.MAX_VALUE;
  // Only some of these fields may be supplied depending on the technology.

  /**
   * timestamp associated with this navigation, may be different than than
   * spectrum
   */
  Instant timestamp = NO_TIMESTAMP_AVAILABLE;  // units?
  /**
   * latitude from this location measurement
   */
  double latitude = NO_GPS_AVAILABLE;
  /**
   * longitude from this location measurement
   */
  double longitude = NO_GPS_AVAILABLE;
  /**
   * orientation of the detector at the associated timestamp
   */
  double orientation = NO_ORIENTATION_AVAILABLE;
  /**
   * bearing of the detector at the associated timestamp
   */
  double bearing = NO_BEARING_AVAILABLE;
  /**
   * speed of the detector at the associated timestamp
   */
  double speed = NO_SPEED_AVAILABLE;
  /**
   * altitude of the detector at the associated timestamp
   */
  double altitude = NO_ALTITUDE_AVAILABLE;

  // FIXME add uncertainty or other needed information
  public Navigation()
  {
  }

  static public Navigation createFromLatLong(Instant timestamp, double lat, double lon)
  {
    Navigation location = new Navigation();
    location.timestamp = timestamp;
    location.latitude = lat;
    location.longitude = lon;
    return location;
  }

  static public Navigation createFromTimestamp(Instant timestamp)
  {
    Navigation location = new Navigation();
    location.timestamp = timestamp;
    return location;
  }

  static public Navigation createFromLatLongAlt(Instant timestamp, double lat, double lon, double alt)
  {
    Navigation location = new Navigation();
    location.timestamp = timestamp;
    location.altitude = alt;
    location.latitude = lat;
    location.longitude = lon;
    return location;
  }

  static public Navigation createFromLatLong(double lat, double lon)
  {
    Navigation location = new Navigation();
    location.latitude = lat;
    location.longitude = lon;
    return location;
  }

  static public Navigation createFromLatLongAlt(double lat, double lon, double alt)
  {
    Navigation location = new Navigation();
    location.altitude = alt;
    location.latitude = lat;
    location.longitude = lon;
    return location;
  }

  public boolean equals(Navigation other)
  {
    return (other.timestamp == timestamp
            && other.altitude == altitude
            && other.latitude == latitude
            && other.longitude == longitude
            && other.orientation == orientation
            && other.speed == speed);
  }

  public double getOrientation()
  {
    return orientation;
  }

  public void setOrientation(double orientation)
  {
    this.orientation = orientation;
  }

  public double getBearing()
  {
    return bearing;
  }

  public void setBearing(double bearing)
  {
    this.bearing = bearing;
  }

  public double getSpeed()
  {
    return speed;
  }

  public void setSpeed(double speed)
  {
    this.speed = speed;
  }

  @Override
  public double getLatitude()
  {
    return latitude;
  }

  public void setLatitude(double latitude)
  {
    this.latitude = latitude;
  }

  @Override
  public double getLongitude()
  {
    return longitude;
  }

  public void setLongitude(double longitude)
  {
    this.longitude = longitude;
  }

  public double getAltitude()
  {
    return altitude;
  }

  public void setAltitude(double altitude)
  {
    this.altitude = altitude;
  }

  public Instant getTimestamp()
  {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp)
  {
    this.timestamp = timestamp;
  }

  public boolean isPositionValid()
  {
    return this.latitude != Navigation.NO_GPS_AVAILABLE;
  }

  public boolean isTimestampValid()
  {
    return this.timestamp != Navigation.NO_TIMESTAMP_AVAILABLE;
  }

  @Override
  public double getHeight()
  {
    return Double.NaN;
  }

  @Override
  public Geodetic getGeodetic()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
