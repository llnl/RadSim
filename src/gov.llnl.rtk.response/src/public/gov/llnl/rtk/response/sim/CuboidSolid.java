// --- file: gov/llnl/rtk/response/sim/Cuboid.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

import gov.llnl.math.DoubleArray;
import gov.llnl.math.euclidean.MutableVector3;
import gov.llnl.math.random.UniformRandom;
import gov.llnl.rtk.response.deposition.CuboidUtility;

/**
 * Represents a normalized 3D cuboid (rectangular box) for geometric
 * computations and ray tracing.
 *
 * <p>
 * The cuboid is defined by three dimensions (width, height, depth), which are
 * always normalized such that:
 * <ul>
 * <li><b>X</b> is the largest dimension,</li>
 * <li><b>Y</b> is the second largest,</li>
 * <li><b>Z</b> is the smallest.</li>
 * </ul>
 * This normalization ensures consistent orientation and feature extraction for
 * simulation and modeling.
 * </p>
 *
 * <p>
 * The class provides methods for:
 * <ul>
 * <li>Randomizing dimensions,</li>
 * <li>Testing point inclusion,</li>
 * <li>Ray-surface intersection (for ray tracing),</li>
 * <li>Solid angle calculations for each face,</li>
 * <li>Extracting geometric features for downstream analysis or machine
 * learning.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Usage:</b> Used as a core shape in Monte Carlo simulations, response
 * modeling, and analytic validation.
 * </p>
 *
 * @author nelson85
 * @author hangal1
 */
public class CuboidSolid implements Shape
{
  /**
   * Dimensions of the cuboid (normalized). Represents the width (x), height
   * (y), and depth (z) of the cuboid.
   */
  public MutableVector3 dimensions = new MutableVector3();
  public double diag = 0;

  /**
   * Sets the dimensions of the cuboid and normalizes them.
   *
   * The dimensions are reordered such that the largest dimension is x, the
   * second largest is y, and the smallest is z.
   *
   * @param x The x-dimension of the cuboid.
   * @param y The y-dimension of the cuboid.
   * @param z The z-dimension of the cuboid.
   */
  public void setDimensions(double x, double y, double z)
  {
    diag = Math.sqrt(x * x + y * y + z * z);
    if (x < y)
    {
      double d = x;
      x = y;
      y = d;
    }
    if (x < z)
    {
      double d = x;
      x = z;
      z = d;
    }
    if (y < z)
    {
      double d = y;
      y = z;
      z = d;
    }
    
    // We only work in normalized units
    dimensions.x = x / diag;
    dimensions.y = y / diag;
    dimensions.z = z / diag;
  }

  /**
   * Generates random dimensions for the cuboid using a uniform random generator
   * and sets those dimensions. The largest face is ordered as XY, and the
   * smallest is YZ.
   *
   * @param ur The uniform random generator used to create random dimensions.
   */
  @Override
  public void drawDimensions(UniformRandom ur)
  {
    // Create a random cuboid to project onto
    double x = ur.draw(0.1, 1);
    double y = ur.draw(0.1, 1);
    double z = ur.draw(0.1, 1);
    
    // We are going to order the faces such that the largest face is always XY
    // and the smallest is YZ
    setDimensions(x, y, z);
  }

  /**
   * Checks whether a given point is inside the cuboid.
   *
   * @param v The point to check, represented as a 3D vector.
   * @return True if the point is inside the cuboid, false otherwise.
   */
  @Override
  public boolean inside(MutableVector3 v)
  {
    if (v.x < -this.dimensions.x / 2 || v.x > this.dimensions.x / 2)
      return false;
    if (v.y < -this.dimensions.y / 2 || v.y > this.dimensions.y / 2)
      return false;
    return !(v.z < -this.dimensions.z / 2 || v.z > this.dimensions.z / 2);
  }

  /**
   * Checks if a value is between two bounds.
   *
   * @param d1 Lower bound.
   * @param d2 Value to check.
   * @param d3 Upper bound.
   * @return True if d2 is between d1 and d3; otherwise, false.
   */
   boolean between(double d1, double d2, double d3)
  {
    return d1 <= d2 && d2 <= d3;
  }

