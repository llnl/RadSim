/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Arrays;
import java.util.List;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for MutableDuration.
 */
public class MutableDurationNGTest
{

  public MutableDurationNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

  /**
   * Test of get method, of class MutableDuration.
   */
  @Test
  public void testGet()
  {
    MutableDuration instance = new MutableDuration(500, 1000);
    assertEquals(instance.get(ChronoUnit.SECONDS), 500);
    assertEquals(instance.get(ChronoUnit.NANOS), 1000);
  }

  @Test(expectedExceptions = UnsupportedTemporalTypeException.class)
  public void testGet1()
  {
    MutableDuration instance = new MutableDuration(500, 1000);
    instance.get(ChronoUnit.DAYS);
  }

  /**
   * Test of getUnits method, of class MutableDuration.
   */
  @Test
  public void testGetUnits()
  {
    MutableDuration instance = new MutableDuration();
    List expResult = Arrays.asList(ChronoUnit.SECONDS, ChronoUnit.NANOS);
    List result = instance.getUnits();
    assertEquals(result, expResult);
  }

  /**
   * Test of addTo method, of class MutableDuration.
   */
  @Test
  public void testAddTo()
  {
    Temporal temporal = Instant.ofEpochMilli(5000);
    MutableDuration instance = MutableDuration.ofSeconds(10);
    Temporal expResult = Instant.ofEpochMilli(15000);
    Temporal result = instance.addTo(temporal);
    assertEquals(result, expResult);
  }

  /**
   * Test of subtractFrom method, of class MutableDuration.
   */
  @Test
  public void testSubtractFrom()
  {
    Temporal temporal = Instant.ofEpochSecond(20);
    MutableDuration instance = MutableDuration.ofSeconds(5);
    Temporal expResult = Instant.ofEpochSecond(15);
    Temporal result = instance.subtractFrom(temporal);
    assertEquals(result, expResult);
  }

  /**
   * Test of addAssign method, of class MutableDuration.
   */
  @Test
  public void testAddAssign()
  {
    Duration d = Duration.ofSeconds(5);
    MutableDuration instance = new MutableDuration();
    instance.addAssign(d);
    assertEquals(instance, MutableDuration.ofSeconds(5));
    instance.addAssign(d);
    assertEquals(instance, MutableDuration.ofSeconds(10));
  }

  /**
   * Test of toDuration method, of class MutableDuration.
   */
  @Test
  public void testToDuration()
  {
    MutableDuration instance = MutableDuration.ofSeconds(30);
    Duration expResult = Duration.ofSeconds(30);
    Duration result = instance.toDuration();
    assertEquals(result, expResult);
  }

  /**
   * Test of compareTo method, of class MutableDuration.
   */
  @Test
  public void testCompareTo()
  {
    TemporalAmount o = Duration.ofSeconds(5);
    MutableDuration instance = MutableDuration.ofSeconds(5);
    assertEquals(instance.compareTo(Duration.ofSeconds(5)), 0);
    assertEquals(instance.compareTo(Duration.ofSeconds(15)), -1);
    assertEquals(instance.compareTo(Duration.ofSeconds(0)), 1);
  }

  /**
   * Test of equals method, of class MutableDuration.
   */
  @Test
  public void testEquals()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    Duration dField = Duration.ZERO;
    MutableDuration mdField = new MutableDuration(10L, 1);
    TemporalAmount taField = (TemporalAmount) new MutableDuration(10L, 1);
    assertFalse(instance.equals(dField));
    assertTrue(instance.equals(mdField));
    assertTrue(instance.equals(taField));
  }

  /**
   * Test of clear method, of class MutableDuration.
   */
  @Test
  public void testClear()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    instance.clear();
    assertTrue(instance.seconds == 0);
    assertTrue(instance.nanos == 0);
  }

  /**
   * Test of isZero method, of class MutableDuration.
   */
  @Test
  public void testIsZero()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    assertFalse(instance.isZero());
    instance = new MutableDuration(0, 1);
    assertFalse(instance.isZero());
    instance = new MutableDuration(0, 0);
    assertTrue(instance.isZero());
  }

  /**
   * Test of isNegative method, of class MutableDuration.
   */
  @Test
  public void testIsNegative()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    assertFalse(instance.isNegative());
    instance = new MutableDuration(-10L, 1);
    assertTrue(instance.isNegative());
  }

  /**
   * Test of assign method, of class MutableDuration.
   */
  @Test
  public void testAssign()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    Duration dField = Duration.ZERO;
    instance.assign(dField);
    assertEquals(instance, new MutableDuration(0, 0));
  }

  /**
   * Test of subtractAssign method, of class MutableDuration.
   */
  @Test
  public void testSubtractAssign()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    Duration dField = Duration.ZERO;
    instance.subtractAssign(dField);
    assertEquals(instance, new MutableDuration(10L, 1));
  }

  /**
   * Test of toMillis method, of class MutableDuration.
   */
  @Test
  public void testToMillis()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    long expResult = 10000;
    long result = instance.toMillis();
    assertEquals(result, expResult);
  }

  /**
   * Test of of method, of class MutableDuration.
   */
  @Test
  public void testOf()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    Duration dField = Duration.ZERO;
    MutableDuration result = new MutableDuration(0, 0);
    MutableDuration expResult = instance.of(dField);
    assertEquals(result, expResult);
  }

  /**
   * Test of ofSeconds method, of class MutableDuration.
   */
  @Test
  public void testOfSeconds()
  {
    // Tested in testOf
  }

  /**
   * Test of of toString, of class MutableDuration.
   */
  @Test
  public void testToString()
  {
    MutableDuration instance = new MutableDuration(10L, 1);
    String result = instance.toString();
    String expResult = "PT10.000000001S";
    assertEquals(result, expResult);
  }
}
