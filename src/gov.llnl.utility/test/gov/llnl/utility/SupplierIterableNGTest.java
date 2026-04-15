/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for SupplierIterable.
 */
public class SupplierIterableNGTest
{

  public SupplierIterableNGTest()
  {
  }

  /**
   * Test of iterator method, of class SupplierIterable.
   */
  @Test
  public void testIterator()
  {
    TestSupplier ts = new TestSupplier();
    SupplierIterable instance = new SupplierIterable(ts);

    Iterator<Integer> iterator = instance.iterator();
    Integer value = 0;
    while (iterator.hasNext())
    {
      assertEquals(iterator.next(), value);
      ++value;
    }
    assertEquals(iterator.hasNext(), false);
    assertNull(iterator.next());
  }

  class TestSupplier implements Supplier<Integer>
  {
    List<Integer> intList = new ArrayList<>()
    {
      {
        add(2);
        add(1);
        add(0);
      }
    };

    @Override
    public Integer get()
    {
      if (intList.isEmpty())
        return null;

      return intList.remove(intList.size() - 1);
    }
  }

}
