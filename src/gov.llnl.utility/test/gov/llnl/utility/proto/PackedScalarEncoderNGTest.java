/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class PackedScalarEncoderNGTest
{
  public static class A
  {
    int[] data;
  }

  public static class AProto extends MessageEncoding<A>
  {
    final static ProtoField[] FIELDS;

    static
    {
      ProtoBuilder<A, A> builder = newBuilder(null, "A", A::new);
      builder.field("a", 1).packed(Type.SInt32).as((o) -> o.data, (o, v) -> o.data = v);
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
    A a = new A();
    a.data = new int[]
    {
      1, 2, -3, 4
    };
    MessageEncoding<A> encoding = new AProto();
    byte[] bytes = encoding.toBytes(a);
    for (byte b : bytes)
    {
      System.out.print(String.format("%02x ", b & 0xff));
    }
    System.out.println();

    A a2 = encoding.parseBytes(bytes);
    System.out.println(a2.data);
    assertEquals(a2.data, a.data);
  }

  public PackedScalarEncoderNGTest()
  {
  }

  @Test
  public void testNewContext()
  {
    PackedScalarEncoding instance = new PackedScalarEncoding(MessageEncoding.Type.Float);
    Object result = instance.newContext();
    assertNotNull(result);
  }

  @Test
  public void testParseField() throws Exception
  {
//    System.out.println("parseField");
//    ProtoContext context = null;
//    ProtoField field = null;
//    int type = 0;
//    Object obj = null;
//    ByteBuffer bs = null;
//    PackedScalarEncoder instance = null;
//    instance.parseField(context, field, type, obj, bs);
//    fail("The test case is a prototype.");
  }

  @Test
  public void testSerializeField() throws Exception
  {
//    System.out.println("serializeField");
//    ProtoField field = null;
//    ByteArrayOutputStream baos = null;
//    Object obj = null;
//    PackedScalarEncoder instance = null;
//    instance.serializeField(field, baos, obj);
//    fail("The test case is a prototype.");
  }

  @Test
  public void testParseFinish()
  {
//    System.out.println("parseFinish");
//    ProtoContext context = null;
//    ProtoField field = null;
//    Object o = null;
//    PackedScalarEncoder instance = null;
//    instance.parseFinish(context, field, o);
//    fail("The test case is a prototype.");
  }

  @Test
  public void testGetFields()
  {
    PackedScalarEncoding instance = new PackedScalarEncoding(null);
    ProtoField[] expResult = null;
    ProtoField[] result = instance.getFields();
    assertEquals(result, expResult);
  }

}
