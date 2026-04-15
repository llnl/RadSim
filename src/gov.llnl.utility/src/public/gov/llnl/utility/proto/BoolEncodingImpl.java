/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import gov.llnl.utility.proto.ProtoBuilder.ObjBoolConsumer;
import java.io.ByteArrayOutputStream;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Encoding for a boolean field.
 *
 * @author nelson85
 */
public class BoolEncodingImpl extends BooleanEncoding
{
  final static BoolEncodingImpl INSTANCE = new BoolEncodingImpl();

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object o, ByteSource bs)
          throws ProtoException
  {
    if (type != 0)
      throw new ProtoException("bad wire type", bs.position());
    int v = bs.get();

    // We must get a valid byte
    if (v == -1)
      throw new ProtoException("truncated", bs.position());

    if (field.setter != null)
    {
      if (field.setter instanceof BiConsumer)
        ((BiConsumer) field.setter).accept(o, v != 0);
      else
        ((ObjBoolConsumer) field.setter).accept(o, v != 0);
    }
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    if (field.getter == null)
      return;

    //check for field existance
    boolean result;

    if (field.getter instanceof Function)
    {
      Object value = ((Function) field.getter).apply(obj);
      if (value == null)
        return;
      result = value == Boolean.TRUE;
    }
    else
      result = ((Predicate) field.getter).test(obj);

    // field and wire type
    if (field.id != -1)
      ProtoEncoding.encodeTag(baos, field, WIRE_VARINT);
    baos.write(result ? 1 : 0);
  }

  @Override
  public int getWireType()
  {
    return 0;
  }

  @Override
  public String getSchemaName()
  {
    return "bool";
  }

}
