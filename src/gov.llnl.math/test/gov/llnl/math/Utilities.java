/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

/**
 *
 * @author nelson85
 */
public class Utilities
{
  /**
   * Determine if all the values in a vector are in specific ranges.
   *
   * If x lies on the line going through a and b then this method will return
   * true if x is on the line segment between a and b and will return false if
   * is not between a and b.
   *
   * If x does not lie on that line, all this method will do is return true if x
   * is in a bounding box formed by a and b.
   *
   * @param x The vector.
   * @param a The left endpoint.
   * @param b The right endpoint.
   * @return true if a_i $lt;= x_i &lt;= b_i for all i and false otherwise.
   */
  public static boolean isInElementWiseRange(double[] x, double[] a, double[] b)
  {
    MathAssert.assertEqualLength(x, a);
    MathAssert.assertEqualLength(x, b);
    for (int i = 0; i < x.length; i++)
    {
      if ((a[i] > x[i]) || (x[i] > b[i]))
      {
        return false;
      }
    }
    return true;
  }
  
}
