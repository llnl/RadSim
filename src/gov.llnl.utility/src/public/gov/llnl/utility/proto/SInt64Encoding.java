/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjLongConsumer;
import java.util.function.ToLongFunction;

/**
 * Encoding for a long with scissor as variable encoding.
 *
 * @author nelson85
 */
class SInt64Encoding extends Int64Encoding
{
  final static SInt64Encoding INSTANCE = new SInt64Encoding();

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object o, ByteSource b)
          throws ProtoException
  {
    if (type != 0)
      throw new ProtoException("bad wire type", b.position());
    long v = Int64Encoding.decodeVInt64(b);
    if ((v & 1) == 0)
      v = v / 2;
    else
      v = -v / 2 - 1;
    if (field.setter instanceof BiConsumer)
      ((BiConsumer) field.setter).accept(o, v);
    else
      ((ObjLongConsumer) field.setter).accept(o, v);
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj)
          throws ProtoException
  {
    long result;
    if (field.getter instanceof Function)
    {
      Object value = ((Function) field.getter).apply(obj);
      if (value == null)
        return;
      result = (Long) value;
    }
    else
      result = ((ToLongFunction) field.getter).applyAsLong(obj);

    if (result < 0)
      result = -result * 2 - 1;
    else
      result *= 2;
    // field and wire type
    if (field.id != -1)
      ProtoEncoding.encodeTag(baos, field, WIRE_VARINT);
    // contents
    Int64Encoding.encodeVInt64(baos, result);
  }

  @Override
  public String getSchemaName()
  {
    return "sint64";
  }
}
