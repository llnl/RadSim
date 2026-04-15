// --- file: gov/llnl/rtk/response/sim/Cylinder.java ---
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
import gov.llnl.rtk.response.deposition.CylinderUtility;

/**
 * Represents a normalized right circular cylinder for geometric computations
 * and ray tracing.
 * <p>
 * This class implements the {@link Shape} interface and provides methods for:
 * <ul>
 * <li>Randomizing cylinder dimensions within specified bounds and normalizing
 * them,</li>
 * <li>Testing whether a point lies inside the cylinder,</li>
 * <li>Computing intersections of rays with the cylinder's surface (including
 * barrel and endcaps),</li>
 * <li>Calculating approximate solid angles subtended by the barrel and
 * endcaps,</li>
 * <li>Extracting normalized geometric features for use in statistical modeling
 * or machine learning.</li>
 * </ul>
 * <p>
 * All geometric quantities are normalized such that the cylinder's dimensions
 * are bounded and suitable for robust simulation. This class is intended for
 * use in Monte Carlo simulations, analytic validation, and geometric response
 * modeling.
 * </p>
 *
 * @author hangal1
 */
public class CylinderSolid implements Shape
{
  /**
   * The normalized radius of the cylinder.
   */
  public double radius;

  /**
   * The normalized height of the cylinder.
   */
  public double height;
  
  public double diag;

  /**
   * The dimensions vector (unused in most methods, included for interface
   * compatibility).
   */
  public MutableVector3 dimensions = new MutableVector3();

  /**
   * Sets the dimensions of the cylinder and normalizes them.
   * <p>
   * The radius and height are scaled such that the quantity
   * {@code sqrt(4*radius^2 + height^2)} equals 1, ensuring consistent
   * normalization for simulation and feature extraction.
   * </p>
   *
   * @param diameter the unnormalized radius of the cylinder
   * @param height the unnormalized height of the cylinder
   */
  public void setDimensions(double diameter, double height)
  {
    diag = Math.sqrt(diameter * diameter + height * height);
    if (diag == 0)
      throw new IllegalArgumentException("Zero dimension cylinder.");
    this.radius = diameter /2  / diag;
    this.height = height / diag;
  }

  /**
   * Randomizes the dimensions of the cylinder using the provided uniform random
   * number generator, and normalizes them for simulation.
   * <p>
   * The radius and height are drawn independently from the uniform interval
   * [0.1, 1.0], then normalized.
   * </p>
   *
   * @param ur the uniform random number generator used to select dimensions
   */
  @Override
  public void drawDimensions(UniformRandom ur)
  {
    // Create a random cylinder to project onto
    double radius = ur.draw(0.1, 1);
    double height = ur.draw(0.1, 1);
    setDimensions(radius*2, height);
  }

  /**
   * Determines whether a given point lies inside the cylinder.
   * <p>
   * The check is performed in cylindrical coordinates: the point must be within
   * the radius in the x-y plane, and within the height along the z-axis.
   * </p>
   *
   * @param v the point to test, as a 3D vector
   * @return {@code true} if the point is inside the cylinder; {@code false}
   * otherwise
   */
  @Override
  public boolean inside(MutableVector3 v)
  {
    if (v == null)
      return false; // Defensive: null is not inside the cylinder

    // Check if the point is inside the cylinder
    double xyDistanceSquared = v.x * v.x + v.y * v.y;
    if (xyDistanceSquared > this.radius * this.radius)
    {
      return false;
    }
    return !(v.z < -this.height / 2 || v.z > this.height / 2);
  }

  /**
   * Computes the distances from a source point to all intersection points with
   * the cylinder's surfaces, along a specified direction.
   * <p>
   * Finds all positive intersection distances for rays with the barrel and
   * endcaps of the cylinder. All intersection distances are written into the
   * provided {@code distance} array. The array must be preallocated with
   * sufficient space for up to four results.
   * </p>
   *
   * @param distance array to store intersection distances (output)
   * @param source starting point of the ray
   * @param direction normalized direction vector of the ray
   * @return the number of intersection points found (number of valid entries in
   * {@code distance})
   */
  @Override
  public int intercept(double[] distance, MutableVector3 source, MutableVector3 direction)
  {
    int hit = 0;

    direction.normalize();
    // Intercept with the circular sides of the cylinder
    double a = direction.x * direction.x + direction.y * direction.y;
    double b = 2 * (source.x * direction.x + source.y * direction.y);
    double c = source.x * source.x + source.y * source.y - this.radius * this.radius;

    double discriminant = b * b - 4 * a * c;
    if (discriminant >= 0)
    {
      double sqrtDiscriminant = Math.sqrt(discriminant);
      double t1 = (-b - sqrtDiscriminant) / (2 * a);
      double t2 = (-b + sqrtDiscriminant) / (2 * a);

      if (t1 > 0)
      {
        double z1 = source.z + t1 * direction.z;
        if (z1 >= -this.height / 2 && z1 <= this.height / 2)
        {
          distance[hit++] = t1;
        }
      }
      if (t2 > 0)
      {
        double z2 = source.z + t2 * direction.z;
        if (z2 >= -this.height / 2 && z2 <= this.height / 2)
        {
          distance[hit++] = t2;
        }
      }
    }

    // Intercept with the top and bottom caps of the cylinder
    if (direction.z != 0)
    {
      double t3 = (-this.height / 2 - source.z) / direction.z;
      double t4 = (this.height / 2 - source.z) / direction.z;

      double x3 = source.x + t3 * direction.x;
      double y3 = source.y + t3 * direction.y;
      if (t3 > 0 && x3 * x3 + y3 * y3 <= this.radius * this.radius)
      {
        distance[hit++] = t3;
      }

      double x4 = source.x + t4 * direction.x;
      double y4 = source.y + t4 * direction.y;
      if (t4 > 0 && x4 * x4 + y4 * y4 <= this.radius * this.radius)
      {
        distance[hit++] = t4;
      }
    }

    // Ensure the order of the distances is correct
    if (hit > 1 && distance[0] > distance[1])
    {
      double temp = distance[0];
      distance[0] = distance[1];
      distance[1] = temp;
    }
    return hit;
  }

}
