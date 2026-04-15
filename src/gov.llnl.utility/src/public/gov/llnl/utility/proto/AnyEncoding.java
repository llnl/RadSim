/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This is to allow arbitrary data to be embedded in a proto.
 *
 * Stored as a message (String:1 encoder-class-name bytes:2 contents). The Proto
 * annotation is required for an arbitrary data encoding.
 *
 * @author nelson85
 */
public class AnyEncoding implements ProtoEncoding
{
  ProtoField field1 = new ProtoField();
  ProtoField field2 = new ProtoField();

  AnyEncoding()
  {
    field1.name = "type";
    field1.setter = (BiConsumer) (Object a, Object b) -> ((Object[]) a)[0] = b;
    field1.encoding = Type.String;
    field1.getter = (Function) (Object a) -> a;
    field1.id = 1;
    field2.name = "contents";
    field2.setter = (BiConsumer) (Object a, Object b) -> ((Object[]) a)[1] = b;
    field2.getter = (Function) (Object a) -> a;
    field2.id = 2;
  }

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object obj, ByteSource bs)
          throws ProtoException
  {
    if (type != WIRE_LEN)
      throw new ProtoException("bad wire type", bs.position());

    ProtoField field3 = new ProtoField();
    field3.name = "field";
    field3.setter = field.setter;
    Object[] ref = new Object[2];

    // parse for type2
    int size = Int32Encoding.decodeVInt32(bs);
    ByteSource bs2 = context.enterMessage(bs, size);

    ProtoEncoding encoder = null;
    while (bs2.hasRemaining())
    {
      try
      {
        int key = Int32Encoding.decodeVInt32(bs2);
        int id = key >>> 3;
        int type2 = key & 0x7;

        switch (id)
        {
          case 1:
            field1.encoding.parseField(context, field1, type2, ref, bs2);
            Class c = Class.forName((String) (ref[0]));
            if (!ProtoEncoding.class.isAssignableFrom(c))
              throw new ProtoException("bad encoder", bs2.position());
            encoder = (ProtoEncoding) c.getConstructor().newInstance();
            break;

          case 2:
            if (encoder == null)
              throw new ProtoException("missing encoder", bs2.position());
            encoder.parseField(context, field3, type2, obj, bs2);
            break;

          default:
            MessageEncoding.ignoreField(type2, bs2);
            break;
        }
      }
      catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
      {
        throw new ProtoException("unable to find encoder", bs2.position());
      }
    }
    context.leaveMessage(bs2);
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {
    try
    {
      Object o = ((Function) (field.getter)).apply(obj);
      if (o == null)
        return;
      Encoding annotation = o.getClass().getAnnotation(Encoding.class);
      if (annotation == null)
        throw new RuntimeException("Can't find encoder for " + o.getClass().getName());
      Class cls = annotation.value();
      if (!ProtoEncoding.class.isAssignableFrom(cls))
        throw new RuntimeException("Bad encoder " + cls.getName());
      ProtoEncoding encoder = (ProtoEncoding) cls.getConstructor().newInstance();
      ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
      Type.String.serializeField(field1, baos2, cls.getName());
      encoder.serializeField(field2, baos2, o);
      ProtoEncoding.encodeTag(baos, field, WIRE_LEN);
      Int32Encoding.encodeVInt32(baos, baos2.size());
      baos2.writeTo(baos);
    }
    catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
    {
      throw new RuntimeException("error creating encoder", ex);
    }
    catch (IOException ex)
    {
      throw new RuntimeException("error while serializing", ex);
    }
  }

  @Override
  public String getSchemaName()
  {
    return "bytes";
  }

}
