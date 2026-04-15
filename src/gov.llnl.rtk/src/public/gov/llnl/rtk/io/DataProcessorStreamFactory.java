// --- file: gov/llnl/rtk/io/DataProcessorStreamFactory.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.io;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.ReaderException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.LinkedList;

/**
 *
 * @author nelson85
 */
public class DataProcessorStreamFactory
{
  static final DataProcessorStreamFactory INSTANCE = new DataProcessorStreamFactory();
  LinkedList<Class<? extends DataProcessorInputStream>> inputStreams = new LinkedList<>();
  LinkedList<Class<? extends DataProcessorOutputStream>> outputStreams = new LinkedList<>();

  public static DataProcessorStreamFactory getInstance()
  {
    return INSTANCE;
  }

  /**
   * Add a fully qualified class name to the list of available stream types.
   * Must be of type DataProcessorInputStream or DataProcessorOutputStream.
   *
   * @param clsName A fully qualified class name.
   */
  public synchronized void register(String clsName)
  {
    try
    {
      Class<?> cls = Class.forName(clsName);
      if (DataProcessorInputStream.class.isAssignableFrom(cls))
      {
        inputStreams.add((Class<? extends DataProcessorInputStream>) cls);
      }
      if (DataProcessorOutputStream.class.isAssignableFrom(cls))
      {
        outputStreams.add((Class<? extends DataProcessorOutputStream>) cls);
      }
    }
    catch (ClassNotFoundException ex)
    {
      RtkPackage.getInstance().getLogger().severe("Unable to load class " + clsName);
    }
  }

  public synchronized Constructor getInputStream(String type) throws ReaderException
  {
    try
    {
      Class klass = Class.forName(type);
      if (DataProcessorInputStream.class.isAssignableFrom(klass))
      {
        Constructor<? extends DataProcessorInputStream> ctor = klass.getConstructor(Path.class);
        if (ctor != null)
        {
          return ctor;
        }
      }
    }
    catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex)
    {
      // skip to SearchList
    }

    for (Class<? extends DataProcessorInputStream> cls : this.inputStreams)
    {
      try
      {
        Constructor<? extends DataProcessorInputStream> ctor = cls.getConstructor(Path.class);
        if (ctor == null)
          continue;
        DataProcessorStreamType ann = cls.getAnnotation(DataProcessorStreamType.class);
        if (ann == null)
          continue;
        if (ann.value().equals(type))
          return ctor;
      }
      catch (NoSuchMethodException | SecurityException ex)
      {
      }
    }
    throw new ReaderException("Unable to find reader for " + type);
  }

  public synchronized Constructor getOutputStream(String type) throws ReaderException
  {
    for (Class<? extends DataProcessorOutputStream> cls : this.outputStreams)
    {
      try
      {
        Constructor<? extends DataProcessorOutputStream> ctor = cls.getConstructor(Path.class);
        if (ctor == null)
          continue;
        DataProcessorStreamType ann = ctor.getAnnotation(DataProcessorStreamType.class);
        if (ann == null)
          continue;
        if (ann.value().equals(type))
          return ctor;
      }
      catch (NoSuchMethodException | SecurityException ex)
      {
      }
    }
    throw new ReaderException("Unable to find writer for " + type);
  }

  static public DataProcessorInputStream createInputStream(Path file, String type) throws ReaderException
  {
    try
    {
      Constructor ctor = getInstance().getInputStream(type);
      return (DataProcessorInputStream) ctor.newInstance(file);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
    {
      throw new ReaderException("Problem creating input stream", ex);
    }
  }

  static public DataProcessorOutputStream createOutputStream(Path file, String type) throws ReaderException
  {
    try
    {
      Constructor ctor = getInstance().getOutputStream(type);
      return (DataProcessorOutputStream) ctor.newInstance(file);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
    {
      throw new ReaderException("Problem creating output stream", ex);
    }
  }
}
