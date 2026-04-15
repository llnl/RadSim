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
import java.util.function.ObjDoubleConsumer;
import java.util.function.ToDoubleFunction;

/**
 *
 * @author nelson85
 */
class DoubleEncodingImpl extends DoubleEncoding
{
  final static DoubleEncodingImpl INSTANCE = new DoubleEncodingImpl();

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object o, ByteSource bs)
          throws ProtoException
  {
    if (type != 1)
      throw new ProtoException("bad wire type", bs.position());
    ByteBuffer b= bs.request(Double.BYTES);
    b.order(ByteOrder.LITTLE_ENDIAN);
    if (field.setter instanceof BiConsumer)
      ((BiConsumer) field.setter).accept(o, b.getDouble());
    else
      ((ObjDoubleConsumer) field.setter).accept(o, b.getDouble());
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    double result;
    if (field.getter instanceof Function)
    {
      Object value = ((Function) field.getter).apply(obj);
      if (value == null)
        return;
      result = (Double) value;
    }
    else
      result = ((ToDoubleFunction) field.getter).applyAsDouble(obj);
    // field and wire type
    if (field.id != -1)
      ProtoEncoding.encodeTag(baos, field, WIRE_FIXED64);
    ByteBuffer bb = ByteBuffer.allocate(Double.BYTES);
    bb.order(ByteOrder.LITTLE_ENDIAN);
    bb.putDouble(result);
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
    return "double";
  }

}
