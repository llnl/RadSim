/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for DateUtilities.
 */
public class DateUtilitiesNGTest
{

  public DateUtilitiesNGTest()
  {
  }

  @Test
  public void testWithZone()
  {
    String value = "2019-05-15T16:12:04-04:00";
    assertEquals(DateUtilities.convert(value, Instant::from).atZone(ZoneId.of("-04:00")).toString(),
            "2019-05-15T16:12:04-04:00");
    //012345678901234567890123
    //          1         2
    value = "2019-05-15T16:12:04-04";
    DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value);
    value = "2019-05-15T16:12:04.00-0400";
    assertEquals(DateUtilities.convert(value, Instant::from).atZone(ZoneId.of("-04:00")).toString(),
            "2019-05-15T16:12:04-04:00");
  }

  /**
   * Test of getIso8601 method, of class DateUtilities.
   */
  @Test
  public void testGetIso8601()
  {
    String expResult = "1970-01-01T00:00:00Z";
    String result = DateUtilities.getIso8601(Instant.ofEpochSecond(0));
    assertEquals(result, expResult);
  }

  /**
   * Test of match method, of class DateUtilities.
   */
  @Test
  public void testMatch()
  {
    DateTimeFormatter result = DateUtilities.match("2010/01/02 12:00:00");
    assertNotNull(result);
  }

  /**
   * Test of convert method, of class DateUtilities.
   */
  @Test
  public void testConvert()
  {
//    String time = "";
//    TemporalQuery<T> query = null;
//    Object expResult = null;
//    Object result = DateUtilities.convert(time, query);
//    assertEquals(result, expResult);
  }

}
