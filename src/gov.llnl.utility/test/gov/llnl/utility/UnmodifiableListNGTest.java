/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import static org.testng.Assert.assertFalse;
import org.testng.annotations.Test;

/**
 * Test code for UnmodifiableList.
 */
public class UnmodifiableListNGTest
{

  static class TestUnmodifiableList extends UnmodifiableList<Integer>
  {
    @Override
    public int size()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Integer> iterator()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Integer get(int index)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Integer> listIterator()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<Integer> listIterator(int index)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex)
    {
      throw new UnsupportedOperationException();
    }
  }

  TestUnmodifiableList testUnmodifiableList;

  public UnmodifiableListNGTest()
  {
    testUnmodifiableList = new TestUnmodifiableList();
  }

  /**
   * Test of add method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testAdd_GenericType()
  {
    testUnmodifiableList.add(1);
  }

  /**
   * Test of remove method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testRemove_Object()
  {
    boolean result = testUnmodifiableList.remove(null);
    assertFalse(result);
  }

  /**
   * Test of containsAll method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testContainsAll()
  {
    testUnmodifiableList.containsAll(new TestUnmodifiableList());
  }

  /**
   * Test of addAll method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testAddAll_Collection()
  {
    testUnmodifiableList.addAll(new TestUnmodifiableList());
  }

  /**
   * Test of addAll method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testAddAll_int_Collection()
  {
    testUnmodifiableList.addAll(0, new TestUnmodifiableList());
  }

  /**
   * Test of removeAll method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testRemoveAll()
  {
    testUnmodifiableList.removeAll(new TestUnmodifiableList());
  }

  /**
   * Test of retainAll method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testRetainAll()
  {
    testUnmodifiableList.retainAll(new TestUnmodifiableList());
  }

  /**
   * Test of clear method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testClear()
  {
    testUnmodifiableList.clear();
  }

  /**
   * Test of set method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testSet()
  {
    testUnmodifiableList.set(0, 0);
  }

  /**
   * Test of add method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testAdd_int_GenericType()
  {
    testUnmodifiableList.add(0, 0);
  }

  /**
   * Test of remove method, of class UnmodifiableList.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testRemove_int()
  {
    testUnmodifiableList.remove(0);
  }
}
