// --- file: gov/llnl/rtk/response/deposition/CylinderUtility.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.DoubleArray;

/**
 *
 * @author nelson85
 */
public class CylinderUtility
{
  static public double[] fillCylinderFeatures(double[] features, int index,
          double x, double y, double z,
          double diameter, double height, double diag)
  {
    if (features == null)
      features = new double[10];

    // Work in unit space for the model
    diameter /= diag;
    height /= diag;
    x /= diag;
    y /= diag;
    z /= diag;
    
    double radius = diameter / 2;
  
    // Compute the solid angle to all faces assuming emitter is in bottom quadant
    double B_barrel_near = computeSolidAngleBarrel(x, y, z, radius, radius, height);
    double B_barrel_far = computeSolidAngleBarrel(x, y, z, -radius, radius, height);
    double B_endcap_bottom = computeSolidAngleEndCap(x, y, z, height / 2, radius);
    double B_endcap_top = computeSolidAngleEndCap(x, y, z, -height / 2, radius);

    // Determine endcap facing the emitter based on emitter position
    double forwardDiskAngle = 0;
    if (z > height / 2)
    {
      forwardDiskAngle = B_endcap_top;
      B_endcap_top = 0;
    }
    else if (z < -height / 2)
    {
      forwardDiskAngle = B_endcap_bottom;
      B_endcap_bottom = 0;
    }
    double forwardBarrelAngle = B_barrel_near;
    B_barrel_near = 0;

    // Assign geometric dimensions of the cylinder
    features[0] = radius;
    features[1] = height;

    // Normalized direction vector from emitter to center of cylinder
    double normEmit = Math.sqrt(x * x + y * y + z * z);
    double emit_r = Math.hypot(x, y);
    features[2] = Math.abs(emit_r / normEmit);
    features[3] = Math.abs(z / normEmit);

    // The probabilty of entering a face relative to the others
    double totalForwardAngle = forwardBarrelAngle + forwardDiskAngle;
    features[4] = forwardBarrelAngle / totalForwardAngle;
    features[5] = forwardDiskAngle / totalForwardAngle;

    // The cosine of the entry angle with respect to each face 
    double totalBackwardAngle = B_barrel_near + B_barrel_far + B_endcap_bottom + B_endcap_top;
    features[6] = B_barrel_near / totalBackwardAngle;
    features[7] = B_barrel_far / totalBackwardAngle;
    features[8] = B_endcap_bottom / totalBackwardAngle;
    features[9] = B_endcap_top / totalBackwardAngle;

    if (DoubleArray.isNaN(features))
    {
      System.out.println("radius " + radius);
      System.out.println("height " + height);
      System.out.println("emit " + x + " " + y + " " + z + " ");
      throw new RuntimeException("NaN in features");
    }
    return features;
  }

  /**
   * Approximates the solid angle subtended by the barrel (lateral surface) of
   * the cylinder as seen from a given point.
   * <p>
   * This method uses an inclusion-exclusion principle over the barrel
   * rectangle, projected in cylindrical coordinates.
   * </p>
   *
   * @param x
   * @param y
   * @param z
   * @param offset the offset along the radial direction (positive for near,
   * negative for far side)
   * @param radius
   * @param height
   * @return the approximate solid angle subtended by the barrel, in steradians
   */
  static public double computeSolidAngleBarrel(double x, double y, double z,
          double offset, double radius, double height)
  {
    double D_xy = Math.hypot(x, y);
    double depth = Math.abs(offset - D_xy);
    double v1 = -radius;
    double v2 = -height / 2 - z;
    double v3 = 2 * radius + v1;
    double v4 = height + v2;
    return g(depth, v2, v1) - g(depth, v4, v1) - g(depth, v2, v3) + g(depth, v4, v3);
  }

  /**
   * Helper function for solid angle calculations.
   * <p>
   * Computes the partial solid angle subtended by a rectangle corner as seen
   * from a point, using the formula
   * {@code atan2(x * y, z * sqrt(z^2 + x^2 + y^2))}.
   * </p>
   *
   * @param z distance along the axis from the observation point to the
   * rectangle plane
   * @param x offset along the first axis from the rectangle center to the
   * corner
   * @param y offset along the second axis from the rectangle center to the
   * corner
   * @return the partial solid angle in steradians
   */
  static public double g(double z, double x, double y)
  {
    return Math.atan2(x * y, z * Math.sqrt(z * z + x * x + y * y));
  }

  /**
   * Approximates the solid angle subtended by an endcap (circular face) of the
   * cylinder as seen from a given point.
   * <p>
   * This method uses an analytic approximation for the solid angle of a disk,
   * with special handling for the case where the observation point is very
   * close to the disk plane.
   * </p>
   *
   * @param x
   * @param y
   * @param z
   * @param offset the z-coordinate of the endcap (use {@code +/- height/2} for
   * top/bottom)
   * @param radius
   * @return the approximate solid angle subtended by the endcap, in steradians
   */
  static public double computeSolidAngleEndCap(double x, double y, double z, double offset, double radius)
  {

    double r = radius;
    //double dz = v.z - this.height / 2.0;
    double dz = z - offset;

    double D_xy = Math.hypot(x, y); // same as sqrt(dx*dx + dy*dy)
    double H = Math.abs(dz);

    double eps = 1e-8;

    // Special case: Observer very close to disk plane
    if (H < eps)
    {
      if (D_xy <= r)
      {
        return 2 * Math.PI;
      }
      else
      {
        return 0.0;
      }
    }

    double distanceToCenter = Math.sqrt(D_xy * D_xy + dz * dz);
    double omega;

    if (D_xy > r)
    {
      double cosAlpha = H / distanceToCenter;
      double baseAngle = 2 * Math.PI * (1 - H / Math.sqrt(H * H + r * r));
      omega = baseAngle * cosAlpha;
    }
    else
    {
      omega = 2 * Math.PI * (1 - H / Math.sqrt(H * H + r * r + D_xy * D_xy));
    }

    return Math.max(omega, 0.0);
  }

  /**
   * Helper function for solid angle calculations of a disk.
   * <p>
   * Computes the partial solid angle subtended by a disk as seen from a point,
   * using the formula {@code atan2(r, z * sqrt(z^2 + r^2))}.
   * </p>
   *
   * @param z distance along the axis from the observation point to the disk
   * plane
   * @param r radius of the disk
   * @return the partial solid angle in steradians
   */
  static public double h(double z, double r)
  {
    return Math.atan2(r, z * Math.sqrt(z * z + r * r));
  }

}
