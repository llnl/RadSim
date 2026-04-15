// --- file: gov/llnl/rtk/geo/CoordinateECRImpl.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public class CoordinateECRImpl implements Serializable, CoordinateECR
{
  private double x;
  private double y;
  private double z;

  public CoordinateECRImpl(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public String toString()
  {
    return String.format("(x=%f, y=%f, z=%f)",
            this.x, this.y, this.z);
  }

  public static void main(String[] args)
  {
    CoordinateGeo srcGEO = new CoordinateGeoImpl(37.6819, 121.7681);
    // target is chosen to be displaced from source by .01 degree N am .01 degree E
    CoordinateGeo tarGEO = new CoordinateGeoImpl(37.6919, 121.7781, 1000.0);

    // we also define coordinate of target projection onto ground
    CoordinateGeo tarGEOearthLevel = new CoordinateGeoImpl(37.6919, 121.7781);
    GeodeticWGS84 geodetic = new GeodeticWGS84();
    CoordinateECR srcECR = geodetic.convert(srcGEO);
    CoordinateECR tarECR = geodetic.convert(tarGEO);
    CoordinateECR tarECRearthLevel = geodetic.convert(tarGEOearthLevel);
    //earth level distance between source and target (secant, not arch)
    double S = Math.sqrt(CoordinateUtilities.measureDistanceSquared(srcECR, tarECRearthLevel));
    //height of target above ground (we set it to 1000., but for full consistency we recalculate)
    double h = Math.sqrt(CoordinateUtilities.measureDistanceSquared(tarECR, tarECRearthLevel));
    // distance between source and target
    double R = Math.sqrt(CoordinateUtilities.measureDistanceSquared(srcECR, tarECR));
    //theta by law of cosines: s^2 = R^2+h^2 - 2Rhcos(theta)
    double theta = Math.acos((R * R + h * h - S * S) / 2 / R / h);

    RTheta rtheta = CoordinateUtilities.calculateRtheta(srcECR, tarECR);
    System.out.println("RTheta.R=" + rtheta.R + ", R=" + R);
    System.out.println("RTheta.Theta=" + rtheta.Theta + ", theta=" + theta);
    //get the following consistent result (high presision accurace for R, discrepancy in 3d digit in theta):
    //RTheta.R=1734.9958746573634, R=1734.9958746573634
    //RTheta.Theta=0.9589480577209686, theta=0.9564051055242958
  }

  @Override
  public double getX()
  {
    return x;
  }

  @Override
  public double getY()
  {
    return y;
  }

  @Override
  public double getZ()
  {
    return z;
  }
}
