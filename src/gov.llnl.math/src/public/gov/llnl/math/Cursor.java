/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import java.util.Arrays;

/**
 * Cursors are used by interpolators to find the location in an ordered lists.
 *
 * @author nelson85
 */
public class Cursor
{
  final double[] intervals;
  final int start;
  final int end;
  final int log;

  int index; // location of the cursor
  double fraction; // strictly 0<= fraction <=1 unless we are extrapolating

  /**
   * Search a limited range.
   * 
   * @param x Array to search.
   * @param start Starting point (inclusive).
   * @param end Ending point (exclusive)
   */
  public Cursor(double[] x, int start, int end)
  {
    this.intervals = x;
    this.start = start;
    this.end = end;
    this.index = (end + start) / 2;
    log = seekLog(end - start);
  }

  public Cursor(double[] x)
  {
    this(x, 0, x.length);
  }

  /**
   * Reposition the cursor.
   *
   * This updates fraction accordingly.
   *
   * @param x is the value to seek.
   * @return the interval in which this sample falls.
   */
  public int seek(double x)
  {
    if (intervals.length < 2)
    {
      index = 0;
      fraction = 0;
      return index;
    }
    this.index = seekSegment(x, intervals, start, end, index, log);
    double x0 = intervals[index];
    double x1 = intervals[index + 1];
    fraction = (x - x0) / (x1 - x0);
    return index;
  }

  public int getIndex()
  {
    return index;
  }

  /**
   * @return the fraction
   */
  public double getFraction()
  {
    return fraction;
  }

  public double get(int i)
  {
    return intervals[i];
  }

  /**
   * Find the interval that contains x using the prior position.
   *
   * Attempt locate the interval that contains the value.If the value is below
   * the first interval then the first interval is returned if the value is
   * above the first interval then the last interval is returned.
   *
   * The previous search position is passed as current such that repeated
   * searches while iterating across the intervals is quick.
   *
   * This code makes the assumption that the intervals are reasonably close to
   * evenly spaces so that it can predict whether it is faster to perform a
   * binary search or linear search.
   *
   * This code assumes that the interval edges are sorted. If this is not the
   * case then the output not defined.
   *
   * @param x is the value to find the interval.
   * @param intervals is the list of intervals to search which must be an
   * ordered array.
   * @param begin is the start of the range (inclusive).
   * @param end is the end of the range (exclusive).
   * @param current is the previous search location to be used to begin the
   * search.
   * @param log is the log based 2 for for the intervals list.
   * @return the index that contains the requested value.
   */
  public static int seekSegment(double x, double[] intervals, int begin, int end, int current, int log)
  {
    if (current < begin)
      current = begin;
    if (current + 1 >= end)
      current = end - 2;
    double x0 = intervals[current];
    double x1 = intervals[current + 1];
    if (x >= x0)
    {
      // Same segment
      if (x < x1)
      {
        return current;
      }
      // Predict if we are better to use linear or log search
      if ((x - x0) > (x1 - x0) * log)
      {
        // Use binary search
        current = findInterval(intervals, current, end, x);
      }
      else
      {
        // Use linear forward search
        while (current + 2 < end && intervals[current + 1] <= x)
          current++;
      }
    }
    else
    {
      // Predict if we are better to use linear or log search
      if ((x0 - x) > (x1 - x0) * log)
      {
        // Use binary search
        current = findInterval(intervals, begin, current, x);
      }
      else
      {
        // Use linear forward search
        while (current > begin && intervals[current] > x)
          current--;
      }
    }
    return current;
  }

  final static double LOG2 = Math.log(2);

  public static int seekLog(int v)
  {
    return (int) Math.ceil(Math.log(v) / LOG2);
  }

  /**
   * Binary search to determine the interval that a value falls into.
   *
   * @param v
   * @param start
   * @param end
   * @param x
   * @return
   */
  public static int findInterval(double[] v, int start, int end, double x)
  {
    int i = Arrays.binarySearch(v, start, end, x);
    if (i < 0)
    {
      i = -i - 2;
      if (i < 0)
        return 0;
    }
    if (i + 1 >= end)
      return end - 2;
    return i;
  }

}
