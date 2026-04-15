/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Iterators.
 */
public class IteratorsNGTest
{

  public IteratorsNGTest()
  {
  }

  /**
   * Test of cast method, of class Iterators.
   */
  @Test
  public void testCast_Iterator_Class()
  {
    Iterator<Object> iter = Arrays.<Object>asList("a", "b", "c").iterator();
    Iterator<String> result = Iterators.cast(iter, String.class);
    ArrayList<String> contents = new ArrayList<>();
    result.forEachRemaining(contents::add);
    assertEquals(contents, Arrays.asList("a", "b", "c"));
  }

  /**
   * Test of cast method, of class Iterators.
   */
  @Test
  public void testCast_Iterator()
  {
    Iterator<String> iter = Arrays.asList("a", "b", "c").iterator();
    Iterator<Object> result = Iterators.cast(iter);
    ArrayList<Object> contents = new ArrayList<>();
    result.forEachRemaining(contents::add);
    assertEquals(contents, Arrays.asList("a", "b", "c"));
  }

  /**
   * Test of adapter method, of class Iterators.
   */
  @Test
  public void testAdapter()
  {
    Iterator<String> iter = Arrays.asList("1", "2", "3").iterator();
    Function<String, Integer> accessor = p -> Integer.parseInt(p);
    Iterator<Integer> result = Iterators.adapter(iter, accessor);
    List<Integer> contents = new ArrayList();
    result.forEachRemaining(contents::add);
    assertEquals(contents, Arrays.asList(1, 2, 3));
  }

}
