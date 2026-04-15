/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import gov.llnl.utility.ArrayMap.EntrySet;
import gov.llnl.utility.ArrayMap.KeySet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ArrayMap.
 */
public class ArrayMapNGTest
{

  public ArrayMapNGTest()
  {
  }

  /**
   * Test of size method, of class ArrayMap.
   */
  @Test
  public void testSize()
  {
    ArrayMap<Integer, Integer> instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put(2, 2);
        put(3, 3);
        put(4, 4);
      }
    };
    assertEquals(instance.size(), 4);
  }

  /**
   * Test of isEmpty method, of class ArrayMap.
   */
  @Test
  public void testIsEmpty()
  {
    ArrayMap<Integer, Integer> instance = new ArrayMap<>();
    assertEquals(instance.isEmpty(), true);
    instance.put(1, 1);
    assertEquals(instance.isEmpty(), false);
  }

  /**
   * Test of containsKey method, of class ArrayMap.
   */
  @Test
  public void testContainsKey()
  {
    ArrayMap<Integer, Integer> instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put(2, 2);
        put(3, 3);
        put(4, 4);
      }
    };
    assertEquals(instance.containsKey(1), true);
    assertEquals(instance.containsKey(0), false);

    ArrayMap<Double, Integer> doubleMap = new ArrayMap<>()
    {
      {
        put(1.0D, 1);
      }
    };
    assertEquals(doubleMap.containsKey(1.0), true);
    assertEquals(doubleMap.containsKey(0), false);
    assertEquals(doubleMap.containsKey("1"), false);

    ArrayMap<String, Integer> strMap = new ArrayMap<>()
    {
      {
        put("1", 1);
      }
    };
    assertEquals(strMap.containsKey("1"), true);
    assertEquals(strMap.containsKey(1), false);
  }

  /**
   * Test of containsValue method, of class ArrayMap.
   */
  @Test
  public void testContainsValue()
  {
    ArrayMap<Integer, Integer> instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put(2, 2);
        put(3, 3);
        put(4, 4);
      }
    };
    ArrayMap<String, Integer> strMap = new ArrayMap<>()
    {
      {
        put("1", 1);
      }
    };

    assertEquals(instance.containsValue(1), true);
    assertEquals(instance.containsValue(0), false);
    assertEquals(instance.containsValue("0"), false);

    assertEquals(strMap.containsValue("1"), false);
    assertEquals(strMap.containsValue(1.0), false);
    assertEquals(strMap.containsValue(1), true);
  }

  /**
   * Test of getEntry method, of class ArrayMap.
   */
  @Test
  public void testGetEntry()
  {
    Object key = null;
    ArrayMap<Integer, Integer> instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put(2, 2);
        put(3, 3);
        put(4, 4);
      }
    };
    TestArrayEntry<Integer, Integer> expResult = new TestArrayEntry<>(1, 1);
    Map.Entry result = instance.getEntry(1);
    assertEquals(result.getKey(), expResult.getKey());
    assertEquals(result.getValue(), expResult.getValue());
    assertNull(instance.getEntry("1"));
    assertNull(instance.getEntry(0));
  }

  /**
   * Test of get method, of class ArrayMap.
   */
  @Test
  public void testGet()
  {
    ArrayMap<Integer, Integer> instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put(2, 2);
        put(3, 3);
        put(4, 4);
      }
    };
    assertEquals(instance.get(1), Integer.valueOf(1));
    assertNull(instance.get(0));
    assertNull(instance.get("1"));
  }

  /**
   * Test of put method, of class ArrayMap.
   */
  @Test
  public void testPut()
  {
    ArrayMap<Integer, Integer> instance = new ArrayMap<>();

    Integer key = 1;
    Integer value = 1;
    Integer result = instance.put(key, value);
    assertNull(result);
    assertEquals(instance.put(key, 2), Integer.valueOf(1));

    ArrayMap instance2 = new ArrayMap();
    Object obj = instance2.put(2, "2");
    assertNull(obj);
    obj = instance2.put(3, 3);
    assertNull(obj);
    obj = instance2.put(2, 2);
    assertEquals(obj, "2");
  }

  /**
   * Test of remove method, of class ArrayMap.
   */
  @Test
  public void testRemove()
  {
    Object o = null;
    ArrayMap instance = new ArrayMap()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    assertEquals(instance.remove("2"), 2);
    assertNull(instance.remove("2"));
    assertNull(instance.remove(4));
  }

  /**
   * Test of putAll method, of class ArrayMap.
   */
  @Test
  public void testPutAll()
  {
    ArrayMap control = new ArrayMap()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    ArrayMap instance = new ArrayMap();
    instance.putAll(control);

    assertEquals(instance.get(3), "3");
  }

  /**
   * Test of clear method, of class ArrayMap.
   */
  @Test
  public void testClear()
  {
    ArrayMap instance = new ArrayMap()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    instance.clear();
    assertEquals(instance.size(), 0);
    assertNull(instance.get(1));
  }

  /**
   * Test of keySet method, of class ArrayMap.
   */
  @Test
  public void testKeySet()
  {
    ArrayMap instance = new ArrayMap()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    Set result = instance.keySet();
    assertTrue(result.contains(1));
    assertTrue(result.contains("2"));
    assertTrue(result.contains(3));
    assertTrue(result.contains("4"));
  }

  /**
   * Test of values method, of class ArrayMap.
   */
  @Test
  public void testValues()
  {
    Map control = new HashMap()
    {
      {
        put(1, 2);
        put("2", 4);
        put(3, "8");
        put("4", "16");
      }
    };
    ArrayMap instance = new ArrayMap()
    {
      {
        put(1, 2);
        put("2", 4);
        put(3, "8");
        put("4", "16");
      }
    };
    Collection result = instance.values();
    // Collection.contains will not work as intended since we'll be comparing
    // different objects with the same underlaying data.
    Object[] objList = result.toArray();
    assertEquals(objList.length, 4);

    for (Object obj : result)
    {
      assertTrue(instance.containsValue(obj));
      assertTrue(control.containsValue(obj));
    }

    assertEquals(result.size(), control.values().size());
  }

  @Test
  public void testValues2()
  {

    Map intControl = new HashMap()
    {
      {
        put(1, 2);
        put(2, 4);
        put(3, 8);
        put(4, 16);
      }
    };
    ArrayMap<Integer, Integer> intInstance = new ArrayMap<>()
    {
      {
        put(1, 2);
        put(2, 4);
        put(3, 8);
        put(4, 16);
      }
    };

    Integer[] controlArray = new Integer[intControl.size()];
    controlArray = (Integer[]) intControl.values().toArray(controlArray);

    Integer[] testArray = new Integer[intInstance.size()];
    testArray = (Integer[]) intInstance.values().toArray(testArray);

    assertEquals(testArray.length, controlArray.length);
    for (int i = 0; i < controlArray.length; ++i)
    {
      assertEquals(testArray[i], controlArray[i]);
    }
  }

  /**
   * Test of entrySet method, of class ArrayMap.
   */
  @Test
  public void testEntrySet()
  {
    ArrayMap instance = new ArrayMap()
    {
      {
        put(1, 2);
        put("2", 4);
        put(3, "8");
        put("4", "16");
      }
    };

    Map control = new HashMap()
    {
      {
        put(1, 2);
        put("2", 4);
        put(3, "8");
        put("4", "16");
      }
    };

    Set<Entry> controlSet = control.entrySet();
    Object[] controlArray = controlSet.toArray();
    Set<Entry> result = instance.entrySet();
    Object[] testArray = result.toArray();

    assertEquals(testArray.length, controlArray.length);
    for (int i = 0; i < controlArray.length; ++i)
    {
      Map.Entry controlEntry = (Map.Entry) controlArray[i];
      Map.Entry testEntry = (Map.Entry) testArray[i];
      assertEquals(controlEntry.getKey(), testEntry.getKey());
      assertEquals(controlEntry.getValue(), testEntry.getValue());
    }
  }

  /**
   * Test of contains method, of class EntrySet in class ArrayMap.
   */
  @Test
  public void testContains()
  {
    ArrayMap<Integer, Integer> intInstance = new ArrayMap<>()
    {
      {
        put(1, 2);
        put(2, 4);
        put(3, 8);
        put(4, 16);
      }
    };

    EntrySet es = (EntrySet) intInstance.entrySet();
    boolean result = es.contains(1);
    assertFalse(result);

    TestArrayEntry<Integer, Integer> entry = new TestArrayEntry<>(1, 2);
    result = es.contains(entry);
    assertTrue(result);

    entry = new TestArrayEntry<>(1, 5);
    result = es.contains(entry);
    assertFalse(result);

    entry = new TestArrayEntry<>(5, 5);
    result = es.contains(entry);
    assertFalse(result);
  }

  /**
   * Test of toArray method, of class EntrySet in class ArrayMap.
   */
  @Test
  public void testEntrySetToArray_0Args()
  {
    ArrayMap<Integer, Integer> intInstance = new ArrayMap<>()
    {
      {
        put(0, 2);
        put(1, 4);
        put(2, 8);
        put(3, 16);
      }
    };

    EntrySet es = (EntrySet) intInstance.entrySet();
    Object[] result = es.toArray();
    assertEquals(result.length, es.size());

    for (Object o : result)
    {
      assertTrue(es.contains(o));
    }
  }

  /**
   * Test of toArray method, of class EntrySet in class ArrayMap.
   */
  @Test
  public void testEntrySetToArray_1Arg()
  {
    ArrayMap<Integer, Integer> intInstance = new ArrayMap<>()
    {
      {
        put(0, 2);
        put(1, 4);
        put(2, 8);
        put(3, 16);
      }
    };

    EntrySet es = (EntrySet) intInstance.entrySet();
    Entry[] arr = new Entry[0];
    Entry[] result = (Entry[]) es.toArray(arr);
    assertEquals(result.length, es.size());

    for (Object o : result)
    {
      assertTrue(es.contains(o));
    }
  }

  /**
   * Test of clear method, of class EntrySet in class ArrayMap.
   */
  @Test
  public void testEntrySetClear()
  {
    ArrayMap<Integer, Integer> intInstance = new ArrayMap<>()
    {
      {
        put(0, 2);
        put(1, 4);
        put(2, 8);
        put(3, 16);
      }
    };

    EntrySet es = (EntrySet) intInstance.entrySet();
    es.clear();
    assertTrue(es.size() == 0);
  }

  /**
   * Test unsupported methods, of class EntrySet in class ArrayMap.
   */
  @Test
  public void testUnsupportedEntrySetMethods()
  {
    ArrayMap<Integer, Integer> intInstance = new ArrayMap<>()
    {
      {
        put(0, 2);
        put(1, 4);
        put(2, 8);
        put(3, 16);
      }
    };

    EntrySet es = (EntrySet) intInstance.entrySet();
    Collection c = new ArrayList<>();

    // Test add
    try
    {
      es.add(new TestArrayEntry(4, 1));
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test remove
    try
    {
      es.remove(new TestArrayEntry(4, 1));
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test containsAll
    try
    {
      es.containsAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test addAll
    try
    {
      es.addAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test retainAll
    try
    {
      es.retainAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test removeAll
    try
    {
      es.removeAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }
  }

  /**
   * Test of remove method, of class KeySet in class ArrayMap.
   */
  @Test
  public void testKeySetRemove()
  {
    ArrayMap instance = new ArrayMap()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    Set instanceSet = instance.keySet();
    boolean result = instanceSet.remove(5);
    assertTrue(result);

    result = instanceSet.remove(1);
    assertFalse(result);
  }

  /**
   * Test of toArray method, of class KeySet in class ArrayMap.
   */
  @Test
  public void testKeySetToArray_0Args()
  {
    ArrayMap<Integer, Integer> instance = new ArrayMap<>()
    {
      {
        put(0, 2);
        put(1, 4);
        put(2, 8);
        put(3, 16);
      }
    };
    KeySet instanceSet = (KeySet) instance.keySet();
    Object[] result = instanceSet.toArray();
    assertEquals(result.length, instanceSet.size());

    for (Object o : result)
    {
      assertTrue(instanceSet.contains(o));
    }
  }

  /**
   * Test of toArray method, of class KeySet in class ArrayMap.
   */
  @Test
  public void testKeySetToArray_1Arg()
  {
    ArrayMap instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    KeySet instanceSet = (KeySet) instance.keySet();
    Object[] arr = new Object[0];
    Object[] result = (Object[]) instanceSet.toArray(arr);
    assertEquals(result.length, instanceSet.size());

    for (Object o : result)
    {
      assertTrue(instanceSet.contains(o));
    }
  }

  /**
   * Test unsupported methods, of class KeySet in class ArrayMap.
   */
  @Test
  public void testUnsupportedKeySetMethods()
  {
    ArrayMap instance = new ArrayMap()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };

    Set instanceSet = instance.keySet();
    Collection c = new ArrayList<>();

    // Test add
    try
    {
      instanceSet.add(new TestArrayEntry(4, 1));
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test containsAll
    try
    {
      instanceSet.containsAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test addAll
    try
    {
      instanceSet.addAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test retainAll
    try
    {
      instanceSet.retainAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }

    // Test removeAll
    try
    {
      instanceSet.removeAll(c);
    }
    catch (UnsupportedOperationException e)
    {
    }
  }

  /**
   * Test of isEmpty method, of class SetBase in class ArrayMap.
   */
  @Test
  public void testSetBaseIsEmpty()
  {
    ArrayMap instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    KeySet instanceSet = (KeySet) instance.keySet();
    assertFalse(instanceSet.isEmpty());

    instance = new ArrayMap<>();
    instanceSet = (KeySet) instance.keySet();
    assertTrue(instanceSet.isEmpty());

  }

  /**
   * Test of clear method, of class SetBase in class ArrayMap.
   */
  @Test
  public void testSetBaseClear()
  {
    ArrayMap instance = new ArrayMap<>()
    {
      {
        put(1, 1);
        put("2", 2);
        put(3, "3");
        put("4", "4");
      }
    };
    KeySet instanceSet = (KeySet) instance.keySet();
    instanceSet.clear();
    assertTrue(instance.size() == 0);
  }

  // Copy class over from ArrayMap.ArrayEntry for testing purposes 
  static class TestArrayEntry<K, V> implements Map.Entry<K, V>, Serializable
  {
    K key;
    V value;

    private TestArrayEntry(K k, V v)
    {
      this.key = k;
      this.value = v;
    }

    @Override
    public K getKey()
    {
      return key;
    }

    @Override
    public V getValue()
    {
      return value;
    }

    @Override
    public V setValue(V v)
    {
      return value;
    }
  }
}
