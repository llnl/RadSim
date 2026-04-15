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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

/**
 *
 * Special encoder for packed scalars.
 *
 * This is constructed by ProtoBuilder.TypeSpec.packed methods.
 *
 * @author nelson85
 */
class PackedScalarEncoding extends MessageEncoding<Object>
{
  final ScalarEncoding parent;

  PackedScalarEncoding(ScalarEncoding e)
  {
    this.parent = e;
  }

  Object newContext()
  {
    return newCollector();
  }

  @Override
  public void parseField(ProtoContext context, ProtoField field, int type, Object obj, ByteSource bs) throws ProtoException
  {
    if (type != 2)
      throw new ProtoException("bad type", bs.position());

    int size = Int32Encoding.decodeVInt32(bs);
    ByteSource bs2 = context.enterMessage(bs, size);

    // Collect in workspace
    PackedCollector ws = (PackedCollector) context.getState(field, true);
    if (ws == null)
    {
      ws = newCollector();
      context.setState(field, ws);
    }
    ws.reserve(size);

    type = parent.getWireType();
    while (bs2.hasRemaining())
    {
      parent.parseField(context, ws, type, ws, bs2);
    }
    context.leaveMessage(bs2);
  }

  @Override
  public void serializeField(ProtoField field, ByteArrayOutputStream baos, Object obj) throws ProtoException
  {

    //check for field existance
    Object result = (Object) ((Function) field.getter).apply(obj);

    // if missing skip
    if (result == null)
      return;

    // Set up a dealer
    ProtoField dealer = newDealer(result);

    // suppress adding tag
    dealer.id = -1;

    // Guess how much memory we will need
    int wiretype = parent.getWireType();
    int ns = 1;
    switch (wiretype)
    {
      case 0:
        ns = 1;
        break;
      case 1:
        ns = 8;
        break;
      case 5:
        ns = 4;
        break;
      default:
        break;
    }

    // Pack up contents
    ByteArrayOutputStream bs2 = new ByteArrayOutputStream(Array.getLength(result) * ns);
    for (int i = 0; i < Array.getLength(result); ++i)
    {
      parent.serializeField(dealer, bs2, result);
    }
    byte[] contents = bs2.toByteArray();

    // field and wire type
    ProtoEncoding.encodeTag(baos, field, WIRE_LEN);
    // size
    Int32Encoding.encodeVInt32(baos, contents.length);
    // place contents
    baos.writeBytes(contents);
  }

  @Override
  public void parseFinish(ProtoContext context, ProtoField field, Object o)
  {
    PackedCollector ws = (PackedCollector) context.getState(field, true);
    if (ws != null)
      ((BiConsumer) field.setter).accept(o, ws.toObject());
  }

//<editor-fold desc="private">
  private PackedCollector newCollector()
  {
    if (parent instanceof Int32Encoding)
      return new ProtoCollectorInt();
    if (parent instanceof Int64Encoding)
      return new ProtoCollectorLong();
    if (parent instanceof FloatEncoding)
      return new ProtoCollectorFloat();
    if (parent instanceof DoubleEncoding)
      return new ProtoCollectorDouble();
    throw new UnsupportedOperationException("Not supported yet.");
    // FIXME missing bool
  }

  private ProtoField newDealer(Object result)
  {
    if (parent instanceof IntEncoding)
      return new ProtoDealerInt(result);
    else if (parent instanceof LongEncoding)
      return new ProtoDealerLong(result);
    else if (parent instanceof FloatEncoding)
      return new ProtoDealerFloat(result);
    else if (parent instanceof DoubleEncoding)
      return new ProtoDealerDouble(result);
    else
      throw new UnsupportedOperationException();
  }

  @Override
  public ProtoField[] getFields()
  {
    return null;
  }

  static abstract class PackedCollector extends ProtoField
  {
    abstract void reserve(int size);

    abstract Object toObject();
  }

  static class ProtoCollectorInt extends PackedCollector
  {
    int[] buffer = null;
    int index = 0;

    ProtoCollectorInt()
    {
      ObjIntConsumer<ProtoCollectorInt> u = ProtoCollectorInt::push;
      this.setter = u;
    }

    static void push(ProtoCollectorInt t, int value)
    {
      if (t.index == t.buffer.length)
      {
        t.resize(t.buffer.length + 32);
      }
      t.buffer[t.index++] = value;
    }

    public void resize(int size)
    {
      int[] nb = new int[size];
      if (buffer != null)
        System.arraycopy(buffer, 0, nb, 0, buffer.length);
      buffer = nb;
    }

