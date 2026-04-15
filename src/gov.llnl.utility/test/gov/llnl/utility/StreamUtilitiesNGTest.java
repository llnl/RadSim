/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for StreamUtilities.
 */
public class StreamUtilitiesNGTest
{

  public StreamUtilitiesNGTest()
  {
  }

  /**
   * Test of from method, of class StreamUtilities.
   */
  @Test
  public void testFrom()
  {
    int[] v = new int[1];
    Supplier<Object> producer = () -> v[0]++;
    Stream result = StreamUtilities.from(producer);
    Object[] a = result.limit(5).toArray();
    assertEquals(Arrays.asList(a), Arrays.asList(0, 1, 2, 3, 4));
  }

}
