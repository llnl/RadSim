/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Cursor.
 */
public class CursorNGTest
{

  public CursorNGTest()
  {
  }

  /**
   * Test of seek method with uniformly spaced intervals.
   */
  @Test
  public void testSeek_UniformIntervals()
  {
    // Create a uniform array of intervals
    double[] intervals = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0};
    Cursor instance = new Cursor(intervals);

    // Test seeking to exact points
    int index = instance.seek(0.0);
    assertEquals(index, 0);
    assertEquals(instance.getFraction(), 0.0, 1e-12);

    index = instance.seek(2.0);
    assertEquals(index, 2);
    assertEquals(instance.getFraction(), 0.0, 1e-12);

    index = instance.seek(5.0);
    assertEquals(index, 4);
    assertEquals(instance.getFraction(), 1.0, 1e-12);

    // Test seeking to points between intervals
    index = instance.seek(2.5);
    assertEquals(index, 2);
    assertEquals(instance.getFraction(), 0.5, 1e-12);

    index = instance.seek(3.75);
    assertEquals(index, 3);
    assertEquals(instance.getFraction(), 0.75, 1e-12);

    index = instance.seek(0.25);
    assertEquals(index, 0);
    assertEquals(instance.getFraction(), 0.25, 1e-12);
  }

  /**
   * Test of seek method with non-uniformly spaced intervals.
   */
  @Test
  public void testSeek_NonUniformIntervals()
  {
    // Create a non-uniform array of intervals
    double[] intervals = {0.0, 1.0, 3.0, 6.0, 10.0};
    Cursor instance = new Cursor(intervals);

    // Test seeking to exact points
    int index = instance.seek(0.0);
    assertEquals(index, 0);
    assertEquals(instance.getFraction(), 0.0, 1e-12);

    index = instance.seek(3.0);
    assertEquals(index, 2);
    assertEquals(instance.getFraction(), 0.0, 1e-12);

    index = instance.seek(10.0);
    assertEquals(index, 3);
    assertEquals(instance.getFraction(), 1.0, 1e-12);

    // Test seeking to points between intervals
    index = instance.seek(2.0);
    assertEquals(index, 1);
    assertEquals(instance.getFraction(), 0.5, 1e-12); // 2.0 is halfway between 1.0 and 3.0

    index = instance.seek(4.5);
    assertEquals(index, 2);
    assertEquals(instance.getFraction(), 0.5, 1e-12); // 4.5 is halfway between 3.0 and 6.0

    index = instance.seek(9.0);
    assertEquals(index, 3);
    assertEquals(instance.getFraction(), 0.75, 1e-12); // 9.0 is 75% between 6.0 and 10.0
  }

  /**
   * Test of seek method with extrapolation below and above ranges.
   */
  @Test
  public void testSeek_Extrapolation()
  {
    // Create an array of intervals
    double[] intervals = {1.0, 2.0, 3.0, 4.0, 5.0};
    Cursor instance = new Cursor(intervals);

    // Test seeking to a point below the lowest interval
    int index = instance.seek(0.0);
    assertEquals(index, 0);
    assertTrue(instance.getFraction() < 0.0); // Negative fraction for extrapolation below range

    // Test seeking to a point above the highest interval
    index = instance.seek(6.0);
    assertEquals(index, 3);
    assertTrue(instance.getFraction() > 1.0); // Fraction > 1.0 for extrapolation above range
  }

  /**
   * Test of seek method with edge cases.
   */
  @Test
  public void testSeek_EdgeCases()
  {
    // Test with a single interval (2 points)
    double[] intervals = {1.0, 2.0};
    Cursor instance = new Cursor(intervals);

    // Should handle single interval correctly
    int index = instance.seek(1.5);
    assertEquals(index, 0);
    assertEquals(instance.getFraction(), 0.5, 1e-12);

    // Test with empty array (should not crash, but behavior may vary)
    double[] empty = {};
    Cursor emptyInstance = new Cursor(empty);
    // No assertion as the behavior might be undefined, but it shouldn't crash

    // Test with very small interval
    double[] smallInterval = {1.0, 1.0 + 1e-15};
    Cursor smallInstance = new Cursor(smallInterval);
    index = smallInstance.seek(1.0 + 5e-16);
    assertEquals(index, 0);
    assertEquals(smallInstance.getFraction(), 0.5, 1e-1); // Approximate due to floating point precision
  }

  /**
   * Test the getIndex and getFraction methods.
   */
  @Test
  public void testGetIndexAndFraction()
  {
    double[] intervals = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0};
    Cursor instance = new Cursor(intervals);

    // Seek and then check getIndex and getFraction
    instance.seek(3.0);
    assertEquals(instance.getIndex(), 1);
    assertEquals(instance.getFraction(), 0.5, 1e-12);

    // Seek again and check
    instance.seek(7.0);
    assertEquals(instance.getIndex(), 3);
    assertEquals(instance.getFraction(), 0.5, 1e-12);

    // Seek to exact interval point
    instance.seek(4.0);
    assertEquals(instance.getIndex(), 2);
    assertEquals(instance.getFraction(), 0.0, 1e-12);
  }

  /**
   * Test the get method.
   */
  @Test
  public void testGet()
  {
    double[] intervals = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0};
    Cursor instance = new Cursor(intervals);

    // Test getting interval values directly
    assertEquals(instance.get(0), 0.0, 1e-12);
    assertEquals(instance.get(2), 4.0, 1e-12);
    assertEquals(instance.get(5), 10.0, 1e-12);
  }

  /**
   * Test the constructor with start and end parameters.
   */
  @Test
  public void testConstructorWithStartAndEnd()
  {
    double[] intervals = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0};
    int start = 1;
    int end = 5;
    Cursor instance = new Cursor(intervals, start, end);

    // Seek a value within the specified range
    instance.seek(5.0);
    assertEquals(instance.getIndex(), 2);
    assertEquals(instance.getFraction(), 0.5, 1e-12);
    assertEquals(intervals[instance.getIndex()]*(1-instance.getFraction()) + intervals[instance.getIndex()+1]*instance.getFraction(), 5.0, 1e-12);

    // Seek to a point outside the narrowed range but inside the array
    // Should still work but be clamped to the range specified in constructor
    instance.seek(9.0);
    assertEquals(instance.getIndex(), 3);
    assertEquals(instance.getFraction(), 1.5, 1e-12);
    assertEquals(intervals[instance.getIndex()]*(1-instance.getFraction()) + intervals[instance.getIndex()+1]*instance.getFraction(), 9.0, 1e-12);
  }

  /**
   * Test the static seekSegment method.
   */
  @Test
  public void testSeekSegment()
  {
    double[] intervals = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0};
    int begin = 0;
    int end = intervals.length;
    int current = 2; // Start search from index 2
    int log = Cursor.seekLog(end - begin);

    // Test segment seeking
    int result = Cursor.seekSegment(3.0, intervals, begin, end, current, log);
    assertEquals(result, 1); // Should find segment index 1 (between 2.0 and 4.0)

    result = Cursor.seekSegment(7.0, intervals, begin, end, current, log);
    assertEquals(result, 3); // Should find segment index 3 (between 6.0 and 8.0)

    result = Cursor.seekSegment(1.0, intervals, begin, end, current, log);
    assertEquals(result, 0); // Should find segment index 0 (between 0.0 and 2.0)
  }

  /**
   * Test the static findInterval method.
   */
  @Test
  public void testFindInterval()
  {
    double[] intervals = {0.0, 2.0, 4.0, 6.0, 8.0, 10.0};
    int start = 0;
    int end = intervals.length;

    // Test interval finding for exact match
    int result = Cursor.findInterval(intervals, start, end, 4.0);
    assertEquals(result, 2); // Should find value at index 2 (4.0)

    // Test interval finding for value between intervals
    result = Cursor.findInterval(intervals, start, end, 5.0);
    assertEquals(result, 2); // Should return index 2 (interval containing 5.0 is [4.0, 6.0])

    // Test interval finding for value below range
    result = Cursor.findInterval(intervals, start, end, -1.0);
    assertEquals(result, 0); // Should return first interval

    // Test interval finding for value above range
    result = Cursor.findInterval(intervals, start, end, 11.0);
    assertEquals(result, end - 2); // Should return last interval
  }
}