/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for StringUtilities.
 */
public class StringUtilitiesNGTest
{

  @Test
  public void testConstructor()
  {
    StringUtilities instance = new StringUtilities();
  }

  /**
   * Test of join method, of class StringUtilities.
   */
  @Test
  public void testJoin_Iterable_String()
  {
    Iterable<String> strings = Arrays.asList("c", "d", "e", "f", "g", "a", "b");
    assertEquals(StringUtilities.join(strings, ""), "cdefgab");
  }

  /**
   * Test of join method, of class StringUtilities.
   */
  @Test
  public void testJoin_StringArr_String()
  {
    String[] strings = new String[]
    {
      "c", "d", "e", "f", "g", "a", "b"
    };
    assertEquals(StringUtilities.join(strings, ", "), "c, d, e, f, g, a, b");
  }

  /**
   * Test of getFixedString method, of class StringUtilities.
   */
  @Test
  public void testGetFixedString() throws UnsupportedEncodingException
  {
    assertEquals(StringUtilities.getFixedString(ByteBuffer.wrap("Hello".getBytes("UTF8")), 5), "Hello");
  }

}
