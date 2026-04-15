/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import gov.llnl.utility.proto.ProtoBuilder.ObjFloatConsumer;
import gov.llnl.utility.proto.ProtoBuilder.ToFloatFunction;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Encoding for a float with fixed length encoding.
 *
 * @author nelson85
 */
class FloatEncodingImpl extends FloatEncoding
{
  final static FloatEncodingImpl INSTANCE = new FloatEncodingImpl();

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object o, ByteSource bs)
          throws ProtoException
  {
    if (type != 5)
      throw new ProtoException("bad wire type", bs.position());
    ByteBuffer b = bs.request(Float.BYTES);
    if (b.remaining()!=Float.BYTES)
      throw new ProtoException("truncated float", bs.position());
    b.order(ByteOrder.LITTLE_ENDIAN);
    if (field.setter instanceof BiConsumer)
      ((BiConsumer) field.setter).accept(o, b.getFloat());
    else
      ((ObjFloatConsumer) field.setter).accept(o, b.getFloat());
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    float result;
    if (field.getter instanceof Function)
    {
      Object value = ((Function) field.getter).apply(obj);
      if (value == null)
        return;
      result = (Float) value;
    }
    else
      result = ((ToFloatFunction) field.getter).applyAsFloat(obj);
    // field and wire type
    if (field.id != -1)
      ProtoEncoding.encodeTag(baos, field, WIRE_FIXED32);
    ByteBuffer bb = ByteBuffer.allocate(4);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putFloat(result);
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
    return "float";
  }

}
