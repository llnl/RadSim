// --- file: gov/llnl/rtk/response/GroupCache.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import java.util.Arrays;

/**
 * Cache for groups.
 *
 * To reduce the cost of compute response functions, it is best to store
 * computed integrals.
 *
 * As Java does not support caches for primitives nor for pairs, this
 * implementation is specialized. Unlike a HashMap, this is not guaranteed to
 * store all items. It will replace items if there are too many collisions.
 *
 * @author nelson85
 * @param <T> is the type of the object stored in the cache.
 */
class GroupCache<T>
{

  Entry[] cache;

  public GroupCache(int n)
  {
    cache = new Entry[n];
  }

  /**
   * Place an entry in the cache.
   *
   * This may replace an existing cache value.
   *
   * @param key1
   * @param key2
   * @param value
   */
  public void put(double key1, double key2, T value)
  {
    int code = hash(key1, key2, cache.length);
    int index = code;
    for (int i = 0; i < 5; ++i)
    {
      if (cache[index] == null)
      {
        cache[index] = new Entry(key1, key2, value);
        return;
      }
      if (cache[index].key1 == key1 && cache[index].key2 == key2)
      {
        cache[index].value = value;
      }
      index++;
      if (index == cache.length)
        index = 0;
    }

    // Collision (replace the first)
    index = code;
    cache[index].key1 = key1;
    cache[index].key2 = key2;
    cache[index].value = value;
  }

  /**
   * Get an entry from the cache.
   *
   * @param key1 is the starting energy of the pair.
   * @param key2 is the ending energy of the pair.
   * @return the stored value or null if not available.
   */
  public T get(double key1, double key2)
  {
    int code = hash(key1, key2, cache.length);
    int index = code;
    for (int i = 0; i < 5; ++i)
    {
      if (cache[index] == null)
      {
        return null;
      }
      if (cache[index].key1 == key1 && cache[index].key2 == key2)
      {
        return (T) cache[index].value;
      }
      index++;
      if (index == cache.length)
        index = 0;
    }
    return null;
  }

  public void clear()
  {
    Arrays.fill(this.cache, null);
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  static class Entry
  {

    double key1;
    double key2;
    Object value;

    Entry(double key1, double key2, Object value)
    {
      this.key1 = key1;
      this.key2 = key2;
      this.value = value;
    }
  }

  /**
   * Hash function for pairs of doubles.
   *
   * Standard double hash function has poor entropy which could result in
   * excessive collisions if the pairs are structured. Thus we will implement a
   * high entropy hash function using a traditional random number generator
   * approach.
   *
   * @param d1 is the first key.
   * @param d2 is the second key.
   * @param mod is the length of the hash table.
   * @return an index in the hash table.
   */
  public static int hash(double d1, double d2, int mod)
  {
    int k1 = Double.hashCode(d1);
    int k2 = Double.hashCode(d2);
    int b = 0x5631251;
    final int c = 0x5651;
    for (int i = 0; i < 4; i++)
    {
      b = b * c + k1;
      b = b * c + k2;
      k1 >>= 8;
      k2 >>= 8;
    }
    b = b % mod;
    if (b < 0)
      b += mod;
    return b;
  }
//</editor-fold>
}