  /**
   * Computes the intersection points of a ray with the cuboid.
   *
   * @param distance Array to store the distances to intersection points.
   * @param source The starting point of the ray.
   * @param direction The direction vector of the ray.
   * @return The number of intersection points.
   */
  @Override
  public int intercept(double[] distance, MutableVector3 source, MutableVector3 direction)
  {
    int hit = 0;
    double dx = this.dimensions.x / 2;
    double dy = this.dimensions.y / 2;
    double dz = this.dimensions.z / 2;

    direction.normalize();
    double len;
    double t1, t2;
    if (direction.x != 0)
    {
      len = (dx - source.x) / direction.x;
      t1 = source.y + len * direction.y;
      t2 = source.z + len * direction.z;
      if (len > 0 && between(-dy, t1, dy) && between(-dz, t2, dz))
        distance[hit++] = len;

      len = (-dx - source.x) / direction.x;
      t1 = source.y + len * direction.y;
      t2 = source.z + len * direction.z;
      if (len > 0 && between(-dy, t1, dy) && between(-dz, t2, dz))
        distance[hit++] = len;
    }

    if (direction.y != 0)
    {
      len = (dy - source.y) / direction.y;
      t1 = source.x + len * direction.x;
      t2 = source.z + len * direction.z;
      if (len > 0 && between(-dx, t1, dx) && between(-dz, t2, dz))
        distance[hit++] = len;

      len = (-dy - source.y) / direction.y;
      t1 = source.x + len * direction.x;
      t2 = source.z + len * direction.z;
      if (len > 0 && between(-dx, t1, dx) && between(-dz, t2, dz))
        distance[hit++] = len;
    }
    if (direction.z != 0)
    {
      len = (dz - source.z) / direction.z;
      t1 = source.x + len * direction.x;
      t2 = source.y + len * direction.y;
      if (len > 0 && between(-dx, t1, dx) && between(-dy, t2, dy))
        distance[hit++] = len;

      len = (-dz - source.z) / direction.z;
      t1 = source.x + len * direction.x;
      t2 = source.y + len * direction.y;
      if (len > 0 && between(-dx, t1, dx) && between(-dy, t2, dy))
        distance[hit++] = len;
    }

    // Make sure the order of the vectors ia always correct
    if (hit > 1 && distance[0] > distance[1])
    {
      double d = distance[0];
      distance[0] = distance[1];
      distance[1] = d;
    }
    return hit;
  }

  /**
   * Computes the solid angle subtended by the ** face of the cuboid at a given
   * point where ** can be XY, YZ, XZ.
   *
   * The size of the XY comes from dimensions. Use offset to select the face.
   *
   * @param v The point from which the solid angle is computed.
   * @param offset The offset of the face along the z/x/y-axis.
   * @return The solid angle subtended by the face.
   */
  public double computeSolidAngleXY(MutableVector3 v, double offset)
  {
    double depth = Math.abs(offset - v.z);
    double v1 = -this.dimensions.x / 2 - v.x;
    double v2 = -this.dimensions.y / 2 - v.y;
    double v3 = this.dimensions.x + v1;
    double v4 = this.dimensions.y + v2;
    return f(depth, v2, v1) - f(depth, v4, v1) - f(depth, v2, v3) + f(depth, v4, v3);
  }

  /**
   * Get the solid angle for an XZ plane.
   *
   * The size of the XZ comes from dimensions. Use offset to select the face.
   *
   * @param v is the location of the source.
   * @param offset is the Y coordinate. (use +/- getDimensions.Y()/2 to select a
   * face on the cuboid.)
   *
   * @return the solid angle.
   */
  public double computeSolidAngleXZ(MutableVector3 v, double offset)
  {
    double depth = Math.abs(offset - v.y);
    double v1 = -this.dimensions.x / 2 - v.x;
    double v2 = -this.dimensions.z / 2 - v.z;
    double v3 = this.dimensions.x + v1;
    double v4 = this.dimensions.z + v2;
    return f(depth, v2, v1) - f(depth, v4, v1) - f(depth, v2, v3) + f(depth, v4, v3);
  }

  /**
   * Get the solid angle for an YZ plane.
   *
   * The size of the YZ comes from dimensions. Use offset to select the face.
   *
   * @param v is the location of the source.
   * @param offset is the X coordinate. (use +/- getDimensions.X()/2 to select a
   * face on the cuboid.)
   *
   * @return the solid angle.
   */
  public double computeSolidAngleYZ(MutableVector3 v, double offset)
  {
    double depth = Math.abs(offset - v.x);
    double v1 = -this.dimensions.y / 2 - v.y;
    double v2 = -this.dimensions.z / 2 - v.z;
    double v3 = this.dimensions.y + v1;
    double v4 = this.dimensions.z + v2;
    return f(depth, v2, v1) - f(depth, v4, v1) - f(depth, v2, v3) + f(depth, v4, v3);
  }

