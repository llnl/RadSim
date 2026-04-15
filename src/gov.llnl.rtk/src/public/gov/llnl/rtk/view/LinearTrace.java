// --- file: gov/llnl/rtk/view/LinearTrace.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;
import gov.llnl.math.euclidean.Vector3Ops;
import gov.llnl.math.spline.CubicHermiteSplineFactory;
import gov.llnl.math.spline.EndBehavior;
import gov.llnl.math.spline.Spline;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class LinearTrace implements Trace
{
  Instant t0;
  Spline xf;
  Vector3 offset; 

  /**
   * Convert a set of positions down a linear path into a path.
   *
   * @param t
   * @param x
   * @param offset
   * @return
   */
  static public Trace create(List<Instant> t, double[] x, Vector3 offset)
  {
    Instant t0 = t.get(0);
    double[] vt = new double[x.length];
    for (int i = 0; i < vt.length; ++i)
    {
      vt[i] = between(t0, t.get(i));
    }

    Spline xf = CubicHermiteSplineFactory.createNatural(vt, x);
    xf.setEndBehavior(EndBehavior.LINEAR);
    return new LinearTrace(t0, xf, offset);
  }

  private LinearTrace(Instant t0, Spline xf, Vector3 offset)
  {
    this.t0 = t0;
    this.xf = xf;
    this.offset = offset;
  }

  @Override
  public Vector3 get(Instant time)
  {
    Vector3 current = Vector3.of(xf.applyAsDouble(between(t0, time)), 0, 0);
    if (offset != null)
      return Vector3Ops.add(offset, current);
    return current;
  }

  /**
   * Convert the delta time into a double.
   *
   * @param t1
   * @param t2
   * @return
   */
  static double between(Instant t1, Instant t2)
  {
    Duration between = Duration.between(t1, t2);
    return between.toNanos() * 1e-9;
  }

}
