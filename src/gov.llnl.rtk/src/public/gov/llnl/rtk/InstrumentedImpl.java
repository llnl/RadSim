// --- file: gov/llnl/rtk/InstrumentedImpl.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstrumentedImpl implements Instrumented
{
  HashMap<Class, List<Consumer>> listeners = new HashMap<>();

  @Override
  public <T> void addListener(Class<T> type, Consumer<T> consumer)
  {
    if (listeners.get(type) == null)
      listeners.put(type, new LinkedList<>());
    listeners.get(type).add(consumer);
  }

  /**
   * Post a message to the listeners.
   *
   * This requires the type so that we don't need to deal with determining the
   * listener for a polymorphic type.
   *
   * @param <T>
   * @param message is the message to be passed.
   * @param type is the type of message being listened for, if it is null then
   * the messages class is consulted.
   *
   */
  @SuppressWarnings("unchecked")
  public <T> void post(T message, Class<T> type)
  {
    if (type == null && message == null)
      return;
    if (type == null)
      type = (Class<T>) message.getClass();
    List<Consumer> consumers = listeners.get(type);
    if (consumers == null)
    {
      return;
    }
    for (Consumer consumer : consumers)
    {
      consumer.accept(message);
    }
  }

  /**
   * Close any resources associated with the instrumentation.
   *
   */
  public void dispose()
  {
    for (Map.Entry<Class, List<Consumer>> entry : listeners.entrySet())
    {
      for (Consumer consumer : entry.getValue())
      {
        if (consumer instanceof AutoCloseable)
        {
          try
          {
            ((AutoCloseable) consumer).close();
          }
          catch (Exception ex)
          {
            Logger.getLogger(InstrumentedImpl.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

}
