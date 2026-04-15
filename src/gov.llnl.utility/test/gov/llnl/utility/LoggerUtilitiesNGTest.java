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
 * Test code for LoggerUtilities.
 */
public class LoggerUtilitiesNGTest
{

  public LoggerUtilitiesNGTest()
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
   * Test of format method, of class LoggerUtilities.
   */
  @Test
  public void testFormat()
  {
    StringBuilder sb = new StringBuilder();
    Logger logger = new Logger("Test", UtilityPackage.LOGGER.getResourceBundleName())
    {
      @Override
      public boolean isLoggable(Level level)
      {
        return true;
      }

      @Override
      public void log(Level level, String msg)
      {
        sb.append("log ").append(level.toString()).append(' ').append(msg);
      }
    };
    Level level = Level.ALL;
    String fmt = "Test %d %s";
    LoggerUtilities.format(logger, level, fmt, 1, "Hello");
    assertEquals(sb.toString(), "log ALL Test 1 Hello");
  }

}
