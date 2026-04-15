/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import org.testng.annotations.Test;
import java.util.logging.Logger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * Test code for LogFormatter.
 */
public class LogFormatterNGTest
{

  public LogFormatterNGTest()
  {
  }

  /**
   * Test of format method, of class LogFormatter.
   */
  @Test
  public void testFormat()
  {
    Logger logger = Logger.getLogger("TestLogFormatter");
    logger.setUseParentHandlers(false);
    LogFormatter lf = new LogFormatter();
    ConsoleHandler ch = new ConsoleHandler();
    ch.setFormatter(lf);
    ch.setLevel(Level.ALL);
    logger.addHandler(ch);

    logger.log(Level.SEVERE, "BottleCaps!", new Exception("FALLOUT!"));
    logger.log(Level.WARNING, "Warning");
    logger.log(Level.CONFIG, "CONFIG");
    logger.log(Level.INFO, "INFO");
    logger.log(Level.FINE, "FINE");
    logger.log(Level.FINER, "FINER");
    logger.log(Level.FINEST, "FINEST");

    ch.close();
  }

}
