/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 * Test code for HashUtilities.
 */
public class HashUtilitiesNGTest
{

  public HashUtilitiesNGTest()
  {
  }

  /**
   * Test of hash method, of class HashUtilities.
   */
  @Test
  public void testHash()
  {
    class TestPattern
    {
      int[] in;
      long out;

      private TestPattern(int[] in, long out)
      {
        this.in = in;
        this.out = out;
      }
    }

    TestPattern[] patterns = new TestPattern[]
    {
      new TestPattern(null, 0),
      new TestPattern(new int[]
      {
      }, -3162216497309240828l),
      new TestPattern(new int[]
      {
        0
      }, -1021191746955413710l),
      new TestPattern(new int[]
      {
        1
      }, -1061438811504499547l),
      new TestPattern(new int[]
      {
        1, 0
      }, 2306653601870186394l)
    };

    for (TestPattern pattern : patterns)
    {
      long result = HashUtilities.byteArrayToLong(HashUtilities.hash(pattern.in));
      assertEquals(result, pattern.out);
    }
  }

  /**
   * Test of byteArrayToHexString method, of class HashUtilities.
   */
  @Test
  public void testByteArrayToHexString()
  {
    byte[] b = new byte[]
    {
      (byte) 0x1, (byte) 0x2, (byte) 0x3, (byte) 0xaa, (byte) 0xbb, (byte) 0xcc
    };
    assertEquals(HashUtilities.byteArrayToHexString(b), "010203aabbcc");
  }

}
