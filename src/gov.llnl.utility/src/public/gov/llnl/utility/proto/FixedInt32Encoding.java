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
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * Encoding for a signed int32 with fixed length encoding.
 *
 * @author nelson85
 */
class FixedInt32Encoding extends Int32Encoding
{

  final static FixedInt32Encoding INSTANCE = new FixedInt32Encoding();

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object o, ByteSource bs)
          throws ProtoException
  {
    if (type != 5)
      throw new ProtoException("bad wire type", bs.position());

    ByteBuffer b = bs.request(Integer.BYTES);
    if (b.remaining() != Integer.BYTES)
      throw new ProtoException("truncated fixed int field", bs.position());
    b.order(ByteOrder.LITTLE_ENDIAN);
    if (field.setter instanceof BiConsumer)
      ((BiConsumer) field.setter).accept(o, b.getInt());
    else
      ((ObjIntConsumer) field.setter).accept(o, b.getInt());
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj)
          throws ProtoException
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
      ProtoEncoding.encodeTag(baos, field, WIRE_FIXED32);
    ByteBuffer bb = ByteBuffer.allocate(4);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putInt(result);
    baos.writeBytes(bb.array());
  }

  @Override
  public int getWireType()
  {
    return 5;
  }
  
    @Override
  public String getSchemaName()
  {
    return "fixedint32";
  }

}
