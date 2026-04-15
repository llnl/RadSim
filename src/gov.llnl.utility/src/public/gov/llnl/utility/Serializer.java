/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Specialized serialization handlers for renamed or altered classes. This can
 * handle relatively benign changes to the class such as renaming or differences
 * in the version UID.
 *
 * @author nelson85
 */
public class Serializer
{
  // List of overrides to apply
  TreeMap<String, SerializationOverride> overrides = new TreeMap<>();
  SerializationOverride defaultOverride = new SerializationOverride();
  boolean compress = false;

  /**
   * Indicates that an class has been renamed so that we can load an out of date
   * serialization file.
   *
   * @param revised
   * @param original
   */
  public void renameClass(String revised, String original)
  {
    SerializationOverride override = overrides.get(original);
    if (override == null)
    {
      override = new SerializationOverride();
      overrides.put(original, override);
    }
    override.original = original;
    override.revised = revised;
  }

  /**
   * Marks that a class will load without verifying the version UID. This is
   * used if we have forgotten to set the version UID prior to serialization.
   * Applies to one class and all of its inner classes.
   *
   * @param cls
   */
  public void setIgnoreVersionUID(String cls)
  {
    SerializationOverride override = overrides.get(cls);
    if (override == null)
    {
      override = new SerializationOverride();
      overrides.put(cls, override);
    }
    override.ignoreVersionUID = true;
  }

  public void setIgnoreVersionUID()
  {
    this.defaultOverride.ignoreVersionUID = true;
  }

  /**
   * Set if compression should be used without the .gz extension.
   *
   * @param compress the compress to set
   */
  public void setCompress(boolean compress)
  {
    this.compress = compress;
  }

  /**
   * Load an object from a serialization file.
   *
   * @param file
   * @return the serialized object.
   * @throws IOException if the file cannot be read.
   * @throws ClassNotFoundException if the class stored in the serialized file
   * cannot be instantiated.
   */
  public Serializable load(Path file) throws IOException, ClassNotFoundException
  {
    // Handle GZIP stream
    if (PathUtilities.isGzip(file))
    {
      try (InputStream is = Files.newInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
              GZIPInputStream gis = new GZIPInputStream(bis);
              ObjectInputStream ois = new RevisedObjectInputStream(gis))
      {
        return (Serializable) ois.readObject();
      }
    }

    try (InputStream is = Files.newInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            ObjectInputStream ois = new RevisedObjectInputStream(bis))
    {
      return (Serializable) ois.readObject();
    }
  }

