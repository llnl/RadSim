/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import gov.llnl.utility.CollectionUtilities.AdapterCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for CollectionUtilities.
 */
public class CollectionUtilitiesNGTest
{

  public CollectionUtilitiesNGTest()
  {
  }

  @Test
  public void testConstructor()
  {
    CollectionUtilities instance = new CollectionUtilities();
  }

  /**
   * Test of adapter method, of class CollectionUtilities.
   */
  @Test
  public void testAdaptor()
  {
    Collection<Object> base = Arrays.asList((Object) "test", (Object) "this");
    Collection<String> expResult = Arrays.asList("test", "this");
    Collection<String> result = CollectionUtilities.adapter(base, p -> (String) p);
    assertEquals(result, expResult);
  }

  /**
   * Test of toArray method, of class CollectionUtilities.
   */
  @Test
  public void testToArray()
  {
    Collection collection = Arrays.asList("alice", "bob", "carl");
    Object[] expResult = new Object[]
    {
      "alice", "bob", "carl"
    };
    Object[] result = CollectionUtilities.toArray(collection);
    assertEquals(result, expResult);
  }

  /**
   * Test of isEmpty method, of class CollectionUtilities.
   */
  @Test
  public void testIsEmpty()
  {
    Collection cln = new ArrayList<>();
    AdapterCollection adapterCln = new AdapterCollection(cln, null);
    assertTrue(adapterCln.isEmpty());
  }

  /**
   * Test of contains method, of class CollectionUtilities.
   */
  @Test
  public void testContains()
  {
    Collection cln = Arrays.asList("alice", "bob", "carl");
    AdapterCollection adapterCln = new AdapterCollection(cln, null);
    assertTrue(adapterCln.contains("alice"));
    assertFalse(adapterCln.contains("jill"));
  }

  @Test
  public void testUnsupportedMethods()
  {
    Collection cln = Arrays.asList(1, 2, 3);
    AdapterCollection adapterCln = new AdapterCollection(cln, null);

    // Test add
    try
    {
      adapterCln.add(null);
    }
    catch (UnsupportedOperationException ex)
    {
    }

    // Test containsAll
    try
    {
      adapterCln.containsAll(cln);
    }
    catch (UnsupportedOperationException ex)
    {
    }

    // Test addAll
    try
    {
      adapterCln.addAll(cln);
    }
    catch (UnsupportedOperationException ex)
    {
    }

    // Test removeAll
    try
    {
      adapterCln.removeAll(cln);
    }
    catch (UnsupportedOperationException ex)
    {
    }

    // Test retainAll
    try
    {
      adapterCln.retainAll(cln);
    }
    catch (UnsupportedOperationException ex)
    {
    }
  }

}
