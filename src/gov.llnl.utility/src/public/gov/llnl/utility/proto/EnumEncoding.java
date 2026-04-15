/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

/**
 * Encoding for a Java enum.This needs to be improved to allow for an arbitrary
 * mapping of enums to ordinals.
 *
 * Currently produces a RuntimeException if the ordinal does not match a proper
 * enum.
 *
 * @author nelson85
 * @param <T>
 */
public class EnumEncoding<T extends Enum> implements ProtoEncoding<T>
{
  final Class<T> cls;
  final T[] values;
  final ToIntFunction<T> lambda1;
  final IntFunction<T> lambda2;

  EnumEncoding(Class<T> cls, ToIntFunction<T> f1, IntFunction<T> f2)
  {
    try
    {
      this.cls = cls;

      // Use values if we aren't given another lookup table.
      this.values = (T[]) cls.getDeclaredMethod("values").invoke(null);

      if (f1 == null)
        f1 = (T t) ->
        {
          return 0;
        };

      if (f2 == null)
        f2 = (int i) ->
        {
          if (i >= values.length)
            throw new RuntimeException();
          return values[i];
        };
      this.lambda1 = f1;
      this.lambda2 = f2;
    }
    catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex)
    {
      throw new RuntimeException(ex);
    }

  }

  @Override
  public String getSchemaName()
  {
    return cls.getSimpleName();
  }

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object obj, ByteSource bs)
          throws ProtoException
  {
    int ordinal = Int32Encoding.decodeVInt32(bs);
    T v = this.lambda2.apply(ordinal);
    ((BiConsumer) field.setter).accept(obj, v);
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    T v = (T) ((Function) field.getter).apply(obj);
    if (v == null)
      return;
    int ordinal = this.lambda1.applyAsInt(v);
    ProtoEncoding.encodeTag(baos, field, WIRE_VARINT);
    Int32Encoding.encodeVInt32(baos, ordinal);
  }

}
