/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Encoding for a map of key value pairs.
 *
 * By default this will produce a HashMap.
 *
 * @author nelson85
 * @param <T1>
 * @param <T2>
 */
class MapEncoding<T1, T2> implements ProtoEncoding<Map<T1, T2>>
{
  private final ProtoField field1 = new ProtoField();
  private final ProtoField field2 = new ProtoField();
  private final Supplier<Map<T1, T2>> supplier;

  public MapEncoding(ProtoEncoding<T1> m1, ProtoEncoding<T2> m2, Supplier<Map<T1, T2>> supplier)
  {
    field1.name = "key";
    field2.name = "value";
    field1.id = 1;
    field2.id = 2;
    field1.setter = lambda1;
    field1.getter = lambda3;
    field1.encoding = m1;
    field2.setter = lambda2;
    field2.getter = lambda4;
    field2.encoding = m2;
    if (supplier == null)
      supplier = HashMap::new;
    this.supplier = supplier;
  }

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object obj, ByteSource bs)
          throws ProtoException
  {
    if (type != WIRE_LEN)
      throw new ProtoException("bad wire type", bs.position());

    // Store a map for state
    Map map = (Map) context.getState(field, false);
    if (map == null)
    {
      map = this.supplier.get();
      context.setState(field, map);
    }

    int size = Int32Encoding.decodeVInt32(bs);
    ByteSource bs2 = context.enterMessage(bs, size);
    Object[] ref = new Object[2];
    while (bs2.hasRemaining())
    {
      int key = ProtoEncoding.decodeTag(bs2);  // validated varint key
      if (key == -1)  // EOF inside a length-delimited submessage is unusual, but safe guard
        break;

      int id = key >>> 3;
      int type2 = key & 0x7;

      switch (id)
      {
        case 1:
          field1.encoding.parseField(context, field1, type2, ref, bs2);
          break;
        case 2:
          field2.encoding.parseField(context, field2, type2, ref, bs2);
          break;
        default:
          MessageEncoding.ignoreField(type2, bs2);
          break;
      }
    }
    if (ref[0] == null)
      throw new ProtoException("key is null", bs.position());
    map.put(ref[0], ref[1]);

    context.leaveMessage(bs2);
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    Map<Object, Object> map = (Map) ((Function) field.getter).apply(obj);
    if (map == null)
      return;
    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
    for (Map.Entry entry : map.entrySet())
    {
      try
      {
        baos2.reset();
        field1.encoding.serializeField(field1, baos2, entry);
        field2.encoding.serializeField(field2, baos2, entry);
        ProtoEncoding.encodeTag(baos, field, WIRE_LEN);
        Int32Encoding.encodeVInt32(baos, baos2.size());
        baos2.writeTo(baos);
      }
      catch (IOException ex)
      {
        throw new RuntimeException(ex);
      }
    }
  }

  final static BiConsumer lambda1 = (BiConsumer) (Object r, Object v) -> ((Object[]) r)[0] = v;
  final static BiConsumer lambda2 = (BiConsumer) (Object r, Object v) -> ((Object[]) r)[1] = v;
  final static Function lambda3 = (Function) (Object r) -> ((Map.Entry) r).getKey();
  final static Function lambda4 = (Function) (Object r) -> ((Map.Entry) r).getValue();

  @Override
  public void parseFinish(ProtoContext context, ProtoField field, Object obj)
  {
    Object value = context.getState(field, false);
    if (value != null)
      ((BiConsumer) field.setter).accept(obj, value);
  }

  @Override
  public String getSchemaName()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("map<").append(this.field1.encoding.getSchemaName()).append(",").append(this.field2.encoding.getSchemaName()).append(">");
    return sb.toString();
  }

}
