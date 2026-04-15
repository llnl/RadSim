/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.util.function.Predicate;

/**
 * Opaque data structure holding the features for the encoder.
 *
 * @author nelson85
 */
public class ProtoField
{
  int id;
  String name;
  ProtoEncoding encoding;
  Object setter;
  Object getter;
  boolean repeated;
  Predicate optional;

  /**
   * @return the id
   */
  public int getId()
  {
    return id;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * @return the encoding
   */
  public ProtoEncoding getEncoding()
  {
    return encoding;
  }

  /**
   * @return the setter
   */
  public Object getSetter()
  {
    return setter;
  }

  /**
   * @return the getter
   */
  public Object getGetter()
  {
    return getter;
  }

  /**
   * @return the repeated
   */
  public boolean isRepeated()
  {
    return repeated;
  }

  /**
   * @return the optional
   */
  public Predicate getOptional()
  {
    return optional;
  }
}
