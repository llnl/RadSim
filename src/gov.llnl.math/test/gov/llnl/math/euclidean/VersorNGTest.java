/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math.euclidean;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class VersorNGTest
{

  public VersorNGTest()
  {
  }

  @Test
  public void testFromQuaternion()
  {
    assertEquals(Versor.of(Vector3.AXIS_X, Math.PI).round(4),
            Versor.fromQuaternion(0, 1, 0, 0));
    assertEquals(Versor.of(Vector3Ops.scale(Vector3.AXIS_Y, 2), Math.PI).round(6),
            Versor.fromQuaternion(0, 0, 1, 0));
    assertEquals(Versor.of(Vector3.ZERO, Math.PI).round(6),
            Versor.fromQuaternion(1, 0, 0, 0));
    assertEquals(Versor.of(Vector3.AXIS_Z, 2 * Math.PI).round(6),
            Versor.fromQuaternion(-1, 0, 0, 0));
  }

  void toAxis(Versor q, MutableVector3 ax, MutableVector3 ay, MutableVector3 az)
  {
    ax.assign(Vector3.AXIS_X);
    ax.rotateAssign(q);
    ay.assign(Vector3.AXIS_Y);
    ay.rotateAssign(q);
    az.assign(Vector3.AXIS_Z);
    az.rotateAssign(q);
  }

  @Test
  public void testFromAxis()
  {
    MutableVector3 ax = new MutableVector3();
    MutableVector3 ay = new MutableVector3();
    MutableVector3 az = new MutableVector3();
    MutableVector3 tx = new MutableVector3();
    MutableVector3 ty = new MutableVector3();
    MutableVector3 tz = new MutableVector3();
    Versor q;
    Versor t;

    q = Versor.fromQuaternion(1, 0.1, 0, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(1, -0.1, 0, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(1, 0, 0.1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(1, 0, -0.1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(1, 0, 0, 0.1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(1, 0, 0, -0.1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 1, 0.1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 1, -0.1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 1, 0, 0.1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 1, 0, -0.1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0.1, 1, 0, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(-0.1, 1, 0, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 0, 1, 0.1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 0, 1, -0.1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0.1, 0, 1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(-0.1, 0, 1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 0.1, 1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, -0.1, 1, 0);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0.1, 0, 0, 1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(-0.1, 0, 0, 1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 0.1, 0, 1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, -0.1, 0, 1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 0, 0.1, 1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

    q = Versor.fromQuaternion(0, 0, -0.1, 1);
    toAxis(q, ax, ay, az);
    t = Versor.fromAxis(ax, ay, az);
    toAxis(t, tx, ty, tz);
    assertEquals(tx.dot(ax) + ty.dot(ay) + tz.dot(az), 3, 1e-9);

  }

  @Test
  public void testToString()
  {
    Versor instance = Versor.of(Vector3.AXIS_X, Math.PI / 2);
    String expResult = "Versor(0.707107,0.707107,0.000000,0.000000)";
    String result = instance.toString();
    assertEquals(result, expResult);
  }

//  @Test
//  public void testOf_4args()
//  {
//    Versor result = Versor.fromQuaternion(1, 1, 1, Math.PI / 4);
//    assertEquals(result.getU(), 0.9238795325112867);
//    assertEquals(result.getI(), 0.22094238269039454);
//    assertEquals(result.getJ(), 0.22094238269039454);
//    assertEquals(result.getK(), 0.22094238269039454);
//  }

  @Test
  public void testInv()
  {
    Versor instance = Versor.of(Vector3.AXIS_X, Math.PI / 2);
    Versor result = instance.inv();
    assertEquals(result.getU(), 0.7071067811865476, 0.0);
    assertEquals(result.getX(), -0.7071067811865475, 0.0);
    assertEquals(result.getY(), 0.0, 1e-10);
    assertEquals(result.getZ(), 0.0, 1e-10);
  }

  @Test
  public void testRotate()
  {
    Vector3 v = Vector3.of(1, 2, 3);
    assertEquals(Versor.of(Vector3.AXIS_X, Math.PI / 2).rotate(v).roundTo(6),
            Vector3.of(1, -3, 2));
    assertEquals(Versor.of(Vector3.AXIS_Y, Math.PI / 2).rotate(v).roundTo(6),
            Vector3.of(3, 2, -1));
    assertEquals(Versor.of(Vector3.AXIS_Z, Math.PI / 2).rotate(v).roundTo(6),
            Vector3.of(-2, 1, 3));
    assertEquals(Versor.of(Vector3.AXIS_X, -Math.PI / 2).rotate(v).roundTo(6),
            Vector3.of(1, 3, -2));
    assertEquals(Versor.of(Vector3.AXIS_Y, -Math.PI / 2).rotate(v).roundTo(6),
            Vector3.of(-3, 2, 1));
    assertEquals(Versor.of(Vector3.AXIS_Z, -Math.PI / 2).rotate(v).roundTo(6),
            Vector3.of(2, -1, 3));
  }

}
