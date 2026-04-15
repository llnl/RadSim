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
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class FloatEncodingImplNGTest
{

  public FloatEncodingImplNGTest() throws ProtoException
  {
  }

  public static class A
  {
    float i;
    Float j;

    public A()
    {
    }

    public A(float i)
    {
      this.i = i;
      this.j = i;
    }

    public float getI()
    {
      return i;
    }

    public void setI(float i)
    {
      this.i = i;
    }

    public Float getJ()
    {
      return j;
    }

    public void setJ(Float j)
    {
      this.j = j;
    }
  }

  public static class AProto extends MessageEncoding<A>
  {
    final static ProtoField[] FIELDS;

    static
    {
      ProtoBuilder<A, A> builder = newBuilder(null, "A", A::new);
      builder.field("i", 1).type(Type.Float).asFloat(A::getI, A::setI);
      builder.field("j", 2).type(Type.Float).as(A::getJ, A::setJ);
      FIELDS = builder.toFields();
    }

    @Override
    public ProtoField[] getFields()
    {
      return FIELDS;
    }
  }

  @Test
  public void testEndToEnd() throws ProtoException
  {
    A a = new A(12345);
    MessageEncoding<A> encoding = new AProto();
    byte[] bytes = encoding.toBytes(a);
    a = encoding.parseBytes(bytes);
    System.out.println(a.i);
    System.out.println(a.j);
    for (byte b : bytes)
    {
      System.out.print(String.format("%02x ", b & 0xff));
    }
    System.out.println();
    assertEquals(a.i, 12345.0f, 1e-8f);
    assertEquals(a.j, 12345.0f, 1e-8f);
  }

  @Test
  public void testParseField() throws Exception
  {
    ProtoContext context = new ProtoContext();
    ProtoField field = AProto.FIELDS[1];
    int type = 5;
    A o = new A(0);
    ByteBuffer b = ByteBuffer.allocate(4);
    b.order(ByteOrder.LITTLE_ENDIAN);
    b.putFloat(1.2345f);
    b.rewind();
    FloatEncodingImpl instance = new FloatEncodingImpl();
    instance.parseField(context, field, type, o, ByteSource.wrap(b));
    assertEquals(o.i, 1.2345f);
  }

  @Test(expectedExceptions = ProtoException.class)
  public void testParseFieldUnderflow() throws Exception
  {
    ProtoContext context = new ProtoContext();
    ProtoField field = AProto.FIELDS[1];
    int type = 1;
    A o = new A(0);
    ByteBuffer b = ByteBuffer.allocate(1);
    b.order(ByteOrder.LITTLE_ENDIAN);
    b.put((byte) 1);
    b.rewind();
    Int32Encoding instance = new Int32Encoding();
    instance.parseField(context, field, type, o, ByteSource.wrap(b));
    assertEquals(o.i, 12345);
  }

  @Test(expectedExceptions = ProtoException.class)
  public void testParseFieldBadWire() throws Exception
  {
    ProtoContext context = new ProtoContext();
    ProtoField field = AProto.FIELDS[1];
    int type = 6;
    A o = new A(0);
    ByteBuffer b = ByteBuffer.allocate(8);
    b.order(ByteOrder.LITTLE_ENDIAN);
    b.putLong(12345);
    b.rewind();
    FloatEncodingImpl instance = new FloatEncodingImpl();
    instance.parseField(context, field, type, o, ByteSource.wrap(b));
    assertEquals(o.i, 12345);
  }

  @Test
  public void testSerializeField() throws ProtoException
  {
    ProtoField field = AProto.FIELDS[1];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Object obj = new A(12345);
    FloatEncodingImpl instance = new FloatEncodingImpl();
    instance.serializeField(field, baos, obj);
    assertEquals(baos.toByteArray(), new byte[]
    {
      0x0d, 0, (byte) 0xe4, 0x40, 0x46
    });
  }

  @Test
  public void testGetWireType()
  {
    FloatEncodingImpl instance = new FloatEncodingImpl();
    int expResult = 5;
    int result = instance.getWireType();
    assertEquals(result, expResult);
  }

}