  /**
   * Save an object to a serialization file. Does not apply any renames.
   * Currently this is no different from SerializationUtilities.
   *
   * @param file
   * @param object
   * @throws IOException
   */
  public void save(Path file, Serializable object) throws IOException
  {

    // Special handling for gzip files
    if (PathUtilities.getFileExtension(file).endsWith(".gz") || this.compress)
    {
      try (OutputStream fileOut = Files.newOutputStream(file);
              GZIPOutputStream gos = new GZIPOutputStream(fileOut);
              ObjectOutputStream out = new ObjectOutputStream(gos);)
      {
        out.writeObject(object);
      }
      return;
    }

    try (OutputStream fileOut = Files.newOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut))
    {
      out.writeObject(object);
    }
  }

  /**
   * Converts a serialization file to match the current class structures. This
   * applies any renames and stores the current version UID.
   *
   * @param file
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public void convert(Path file) throws IOException, ClassNotFoundException
  {
    Serializable object = load(file);
    save(file, object);
  }

  /**
   * Convert an object into bytes using serialization.
   *
   * @param object
   * @return a byte stream representing the object.
   * throws InvalidClassException is something is wrong with a class used by
   * serialization. (FIXME reference not found)
   * @throws NotSerializableException if an object does not implement the
   * java.io.Serializable interface.
   * @throws IOException for all usual I/O errors.
   */
  public byte[] pack(Serializable object) throws IOException
  {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream())
    {
      if (compress)
      {
        try (GZIPOutputStream gos = new GZIPOutputStream(bos);
                ObjectOutputStream out = new ObjectOutputStream(gos))
        {
          out.writeObject(object);
          out.flush();
        }
        return bos.toByteArray();
      }
      else
      {
        try (ObjectOutputStream out = new ObjectOutputStream(bos))
        {
          out.writeObject(object);
          out.flush();
        }
        return bos.toByteArray();
      }
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Unpack a set of bytes into an object using serialization.
   *
   * @param buffer holding a previously serialized object.
   * @return the deserialized object.
   * @throws ClassNotFoundException if the class of a serialized object cannot
   * be found.
   * @throws IOException for the usual Input/Output related exceptions.
   */
  public Serializable unpack(byte[] buffer) throws IOException, ClassNotFoundException
  {
    boolean isCompressed = (buffer[0] == 0x1f) && (buffer[1] == (byte) 0x8b);
    try (ByteArrayInputStream bis = new ByteArrayInputStream(buffer))
    {
      if (isCompressed)
      {
        try (GZIPInputStream gis = new GZIPInputStream(bis);
                ObjectInputStream in = new ObjectInputStream(gis))
        {
          return (Serializable) in.readObject();
        }
      }
      else
      {
        try (ObjectInputStream in = new ObjectInputStream(bis))
        {
          return (Serializable) in.readObject();
        }
      }
    }
  }
//<editor-fold desc="utilities">

  public static String encode(Serializable object) throws IOException
  {
    Serializer serializer = new Serializer();
    serializer.setCompress(true);
    byte[] bytes = serializer.pack(object);
    return Base64.getEncoder().encodeToString(bytes);
  }

  public static Serializable decode(String str) throws ClassNotFoundException, IOException
  {
    Serializer serializer = new Serializer();
    serializer.setCompress(true);
    byte[] bytes = Base64.getDecoder().decode(str);
    return serializer.unpack(bytes);
  }

  /**
   * Produce a deep copy of an object using serialization.
   *
   * @param <T>
   * @param object
   * @return
   * @throws NotSerializableException if an object does not implement the
   * java.io.Serializable interface.
   */
  @SuppressWarnings("unchecked")
  public static <T extends Serializable> T copy(T object) throws NotSerializableException
  {
    try
    {
      byte[] bytes;
      try (ByteArrayOutputStream bos = new ByteArrayOutputStream())
      {
        try (ObjectOutputStream out = new ObjectOutputStream(bos))
        {
          out.writeObject(object);
          out.flush();
        }
        bytes = bos.toByteArray();
      }

      try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes))
      {
        try (ObjectInputStream in = new ObjectInputStream(bis))
        {
          return (T) in.readObject();
        }

      }
    }
    catch (ClassNotFoundException | IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

//</editor-fold>
//<editor-fold desc="internal" defaultstate="collapsed">
  // Look for an override for a class
  private SerializationOverride findOverride(String cls)
  {
    SerializationOverride override = overrides.get(cls);
    if (override != null)
      return override;

    if (cls.contains("$"))
    {
      int i1 = cls.indexOf("$");
      override = overrides.get(cls.substring(0, i1));
    }
    if (override == null)
      return defaultOverride;
    return override;
  }

  // Get the local ObjectStreamClass for the current object tree
  private ObjectStreamClass lookupClass(String cls)
  {
    UtilityPackage.LOGGER.info("Lookup class " + cls);

    // Look up the local class
    Class<?> localClass;
    try
    {
      localClass = Class.forName(cls);
    }
    catch (ClassNotFoundException e)
    {
      UtilityPackage.LOGGER.severe("Unable to find class " + cls);
      return null;
    }

    // Get the class descriptor
    return ObjectStreamClass.lookup(localClass);
  }

  public class SerializationOverride
  {
    String original = null;
    String revised = null;
    boolean ignoreVersionUID;

    boolean isRename()
    {
      return revised != null;
    }

    String getName(String className)
    {
      // Straight renameClass
      if (className == null ? original == null : className.equals(original))
        return revised;

      if (original == null)
        return className;

      if (!className.startsWith(original))
        throw new RuntimeException("Unable to convert " + className + " to match " + original);

      // Inner class renameClass
      return revised + className.substring(original.length());
    }
  }

  class RevisedObjectInputStream extends ObjectInputStream
  {
    private RevisedObjectInputStream(InputStream is) throws IOException
    {
      super(is);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor()
            throws IOException, ClassNotFoundException
    {
      // Get the resultClassDescription from the file
      ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

      // Determine the local class
      String className = resultClassDescriptor.getName();
      UtilityPackage.LOGGER.fine("Read class description " + className);

      // Check for an override
      SerializationOverride override = findOverride(className);

      // Handle renames
      if (override.isRename())
        return lookupClass(override.getName(className));

      // Handle ignore VersionUID
      if (override.ignoreVersionUID)
        return lookupClass(override.getName(className));

      return resultClassDescriptor;
    }
  }
//</editor-fold>
}
