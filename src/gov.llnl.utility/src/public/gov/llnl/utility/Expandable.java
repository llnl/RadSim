/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface for data types that support keyed data.
 *
 * The {@code Expandable} interface provides a mechanism for objects to store
 * and manage optional attributes in the form of key-value pairs. Keys are
 * {@code String}, and values are {@code Serializable} objects.
 *
 * <p><b>Design Philosophy:</b></p>
 * <ul>
 *   <li><b>Extensibility:</b> Allows dynamic addition of attributes without
 *       modifying the object structure.</li>
 *   <li><b>Flexibility:</b> Enables modules to attach custom data to objects for
 *       use within the same module.</li>
 *   <li><b>Low Usage Feature:</b> Primarily intended for carrying data between
 *       modules, often back to the module that added the data.</li>
 *   <li><b>Locking Requirement:</b> Objects implementing this interface are
 *       expected to be locked by the party using them to ensure safe access in
 *       concurrent environments.</li>
 * </ul>
 *
 * <p><b>Usage Guidelines:</b></p>
 * <ul>
 *   <li>Use this interface when objects need to carry optional data that is not
 *       part of their core structure.</li>
 *   <li>Avoid relying on attributes for critical functionality unless explicitly
 *       documented.</li>
 *   <li>Ensure proper locking when accessing or modifying attributes in
 *       multi-threaded environments.</li>
 * </ul>
 *
 * <p><b>Thread Safety:</b></p>
 * <ul>
 *   <li>This interface does <b>not enforce thread safety</b>. Implementations of
 *       this interface must ensure proper synchronization when accessed or
 *       modified concurrently.</li>
 *   <li>External locking or synchronization mechanisms are required if the
 *       attributes are accessed in multi-threaded environments.</li>
 *   <li>If thread safety is a concern, consider using a thread-safe implementation
 *       of the attributes map (e.g., {@code ConcurrentHashMap}).</li>
 * </ul>
 *
 * @author nelson85
 */
public interface Expandable
{

  /**
   * Access the attributes map.
   *
   * <p>
   * This map supports operations such as {@code clear()}, {@code get()}, and
   * {@code remove()}.</p>
   *
   * @return a map containing all attributes as key-value pairs.
   */
  Map<String, Serializable> getAttributes();

  /**
   * Get the value of an attribute by its name.
   *
   * @param name the name of the attribute
   * @return the value of the attribute, or {@code null} if the attribute is not
   * set
   */
  Serializable getAttribute(String name);

  /**
   * Set the value of an attribute.
   *
   * @param <T> the type of the attribute value, which must implement
   * {@code Serializable}
   * @param name the name of the attribute
   * @param value the value of the attribute
   */
  <T extends Serializable> void setAttribute(String name, T value);

//<editor-fold desc="default">
  /**
   * Get the attribute with class specified. If the attribute exists and has the
   * correct type, then the attribute is returned. Otherwise, it will return a
   * null.
   *
   * @param <T>
   * @param name of the attribute
   * @param cls of the attribute
   * @return the attribute with the name, or null if the object is not set.
   * @throws ClassCastException if the type of the attribute is incorrect.
   */
  default <T> T getAttribute(String name, Class<T> cls) throws ClassCastException
  {
    return getAttribute(name, cls, null);
  }

  /**
   * Get the attribute with class specified.
   *
   * If the attribute exists and has the correct type, then the attribute is
   * returned. Otherwise, it will return a null.
   *
   * @param <T>
   * @param key
   * @return the attribute with the name, or null if the object is not set.
   * @throws ClassCastException if the type of the attribute is incorrect.
   */
  default <T> T getAttribute(Attribute<T> key) throws ClassCastException
  {
    Map<String, Serializable> map = this.getAttributes();
    if (map == null)
      return key.getDefaultValue();

    // Step 1 try to get the value
    Object value = map.get(key.getName());
    Class<T> cls = key.getAttributeClass();
    // Step 2 we found it so all is good
    if (value != null && cls.isInstance(value))
    {
      return cls.cast(value);
    }

    // Step 3 if the type is wrong this is an error
    if (value != null)
      throw new ClassCastException("incorrect attributed type " + cls + "!=" + value.getClass());

    return key.getDefaultValue();
  }

  /**
   * Get the attribute with class specified.
   *
   * If the attribute exists and has the correct type, then the attribute is
   * returned. If it is the wrong type then it will throw an ClassCastException.
   * Otherwise, it will return the given default value.
   *
   * @param <T>
   * @param name of the attribute
   * @param cls of the attribute
   * @param defaultValue
   * @return the attribute with the name, or null if the object is not set.
   * @throws ClassCastException if the type of the attribute is incorrect.
   */
  default <T> T getAttribute(String name, Class<T> cls, T defaultValue) throws ClassCastException
  {
    Map<String, Serializable> map = this.getAttributes();
    if (map == null)
      return defaultValue;

    // Step 1 try to get the value
    Object value = map.get(name);

    // Step 2 we found it so all is good
    if (value != null && cls.isInstance(value))
    {
      return cls.cast(value);
    }

    // Step 3 if the type is wrong this is an error
    if (value != null)
      throw new ClassCastException("incorrect attributed type " + cls + "!=" + value.getClass());

    return defaultValue;
  }

  /**
   * Check if an attribute is set.
   *
   * @param key the name of the attribute
   * @return {@code true} if the attribute exists, {@code false} otherwise
   */
  default boolean hasAttribute(String key)
  {
    Map<String, Serializable> map = this.getAttributes();
    return map != null && map.containsKey(key);
  }

  /**
   * Remove an attribute by its name.
   *
   * @param key the name of the attribute to remove
   */
  default void removeAttribute(String key)
  {
    Map<String, Serializable> map = this.getAttributes();
    if (map != null)
      map.remove(key);
  }

  /**
   * Copy all attributes from another {@code Expandable} object.
   *
   * @param ai the source {@code Expandable} object
   * @throws NullPointerException if the source object is {@code null}
   */
  default void copyAttributes(Expandable ai)
  {
    Map<String, Serializable> attr = ai.getAttributes();
    if (attr != null)
      getAttributes().putAll(attr);
  }
//</editor-fold>
}
