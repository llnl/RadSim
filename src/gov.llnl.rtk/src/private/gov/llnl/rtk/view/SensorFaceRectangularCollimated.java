// --- file: gov/llnl/rtk/view/SensorFaceRectangularCollimated.java ---
/* 
 * Copyright (c) 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.euclidean.Versor;

public class SensorFaceRectangularCollimated extends SensorFaceRectangular
{

  double extentSide, extentTop;

  public SensorFaceRectangularCollimated(
          double width, double height,
          Vector3 origin, Versor orientation,
          double extentSide, double extentTop)
  {
    super(width, height, origin, orientation);
    this.extentSide = extentSide;
    this.extentTop = extentTop;
  }

  public SensorFaceRectangularCollimated()
  {
  }

  @Override
  public double computeSolidAngle(Vector3 v)
  {
    Vector3 v2 = Vector3Ops.subtract(v, origin);
    Vector3 v3 = orientation.inv().rotate(v2);

    double vx = v3.getX();
    double vy = v3.getY();
    double vz = v3.getZ();

    double[] ey = computeShadow(vx, vy, width, extentSide);
    double[] ez = computeShadow(vx, vz, height, extentTop);

    if (ey == null || ez == null)
      return 0;

    // Integration of surface for equation
    //   getX/(getX^2+getY^2+getZ^2)^(3/2) over the surface
    return f(vx, ey[0], ez[0])
            - f(vx, ey[1], ez[0])
            - f(vx, ey[0], ez[1])
            + f(vx, ey[1], ez[1]);
  }

  public String auditSolidAngle(Vector3 v)
  {
    Vector3 v2 = Vector3Ops.subtract(v, origin);
    Vector3 v3 = orientation.inv().rotate(v2);

    double vx = v3.getX();
    double vy = v3.getY();
    double vz = v3.getZ();

    double[] ey = computeShadow(vx, vy, width, extentSide);
    double[] ez = computeShadow(vx, vz, height, extentTop);
    double sa = f(vx, ey[0], ez[0])
            - f(vx, ey[1], ez[0])
            - f(vx, ey[0], ez[1])
            + f(vx, ey[1], ez[1]);
    StringBuilder sb = new StringBuilder();
    sb.append("vx=").append(vx).append("\n");
    sb.append("vy=").append(vy).append("\n");
    sb.append("vz=").append(vz).append("\n");
    sb.append("ey0=").append(ey[0]).append("\n");
    sb.append("ey1=").append(ey[1]).append("\n");
    sb.append("ez0=").append(ez[0]).append("\n");
    sb.append("ez1=").append(ez[1]).append("\n");
    sb.append("sa=").append(sa).append("\n");
    return sb.toString();
  }

  static public double[] computeShadow(
          double range,
          double offset,
          double width,
          double extent)
  {
    double y1 = -offset - width / 2;
    double y2 = -offset + width / 2;
    if (extent == 0 || Math.abs(offset) <= width / 2)
      return new double[]
      {
        y1, y2
      };

    if (range <= extent)
      return null;

    // Compute the shadow
    if (y1 > 0)
    {
      double s = y1 * (1 + extent / (range - extent));
      if (s > y2)
        return null;
      return new double[]
      {
        s, y2
      };
    }
    if (y2 < 0)
    {
      double s = y2 * (1 + extent / (range - extent));
      if (s < y1)
        return null;
      return new double[]
      {
        y1, s
      };
    }
    throw new RuntimeException("what?");
  }

}
