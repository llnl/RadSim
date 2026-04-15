// --- file: gov/llnl/rtk/Instrumented.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk;

import java.util.function.Consumer;

/**
 *
 * @author nelson85
 */
public interface Instrumented
{
  /**
   * Add a listener to a processor.
   *
   * Whenever a particular piece of data is created, it can be posted to the
   * listeners.
   *
   * @param <T>
   * @param type
   * @param consumer
   */
  <T> void addListener(Class<T> type, Consumer<T> consumer);
}
