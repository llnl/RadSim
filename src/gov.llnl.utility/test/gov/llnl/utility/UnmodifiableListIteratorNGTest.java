/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import org.testng.annotations.Test;

/**
 * Test code for UnmodifiableListIterator.
 */
public class UnmodifiableListIteratorNGTest
{
  static class TestUnmodifiableListIterator extends UnmodifiableListIterator<Integer>
  {
    @Override
    public boolean hasNext()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Integer next()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasPrevious()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Integer previous()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int nextIndex()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int previousIndex()
    {
      throw new UnsupportedOperationException();
    }
  }

  TestUnmodifiableListIterator testUnmodifiableListIterator;

  public UnmodifiableListIteratorNGTest()
  {
    testUnmodifiableListIterator = new TestUnmodifiableListIterator();
  }

  /**
   * Test of remove method, of class UnmodifiableListIterator.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testRemove()
  {
    testUnmodifiableListIterator.remove();
  }

  /**
   * Test of set method, of class UnmodifiableListIterator.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testSet()
  {
    testUnmodifiableListIterator.set(1);
  }

  /**
   * Test of add method, of class UnmodifiableListIterator.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testAdd()
  {
    testUnmodifiableListIterator.add(1);
  }

}
