// --- file: gov/llnl/rtk/response/sim/Basis.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

import gov.llnl.math.euclidean.MutableVector3;
import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.random.UniformRandom;

/**
 * Defines an orthonormal basis in 3D space for geometric computations.
 * <p>
 * The basis consists of three mutually orthogonal unit vectors:
 * <ul>
 * <li>{@code vz} - The primary direction (typically "forward").</li>
 * <li>{@code vx} - Perpendicular to {@code vz}, forms the X axis of the
 * basis.</li>
 * <li>{@code vy} - Perpendicular to both {@code vz} and {@code vx}, forms the Y
 * axis.</li>
 * </ul>
 * </p>
 * <p>
 * The {@code assign} method aligns the basis to a new reference direction,
 * recalculating {@code vx} and {@code vy} to maintain orthogonality. The
 * {@code drawConeVector} method generates a random vector within a specified
 * cone about the {@code vz} axis, supporting efficient sampling for Monte Carlo
 * simulations.
 * </p>
 * <p>
 * <b>Usage:</b> This class is intended to be used as part of simulation classes
 * (e.g., {@code CuboidSim}, {@code CylinderSim}), with one instance per
 * simulation or per thread. It is not thread-safe or reentrant by design; each
 * thread or simulation should maintain its own {@code Basis} instance. Fields
 * are public and mutable for performance in controlled, single-threaded
 * simulation contexts.
 * </p>
 *
 * @author nelson85
 */
public class Basis
{
  // This a camera type system.  Z is the forward direction and XY are off to the sides
  public MutableVector3 vz = new MutableVector3();
  public MutableVector3 vx = new MutableVector3();
  public MutableVector3 vy = new MutableVector3();

  /**
   * Aligns the basis vectors so that {@code vz} points in the direction of the
   * specified reference vector.
   * <p>
   * After calling this method, {@code vz} is set to the normalized direction of
   * {@code ref}, and {@code vx} and {@code vy} are recalculated to form an
   * orthonormal basis.
   * </p>
   *
   * @param ref the reference vector indicating the new forward (Z) direction
   * for the basis
   */
  public void assign(MutableVector3 ref)
  {
    vz.assign(ref);
    int i = largest(ref);
    switch (i)
    {
      case 0:
        vx.z = -ref.x; // largest X->Z
        vx.y = ref.z;
        vx.x = -(vx.y * ref.y + vx.z * ref.z) / ref.x;
        break;
      case 1:
        vx.x = -ref.y; // largest Y->Z
        vx.z = ref.x;
        vx.y = -(vx.x * ref.x + vx.z * ref.z) / ref.y;
        break;
      case 2:
        vx.x = -ref.z; // largest Z->X
        vx.y = ref.x;
        vx.z = -(vx.x * ref.x + vx.y * ref.y) / ref.z;
        break;
    }
    vx.normalize();

    // Use cross to the the other vector
    Vector3Ops.cross(vy, vx, vz);
    vy.normalize();
  }

  /**
   * Generates a random vector within a cone defined about the {@code vz} axis
   * of this basis.
   * <p>
   * The generated vector is written to {@code out}. The cone is specified by
   * its axis ({@code vz}) and by the range of cosines of the cone angle, from
   * {@code f1} (minimum cosine) to {@code f2} (maximum cosine). If {@code f1}
   * equals {@code f2}, the cone has a fixed angle. If {@code f1} and {@code f2}
   * differ, the angle is chosen randomly within the corresponding range.
   * </p>
   *
   * @param out the vector to store the generated result; will be overwritten
   * @param ur the uniform random number generator used for sampling
   * @param f1 the minimum cosine of the cone angle (defines the widest part of
   * the cone)
   * @param f2 the maximum cosine of the cone angle (1.0 means the axis
   * direction)
   */
  public void drawConeVector(MutableVector3 out, UniformRandom ur, double f1, double f2)
  {
    double z = f1;
    if (f1 != f2)
      z = ur.draw(f1, f2);
    double theta = ur.draw(0, 2 * Math.PI);
    double u = Math.sqrt(1 - z * z);
    double x = u * Math.cos(theta);
    double y = u * Math.sin(theta);

    // Project on basis
    out.assign(Vector3.ZERO);
    out.addAssignScaled(vz, z);
    out.addAssignScaled(vx, x);
    out.addAssignScaled(vy, y);
  }

  /**
   * Determines the index of the component of the given vector with the largest
   * absolute value.
   * <p>
   * The method compares the squared values of the x, y, and z components of the
   * vector, and returns the index of the component with the greatest magnitude.
   * </p>
   *
   * @param v the vector to examine
   * @return the index of the largest component: {@code 0} for x, {@code 1} for
   * y, or {@code 2} for z
   */
  static int largest(MutableVector3 v)
  {
    double x = v.x * v.x;
    double y = v.y * v.y;
    double z = v.z * v.z;
    if (x > y)
      return (x > z) ? 0 : 2;
    else
      return (y > z) ? 1 : 2;
  }

}
