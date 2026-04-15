/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class RenderItemNGTest
{

  public RenderItemNGTest()
  {
  }

  @Test
  public void testValues()
  {
    RenderItem[] expResult = new RenderItem[]
    {
      RenderItem.PHOTOELECTRIC,
      RenderItem.XRAY_SCATTER,
      RenderItem.ANNIHILATION,
      RenderItem.ESCAPE_SINGLE,
      RenderItem.ESCAPE_DOUBLE,
      RenderItem.XRAY_ESCAPE,
      RenderItem.CONTINUUM,
//      RenderItem.INCOMPLETE,
      RenderItem.LLD
    };
    RenderItem[] result = RenderItem.values();
    assertEquals(result, expResult);
  }

  @Test
  public void testValueOf()
  {
    assertEquals(RenderItem.valueOf("PHOTOELECTRIC"), RenderItem.PHOTOELECTRIC);
  }

}
