/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind.readers;

import gov.llnl.utility.xml.bind.readers.DurationContents;
import gov.llnl.utility.xml.bind.ElementContextImpl;
import gov.llnl.utility.xml.bind.ReaderContextImpl;
import gov.llnl.utility.io.ReaderException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Test code for DurationContents.
 */
public class DurationContentsNGTest
{

  public DurationContentsNGTest()
  {
  }

  /**
   * Test of getObjectClass method, of class DurationContents.
   */
  @Test
  public void testGetObjectClass()
  {
    DurationContents instance = new DurationContents();
    assertEquals(instance.getObjectClass(), Duration.class);
  }

  /**
   * Test of start method, of class DurationContents.
   */
  @Test(expectedExceptions =
  {
    ReaderException.class
  })
  public void testStart() throws Exception
  {
    AttributesImpl attr = new AttributesImpl();
    attr.addAttribute("", "units", "units", "null", null);
    ElementContextImpl ec = new ElementContextImpl(null, null, null, null, null)
    {
      @Override
      public Attributes getAttributes()
      {
        return attr;
      }
    };
    ReaderContextImpl rc = new ReaderContextImpl()
    {
      @Override
      public ElementContextImpl getCurrentContext()
      {
        return ec;
      }
    };
    DurationContents dc = new DurationContents();

    assertNull(dc.start(null, attr));

    // Set nanos
    attr.setValue(0, "ns");
    assertNull(dc.start(rc, attr));
    assertTrue(dc.contents(rc, "1").equals(Duration.of(1L, ChronoUnit.NANOS)));

    // Set micros
    attr.setValue(0, "us");
    assertNull(dc.start(rc, attr));
    assertTrue(dc.contents(rc, "1").equals(Duration.of(1L, ChronoUnit.MICROS)));

    // Set millis
    attr.setValue(0, "ms");
    assertNull(dc.start(rc, attr));
    assertTrue(dc.contents(rc, "1").equals(Duration.of(1L, ChronoUnit.MILLIS)));

    // Set seconds
    attr.setValue(0, "s");
    assertNull(dc.start(rc, attr));
    assertTrue(dc.contents(rc, "1").equals(Duration.of(1L, ChronoUnit.SECONDS)));

    // Test ReaderException
    attr.setValue(0, "superseconds");
    dc.start(rc, attr);
    dc.contents(rc, "1");
  }

  /**
   * Test of contents method, of class DurationContents.
   *
   * @throws java.lang.Exception
   */
  @Test(expectedExceptions =
  {
    ReaderException.class
  })
  public void testContents() throws Exception
  {
    AttributesImpl attr = new AttributesImpl();
    ElementContextImpl ec = new ElementContextImpl(null, null, null, null, null)
    {
      @Override
      public Attributes getAttributes()
      {
        return attr;
      }
    };
    ReaderContextImpl rc = new ReaderContextImpl()
    {
      @Override
      public ElementContextImpl getCurrentContext()
      {
        return ec;
      }
    };
    DurationContents dc = new DurationContents();

    // When timeUnit is null
    assertTrue(dc.contents(rc, "PT3.141592653S").equals(Duration.of(3141592653L, ChronoUnit.NANOS)));

    // When timeUnit is set
    attr.addAttribute("", "units", "units", "string", "ns");
    dc.start(null, attr);
    Duration duration = dc.contents(rc, "3141592653");
    assertEquals(duration.toString(), "PT3.141592653S");
    assertTrue(duration.equals(Duration.of(3141592653L, ChronoUnit.NANOS)));

    // Test NumberFormatException
    try
    {
      dc.contents(rc, "T2.0");
    }
    catch (NumberFormatException ex)
    {
      // Test passed
    }

    // Test DateTimeParseException
    dc = new DurationContents();
    dc.contents(rc, "3.14");
  }
}
