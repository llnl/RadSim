/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * Encoding for a signed int32 with variable length encoding.
 *
 * Negative numbers store poorly in this encoding.
 *
 * @author nelson85
 */
public class Int32Encoding extends IntEncoding
{
  final static Int32Encoding INSTANCE = new Int32Encoding();

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object o, ByteSource b)
          throws ProtoException
  {
    if (type != 0)
      throw new ProtoException("bad wire type", b.position());
    if (field.setter instanceof BiConsumer)
      ((BiConsumer) field.setter).accept(o, Int32Encoding.decodeVInt32(b));
    else
      ((ObjIntConsumer) field.setter).accept(o, Int32Encoding.decodeVInt32(b));
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    int result;
    if (field.getter instanceof Function)
    {
      Object value = ((Function) field.getter).apply(obj);
      if (value == null)
        return;
      result = (Integer) value;
    }
    else
      result = ((ToIntFunction) field.getter).applyAsInt(obj);
    // field and wire type
    if (field.id != -1)
      ProtoEncoding.encodeTag(baos, field, WIRE_VARINT);
    // contents
    Int32Encoding.encodeVInt32(baos, result);
  }

  static int decodeVInt32(ByteSource is) throws ProtoException
  {
    int i = 0;
    int shift = 0;
    while (true)
    {
      int j = is.get();
      if (j == -1)
        throw new ProtoException("truncated fixed int field", 0);
      i |= (j & (0x7f)) << shift;
      if ((j & 0x80) == 0)
        break;
      shift += 7;
    }
    return i;
  }

  static void encodeVInt32(ByteArrayOutputStream baos, int v)
  {
    while (true)
    {
      if (v >= 0 && v < 0x80)
      {
        baos.write(v);
        return;
      }
      baos.write((v & 0x7f) | 0x80);
      v >>>= 7;
    }
  }

  @Override
  public int getWireType()
  {
    return 0;
  }

  @Override
  public String getSchemaName()
  {
    return "int32";
  }
}
