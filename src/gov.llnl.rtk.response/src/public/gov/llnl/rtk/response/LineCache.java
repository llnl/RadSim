// --- file: gov/llnl/rtk/response/LineCache.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import java.util.Arrays;

/**
 * Utility to store data associated with a line.
 *
 * As Java does not support caches for primitives, this implementation is
 * specialized. Unlike a HashMap, this is not guaranteed to store all items. It
 * will replace items if there are too many collisions.
 *
 * @author nelson85
 * @param <T> is the type of object stored in the cache.
 */
class LineCache<T>
{

  Entry[] cache;

  public LineCache(int n)
  {
    cache = new Entry[n];
  }

  /**
   * Place an entry in the cache.
   *
   * This may replace an existing cache value.
   *
   * @param key
   * @param value
   */
  public void put(double key, T value)
  {
    int code = hash(key, cache.length);
    int index = code;
    for (int i = 0; i < 5; ++i)
    {
      if (cache[index] == null)
      {
        cache[index] = new Entry(key, value);
        return;
      }
      if (cache[index].key == key)
      {
        cache[index].value = value;
      }
      index++;
      if (index == cache.length)
        index = 0;
    }

    // Collision (replace the first)
    index = code;
    cache[index].key = key;
    cache[index].value = value;
  }

  /**
   * Get an entry from the cache.
   *
   * @param key is the energy for the cache entry.
   * @return the stored value or null if not available.
   */
  public T get(double key)
  {
    int index = hash(key, cache.length);
    for (int i = 0; i < 5; ++i)
    {
      if (cache[index] == null)
      {
        return null;
      }
      if (cache[index].key == key)
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

    double key;
    Object value;

    Entry(double key, Object value)
    {
      this.key = key;
      this.value = value;
    }
  }

  /**
   * Hash function for doubles.
   *
   * Standard double hash function has poor entropy which could result in
   * excessive collisions if the values are structured. Thus we will implement a
   * high entropy hash function using a traditional random number generator
   * approach.
   *
   * @param d1 is the key.
   * @param mod is the length of the hash table.
   * @return an index in the hash table.
   */
  public static int hash(double d1, int mod)
  {
    int k1 = Double.hashCode(d1);
    int b = 0x5631252;
    final int c = 0x5651;
    for (int i = 0; i < 4; i++)
    {
      b = b * c + k1;
      k1 >>= 8;
    }
    b = b % mod;
    if (b < 0)
      b += mod;
    return b;
  }
//</editor-fold>
}
