/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class BoolEncodingImplNGTest
{

  public static class A
  {
    boolean i;
    Boolean j;

    public A()
    {
    }

    public A(boolean i)
    {
      this.i = i;
      this.j = i;
    }

    public boolean getI()
    {
      return i;
    }

    public void setI(boolean i)
    {
      this.i = i;
    }

    public Boolean getJ()
    {
      return j;
    }

    public void setJ(Boolean j)
    {
      this.j = j;
    }
  }

  public static class AProto extends MessageEncoding<A>
  {
    final static ProtoField[] FIELDS;

    static
    {
      ProtoBuilder<A, A> builder = newBuilder(null, "A", () -> new A());
      builder.field("i", 1).type(Type.Bool).asBool(A::getI, A::setI);
      builder.field("j", 2).type(Type.Bool).as(A::getJ, A::setJ);
      FIELDS = builder.toFields();
    }

    @Override
    public ProtoField[] getFields()
    {
      return FIELDS;
    }
  }

  @Test(expectedExceptions = ProtoException.class)
  public void testParseFieldBadWire() throws Exception
  {
    ProtoContext context = new ProtoContext();
    ProtoField field = AProto.FIELDS[1];
    int type = 6;
    A o = new A(true);
    ByteBuffer b = ByteBuffer.allocate(4);
    BoolEncodingImpl instance = new BoolEncodingImpl();
    instance.parseField(context, field, type, o, ByteSource.wrap(b));
    assertEquals(o.i, 12345);
  }

  @Test
  public void testSerializeField() throws ProtoException
  {
    ProtoField field = AProto.FIELDS[1];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Object obj = new A(true);
    BoolEncodingImpl instance = new BoolEncodingImpl();
    instance.serializeField(field, baos, obj);
    assertEquals(baos.toByteArray(), new byte[]
    {
      0x08, 1,
    });
  }

  @Test
  public void testGetWireType()
  {
    BoolEncodingImpl instance = new BoolEncodingImpl();
    int expResult = 0;
    int result = instance.getWireType();
    assertEquals(result, expResult);
  }

}