  /**
   * Computes the partial solid angle subtended by a rectangle corner as seen
   * from a point.
   *
   * <p>
   * This is a helper for computing the total solid angle subtended by a
   * rectangular face, using the inclusion-exclusion principle over its corners.
   * The formula is derived from:
   * <ul>
   * <li>Oosterom & Strackee, "The Solid Angle of a Plane Triangle," IEEE Trans.
   * Biomed. Eng., 1983.</li>
   * <li>See also:
   * https://en.wikipedia.org/wiki/Solid_angle#Rectangular_aperture</li>
   * </ul>
   * </p>
   *
   * @param z Distance from the rectangle plane to the observation point.
   * @param x, y Offsets from the rectangle center to the corner.
   * @return Partial solid angle in steradians.
   */
  static double f(double z, double x, double y)
  {
    return Math.atan2(x * y, z * Math.sqrt(z * z + x * x + y * y));
  }

//  /**
//   * Computes a set of geometric features for the cuboid and a given emission
//   * point.
//   * <p>
//   * Features include normalized dimensions, direction cosines, solid angles,
//   * and entry probabilities, all designed to be in [0, 1] for use in ML or
//   * statistical modeling.
//   * </p>
//   *
//   * @param features Array to store results (may be null to allocate a new
//   * array).
//   * @param emit Emission point (relative to cuboid center).
//   * @return Array of computed features.
//   */
//  @Override
//  public double[] computeFeatures(double[] features, MutableVector3 emit)
//  {
//    if (features == null)
//      features = new double[15];
//    if (emit.x>0 || emit.y>0 || emit.z>0)
//      throw new RuntimeException("Bad emitter");
//    
//    CuboidUtility.fillCuboidFeatures(features, 0,
//            emit.x, emit.y, emit.z, 
//            this.dimensions.x, this.dimensions.y, this.dimensions.z, 
//            1.0);
//
//    if (DoubleArray.isNaN(features))
//    {
//      System.out.println("D: " + features[0] + " " + features[1] + " " + features[2]);
//      System.out.println("E: " + features[3] + " " + features[4] + " " + features[5]);
//      System.out.println("E0: " + emit.x + " " + emit.y + " " + emit.z);
//      throw new RuntimeException("Feature error");
//    }
//
//    // We can try other derived features from here
//    return features;
//  }

}

/*
  # Appendix: General Approach for Solid Angle Computation

  The methods `computeSolidAngleXY`, `computeSolidAngleXZ`, and
  `computeSolidAngleYZ` compute the solid angle subtended by a rectangular face
  of a cuboid as seen from an arbitrary point in space. This calculation is
  fundamental in radiation transport, geometric optics, and Monte Carlo
  simulation.

  The standard approach uses the inclusion-exclusion principle, summing the
  contributions from each corner of the rectangle. The helper function:

      static public double f(double z, double x, double y)
      {
        return Math.atan2(x * y, z * Math.sqrt(z * z + x * x + y * y));
      }

  computes the partial solid angle contribution from a single corner, based on
  the geometry of the rectangle and the observer's position.

  The total solid angle Omega subtended by a rectangle (with sides parallel to
  the axes, centered at the origin, and the observation point at (x0, y0, z0))
  is given by:

      Omega = sum_{i=1}^4 [(-1)^(i+1) * atan2((x_i - x0)*(y_i - y0),
             z0 * sqrt(z0^2 + (x_i - x0)^2 + (y_i - y0)^2))]

  where (x_i, y_i) are the coordinates of the four corners of the rectangle.
  This formula is implemented in code by evaluating the helper function
  `f(z, x, y)` at each corner, with appropriate signs, and summing the results
  according to the inclusion-exclusion principle.

  **References:**
  - Oosterom, A. van, and J. Strackee. "The solid angle of a plane triangle."
    IEEE Transactions on Biomedical Engineering, vol. BME-30, no. 2, 1983,
    pp. 125-126.
  - J. J. Conway. "Solid angle subtended by a rectangle."
    (University of Maryland, 2006).
  - Wikipedia contributors. "Solid angle — Rectangular aperture."
    Wikipedia, The Free Encyclopedia.

  **Note:** This approach is widely used in computational physics and
  engineering for calculating view factors, radiative transfer, and geometric
  efficiency in detector simulations.
*/
