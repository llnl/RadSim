/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

// Collection of utility functions to speed up matlab conversions
import gov.llnl.math.MathExceptions.MathException;
import gov.llnl.math.stream.DoubleIndexImpl;
import gov.llnl.utility.ArrayEncoding;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

/**
 * Collection of utilities to act on double arrays as vectors. By using this
 * consistently throughout our code, we can have the benefits of loop unrolling
 * as we find it necessary to achieve performance goals. Many of the forms are
 * used to support the matrix class.
 * <p>
 * As most of these operations are intended for tight looping operations, the
 * number of indices to increment is significant in the number of operations
 * required. Thus generally we should not promote to a version with a larger
 * number of loop indices. Assignment operations for binary operators are more
 * quick than to target operations as to target requires three loop indices.
 * Promotion to ranged versions is usually acceptable as it does not increase
 * the number of operations required. {@code operatorAssign(copy(x),y)} is
 * slower because we have to walk the list once for the copy and a second time
 * for the operation. Where this is the case a method that produces the copy is
 * preferred.
 * <p>
 * Two styles of loop unrolling are currently used in this file. It is not clear
 * how much benefit the full unrolling has over the 8 and 1 method. Speed
 * comparisons should be performed. A number of the functions have not yet been
 * unrolled as those methods have not yet appeared on a critical path.
 * <p>
 * Where possible the exceptions produced shall be listed even if exception is
 * runtime checked. Because these methods are used in loops, lazy try method is
 * used for range checking when a range of a vector is operated on. In a lazy
 * check the operation is performed up to the point at which the range is
 * violated. Operations in which the two vectors shall be the same length is not
 * evaluated in a lazy fashion as the cost of determining the length is required
 * anyway. This is important to understand when considering the state of the
 * vectors after an exception is thrown.
 * <p>
 * Operators that act on a vector and scalar come in:
 * <ul>
 * <li> {@code operator(vector, scalar)} - Produces a copy of the vector after
 * operation.
 * <li> {@code operatorAssign(vector, scalar)} - Apply the operation to each
 * member of the vector.
 * <li> {@code operatorAssignRange(vector, begin, end, scalar)} - Apply the
 * operation to a region in the vector.
 * </ul>
 * <p>
 * Operators that act on two vectors come in:
 * <ul>
 * <li> {@code operatorAssign(target, source)} - Apply the operation to each
 * member of the vector.
 * <li>
 * {@code operatorAssign(target, targetOffset, source, sourceOffset, length)} -
 * Apply the operation to each member of the vector.
 * </ul>
 * <p>
 * Some of the methods are just front ends for system calls when the system has
 * vector operations and the speed of the system call was found to be
 * reasonable.
 *
 * @see java.lang.System#arraycopy
 * @see java.util.Arrays#copyOfRange
 * @author nelson85
 */
public class DoubleArray
{
//<editor-fold defaultstate="collapsed" desc="assign">
  /**
   * Assign one vector to another. This is just a front end for
   * System.arraycopy.
   *
   * @param target is the vector to copy to.
   * @param source is the vector to copy from.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] assign(double target[], double source[])
          throws IndexOutOfBoundsException
  {
    MathAssert.assertEqualLength(target, source);
    System.arraycopy(source, 0, target, 0, source.length);
    return target;
  }

  /**
   * Assign one vector to another. This is just a front end for
   * System.arraycopy.
   *
   * @param target is the vector to copy to.
   * @param source is the vector to copy from.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] assignRange(double target[], double source[], int begin, int end)
          throws IndexOutOfBoundsException
  {
    return assign(target, begin, source, begin, end - begin);
  }

  /**
   * Assign a portion of one vector to another. This is just a front end for
   * System.arraycopy.
   *
   * @param target is the vector to copy to.
   * @param targetOffset is the start of the copy region in the target.
   * @param source is the vector to copy from.
   * @param sourceOffset is the start of the copy region in the source.
   * @param length is the length of the section to copy.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the range of the source or target is
   * exceeded.
   */
  public static double[] assign(
          double[] target, int targetOffset,
          double[] source, int sourceOffset,
          int length)
          throws IndexOutOfBoundsException
  {
    try
    {
      System.arraycopy(source, sourceOffset, target, targetOffset, length);
      return target;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(target, targetOffset, targetOffset + length, "target");
      MathAssert.assertRange(source, sourceOffset, sourceOffset + length, "source");
      throw ex;
    }
  }

  /**
   * Assign all values to be the maximum of the element and a scalar.
   *
   * @param target is the vector to operate on.
   * @param scalar is the scalar to compare to.
   * @return the target vector.
   */
  public static double[] assignMaximumOf(double[] target, double scalar)
  {
    for (int i = 0; i < target.length; i++)
      target[i] = Math.max(target[i], scalar);
    return target;
  }

  /**
   * Assign all values to be the minimum of the element and a scalar.
   *
   * @param target is the vector to operate on.
   * @param scalar is the scalar to compare to.
   * @return the target vector.
   */
  public static double[] assignMinimumOf(double[] target, double scalar)
  {
    for (int i = 0; i < target.length; i++)
      target[i] = Math.min(target[i], scalar);
    return target;
  }

  /**
   * Concatenate multiple arrays.
   *
   * @param arrays
   * @return
   */
  public static double[] cat(double[]

    ... arrays)
  {
    int s = 0;
    for (double[] a : arrays)
    {
      s += a.length;
    }

    double[] out = new double[s];
    int i = 0;
    for (double[] a : arrays)
    {
      DoubleArray.assign(out, i, a, 0, a.length);
      i += a.length;
    }
    return out;
  }

//</editor-fold>
//<editor-fold desc="copy" defaultstate="collapsed">
  /**
   * Create a new array with members.
   * <p>
   * This is equivalent to `new double[]{values}`.
   * <p>
   * It can be used in places where an inline initialization is required, but
   * the java formatter would otherwise expand it to multiple lines which
   * clutters up the code.
   *
   * @param values
   * @return a new array containing those values.
   */
  public static double[] of(double... values)
  {
    return values;
  }

  /**
   * Create a copy of an array.
   *
   * This is the same as `values.clone()` but does not fail if the array is
   * null.
   *
   * @param vector
   * @return a new array or null if the values are null.
   */
  public static double[] copyOf(double... vector)
  {
    if (vector == null)
      return null;
    return copyOfRange(vector, 0, vector.length);
  }

