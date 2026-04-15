/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.math;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import java.time.Duration;

/**
 * Test code for TimeUtilities.
 */
public class TimeUtilitiesNGTest
{

  public TimeUtilitiesNGTest()
  {
  }

  /**
   * Test of convertTimeConstant method with double arguments.
   */
  @Test
  public void testConvertTimeConstant_double_double()
  {
    // Test case 1: dt much smaller than timeConstant
    double timeConstant = 100.0;
    double dt = 1.0;
    double expResult = 0.00995; // approximately 1/timeConstant for small dt/timeConstant
    double result = TimeUtilities.convertTimeConstant(timeConstant, dt);
    assertEquals(result, expResult, 1e-5);

    // Test case 2: dt equals timeConstant
    timeConstant = 10.0;
    dt = 10.0;
    expResult = 0.6321205588; // 1 - e^(-1)
    result = TimeUtilities.convertTimeConstant(timeConstant, dt);
    assertEquals(result, expResult, 1e-10);

    // Test case 3: dt much larger than timeConstant
    timeConstant = 1.0;
    dt = 100.0;
    expResult = 1.0; // approaches 1.0 as dt/timeConstant increases
    result = TimeUtilities.convertTimeConstant(timeConstant, dt);
    assertEquals(result, expResult, 1e-40); // Almost 1.0 but not exactly

    // Test case 4: Extreme values
    timeConstant = 1000000.0;
    dt = 0.001;
    expResult = 1e-9; // approximately dt/timeConstant for very small dt/timeConstant
    result = TimeUtilities.convertTimeConstant(timeConstant, dt);
    assertEquals(result, expResult, 1e-10);
  }

  /**
   * Test of convertTimeConstant method with TemporalAmount arguments.
   */
  @Test
  public void testConvertTimeConstant_TemporalAmount_TemporalAmount()
  {
    // Test using Duration objects
    Duration timeConstant = Duration.ofSeconds(10);
    Duration dt = Duration.ofSeconds(1);
    double expResult = 0.0952; // approximately dt/timeConstant for small dt/timeConstant
    double result = TimeUtilities.convertTimeConstant(timeConstant, dt);
    assertEquals(result, expResult, 1e-4);

    // Test with different durations
    timeConstant = Duration.ofMinutes(1); // 60 seconds
    dt = Duration.ofSeconds(10);
    expResult = 0.1535; // 1 - e^(-10/60)
    result = TimeUtilities.convertTimeConstant(timeConstant, dt);
    assertEquals(result, expResult, 1e-4);
  }
}