    @Override
    public void reserve(int size)
    {
      if (buffer == null)
        resize(size / 4);
      else
        resize(buffer.length + size / 4);
    }

    @Override
    public Object toObject()
    {
      if (index != buffer.length)
        return Arrays.copyOfRange(buffer, 0, index);
      return buffer;
    }
  }

  static class ProtoCollectorLong extends PackedCollector
  {
    long[] buffer = null;
    int index = 0;

    ProtoCollectorLong()
    {
      this.setter = (ObjLongConsumer<ProtoCollectorLong>) (ProtoCollectorLong::push);
    }

    static void push(ProtoCollectorLong t, long value)
    {
      if (t.index == t.buffer.length)
      {
        t.resize(t.buffer.length + 32);
      }
      t.buffer[t.index++] = value;
    }

    public void resize(int size)
    {
      long[] nb = new long[size];
      if (buffer != null)
        System.arraycopy(buffer, 0, nb, 0, buffer.length);
      buffer = nb;
    }

    @Override
    public void reserve(int size)
    {
      if (buffer == null)
        resize(size / 8);
      else
        resize(buffer.length + size / 8);
    }

    @Override
    public Object toObject()
    {
      if (index != buffer.length)
        return Arrays.copyOfRange(buffer, 0, index);
      return buffer;
    }
  }

  static class ProtoCollectorFloat extends PackedCollector
  {
    float[] buffer = null;
    int index = 0;

    ProtoCollectorFloat()
    {
      this.setter = (ObjFloatConsumer<ProtoCollectorFloat>) (ProtoCollectorFloat::push);
    }

    static void push(ProtoCollectorFloat t, float value)
    {
      if (t.index == t.buffer.length)
      {
        t.resize(t.buffer.length + 32);
      }
      t.buffer[t.index++] = value;
    }

    public void resize(int size)
    {
      float[] nb = new float[size];
      if (buffer != null)
        System.arraycopy(buffer, 0, nb, 0, buffer.length);
      buffer = nb;
    }

    @Override
    public void reserve(int size)
    {
      if (buffer == null)
        resize(size / 4);
      else
        resize(buffer.length + size / 4);
    }

    @Override
    public Object toObject()
    {
      if (index != buffer.length)
        return Arrays.copyOfRange(buffer, 0, index);
      return buffer;
    }
  }

  static class ProtoCollectorDouble extends PackedCollector
  {
    double[] buffer = null;
    int index = 0;

    ProtoCollectorDouble()
    {
      this.setter = (ObjDoubleConsumer<ProtoCollectorDouble>) (ProtoCollectorDouble::push);
    }

    static void push(ProtoCollectorDouble t, double value)
    {
      if (t.index == t.buffer.length)
      {
        t.resize(t.buffer.length + 32);
      }
      t.buffer[t.index++] = value;
    }

    public void resize(int size)
    {
      double[] nb = new double[size];
      if (buffer != null)
        System.arraycopy(buffer, 0, nb, 0, buffer.length);
      buffer = nb;
    }

    @Override
    public void reserve(int size)
    {
      if (buffer == null)
        resize(size / 8);
      else
        resize(buffer.length + size / 8);
    }

    @Override
    public Object toObject()
    {
      if (index != buffer.length)
        return Arrays.copyOfRange(buffer, 0, index);
      return buffer;
    }
  }

  private class ProtoDealerInt extends ProtoField
  {
    int[] buffer;
    int index = 0;

    ProtoDealerInt(Object o)
    {
      buffer = (int[]) o;
      ToIntFunction u = (a) -> buffer[index++];
      this.getter = u;
    }
  }

  private class ProtoDealerLong extends ProtoField
  {
    long[] buffer;
    int index = 0;

    ProtoDealerLong(Object o)
    {
      buffer = (long[]) o;
      ToLongFunction u = (a) -> buffer[index++];
      this.getter = u;
    }
  }

  private class ProtoDealerFloat extends ProtoField
  {
    float[] buffer;
    int index = 0;

    ProtoDealerFloat(Object o)
    {
      buffer = (float[]) o;
      ToFloatFunction u = (a) -> buffer[index++];
      this.getter = u;
    }
  }

  private class ProtoDealerDouble extends ProtoField
  {
    double[] buffer;
    int index = 0;

    ProtoDealerDouble(Object o)
    {
      buffer = (double[]) o;
      ToDoubleFunction u = (a) -> buffer[index++];
      this.getter = u;
    }
  }

  @Override
  public String getSchemaName()
  {
    return this.parent.getSchemaName();
  }

  @Override
  public String getSchemaOptions()
  {
    return " [packed=true]";
  }

//</editor-fold>
}
