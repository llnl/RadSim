/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.proto;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author nelson85
 */
public class ComplexNGTest
{

  @Test
  void testComplex() throws ProtoException
  {
    Complex c = new Complex();
    c.a = 1;
    c.b = -2;
    c.c = 3;
    c.name = "hello";
    c.strs.add("A");
    c.strs.add("B");
    c.strs.add("C");
    c.map.put("A", 1);
    c.map.put("B", 2);

    MessageEncoding<Complex> encoding = new ComplexProto();
    byte[] bytes = encoding.toBytes(c);

    for (byte b : bytes)
    {
      System.out.print(String.format("%02x ", b & 0xff));
    }
    System.out.println();
    Complex c2 = encoding.parseBytes(bytes);
    assertEquals(c2.a, c.a);
    assertEquals(c2.b, c.b);
    assertEquals(c2.c, c.c);
    assertEquals(c2.name, c.name);
    assertEquals(c2.map.get("A"), Integer.valueOf(1));
    assertEquals(c2.map.get("B"), Integer.valueOf(2));
  }

  @Test
  void testSchema()
  {
    Proto3SchemaBuilder builder = new Proto3SchemaBuilder();
    builder.add(new ComplexProto());
    System.out.println(builder.build());
  }
}
