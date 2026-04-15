/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import java.util.Arrays;

/**
 * Used when a circular beam illuminates a surface.
 * 
 * @author nelson85
 */
public class CircularBeamProfile implements BeamProfile
{
  final double thresh;
  final double[] r = new double[4];

  public CircularBeamProfile(double theta)
  {
    double t = Math.sin(theta);
    this.thresh = t * t;
  }

  @Override
  public double getFactor(
          double ux, double uy, double uz,
          double vx, double vy, double vz,
          double pu, double pv,
          double lu, double lv,
          double sx, double sy, double sz,
          double dx, double dy, double dz)
  {
    dx -= sx;
    dy -= sy;
    dz -= sz;

    lu /= 2;
    lv /= 2;
    double c1x = dx + ux * (pu + lu) + vx * (pv + lv);
    double c1y = dy + uy * (pu + lu) + vy * (pv + lv);
    double c1z = dz + uz * (pu + lu) + vz * (pv + lv);

    double c2x = dx + ux * (pu - lu) + vx * (pv + lv);
    double c2y = dy + uy * (pu - lu) + vy * (pv + lv);
    double c2z = dz + uz * (pu - lu) + vz * (pv + lv);

    double c3x = dx + ux * (pu - lu) + vx * (pv - lv);
    double c3y = dy + uy * (pu - lu) + vy * (pv - lv);
    double c3z = dz + uz * (pu - lu) + vz * (pv - lv);

    double c4x = dx + ux * (pu + lu) + vx * (pv - lv);
    double c4y = dy + uy * (pu + lu) + vy * (pv - lv);
    double c4z = dz + uz * (pu + lu) + vz * (pv - lv);

    double norm = (dx * dx + dy * dy + dz * dz);
    double f1 = (c1x * dx + c1y * dy + c1z * dz);
    double f2 = (c2x * dx + c2y * dy + c2z * dz);
    double f3 = (c3x * dx + c3y * dy + c3z * dz);
    double f4 = (c4x * dx + c4y * dy + c4z * dz);

    r[0] = (1 - f1 * f1 / (c1x * c1x + c1y * c1y + c1z * c1z) / norm);
    r[1] = (1 - f2 * f2 / (c2x * c2x + c2y * c2y + c2z * c2z) / norm);
    r[2] = (1 - f3 * f3 / (c3x * c3x + c3y * c3y + c3z * c3z) / norm);
    r[3] = (1 - f4 * f4 / (c4x * c4x + c4y * c4y + c4z * c4z) / norm);

    // less that 1 is inside, greater that 1 is outside
    Arrays.sort(r);
    if (r[0] >= thresh)
      return 0;
    if (r[3] < thresh)
      return 1;
    // r3>=1

    r[0] = Math.sqrt(r[0] / thresh);
    r[1] = Math.sqrt(r[1] / thresh);
    r[2] = Math.sqrt(r[2] / thresh);
    r[3] = Math.sqrt(r[3] / thresh);

    if (r[2] < 1)
    {
      double g1 = (r[3] - 1) / (r[3] - r[1]);
      double g2 = (r[3] - 1) / (r[3] - r[2]);
      return 1 - 0.5 * g1 * g2;
    }
    // r3>=1 r2>=1

    if (r[1] < 1)
    {
      double g1 = (1 - r[1]) / (r[3] - r[1]);
      double g2 = (1 - r[0]) / (r[2] - r[0]);
      return (g1 + g2) / 2;
    }

    // r3>=1, r2>=1, r1>=1, r0<1
    double g1 = (1 - r[0]) / (r[1] - r[0]);
    double g2 = (1 - r[0]) / (r[2] - r[0]);
    return 0.5 * g1 * g2;
  }

  static double sqr(double x)
  {
    return x * x;
  }
}
