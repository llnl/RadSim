// --- file: gov/llnl/rtk/view/SensorFaceRectangular.java ---
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
import gov.llnl.utility.xml.bind.ReaderInfo;

@ReaderInfo(SensorFaceRectangularReader.class)
public class SensorFaceRectangular implements SensorFace
{
  double width, height;

  public double getWidth()
  {
    return width;
  }

  public double getHeight()
  {
    return height;
  }
  Vector3 origin;
  Versor orientation;

  public SensorFaceRectangular()
  {
  }

  public SensorFaceRectangular(double width, double height, Vector3 origin, Versor orientation)
  {
    this.width = width;
    this.height = height;
    this.origin = origin;
    this.orientation = orientation;
  }

  @Override
  public Vector3 getOrigin()
  {
    return origin;
  }

  @Override
  public Versor getOrientation()
  {
    return orientation;
  }

  @Override
  public double computeSolidAngle(Vector3 v)
  {
    Vector3 v2 = Vector3Ops.subtract(v, origin);
    Vector3 v3 = orientation.inv().rotate(v2);

    double vx = v3.getX();
    double vy = v3.getY();
    double vz = v3.getZ();
    if (vx < 0)
      return 0;

    // Integration of surface for equation
    //   getX/(getX^2+getY^2+getZ^2)^(3/2) over the surface
    return f(vx, vy - width / 2, vz - height / 2)
            - f(vx, vy + width / 2, vz - height / 2)
            - f(vx, vy - width / 2, vz + height / 2)
            + f(vx, vy + width / 2, vz + height / 2);
  }

  static public double f(double x, double y, double z)
  {
    return Math.atan(y * z / x / Math.sqrt(x * x + y * y + z * z));
  }

  @Override
  public double getArea()
  {
    return width * height;
  }

  @Override
  public String toString()
  {
    return String.format("SensorFaceRectangular(%.2f,%.2f, %s)", this.width, this.height, origin.toString());
  }

  public String auditSolidAngle(Vector3 v)
  {
    StringBuilder sb=new StringBuilder();
     Vector3 v2 = Vector3Ops.subtract(v, origin);
    Vector3 v3 = orientation.inv().rotate(v2);

    sb.append("   delta to origin ").append(v2).append("\n");
    sb.append("   relative to face ").append(v3).append("\n");
    sb.append("   solid angle ").append(computeSolidAngle(v)).append("\n");
    return sb.toString();
  }

}
