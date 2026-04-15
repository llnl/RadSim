/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for LoggerStream.
 */
public class LoggerStreamNGTest
{

  public LoggerStreamNGTest()
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
   * Test of create method, of class LoggerStream.
   */
  @Test
  public void testCreate()
  {
    Logger logger = UtilityPackage.LOGGER;
    Level level = Level.ALL;
    LoggerStream result = LoggerStream.create(logger, level);
    assertNotNull(result);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void testCreate1()
  {
    Logger logger = null;
    Level level = Level.ALL;
    LoggerStream result = LoggerStream.create(logger, level);
    assertNotNull(result);
  }

  @Test(expectedExceptions = NullPointerException.class)
  public void testCreate2()
  {
    Logger logger = UtilityPackage.LOGGER;
    Level level = null;
    LoggerStream result = LoggerStream.create(logger, level);
    assertNotNull(result);
  }

  /**
   * Test of close method, of class LoggerStream.
   */
  @Test
  public void testClose()
  {
    StringBuilder sb = new StringBuilder();
    Logger harness = new Logger("Test", UtilityPackage.LOGGER.getResourceBundleName())
    {
      public void log(Level level, String msg)
      {
        sb.append("log ").append(level.toString()).append(' ').append(msg);
      }

      public void logp(Level level, String cls, String mth, String msg)
      {
        sb.append("log ").append(level.toString())
                .append(' ').append(cls)
                .append(' ').append(mth)
                .append(' ').append(msg);
      }
    };
    LoggerStream instance = LoggerStream.create(harness, Level.ALL);
    instance.println("test");
    instance.println("hello");
    instance.close();
    assertEquals(sb.toString(),
            "log ALL gov.llnl.utility.LoggerStreamNGTest testClose test" + System.lineSeparator()
            + "hello" + System.lineSeparator());
  }

}
