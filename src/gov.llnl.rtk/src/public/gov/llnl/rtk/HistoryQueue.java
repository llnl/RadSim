// --- file: gov/llnl/rtk/HistoryQueue.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk;

import java.io.Serializable;

/**
 *
 * @author nelson85
 * @param <T>
 */
public interface HistoryQueue<T> extends Serializable, Iterable<T>
{
  void clear();

  T get(int i);

  int size();

}
