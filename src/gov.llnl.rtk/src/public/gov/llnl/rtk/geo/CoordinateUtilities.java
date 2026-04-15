// --- file: gov/llnl/rtk/geo/CoordinateUtilities.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

import static gov.llnl.math.DoubleUtilities.sqr;

/**
 *
 * @author nelson85
 */
public class CoordinateUtilities
{
  private static double length(CoordinateECR coord)
  {
    double x = coord.getX();
    double y = coord.getY();
    double z = coord.getZ();

    return Math.sqrt(x * x + y * y + z * z);
  }

  public static CoordinateECR.RTheta calculateRtheta(CoordinateECR src, CoordinateECR det)
  {
    //angle between source and detector ECR vectors
    //double alpha = Math.acos((src.x*det.x+src.y*det.y+src.z*det.z)/src.length()/det.length());
    double cosAlpha = (src.getX() * det.getX() + src.getY() * det.getY() + src.getZ() * det.getZ()) / length(src) / length(det);
    double sinAlpha = Math.sqrt(1 - cosAlpha * cosAlpha);
    //distance between source and detector
    double R = Math.sqrt(measureDistanceSquared(src, det));
    //incident angle between src and detector by law of sines
    //    double Theta = Math.asin(src.length() * Math.sin(alpha) / R);
    double Theta = Math.asin(length(src) * sinAlpha / R);
    return new CoordinateECR.RTheta(R, Theta);
  }

  public static double measureDistanceSquared(CoordinateECR coord1, CoordinateECR coord2)
  {
    return sqr(coord1.getX() - coord2.getX()) + sqr(coord1.getY() - coord2.getY()) + sqr(coord1.getZ() - coord2.getZ());
  }

  /**
   * Find the midpoint of the line segment from a to b. Then find the point n
   * meters away from that midpoint in the direction orthogonal to the line
   * segment.
   *
   * We define orthogonal as 90 degrees clockwise from the direction of travel.
   * So, the point returned would be to the right when traveling from a to b.
   *
   * A negative n means that the point should be to the left.
   *
   * @param a The start of the line segment.
   * @param b The end of the line segment.
   * @param n The distance from the line segment.
   * @return The point orthogonally n meters away from the line segment.
   */
  public static CoordinateGeo findOrthogonalPoint(CoordinateGeo a, CoordinateGeo b, double n)
  {
    Geodetic geod = new GeodeticWGS84();
    CoordinateECR aPrime = geod.convert(a);
    CoordinateECR bPrime = geod.convert(b);
    double[] pointA =
    {
      aPrime.getX(), aPrime.getY()
    };
    double[] pointB =
    {
      bPrime.getX(), bPrime.getY()
    };
    double[] pointQ = findOrthogonalPoint(pointA, pointB, n);
    CoordinateECR ecr = new CoordinateECRImpl(pointQ[0], pointQ[1], 0);
    return geod.convert(ecr);
  }

  /**
   * Helper method for the findOrthogonalPoint that returns a CoordinateGeo.
   *
   * @param a Start point.
   * @param b End point.
   * @param n Distance from line segment.
   * @return The point on the orthogonal line.
   */
  public static double[] findOrthogonalPoint(double[] a, double[] b, double n)
  {
    if (a.length != 2)
    {
      throw new RuntimeException("a.length=" + a.length + "!=2");
    }
    if (b.length != 2)
    {
      throw new RuntimeException("b.length=" + b.length + "!=2");
    }
    double ax = a[0];
    double ay = a[1];
    double bx = b[0];
    double by = b[1];
    // Find the midpoint.
    double px = (ax + bx) / 2;
    double py = (ay + by) / 2;
    // See https://stackoverflow.com/questions/133897/how-do-you-find-a-point-at-a-given-perpendicular-distance-from-a-line
    double delta_x = bx - ax;
    double delta_y = by - ay;
    double distance = Math.sqrt((delta_x * delta_x) + (delta_y * delta_y));
    double normalized_delta_x = delta_x / distance;
    double normalized_delta_y = delta_y / distance;
    // Make sure the point is to the right, when looking towards the endpoint, b.
    double slope = delta_y / delta_x;
    double qx;
    double qy;
    if (slope >= 0)
    {
      // delta_y and delta_x are the same sign.
      // x coordinate will be in the same direction as b.
      // y coordinate will be in the opposite direction as b.
      qx = px + n * normalized_delta_x;
      qy = py - n * normalized_delta_y;
    }
    else
    {
      // delta_y and delta_x are opposite signs.
      // x coordinate will be in the opposite direction as b.
      // y coordinate will be in the same direction as b.
      qx = px - n * normalized_delta_x;
      qy = py + n * normalized_delta_y;
    }
    double[] pointQ =
    {
      qx, qy
    };
    return pointQ;
  }

  //https://www.movable-type.co.uk/scripts/latlong.html
  public static double calculateBearing(CoordinateGeo c1, CoordinateGeo c2)
  {
    double c1Lat = Math.toRadians(c1.getLatitude());
    double c1Lon = Math.toRadians(c1.getLongitude());
    double c2Lat = Math.toRadians(c2.getLatitude());
    double c2Lon = Math.toRadians(c2.getLongitude());

    double deltaLon = c2Lon - c1Lon;
    double y = Math.sin(deltaLon) * Math.cos(c2Lat);
    double x = Math.cos(c1Lat) * Math.sin(c2Lat) - Math.sin(c1Lat) * Math.cos(c2Lat) * Math.cos(deltaLon);

    return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
  }

  public static CoordinateGeo getIntermediatePoint(CoordinateGeo c1, CoordinateGeo c2, double f)
  {
    Geodetic geo = new SurfaceWGS84();
    double d = geo.computeAngularDistance(c1, c2);

    double c1Lat = Math.toRadians(c1.getLatitude());
    double c1Lon = Math.toRadians(c1.getLongitude());
    double c2Lat = Math.toRadians(c2.getLatitude());
    double c2Lon = Math.toRadians(c2.getLongitude());

    double sinD = Math.sin(d);
    double a = Math.sin((1 - f) * d) / sinD;
    double b = Math.sin(f * d) / sinD;
    double x = a * Math.cos(c1Lat) * Math.cos(c1Lon) + b * Math.cos(c2Lat) * Math.cos(c2Lon);
    double y = a * Math.cos(c1Lat) * Math.sin(c1Lon) + b * Math.cos(c2Lat) * Math.sin(c2Lon);
    double z = a * Math.sin(c1Lat) + b * Math.sin(c2Lat);
    double w = Math.toDegrees(Math.atan2(z, Math.sqrt(x * x + y * y)));
    double l = Math.toDegrees(Math.atan2(y, x));
    return new CoordinateGeoImpl(w, l, (c1.getHeight() + c2.getHeight()) / 2.0);
  }

  public static CoordinateGeo getPointAtDistanceBearing(CoordinateGeo c1, double bearing, double distance)
  {
    double R = 6378137.0; // radius of earh

    double d = distance / R;
    double radBearing = Math.toRadians(bearing);
    double lambda = Math.toRadians(c1.getLongitude());
    double phi = Math.toRadians(c1.getLatitude());
    double phi2 = Math.asin(Math.sin(phi) * Math.cos(d) + Math.cos(phi) * Math.sin(d) * Math.cos(radBearing));
    double lambda2 = lambda + Math.atan2(Math.sin(radBearing) * Math.sin(d) * Math.cos(phi),
            Math.cos(d) - Math.sin(phi) * Math.sin(phi2));
    return new CoordinateGeoImpl(Math.toDegrees(phi2),
            Math.toDegrees(lambda2),
            c1.getHeight());
  }

}
