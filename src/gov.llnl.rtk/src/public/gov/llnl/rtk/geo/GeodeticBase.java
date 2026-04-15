// --- file: gov/llnl/rtk/geo/GeodeticBase.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

/**
 *
 * @author nelson85
 */
public class GeodeticBase implements Geodetic
{
  final Ellipsoid ellipsoid;
  final boolean surface;

  protected GeodeticBase(Ellipsoid eps, boolean isSurface)
  {
    this.ellipsoid = eps;
    this.surface = isSurface;
  }

  @Override
  public CoordinateECR convert(CoordinateGeo geo)
  {
    // Formulas from https://en.wikipedia.org/wiki/Geographic_coordinate_conversion
    double height = 0;
    if (!surface)
      height = geo.getHeight();
    double phi = Math.toRadians(geo.getLatitude());
    double lambda = Math.toRadians(geo.getLongitude());
    double sphi = Math.sin(phi);
    double chi = Math.sqrt(1 - ellipsoid.E2 * sphi * sphi);
    double nphi = ellipsoid.A / chi;
    double r = (nphi + height) * Math.cos(phi);
    double x = r * Math.cos(lambda);
    double y = r * Math.sin(lambda);
    double z = (nphi * (1 - ellipsoid.E2) + height) * sphi;

    return new CoordinateECRImpl(x, y, z);

  }

  @Override
  public CoordinateGeo convert(CoordinateECR coord)
  {
    double X2 = coord.getX() * coord.getX();
    double Y2 = coord.getY() * coord.getY();
    double Z2 = coord.getZ() * coord.getZ();

    double a = ellipsoid.A;
    double b = ellipsoid.B;
    double e2 = ellipsoid.E2;
    double e4 = ellipsoid.E2 * ellipsoid.E2;

    double r2 = X2 + Y2;
    double r = Math.sqrt(r2);
    double E2 = a * a - b * b;
    double F = 54 * b * b * Z2;
    double G = r2 + (1 - e2) * Z2 - e2 * E2;
    double C = e4 * F * r2 / G / G / G;
    double S = Math.pow(1 + C + Math.sqrt(C * C + 2 * C), 1.0 / 3.0);
    double M = (S + 1 / S + 1) * G;
    double P = F / 3 / M / M;
    double Q = Math.sqrt(1 + 2 * e4 * P);
    double r0 = -(P * e2 * r) / (1 + Q)
            + Math.sqrt(0.5 * a * a * (1 + 1 / Q) - P * (1 - e2) * Z2 / Q / (1 + Q) - 0.5 * P * r2);
    double W = (r - e2 * r0);
    double U = Math.sqrt(W * W + Z2);
    double V = Math.sqrt(W * W + (1 - e2) * Z2);
    double Z0 = b * b * coord.getZ() / a / V;
    double h = U * (1 - b * b / a / V);
    double phi = Math.atan((coord.getZ() + ellipsoid.EP2 * Z0) / r);
    double theta = Math.atan2(coord.getY(), coord.getX());
    return new CoordinateGeoImpl(Math.toDegrees(phi),
            Math.toDegrees(theta), h);
  }

  @Override
  public double computeDistance(CoordinateGeo pos1, CoordinateGeo pos2)
  {
    CoordinateECR pos1ECR = this.convert(pos1);
    CoordinateECR pos2ECR = this.convert(pos2);
    return Math.sqrt(CoordinateUtilities.measureDistanceSquared(pos1ECR, pos2ECR));
  }

  @Override
  public double computeAngularDistance(CoordinateGeo pos1, CoordinateGeo pos2)
  {
    return computeDistance(pos1, pos2) / ellipsoid.A;
  }

}
