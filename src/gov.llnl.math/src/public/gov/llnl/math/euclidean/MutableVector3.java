/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.euclidean;

import java.io.Serializable;

/**
 * When we are doing lots of shifts and rotations, the cost of creating objects
 * can be oppressive. WorkingVector3 is to reduce the cost of writing efficient
 * code.
 *
 * @author nelson85
 */
public class MutableVector3 implements Vector3, Serializable
{
  public double x, y, z;

  public MutableVector3(double x,double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public MutableVector3(Vector3 v)
  {
    x = v.getX();
    y = v.getY();
    z = v.getZ();
  }

  public MutableVector3()
  {
    x = 0;
    y = 0;
    z = 0;
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

  public void assign(Vector3 v)
  {
    this.x = v.getX();
    this.y = v.getY();
    this.z = v.getZ();
  }

  public void assign(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public MutableVector3 addAssign(Vector3 v)
  {
    x += v.getX();
    y += v.getY();
    z += v.getZ();
    return this;
  }

  public MutableVector3 addAssignScaled(Vector3 v, double scale)
  {
    x += v.getX() * scale;
    y += v.getY() * scale;
    z += v.getZ() * scale;
    return this;
  }

  public MutableVector3 negateAssign()
  {
    x = -x;
    y = -y;
    z = -z;
    return this;
  }

  public MutableVector3 subtractAssign(Vector3 v)
  {
    x -= v.getX();
    y -= v.getY();
    z -= v.getZ();
    return this;
  }

  public MutableVector3 multiplyAssign(double scalar)
  {
    x *= scalar;
    y *= scalar;
    z *= scalar;
    return this;
  }

  public MutableVector3 divideAssign(double scalar)
  {
    x /= scalar;
    y /= scalar;
    z /= scalar;
    return this;
  }

  public MutableVector3 rotateAssign(Versor q1)
  {
    double vi = q1.getX();
    double vj = q1.getY();
    double vk = q1.getZ();
    double vu = q1.getU();

    double nu = -vi * x - vj * y - vk * z;
    double nx = vu * x + vj * z - vk * y;
    double ny = vu * y + vk * x - vi * z;
    double nz = vu * z + vi * y - vj * x;

    x = -nu * vi + nx * vu - ny * vk + nz * vj;
    y = -nu * vj + ny * vu - nz * vi + nx * vk;
    z = -nu * vk + nz * vu - nx * vj + ny * vi;
    return this;
  }

  public MutableVector3 rotateInvAssign(Versor q1)
  {
    double vi = -q1.getX();
    double vj = -q1.getY();
    double vk = -q1.getZ();
    double vu = q1.getU();

    double nu = -vi * x - vj * y - vk * z;
    double nx = vu * x + vj * z - vk * y;
    double ny = vu * y + vk * x - vi * z;
    double nz = vu * z + vi * y - vj * x;

    x = -nu * vi + nx * vu - ny * vk + nz * vj;
    y = -nu * vj + ny * vu - nz * vi + nx * vk;
    z = -nu * vk + nz * vu - nx * vj + ny * vi;
    return this;
  }

  @Override
  public String toString()
  {
    return String.format("Vector3(%.4f,%.4f,%.4f)", x, y, z);
  }

  public static void main(String[] s)
  {
    Vector3 v = Vector3.of(1, 2, 3);
    Vector3 v2 = Vector3.of(0.5, 0.6, 0.7);
    MutableVector3 w = new MutableVector3();
    Versor q = Versor.of(Vector3.of(1, 1, 1), Math.PI / 4);
    w.assign(v);
    w.subtractAssign(v2);
    w.rotateAssign(q);
    w.rotateInvAssign(q);
    w.addAssign(v2);
    System.out.println(w);
  }

  /** 
   * Convert the vector to unit length.
   */
  public void normalize()
  {
    double n = norm();
    if (n != 0)
      this.divideAssign(n);
  }

}
