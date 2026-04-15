/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.support;

import gov.llnl.math.Cursor;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class CursorNGTest
{

  public CursorNGTest()
  {
  }

  @Test
  public void testSeek()
  {
    double x = 0.0;
    //                                        0 1 2 
    Cursor instance = new Cursor(new double[]
    {
      1, 2, 3, 4, 6
    }, 0, 4);
    assertEquals(instance.seek(6), 2);
    assertEquals(instance.seek(0), 0);
    assertEquals(instance.seek(0.9), 0);
    assertEquals(instance.seek(1), 0);
    assertEquals(instance.seek(2), 1);
    assertEquals(instance.seek(3), 2);
    assertEquals(instance.seek(4), 2);
    assertEquals(instance.seek(5), 2);
  }

  @Test
  public void testGetFraction()
  {
    Cursor instance = new Cursor(new double[]
    {
      1, 2, 4, 5, 6
    }, 0, 4);
    double result = instance.getFraction();
    assertEquals(instance.seek(0), 0);
    assertEquals(instance.getFraction(), -1.0, 1e-7);
    assertEquals(instance.seek(1), 0);
    assertEquals(instance.getFraction(), 0.0, 1e-7);
    assertEquals(instance.seek(1.9), 0);
    assertEquals(instance.getFraction(), 0.9, 1e-7);
    assertEquals(instance.seek(3), 1);
    assertEquals(instance.getFraction(), 0.5, 1e-7);
    assertEquals(instance.seek(5), 2);
    assertEquals(instance.getFraction(), 1.0, 1e-7);
    assertEquals(instance.seek(6), 2);
    assertEquals(instance.getFraction(), 2.0, 1e-7);
  }

}
