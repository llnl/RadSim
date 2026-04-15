/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;

/**
 * Special encoding that includes one of a given type.
 *
 * All types must derive from a common base.
 *
 * @author nelson85
 * @param <T>
 */
public class OneOfEncoding<T> extends MessageEncoding<T>
{
  final Class<T> cls;
  final ProtoBuilder builder;
  ProtoField[] fields;

  public OneOfEncoding(String name, Class<T> cls)
  {
    this.cls = cls;
    this.builder = newBuilder(null, name, () -> new Object[1],
            (p) -> (T) ((Object[]) p)[0]
    );
  }

  public void add(int code, Class<? extends T> cls, MessageEncoding<? extends T> encoding)
  {
    builder.field("c" + code, code, () -> new ClassField(cls))
            .encoding(encoding)
            .as(o -> o, (o, v) -> ((Object[]) o)[0] = v);
  }

  private static class ClassField extends ProtoField
  {
    Class cls;

    ClassField(Class cls)
    {
      this.cls = cls;
    }
  }

  @Override
  public byte[] serializeContents(ProtoContext context, T values) throws ProtoException
  {
    if (values == null)
      return null;
    // Figure out which encoding is best
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ClassField closestField = null;
    int closestDistance = Integer.MAX_VALUE;
    Class<?> valueClass = values.getClass();

    ProtoField[] list = this.getFields();
    for (ProtoField f : list)
    {
      if (f instanceof ClassField)
      {
        ClassField u = (ClassField) f;
        if (u.cls.isInstance(values))
        {
          int distance = getInheritanceDistance(valueClass, u.cls);
          if (distance < closestDistance)
          {
            closestDistance = distance;
            closestField = u;
          }
        }
      }
    }

    if (closestField != null)
    {
      closestField.encoding.serializeField(closestField, baos, values);
      return baos.toByteArray();
    }

    return baos.toByteArray();
  }

  private static int getInheritanceDistance(Class<?> candidate, Class<?> target)
  {
    if (!target.isAssignableFrom(candidate))
      return Integer.MAX_VALUE;
    if (candidate.equals(target))
      return 0;
    int distance = 0;
    Class<?> current = candidate;
    while (current != null && !current.equals(target))
    {
      if (target.isInterface())
      {
        // Check all interfaces
        for (Class<?> iface : current.getInterfaces())
        {
          if (iface.equals(target))
            return distance + 1;
        }
      }
      current = current.getSuperclass();
      distance++;
    }
    return distance;
  }

  @Override
  public ProtoField[] getFields()
  {
    if (fields == null)
      fields = builder.toFields();
    return fields;
  }

}
