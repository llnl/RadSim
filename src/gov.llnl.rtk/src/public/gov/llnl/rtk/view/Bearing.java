// --- file: gov/llnl/rtk/view/Bearing.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.view;

import gov.llnl.math.euclidean.Vector3;

/**
 * Utility class for computing a bearing with vectors.
 *
 * @author nelson85
 */
public class Bearing
{
  /**
   * Compute the bearing to an object relative to our travel.
   *
   * @param course is the direction we are traveling.
   * @param object is the direction to the object relative to our position.
   * @return the angle to toward the object.
   */
  public static double compute(Vector3 course, Vector3 object)
  {
    double vx = course.getX();
    double vy = course.getY();
    double r = Math.sqrt(vx * vx + vy * vy);

    // There is no bearing if we are not moving.
    if (r == 0)
      return 0;

    vx /= r;
    vy /= r;
    double ox = object.getX();
    double oy = object.getY();
    double cos = ox * vx + oy * vy;
    double sin = oy * vx - ox * vy;
    return Math.atan2(sin, cos);
  }

}