  /**
   * Copy the elements from a vector in a in a range.
   *
   * @param source is the vector to copy from.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return a copy of the region.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * source.
   */
  public static double[] copyOfRange(double[] source, int begin, int end)
          throws IndexOutOfBoundsException
  {
    if (source == null)
      return null;
    double out[] = new double[end - begin];
    System.arraycopy(source, begin, out, 0, end - begin);
    return out;
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="fill">
  /**
   * Assign all of the elements of a double array to a value.
   *
   * @param target is the vector to operate on.
   * @param scalar is the value to assign.
   * @return the target array.
   */
  public static double[] fill(double target[], double scalar)
  {
    return fillRange(target, 0, target.length, scalar);
  }

  /**
   * Assign all of the elements of a double array to a value.
   *
   * @param target is the vector to operate on.
   * @param value is the value to fill.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the target array.
   * @throws IndexOutOfBoundsException if the requested range exceeds the range
   * in the target.
   */
  public static double[] fillRange(double[] target, int begin, int end, double value)
          throws IndexOutOfBoundsException
  {
    try
    {
      for (int i = begin; i < end; ++i)
      {
        target[i] = value;
      }
      return target;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(target, begin, end, "target");
      throw ex;
    }
  }

  /**
   * Assign all of the elements of a double array to a value.
   *
   * @param target is the vector to operate on.
   * @param scalar is the value to assign.
   * @return the target array.
   */
  public static double[] fill(double target[], DoubleSupplier scalar)
  {
    return fillRange(target, 0, target.length, scalar);
  }

  /**
   * Assign all of the elements of a double array to a value.
   *
   * @param target is the vector to operate on.
   * @param value is the value to fill.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the target array.
   * @throws IndexOutOfBoundsException if the requested range exceeds the range
   * in the target.
   */
  public static double[] fillRange(double[] target, int begin, int end, DoubleSupplier value)
          throws IndexOutOfBoundsException
  {
    try
    {
      for (int i = begin; i < end; ++i)
      {
        target[i] = value.getAsDouble();
      }
      return target;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(target, begin, end, "target");
      throw ex;
    }
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="add">
  /**
   * Produce a vector which is the sum of a scalar and a vector.
   *
   * @param source is the vector to operate on.
   * @param scalar is the vector to add.
   * @return a new vector with the sum of the source and scalar.
   */
  public static double[] add(double[] source, double scalar)
  {
    double[] out = new double[source.length];
    for (int i = 0; i < source.length; ++i)
    {
      out[i] = scalar + source[i];
    }
    return out;
  }

  /**
   * Add a scalar to every member of a double array. Shortcuts evaluation if the
   * scalar is 0.
   *
   * @param target is the vector to operate on.
   * @param scalar is the vector to add.
   * @return the target array.
   */
  public static double[] addAssign(double[] target, double scalar)
  {
    return addAssignRange(target, 0, target.length, scalar);
  }

  /**
   * Add a scalar to all of the elements of a double array to a value. Shortcuts
   * evaluation if the scalar is 0.
   *
   * @param target is the vector to operate on.
   * @param scalar is the value to add.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the target array.
   * @throws IndexOutOfBoundsException if the requested range exceeds the range
   * in the target, unless the scalar is 0.
   */
  public static double[] addAssignRange(double[] target, int begin, int end, double scalar)
          throws IndexOutOfBoundsException
  {
    try
    {
      if (scalar == 0)
        return target;
      for (int i = begin; i < end; ++i)
      {
        target[i] += scalar;
      }
      return target;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(target, begin, end, "target");
      throw ex;
    }
  }

  public static double[] add(double[] a, double[] b)
  {
    // Check sizes before cloning.
    MathAssert.assertEqualLength(a, b);
    return addAssign(a.clone(), b);
  }

  /**
   * Add one double array to another. The length of the source must be at least
   * as long as the target.
   *
   * @param target is the vector to operate on.
   * @param source is the vector to add.
   * @return the target array.
   * @throws IndexOutOfBoundsException if the lengths of the vectors differ.
   */
  public static double[] addAssign(double target[], double source[])
          throws IndexOutOfBoundsException
  {
    MathAssert.assertEqualLength(target, source);
    // This is slightly more efficient than the ranged version so we keep it
    int i = 0;
    while (i + 8 <= target.length)
    {
      target[i + 0] += source[i + 0];
      target[i + 1] += source[i + 1];
      target[i + 2] += source[i + 2];
      target[i + 3] += source[i + 3];
      target[i + 4] += source[i + 4];
      target[i + 5] += source[i + 5];
      target[i + 6] += source[i + 6];
      target[i + 7] += source[i + 7];
      i += 8;
    }
    for (; i < target.length; ++i)
    {
      target[i] += source[i];
    }
    return target;
  }

  /**
   * Add all of the elements in a region of one vector to another.
   *
   * @param target is the vector to operate on.
   * @param targetOffset is the start of the region in the target.
   * @param source is the vector to add.
   * @param sourceOffset is the start of the region in the source.
   * @param length is the length of the region to operate on.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the requested range is outside of the
   * target or source.
   */
  public static double[] addAssign(
          double[] target, int targetOffset,
          double[] source, int sourceOffset,
          int length)
          throws IndexOutOfBoundsException
  {
    int i2 = sourceOffset;
    int end = targetOffset + length;
    for (int i1 = targetOffset; i1 < end; ++i1, ++i2)
    {
      target[i1] += source[i2];
    }
    return target;
  }

  /**
   * Adds a scaled copy of an array to another. The arrays must be the same
   * length. This is just a front end for addAssignScaled.
   *
   * @param target
   * @param source
   * @param scalar is the scale to apply to the source vector.
   * @return 
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] addAssignScaled(double[] target, double[] source, double scalar)
          throws IndexOutOfBoundsException
  {
    return DoubleArray.addAssignScaled(target, 0, source, 0, target.length, scalar);
  }

  /**
   * Vectorized element-wise multiply of double vector with scalar accumulating
   * in target. Used in matrix multiply operation.
   *
   * @param target double vector to accumulate the result.
   * @param targetOffset starting offset in target
   * @param source source double vector
   * @param sourceOffset starting offset in first source
   * @param length number of elements to multiply
   * @param scalar is the scale to apply to the source vector.
   * @return 
   * @throws IndexOutOfBoundsException if the requested range is outside of the
   * target or source.
   */
  static public double[] addAssignScaled(
          double[] target, int targetOffset,
          double[] source, int sourceOffset,
          int length,
          double scalar)
          throws IndexOutOfBoundsException
  {
    if (scalar == 0)
      return target;
    try
    {
      int i = 0;
      int i0 = sourceOffset;
      int i1 = targetOffset;
      while (i + 16 <= length)
      {
        target[i1] += source[i0] * scalar;
        target[i1 + 1] += source[i0 + 1] * scalar;
        target[i1 + 2] += source[i0 + 2] * scalar;
        target[i1 + 3] += source[i0 + 3] * scalar;
        target[i1 + 4] += source[i0 + 4] * scalar;
        target[i1 + 5] += source[i0 + 5] * scalar;
        target[i1 + 6] += source[i0 + 6] * scalar;
        target[i1 + 7] += source[i0 + 7] * scalar;
        target[i1 + 8] += source[i0 + 8] * scalar;
        target[i1 + 9] += source[i0 + 9] * scalar;
        target[i1 + 10] += source[i0 + 10] * scalar;
        target[i1 + 11] += source[i0 + 11] * scalar;
        target[i1 + 12] += source[i0 + 12] * scalar;
        target[i1 + 13] += source[i0 + 13] * scalar;
        target[i1 + 14] += source[i0 + 14] * scalar;
        target[i1 + 15] += source[i0 + 15] * scalar;
        i += 16;
        i0 += 16;
        i1 += 16;
      }
      if (i + 8 <= length)
      {
        target[i1] += source[i0] * scalar;
        target[i1 + 1] += source[i0 + 1] * scalar;
        target[i1 + 2] += source[i0 + 2] * scalar;
        target[i1 + 3] += source[i0 + 3] * scalar;
        target[i1 + 4] += source[i0 + 4] * scalar;
        target[i1 + 5] += source[i0 + 5] * scalar;
        target[i1 + 6] += source[i0 + 6] * scalar;
        target[i1 + 7] += source[i0 + 7] * scalar;
        i += 8;
        i0 += 8;
        i1 += 8;
      }
      if (i + 4 <= length)
      {
        target[i1] += source[i0] * scalar;
        target[i1 + 1] += source[i0 + 1] * scalar;
        target[i1 + 2] += source[i0 + 2] * scalar;
        target[i1 + 3] += source[i0 + 3] * scalar;
        i += 4;
        i0 += 4;
        i1 += 4;
      }
      if (i + 2 <= length)
      {
        target[i1] += source[i0] * scalar;
        target[i1 + 1] += source[i0 + 1] * scalar;
        i += 2;
        i0 += 2;
        i1 += 2;
      }
      if (i < length)
      {
        target[i1] += source[i0] * scalar;
      }
      return target;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(source, sourceOffset, sourceOffset + length, "source");
      MathAssert.assertRange(target, targetOffset, targetOffset + length, "target");
      throw ex;
    }
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="subtract">
  /**
   * Subtract one vector from another.
   *
   * @param target is the vector to operate on.
   * @param source is the vector to subtract.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] subtractAssign(double target[], double source[])
          throws IndexOutOfBoundsException
  {
    MathAssert.assertEqualLength(target, source);
    for (int i = 0; i < target.length; ++i)
    {
      target[i] -= source[i];
    }
    return target;
  }

  /**
   * Subtract all of the elements in a region of one vector to another.
   *
   * @param target is the vector to operate on.
   * @param targetOffset is the start of the region in the target.
   * @param source is the vector to subtract.
   * @param sourceOffset is the start of the region in the source.
   * @param length is the length of the region to operate on.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the requested range is outside of the
   * target or source.
   */
  public static double[] subtractAssign(
          double[] target, int targetOffset,
          double[] source, int sourceOffset, int length)
          throws IndexOutOfBoundsException
  {
    try
    {
      int end = targetOffset + length;
      for (; targetOffset < end; ++targetOffset, ++sourceOffset)
      {
        target[targetOffset] -= source[sourceOffset];
      }
      return target;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(target, targetOffset, targetOffset + length, "target");
      MathAssert.assertRange(source, sourceOffset, sourceOffset + length, "source");
      throw ex;
    }
  }

  /**
   * Subtract one vector from another, without modifying either.
   *
   * @param u The vector.
   * @param v The vector being subtracted.
   * @return The difference of the vectors.
   */
  public static double[] subtract(double[] u, double[] v)
  {
    MathAssert.assertEqualLength(u, v);
    return subtractAssign(u.clone(), v);
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="negateAssign">
  /**
   * Negate all of the elements in a vector.
   *
   * @param target is the vector to operate on.
   * @return the target vector.
   */
  public static double[] negateAssign(double[] target)
  {
    return negateAssignRange(target, 0, target.length);
  }

  /**
   * Negate all of the elements in a range.
   *
   * @param target is the vector to operate on.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * target.
   */
  public static double[] negateAssignRange(double[] target, int begin, int end)
          throws IndexOutOfBoundsException
  {
    for (int i = begin; i < end; ++i)
    {
      target[i] = -target[i];
    }
    return target;
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="multiply">
  /**
   * Produce a vectors that is the product of a vector and a scalar.
   *
   * @param source is the vector to operate on.
   * @param scalar is the value to multiplied by.
   * @return the target vector.
   */
  public static double[] multiply(double[] source, double scalar)
  {
    double[] out = new double[source.length];
    for (int i = 0; i < source.length; ++i)
      out[i] = scalar * source[i];
    return out;
  }

  /**
   * Produce a vectors that is the product of a vector and a scalar.
   *
   * @param sourceA is the first operand.
   * @param sourceB is the second operand.
   * @return a new vector which is the elementwise product of the two vectors.
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] multiply(double[] sourceA, double[] sourceB)
          throws IndexOutOfBoundsException
  {
    MathAssert.assertEqualLength(sourceA, sourceB);
    double[] out = new double[sourceA.length];
    for (int i = 0; i < sourceA.length; ++i)
      out[i] = sourceA[i] * sourceB[i];
    return out;
  }

  /**
   * Vectorized element-wise multiply of two double vectors.
   *
   * @param target double vector to store the result.
   * @param targetOffset starting offset in target
   * @param srcA first source double vector
   * @param srcAOffset starting offset in first source
   * @param srcB second source double vector
   * @param srcBOffset starting offset in second source
   * @param length number of elements to multiply
   * @throws IndexOutOfBoundsException if the requested region is outside of the
   * target or either of the sources.
   */
  public static void multiply(
          double[] target, int targetOffset,
          double[] srcA, int srcAOffset,
          double[] srcB, int srcBOffset,
          int length)
          throws IndexOutOfBoundsException
  {
    try
    {
      int i = 0;
      int i0 = srcAOffset;
      int i1 = srcBOffset;
      int i3 = targetOffset;

      while (i + 16 <= length)
      {
        target[i3] = srcA[i0] * srcB[i1];
        target[i3 + 1] = srcA[i0 + 1] * srcB[i1 + 1];
        target[i3 + 2] = srcA[i0 + 2] * srcB[i1 + 2];
        target[i3 + 3] = srcA[i0 + 3] * srcB[i1 + 3];
        target[i3 + 4] = srcA[i0 + 4] * srcB[i1 + 4];
        target[i3 + 5] = srcA[i0 + 5] * srcB[i1 + 5];
        target[i3 + 6] = srcA[i0 + 6] * srcB[i1 + 6];
        target[i3 + 7] = srcA[i0 + 7] * srcB[i1 + 7];
        target[i3 + 8] = srcA[i0 + 8] * srcB[i1 + 8];
        target[i3 + 9] = srcA[i0 + 9] * srcB[i1 + 9];
        target[i3 + 10] = srcA[i0 + 10] * srcB[i1 + 10];
        target[i3 + 11] = srcA[i0 + 11] * srcB[i1 + 11];
        target[i3 + 12] = srcA[i0 + 12] * srcB[i1 + 12];
        target[i3 + 13] = srcA[i0 + 13] * srcB[i1 + 13];
        target[i3 + 14] = srcA[i0 + 14] * srcB[i1 + 14];
        target[i3 + 15] = srcA[i0 + 15] * srcB[i1 + 15];
        i += 16;
        i0 += 16;
        i1 += 16;
        i3 += 16;
      }
      if (i + 8 <= length)
      {
        target[i3] = srcA[i0] * srcB[i1];
        target[i3 + 1] = srcA[i0 + 1] * srcB[i1 + 1];
        target[i3 + 2] = srcA[i0 + 2] * srcB[i1 + 2];
        target[i3 + 3] = srcA[i0 + 3] * srcB[i1 + 3];
        target[i3 + 4] = srcA[i0 + 4] * srcB[i1 + 4];
        target[i3 + 5] = srcA[i0 + 5] * srcB[i1 + 5];
        target[i3 + 6] = srcA[i0 + 6] * srcB[i1 + 6];
        target[i3 + 7] = srcA[i0 + 7] * srcB[i1 + 7];
        i += 8;
        i0 += 8;
        i1 += 8;
        i3 += 8;
      }
      if (i + 4 <= length)
      {
        target[i3] = srcA[i0] * srcB[i1];
        target[i3 + 1] = srcA[i0 + 1] * srcB[i1 + 1];
        target[i3 + 2] = srcA[i0 + 2] * srcB[i1 + 2];
        target[i3 + 3] = srcA[i0 + 3] * srcB[i1 + 3];
        i += 4;
        i0 += 4;
        i1 += 4;
        i3 += 4;
      }
      if (i + 2 <= length)
      {
        target[i3] = srcA[i0] * srcB[i1];
        target[i3 + 1] = srcA[i0 + 1] * srcB[i1 + 1];
        i += 2;
        i0 += 2;
        i1 += 2;
        i3 += 2;
      }
      if (i < length)
      {
        target[i3] = srcA[i0] * srcB[i1];
      }
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(srcA, srcAOffset, srcAOffset + length, "operand1");
      MathAssert.assertRange(srcB, srcBOffset, srcBOffset + length, "operand2");
      MathAssert.assertRange(target, targetOffset, targetOffset + length, "target");
      throw ex;
    }
  }

  /**
   * Multiply all of the elements in a vector.
   *
   * @param target is the vector to operate on.
   * @param scalar is the value to multiply by.
   * @return the target vector.
   */
  public static double[] multiplyAssign(double target[], double scalar)
  {
    return multiplyAssignRange(target, 0, target.length, scalar);
  }

  /**
   * Multiply each element of the target by the source.
   *
   * @param target is the vector to operate on.
   * @param source is the vector to multiply from.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] multiplyAssign(double target[], double[] source)
          throws IndexOutOfBoundsException
  {
    MathAssert.assertEqualLength(target, source);
    for (int i = 0; i < target.length; i++)
    {
      target[i] *= source[i];
    }
    return target;
  }

  /**
   * Multiply each element of the target by the source.
   *
   * @param target is the vector to operate on.
   * @param source is the vector to multiply from.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] multiplyAssignRange(double target[], double[] source, int begin, int end)
          throws IndexOutOfBoundsException
  {
    for (int i = begin; i < end; i++)
    {
      target[i] *= source[i];
    }
    return target;
  }

  /**
   * Multiply all of the elements in a region of one vector to another.
   *
   * @param target is the vector to operate on.
   * @param targetOffset is the start of the region in the target.
   * @param source is the vector to multiply.
   * @param sourceOffset is the start of the region in the source.
   * @param length is the length of the region to operate on.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the requested range is outside of the
   * target or source.
   */
  public static double[] multiplyAssign(
          double[] target, int targetOffset,
          double[] source, int sourceOffset, int length)
          throws IndexOutOfBoundsException
  {
    while (length > 0)
    {
      target[targetOffset] *= source[sourceOffset];
      ++targetOffset;
      ++sourceOffset;
      --length;
    }
    return target;
  }

  /**
   * Multiply all of the elements in a range.
   *
   * @param target is the vector to operate on.
   * @param scalar is the value to multiply by.
   * @param begin is the start of the range inclusive.
   * @param end is the end of the range exclusive.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * target.
   */
  public static double[] multiplyAssignRange(double[] target, int begin, int end, double scalar)
          throws IndexOutOfBoundsException
  {
    for (int i = begin; i < end; ++i)
    {
      target[i] *= scalar;
    }
    return target;
  }

  /**
   * Compute the inner product for all elements in two arrays. The length of
   * arrays must be equal.
   *
   * @param srcA
   * @param srcB
   * @return the inner product of the two vectors.
   * @throws IndexOutOfBoundsException if the lengths of the vectors differ.
   */
  public static double multiplyInner(double srcA[], double srcB[])
          throws IndexOutOfBoundsException
  {
    MathAssert.assertEqualLength(srcA, srcB);
    return multiplyInnerRange(srcA, srcB, 0, srcA.length);
  }

  /**
   * Compute the inner product for all elements in two arrays over a range.
   * Range checking is lazy.
   *
   * @param srcA
   * @param srcB
   * @param begin is the start of the range (inclusive)
   * @param end is the end of the range (exclusive)
   * @return the inner product of the two vectors.
   * @throws IndexOutOfBoundsException the range is outsize of length of
   * sources.
   */
  public static double multiplyInnerRange(double[] srcA, double[] srcB,
          int begin, int end)
          throws IndexOutOfBoundsException
  {
    try
    {
      double out = 0;
      int i = begin;
      while (end - i >= 8)
      {
        out += srcA[i] * srcB[i] + srcA[i + 1] * srcB[i + 1]
                + srcA[i + 2] * srcB[i + 2] + srcA[i + 3] * srcB[i + 3]
                + srcA[i + 4] * srcB[i + 4] + srcA[i + 5] * srcB[i + 5]
                + srcA[i + 6] * srcB[i + 6] + srcA[i + 7] * srcB[i + 7];
        i += 8;
      }
      for (; i < end; ++i)
        out += srcA[i] * srcB[i];
      return out;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(srcA, begin, end, "operand1");
      MathAssert.assertRange(srcB, begin, end, "operand2");
      throw ex;
    }
  }

  /**
   * Compute the inner product for all elements in two arrays over a range.Range
   * checking is lazy.
   *
   * @param srcA
   * @param srcB
   * @param begin is the start of the range (inclusive)
   * @param end is the end of the range (exclusive)
   * @param func
   * @return the inner product of the two vectors.
   * @throws IndexOutOfBoundsException the range is outsize of length of
   * sources.
   */
  public static double productInnerRange(double[] srcA, double[] srcB,
          int begin, int end, DoubleBinaryOperator func)
          throws IndexOutOfBoundsException
  {
    try
    {
      double out = 0;
      int i = begin;
      while (end - i >= 8)
      {
        out += func.applyAsDouble(srcA[i], srcB[i])
                + func.applyAsDouble(srcA[i + 1], srcB[i + 1])
                + func.applyAsDouble(srcA[i + 2], srcB[i + 2])
                + func.applyAsDouble(srcA[i + 3], srcB[i + 3])
                + func.applyAsDouble(srcA[i + 4], srcB[i + 4])
                + func.applyAsDouble(srcA[i + 5], srcB[i + 5])
                + func.applyAsDouble(srcA[i + 6], srcB[i + 6])
                + func.applyAsDouble(srcA[i + 7], srcB[i + 7]);
        i += 8;
      }
      for (; i < end; ++i)
        out += func.applyAsDouble(srcA[i], srcB[i]);
      return out;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(srcA, begin, end, "operand1");
      MathAssert.assertRange(srcB, begin, end, "operand2");
      throw ex;
    }
  }

  /**
   * Compute the vector product of a portion of two vectors.
   *
   * @param srcA is the first double vector
   * @param srcAOffset is the starting index in the first source
   * @param srcB is the second double vector
   * @param srcBOffset is the starting index in the second source
   * @param length is the number of elements to multiply
   * @return the inner product of the two vector regions.
   * @throws IndexOutOfBoundsException
   */
  public static double multiplyInner(
          double[] srcA, int srcAOffset,
          double[] srcB, int srcBOffset,
          int length)
          throws IndexOutOfBoundsException
  {
    try
    {
      double p = 0.0;
      int i = 0;
      int i0 = srcAOffset;
      int i1 = srcBOffset;

      while (i + 16 <= length)
      {
        p += srcA[i0] * srcB[i1];
        p += srcA[i0 + 1] * srcB[i1 + 1];
        p += srcA[i0 + 2] * srcB[i1 + 2];
        p += srcA[i0 + 3] * srcB[i1 + 3];
        p += srcA[i0 + 4] * srcB[i1 + 4];
        p += srcA[i0 + 5] * srcB[i1 + 5];
        p += srcA[i0 + 6] * srcB[i1 + 6];
        p += srcA[i0 + 7] * srcB[i1 + 7];
        p += srcA[i0 + 8] * srcB[i1 + 8];
        p += srcA[i0 + 9] * srcB[i1 + 9];
        p += srcA[i0 + 10] * srcB[i1 + 10];
        p += srcA[i0 + 11] * srcB[i1 + 11];
        p += srcA[i0 + 12] * srcB[i1 + 12];
        p += srcA[i0 + 13] * srcB[i1 + 13];
        p += srcA[i0 + 14] * srcB[i1 + 14];
        p += srcA[i0 + 15] * srcB[i1 + 15];
        i += 16;
        i0 += 16;
        i1 += 16;
      }
      if (i + 8 <= length)
      {
        p += srcA[i0] * srcB[i1];
        p += srcA[i0 + 1] * srcB[i1 + 1];
        p += srcA[i0 + 2] * srcB[i1 + 2];
        p += srcA[i0 + 3] * srcB[i1 + 3];
        p += srcA[i0 + 4] * srcB[i1 + 4];
        p += srcA[i0 + 5] * srcB[i1 + 5];
        p += srcA[i0 + 6] * srcB[i1 + 6];
        p += srcA[i0 + 7] * srcB[i1 + 7];
        i += 8;
        i0 += 8;
        i1 += 8;
      }
      if (i + 4 <= length)
      {
        p += srcA[i0] * srcB[i1];
        p += srcA[i0 + 1] * srcB[i1 + 1];
        p += srcA[i0 + 2] * srcB[i1 + 2];
        p += srcA[i0 + 3] * srcB[i1 + 3];

        i += 4;
        i0 += 4;
        i1 += 4;
      }
      if (i + 2 <= length)
      {
        p += srcA[i0] * srcB[i1];
        p += srcA[i0 + 1] * srcB[i1 + 1];
        i += 2;
        i0 += 2;
        i1 += 2;
      }
      if (i < length)
      {
        p += srcA[i0] * srcB[i1];
      }
      return p;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(srcA, srcAOffset, srcAOffset + length, "operand1");
      MathAssert.assertRange(srcB, srcBOffset, srcBOffset + length, "operand2");
      throw ex;
    }
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="divide">
  /**
   * Divide all of the elements in a vector.
   *
   * @param target is the vector to operate on.
   * @param scalar is the value to divide by.
   * @return the target vector.
   */
  public static double[] divideAssign(double[] target, double scalar)
  {
    return divideAssignRange(target, 0, target.length, scalar);
  }

  /**
   * Divide all of the elements in a range.
   *
   * @param target is the vector to operate on.
   * @param scalar is the value to divide by.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * target.
   */
  public static double[] divideAssignRange(double[] target, int begin, int end, double scalar)
          throws IndexOutOfBoundsException
  {
    for (int i = begin; i < end; i++)
    {
      target[i] /= scalar;
    }
    return target;
  }

  /**
   * Divide each element of the target by the source.
   *
   * @param target is the vector to operate on.
   * @param source is the vector to multiply from.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the vectors are different lengths.
   */
  public static double[] divideAssign(double target[], double[] source)
          throws IndexOutOfBoundsException
  {
    MathAssert.assertEqualLength(target, source);
    for (int i = 0; i < target.length; i++)
    {
      target[i] /= source[i];
    }
    return target;
  }

  /**
   * Divide all of the elements in a region of one vector to another.
   *
   * @param target is the vector to operate on.
   * @param targetOffset is the start of the region in the target.
   * @param source is the vector to divide.
   * @param sourceOffset is the start of the region in the source.
   * @param length is the length of the region to operate on.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if the requested range is outside of the
   * target or source.
   */
  public static double[] divideAssign(
          double[] target, int targetOffset,
          double[] source, int sourceOffset, int length)
          throws IndexOutOfBoundsException
  {
    while (length > 0)
    {
      target[targetOffset] /= source[sourceOffset];
      ++targetOffset;
      ++sourceOffset;
      --length;
    }
    return target;
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="string">
  /**
   * Convert a string with separated doubles into a double array. This method
   * uses regular expressions so it can be rather slow for long lists.
   *
   * @param str is a string with whitespace delimited doubles
   * @return the array of doubles converted
   */
  @Deprecated
  public static double[] fromString(String str)
  {
    return ArrayEncoding.parseDoubles(str);
  }

  public static String toString(double... values)
  {
    return ArrayEncoding.toStringDoubles(values);
  }

  public static String toString(double[] values, String format)
  {
    return ArrayEncoding.toStringDoubles(values, format);
  }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="norm">

  /**
   * Divide a double array by its sum.
   *
   * @param d
   * @return the target array with the normalization.
   */
  public static double[] normColumns1(double[] d)
  {
    return normColumns1Range(d, 0, d.length);
  }

  /**
   * Divide a double vectors by its sum squared.
   *
   * @param target
   * @return the target array with the normalization.
   */
  public static double[] normColumns2(double[] target)
  {
    return normColumns2Range(target, 0, target.length);
  }

  /**
   * Divide a double vector by its sum over a range of indices.
   *
   * @param target is the double array to modify
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * target.
   */
  public static double[] normColumns1Range(double[] target, int begin, int end)
          throws IndexOutOfBoundsException
  {
    try
    {
//      double f = DoubleArray.accumulateRange(target, begin, end, Math::abs);
      double f = DoubleArray.accumulateRange(target, begin, end, (double x) -> Math.abs(x));
      if (f == 0)
        return target;
      return DoubleArray.divideAssignRange(target, begin, end, f);
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(target, begin, end, "target");
      throw ex;
    }
  }

  /**
   * Divide a double vector by its sum squared over a range of indices.
   *
   * @param target is the double array to modify
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the target vector.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * target.
   */
  public static double[] normColumns2Range(double[] target, int begin, int end)
          throws IndexOutOfBoundsException
  {
    try
    {
      double f = DoubleArray.sumSqrRange(target, begin, end);
      if (f == 0)
        return target;
      f = Math.sqrt(f);
      return DoubleArray.divideAssignRange(target, begin, end, f);
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(target, begin, end, "target");
      throw ex;
    }
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="sum">
  /**
   * Add the sum of all of the elements in a double array.
   *
   * @param vector the array to be summed
   * @return the sum of the elements
   */
  public static double sum(double[] vector)
  {
    return sumRange(vector, 0, vector.length);
  }

  /**
   * Add the mean of all of the elements in a double array.
   *
   * @param vector the array to be summed
   * @return the sum of the elements
   */
  public static double mean(double[] vector)
  {
    return sumRange(vector, 0, vector.length) / vector.length;
  }

  /**
   * Add up all of the values in a range after applying a function.
   *
   * @param source
   * @param begin
   * @param end
   * @param f
   * @return the sum of the evaluated values.
   */
  public static double accumulateRange(double[] source, int begin, int end, DoubleUnaryOperator f)
  {
    MathAssert.assertRange(source, begin, end, "source");
    double sm = 0;
    for (int i = begin; i < end; ++i)
    {
      sm += f.applyAsDouble(source[i]);
    }
    return sm;
  }

  /**
   * Add the sum of all of the elements in a double array over a range.
   *
   * @param source the array to be summed
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the sum of the elements
   * @throws IndexOutOfBoundsException
   */
  public static double sumRange(double[] source, int begin, int end)
          throws IndexOutOfBoundsException
  {
    try
    {
      double sm = 0;
      int i = begin;
      while (i + 8 <= end)
      {
        sm += source[i] +
                source[i + 1] +
                source[i + 2] +
                source[i + 3] +
                source[i + 4] +
                source[i + 5] +
                source[i + 6] +
                source[i + 7];
        i += 8;
      }
      for (; i < end; ++i)
      {
        sm += source[i];
      }
      return sm;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(source, begin, end, "source");
      throw ex;
    }
  }

  /**
   * Add the sum of squared of all of the elements in a double array.
   *
   * @param vector the array to be summed
   * @return the sum of the elements
   */
  public static double sumSqr(double[] vector)
  {
    return DoubleArray.multiplyInner(vector, 0, vector, 0, vector.length);
  }

  /**
   * Add the sum of squared of all of the elements in a double array.
   *
   * @param source the array to be summed
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the sum of the elements
   * @throws IndexOutOfBoundsException
   */
  public static double sumSqrRange(double[] source, int begin, int end)
          throws IndexOutOfBoundsException
  {
    try
    {
      return DoubleArray.multiplyInner(source, begin, source, begin, end - begin);
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(source, begin, end, "source");
      throw ex;
    }
  }

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="vector-operations">
  /**
   * Project the vector u onto the vector v.
   *
   * @param u The vector being projected.
   * @param v The vector being projected onto.
   * @return The projection of u onto v.
   */
  public static double[] project(double[] u, double[] v)
  {
    double dotProduct = multiplyInner(u, v);
    double normSquared = sumSqr(v);
    double scalarMultiple = dotProduct / normSquared;
    return multiply(v, scalarMultiple);
  }

  /**
   * Compute the Euclidean distance between two vectors.
   *
   * @param u One vector.
   * @param v Another vector.
   * @return The distance between u and v.
   */
  public static double computeEuclideanDistance(double[] u, double[] v)
  {
    MathAssert.assertEqualLength(u, v);
    double accumulator = 0;
    for (int i = 0; i < u.length; i++)
    {
      accumulator += (u[i] - v[i]) * (u[i] - v[i]);
    }
    return Math.sqrt(accumulator);
  }

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
   * @return true if a_i <= x_i <= b_i for all i and false otherwise.
   */
  public static boolean isInElementWiseRange(double[] x, double[] a,
          double[] b)
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

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="find-extrema">
  public static int indexOf(double[] values, double d)
  {
    if (values == null)
      return -1;
    return indexOfRange(values, 0, values.length, d);
  }

  public static int indexOfRange(double[] values, int begin, int end, double d)
  {
    for (int i = begin; i < end; ++i)
      if (values[i] == d)
        return i;
    return -1;
  }

  /**
   * Finds the index of the maximum in a vector.
   *
   * @param vector is the vector to search.
   * @return the index of the maximum.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * vector.
   */
  public static int findIndexOfMaximum(double[] vector)
  {
    return findIndexOfMaximumRange(vector, 0, vector.length);
  }

  public static double max(double[] vector)
  {
    return vector[findIndexOfMaximumRange(vector, 0, vector.length)];
  }

  public static double min(double[] vector)
  {
    return vector[findIndexOfMinimumRange(vector, 0, vector.length)];
  }

  public static double std(double[] vector)
  {
    int n = vector.length;
    if (n < 2)
      return 0;
    double m0 = 0;
    double m1 = 0;
    for (int i = 0; i < n; ++i)
    {
      m0 += vector[i];
      m1 += vector[i] * vector[i];
    }
    m0 /= n;
    return Math.sqrt(1.0 / (n - 1) * (m1 - n * m0 * m0));
  }

  /**
   * Finds the index of the maximum in a region of a vector.
   *
   * @param source is the vector to search.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the index of the maximum.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * vector.
   */
  public static int findIndexOfMaximumRange(double[] source, int begin, int end)
  {
    try
    {
      int index = begin;
      for (int i = begin + 1; i < end; ++i)
      {
        if (source[i] > source[index])
        {
          index = i;
        }
      }
      return index;
    }
    catch (IndexOutOfBoundsException ex)
    {
      MathAssert.assertRange(source, begin, end, "source");
      throw ex;
    }
  }

  /**
   * Find the maximum absolute value from an array of doubles.
   *
   * @param vector is the vector to search.
   * @return the absolute maximum.
   */
  public static double findMaximumAbsolute(double vector[])
  {
    if (vector.length == 0)
      return 0;
    double mx = Math.abs(vector[0]);
    for (int i = 0; i < vector.length; ++i)
    {
      if (vector[i] > mx)
      {
        mx = vector[i];
      }
      else if (-vector[i] > mx)
      {
        mx = -vector[i];
      }
    }
    return mx;
  }

  /**
   * Finds the index of the minimum in a vector.
   *
   * @param vector is the vector to search.
   * @return the index of the minimum.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * vector.
   */
  public static int findIndexOfMinimum(double[] vector)
  {
    return findIndexOfMinimumRange(vector, 0, vector.length);
  }

  /**
   * Finds the index of the minimum in a region of a vector.
   *
   * @param vector is the vector to search.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return the index of the minimum.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * vector.
   */
  public static int findIndexOfMinimumRange(double[] vector, int begin, int end)
  {
    int index = begin;
    for (int i = begin + 1; i < end; ++i)
    {
      if (vector[i] < vector[index])
        index = i;
    }
    return index;
  }

  /**
   * Find the indices of the minimum and maximum in a vector.
   *
   * @param vector is the vector to search.
   * @return an array with the index of the minimum and the maximum.
   */
  public static int[] findIndexOfExtrema(double[] vector)
  {
    if (vector == null)
      return null;
    return findIndexOfExtremaRange(vector, 0, vector.length);
  }

  /**
   * Find the indices of the minimum and maximum in region of a vector.
   *
   * @param vector is the vector to search.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return an array with the index of the minimum and the maximum.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * vector.
   */
  public static int[] findIndexOfExtremaRange(double[] vector, int begin, int end)
          throws IndexOutOfBoundsException
  {
    if (vector == null)
      return null;

    int minIndex = begin;
    int maxIndex = begin;

    double min = vector[begin];
    double max = min;
    for (int i = begin; i < end; ++i)
    {
      if (vector[i] < min)
      {
        minIndex = i;
        min = vector[i];
      }
      else if (vector[i] > max)
      {
        maxIndex = i;
        max = vector[i];
      }
    }
    return new int[]
    {
      minIndex, maxIndex
    };
  }

  /**
   * Find the first element that meets a specified condition. Search is O(n).
   *
   * @param vector is the vector to search.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @param cond is the condition to be met.
   * @return the index of the first element to meet the condition, or -1 if not
   * found.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * vector.
   */
  public static int findIndexOfFirstRange(double[] vector, int begin, int end, DoublePredicate cond)
          throws IndexOutOfBoundsException
  {
    for (int i = begin; i < end; ++i)
    {
      if (cond.test(vector[i]))
        return i;
    }
    return -1;
  }

  /**
   * Find the last element that meets a specified condition.
   *
   * @param vector is the vector to search.
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @param cond is the condition to be met.
   * @return the last element to meet the condition.
   * @throws IndexOutOfBoundsException if requested range is outside of the
   * vector.
   */
  public static int findIndexOfLastRange(double[] vector, int begin, int end, DoublePredicate cond)
          throws IndexOutOfBoundsException
  {
    for (int i = end - 1; i >= begin; --i)
    {
      if (cond.test(vector[i]))
        return i;
    }
    return -1;
  }

  public static int countConditional(double[] vector, DoublePredicate cond)
  {
    return countConditionalRange(vector, 0, vector.length, cond);
  }

  public static int countConditionalRange(double[] vector, int begin, int end, DoublePredicate cond)
  {
    int count = 0;
    for (int i = end - 1; i >= begin; --i)
    {
      if (cond.test(vector[i]))
        count++;
    }
    return count;
  }

  /**
   * General purpose search for extrema.
   *
   * @param vector
   * @param begin
   * @param end
   * @param comp
   * @return the index of element with the largest value according to the
   * comparator.
   */
  public static int findIndexOfRange(double[] vector, int begin, int end, DoubleComparator comp)
  {
    int mx = begin;
    double value = vector[begin];
    for (int i = begin; i < end; ++i)
    {
      if (comp.compare(vector[i], value) == 1)
      {
        mx = i;
        value = vector[i];
      }
    }
    return mx;
  }

  /**
   * Reverse the order of a double array.
   *
   * @param in
   * @return the original double array with items reversed.
   */
  public static double[] reverseAssign(double[] in)
  {
    int n = in.length;
    int j = n - 1;
    for (int i = 0; i < n / 2; ++i, --j)
    {
      double v = in[i];
      in[i] = in[n - i - 1];
      in[j] = v;
    }
    return in;
  }

  public static void assertIntegerArray(double[] values)
  {
    for (double v : values)
    {
      if (v != (int) v)
        throw new MathException("Non integer value found");
    }
  }

  /**
   * Find the index of the median.
   *
   * This implementation uses sorting operations. It could be done more
   * efficiently. But as it is used infrequently, an improved implementation is
   * not required.
   *
   * This code was used at some point but does not appear to be used at present.
   * We could consider removing it.
   *
   * @param vector
   * @return
   */
  static public int findIndexOfMedian(double[] vector)
  {
    // Correct solution here is to use a varient of quick sort in which we
    // only sort the half of the data that contains the median.  We should
    // start the algorithm by using the median of 3 method.  Choose 3 random
    // values in the array and select the median of the 3 as the starting pivot.

    ArrayList< DoubleIndexImpl> in = new ArrayList<>();
    in.ensureCapacity(vector.length);
    for (int i = 0; i < vector.length; ++i)
      in.add(new DoubleIndexImpl(i, vector[i]));
    Collections.sort(in, (DoubleIndexImpl t, DoubleIndexImpl t1) -> Double.compare(t.value_, t1.value_));

    return in.get(vector.length / 2).index_;
  }

//</editor-fold>
//<editor-fold desc="equivalent" defaultstate="collapsed">
  /**
   * Determine if two vectors are equal within roundoff. Applies consideration
   * of round of to doubles. This does not obey the standard equivalent contract
   * because two doubles may be within round off but only one of them may be
   * within round off to a third. Also two arrays may have different hashCodes
   * but still be equivalent.
   *
   * @param operand1 is the first operand.
   * @param operand2 is the second operand.
   * @return true if the vectors are equal within the accuracy of doubles.
   */
  public static boolean equivalent(double[] operand1, double[] operand2)
  {
    if (operand1 == null && operand2 == null)
      return true;
    if (operand1 == null || operand2 == null)
      return false;
    if (operand1.length != operand2.length)
      return false;
    return equivalent(operand1, 0, operand2, 0, operand1.length);
  }

  /**
   * Determine if a region of two vectors are equal.
   *
   * This requires exact bit wise matches.
   *
   * @param a
   * @param aOffset
   * @param b
   * @param bOffset
   * @param length is the length of the region to compare.
   * @return true if the vectors are equal within the accuracy of doubles.
   */
  public static boolean equals(
          double[] a, int aOffset,
          double[] b, int bOffset,
          int length)
  {
    MathAssert.assertRange(a, aOffset, aOffset + length);
    MathAssert.assertRange(b, bOffset, bOffset + length);
    while (length > 0)
    {
      double av = a[aOffset++];
      double bv = b[bOffset++];
      if (av != bv)
        return false;
      --length;
    }
    return true;
  }

  /**
   * Determine if a region of two vectors are equal within the limits of
   * precision.
   *
   * @param a
   * @param aOffset
   * @param b
   * @param bOffset
   * @param length is the length of the region to compare.
   * @return true if the vectors are equal within the accuracy of doubles.
   */
  public static boolean equivalent(
          double[] a, int aOffset,
          double[] b, int bOffset,
          int length)
  {
    MathAssert.assertRange(a, aOffset, aOffset + length);
    MathAssert.assertRange(b, bOffset, bOffset + length);

    while (length > 0)
    {
      double av = a[aOffset++];
      double bv = b[bOffset++];
      if (Math.abs(av - bv) > 1e-14 * (Math.abs(av) + Math.abs(bv)))
        return false;
      --length;
    }
    return true;
  }

  /**
   * Determine if any element of a double array is NaN.
   *
   * @param vector
   * @return true if one of the elements is NaN.
   */
  public static boolean isNaN(double[] vector)
  {
    return isNaNRange(vector, 0, vector.length);
  }

  /**
   * Determine if any element of a double array is NaN.
   *
   * @param vector
   * @param begin is the start of the range, inclusive.
   * @param end is the end of the range, exclusive.
   * @return true if one of the elements is NaN.
   */
  public static boolean isNaNRange(double[] vector, int begin, int end)
  {
    for (int i = begin; i < end; ++i)
    {
      if (vector[i] != vector[i])
        return true;
    }
    return false;
  }

//</editor-fold>
//<editor-fold desc="conversion" defaultstate="collapsed">
  /**
   * Convert a array of boxed Doubles into primitives.
   *
   * @param source is an array of boxed doubles.
   * @return an array of primitives containing the contexts of source.
   * @throws NullPointerException if one of the elements in null.
   */
  public static double[] toPrimitives(Double[] source)
          throws NullPointerException
  {
    double out[] = new double[source.length];
    for (int i = 0; i < source.length; ++i)
      out[i] = source[i];
    return out;
  }

  public static double[] newArray(double... v)
  {
    return v;
  }

  /**
   * Convert a collection of boxed Doubles into primitives.
   *
   * @param source is a collection of boxed doubles.
   * @return an array of primitives containing the contexts of source.
   * @throws NullPointerException if one of the elements in null.
   */
  public static double[] toPrimitives(Collection<? extends Number> source)
  {
    int i = 0;
    double out[] = new double[source.size()];
    for (Number f : source)
      out[i++] = f.doubleValue();
    return out;
  }

  /**
   * Convert an array of doubles into boxed types. Use
   * Arrays.asList(DoubleArray.toObjects(v)) if a container is required.
   *
   * @param source is an array of primitives to convert.
   * @return an array containing boxed doubles.
   */
  static public Double[] toObjects(double[] source)
  {
    if (source == null)
      return null;
    Double[] out = new Double[source.length];
    for (int i = 0; i < source.length; ++i)
      out[i] = source[i];
    return out;
  }

//</editor-fold>
//<editor-fold desc="functions" defaultstate="collapsed">
  /**
   * Compute the exponential of each element of a list.
   *
   * @param source is the operand.
   * @return a vector which is the exponentials.
   */
  public static double[] exp(double[] source)
  {
    return apply(source, Math::exp);
  }

  /**
   * Compute the power of each element of a list
   *
   * @param source is the operand.
   * @param power.
   * @return a vector which is the powers.
   */
  public static double[] pow(double[] source, double power)
  {
    return apply(source, (double d) -> Math.pow(d, power));
  }

  /**
   * Apply an operator to every element of an array.
   *
   * @param input is an array of doubles.
   * @param operator is the operator to apply.
   * @return a new array holding the result of applying the operator to each of
   * the elements, or null if input is null.
   */
  static public double[] apply(double[] input, DoubleUnaryOperator operator)
  {
    if (input == null)
      return null;

    double out[] = new double[input.length];
    for (int i = 0; i < input.length; ++i)
      out[i] = operator.applyAsDouble(input[i]);
    return out;
  }

  /**
   * Apply an operator to every element of an array and assign it to the
   * corresponding input element. This will mutate the input array.
   *
   * @param input is an array of doubles.
   * @param operator is the operator to apply.
   * @return the result of applying the operator to the elements, or null if
   * input is null.
   */
  static public double[] applyAssign(double[] input, DoubleUnaryOperator operator)
  {
    return applyAssignRange(input, operator, 0, input.length);
  }

  static public double[] applyAssign(double[] out, double[] in, DoubleUnaryOperator operator)
  {
    MathAssert.assertEqualLength(out, in);
    for (int i = 0; i < out.length; i++)
    {
      out[i] = operator.applyAsDouble(in[i]);
    }
    return out;
  }

  /**
   * Apply an operator to every element of an array and assign it to the
   * corresponding input element.This will mutate the input array.
   *
   * This will mutate the input array.
   *
   * @param input is an array of doubles.
   * @param operator
   * @param begin
   * @param end
   * @return the result of applying the operator to the elements, or null if
   * input is null.
   */
  static public double[] applyAssignRange(double[] input, DoubleUnaryOperator operator, int begin, int end)
  {
    if (input == null)
      return null;
    for (int i = begin; i < end; ++i)
      input[i] = operator.applyAsDouble(input[i]);
    return input;
  }

//</editor-fold>
//<editor-fold desc="sort" defaultstate="collapsed">
  /**
   * Remove.
   *
   * FIXME This algorithm does not appear to be sufficiently generalized.
   * Identify the user of this code and refactor.
   *
   * @param a
   * @param b
   */
  public static void sortPairs(double[] a, double[] b)
  {
    ArrayList<DoublePair> contents = new ArrayList<>();
    for (int i = 0; i < a.length; ++i)
      contents.add(new DoublePair(a[i], b[i]));
    Collections.sort(contents);
    for (int i = 0; i < a.length; ++i)
    {
      a[i] = contents.get(i).first;
      b[i] = contents.get(i).second;
    }
  }

  static class DoublePair implements Comparable<DoublePair>
  {
    double first;
    double second;

    DoublePair(double a, double b)
    {
      first = a;
      second = b;
    }

    @Override
    public int compareTo(DoublePair o)
    {
      return Double.compare(first, o.first);
    }
  }

//</editor-fold>
}
