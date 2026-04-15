/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjLongConsumer;
import java.util.function.ToLongFunction;

/**
 * Encoding for a signed int64 with fixed length encoding.
 *
 * @author nelson85
 */
public class FixedInt64Encoding extends Int64Encoding
{

  final static FixedInt64Encoding INSTANCE = new FixedInt64Encoding();

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object o, ByteSource bs)
          throws ProtoException
  {
    if (type != 1)
      throw new ProtoException("bad wire type", bs.position());
    ByteBuffer b = bs.request(Long.BYTES);
    if (b.remaining() != Long.BYTES)
      throw new ProtoException("truncated fixed int field", bs.position());
    b.order(ByteOrder.LITTLE_ENDIAN);
    if (field.setter instanceof BiConsumer)
      ((BiConsumer) field.setter).accept(o, b.getLong());
    else
      ((ObjLongConsumer) field.setter).accept(o, b.getLong());
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
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

    // field and wire type
    if (field.id != -1)
      ProtoEncoding.encodeTag(baos, field, WIRE_FIXED64);
    ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putLong(result);
    baos.writeBytes(bb.array());
  }

  @Override
  public int getWireType()
  {
    return 1;
  }

  @Override
  public String getSchemaName()
  {
    return "fixedint64";
  }

}
