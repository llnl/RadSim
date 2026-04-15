/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test code for DoubleComparator.
 */
public class DoubleComparatorNGTest
{

  /**
   * Test of compare method from class Positive, of class DoubleArray.
   */
  @Test
  public void testComparePositive()
  {
    DoubleComparator comp = new DoubleComparator.Positive();
    Assert.assertTrue(comp.compare(-1, -1) == 0);
    Assert.assertTrue(comp.compare(-1, 0) < 0);
    Assert.assertTrue(comp.compare(-1, 1) < 0);
    Assert.assertTrue(comp.compare(0, -1) > 0);
    Assert.assertTrue(comp.compare(0, 0) == 0);
    Assert.assertTrue(comp.compare(0, 1) < 0);
    Assert.assertTrue(comp.compare(1, -1) > 0);
    Assert.assertTrue(comp.compare(1, 0) > 0);
    Assert.assertTrue(comp.compare(1, 1) == 0);
  }

  /**
   * Test of compare method from class Negative, of class DoubleArray.
   */
  @Test
  public void testCompareNegative()
  {
    DoubleComparator comp = new DoubleComparator.Negative();
    Assert.assertTrue(comp.compare(-1, -1) == 0);
    Assert.assertTrue(comp.compare(-1, 0) > 0);
    Assert.assertTrue(comp.compare(-1, 1) > 0);
    Assert.assertTrue(comp.compare(0, -1) < 0);
    Assert.assertTrue(comp.compare(0, 0) == 0);
    Assert.assertTrue(comp.compare(0, 1) > 0);
    Assert.assertTrue(comp.compare(1, -1) < 0);
    Assert.assertTrue(comp.compare(1, 0) < 0);
    Assert.assertTrue(comp.compare(1, 1) == 0);
  }

  /**
   * Test of compare method from class Absolute, of class DoubleArray.
   */
  @Test
  public void testCompareAbsolute()
  {
    DoubleComparator comp = new DoubleComparator.Absolute();
    Assert.assertTrue(comp.compare(-1, -1) == 0, "-1 <=> -1");
    Assert.assertTrue(comp.compare(-1, 0) > 0, "-1 <=> 0");
    Assert.assertTrue(comp.compare(-1, 1) == 0, "-1 <=> 1");
    Assert.assertTrue(comp.compare(0, -1) < 0, "0 <=> -1");
    Assert.assertTrue(comp.compare(0, 0) == 0, "0 <=> 0");
    Assert.assertTrue(comp.compare(0, 1) < 0, "0 <=> 1");
    Assert.assertTrue(comp.compare(1, -1) == 0, "1 <=> -1");
    Assert.assertTrue(comp.compare(1, 0) > 0, "1 <=> 0");
    Assert.assertTrue(comp.compare(1, 1) == 0, "1 <=> 1");
  }

}
