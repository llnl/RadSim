/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import static gov.llnl.utility.ClassUtilities.*;
import gov.llnl.utility.ClassUtilities.Primitive;
import gov.llnl.utility.ClassUtilities.ValueOf;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 * Test code for ClassUtilities.
 */
public class ClassUtilitiesNGTest
{
  static class TestCase
  {
    Class cls;
    Object expectedResult;

    private TestCase(Class<?> cls, Primitive result)
    {
      this.cls = cls;
      this.expectedResult = result;
    }
  }

  @Test
  public void testConstructor()
  {
    ClassUtilities instance = new ClassUtilities();
  }

  /**
   * Test of getPrimitive method, of class ClassUtilities.
   */
  @Test
  public void testGetPrimitive()
  {
    TestCase[] cases = new TestCase[]
    {
      new TestCase(boolean.class, BOOLEAN_PRIMITIVE),
      new TestCase(char.class, CHARACTER_PRIMITIVE),
      new TestCase(short.class, SHORT_PRIMITIVE),
      new TestCase(int.class, INTEGER_PRIMITIVE),
      new TestCase(long.class, LONG_PRIMITIVE),
      new TestCase(float.class, FLOAT_PRIMITIVE),
      new TestCase(double.class, DOUBLE_PRIMITIVE),
      new TestCase(byte.class, BYTE_PRIMITIVE),
      new TestCase(null, null)
    };

    for (TestCase test : cases)
    {
      Class c = test.cls;
      ClassUtilities.Primitive result = ClassUtilities.getPrimitive(c);
      assertEquals(result, test.expectedResult);
    }
  }

  enum EnumTest
  {
    A, B
  };

  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testNewValueOf()
  {
    ValueOf vo = ClassUtilities.newValueOf(EnumTest.class);
    Object result = vo.valueOf("A");
    assertEquals(result, EnumTest.A);

    assertEquals(ClassUtilities.newValueOf(this.getClass()), null);
    assertEquals(ClassUtilities.newValueOf(int.class), INTEGER_PRIMITIVE);
    assertEquals(ClassUtilities.newValueOf(Integer.class), INTEGER_PRIMITIVE);
    assertEquals(ClassUtilities.newValueOf(String.class), STRING_CONVERTER);
    assertEquals(ClassUtilities.newValueOf(UUID.class), UUID_CONVERTER);
    assertEquals(ClassUtilities.newValueOf(Instant.class), INSTANT_CONVERTER);
    assertEquals(ClassUtilities.newValueOf(Boolean.class), BOOLEAN_PRIMITIVE);

    // Test UnsupportedOperationException
    ClassUtilities.newValueOf(Date.class);
  }

  class Foo
  {
  }

  /**
   * Test of isInnerClass method, of class ClassUtilities.
   */
  @Test
  public void testIsInnerClass()
  {
    assertTrue(isInnerClass(Foo.class));
    assertFalse(isInnerClass(ClassUtilitiesNGTest.class));
  }

  /**
   * Test of isBoxed method, of class ClassUtilities.
   */
  @Test
  public void testIsBoxed()
  {
    assertTrue(isBoxed(Short.class));
    assertTrue(isBoxed(Character.class));
    assertTrue(isBoxed(Integer.class));
    assertTrue(isBoxed(Long.class));
    assertTrue(isBoxed(Byte.class));
    assertTrue(isBoxed(Double.class));
    assertTrue(isBoxed(Float.class));
    assertFalse(isBoxed(Object.class));
  }

  /**
   * Test of getPrimitiveType method, of class ClassUtilities.
   */
  @Test
  public void testGetPrimitiveType()
  {
    assertEquals(getPrimitiveType(Byte.class), Byte.TYPE);
    assertEquals(getPrimitiveType(Character.class), Character.TYPE);
    assertEquals(getPrimitiveType(Short.class), Short.TYPE);
    assertEquals(getPrimitiveType(Integer.class), Integer.TYPE);
    assertEquals(getPrimitiveType(Long.class), Long.TYPE);
    assertEquals(getPrimitiveType(Float.class), Float.TYPE);
    assertEquals(getPrimitiveType(Double.class), Double.TYPE);
    assertEquals(getPrimitiveType(String.class), null);
  }

  /**
   * Test of forNamePrimative method, of class ClassUtilities.
   */
  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testForNamePrimative()
  {
    assertEquals(ClassUtilities.forNamePrimative("double"), Double.TYPE);
    assertEquals(ClassUtilities.forNamePrimative("int"), Integer.TYPE);
    assertEquals(ClassUtilities.forNamePrimative("boolean"), Boolean.TYPE);
    assertEquals(ClassUtilities.forNamePrimative("char"), Character.TYPE);
    assertEquals(ClassUtilities.forNamePrimative("short"), Short.TYPE);
    assertEquals(ClassUtilities.forNamePrimative("long"), Long.TYPE);

    // Test UnsupportedOperationException
    ClassUtilities.forNamePrimative("");
  }

  /**
   * Test of getBoxedPrimitive method, of class ClassUtilities.
   */
  @Test
  public void testGetBoxedPrimitive()
  {
    assertEquals(ClassUtilities.getBoxedPrimitive(null), null);
    assertEquals(ClassUtilities.getBoxedPrimitive(Integer.class), INTEGER_PRIMITIVE);
    assertEquals(ClassUtilities.getBoxedPrimitive(Double.class), DOUBLE_PRIMITIVE);
    assertEquals(ClassUtilities.getBoxedPrimitive(Boolean.class), BOOLEAN_PRIMITIVE);
    assertEquals(ClassUtilities.getBoxedPrimitive(Float.class), FLOAT_PRIMITIVE);
    assertEquals(ClassUtilities.getBoxedPrimitive(Byte.class), BYTE_PRIMITIVE);
    assertEquals(ClassUtilities.getBoxedPrimitive(Short.class), SHORT_PRIMITIVE);
    assertEquals(ClassUtilities.getBoxedPrimitive(Long.class), LONG_PRIMITIVE);
    assertEquals(ClassUtilities.getBoxedPrimitive(Character.class), CHARACTER_PRIMITIVE);

  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testBooleanPrimitive()
  {
    Primitive primitive = BOOLEAN_PRIMITIVE;
    assertEquals(primitive.valueOf("true"), true);
    assertEquals(primitive.valueOf("false"), false);
    assertEquals(primitive.valueOf("0"), false);
    assertEquals(primitive.valueOf("1"), true);
    assertEquals(primitive.valueOf("TRUE"), true);
    assertEquals(primitive.valueOf("FALSE"), false);
    assertEquals(primitive.valueOf("HelloWorld"), false);
    assertEquals(primitive.getBoxedType(), Boolean.class);
    assertEquals(primitive.getPrimitiveType(), boolean.class);
    assertEquals(primitive.cast(Boolean.valueOf(true)), true);
    assertEquals(primitive.cast(Boolean.valueOf(false)), false);
    // Test ClassCastException
    assertEquals(primitive.cast("true"), true);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testBytePrimitive()
  {
    Primitive primitive = BYTE_PRIMITIVE;
    assertEquals(primitive.valueOf("64"), (byte) 64);
    assertEquals(primitive.getBoxedType(), Byte.class);
    assertEquals(primitive.getPrimitiveType(), byte.class);
    assertEquals(primitive.cast(Byte.valueOf("64")), (byte) 64);
    // Test ClassCastException
    assertEquals(primitive.cast("true"), (byte) 64);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testCharacterPrimitive()
  {
    Primitive primitive = CHARACTER_PRIMITIVE;
    assertEquals(primitive.valueOf("Follow the white rabbit"), 'F');
    assertEquals(primitive.getBoxedType(), Character.class);
    assertEquals(primitive.getPrimitiveType(), char.class);
    assertEquals(primitive.cast(Character.valueOf('A')), 'A');
    // Test ClassCastException
    assertEquals(primitive.cast(64), 'a');
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testShortPrimitive()
  {
    Primitive primitive = SHORT_PRIMITIVE;
    assertEquals(primitive.valueOf("1776"), (short) 1776);
    assertEquals(primitive.getBoxedType(), Short.class);
    assertEquals(primitive.getPrimitiveType(), short.class);
    assertEquals(primitive.cast(Short.valueOf("1776")), (short) 1776);
    assertEquals(primitive.cast(Byte.valueOf("100")), (short) 100);
    // Test ClassCastException
    assertEquals(primitive.cast("Freedom"), (short) 1776);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testIntegerPrimitive()
  {
    Primitive primitive = INTEGER_PRIMITIVE;
    assertEquals(primitive.valueOf("1776"), 1776);
    assertEquals(primitive.getBoxedType(), Integer.class);
    assertEquals(primitive.getPrimitiveType(), int.class);
    assertEquals(primitive.cast(Integer.valueOf("1776")), 1776);
    assertEquals(primitive.cast(Byte.valueOf("100")), 100);
    assertEquals(primitive.cast(Short.valueOf("99")), 99);
    // Test ClassCastException
    assertEquals(primitive.cast("Freedom"), 1776);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testLongPrimitive()
  {
    Primitive primitive = LONG_PRIMITIVE;
    assertEquals(primitive.valueOf("1776"), 1776L);
    assertEquals(primitive.getBoxedType(), Long.class);
    assertEquals(primitive.getPrimitiveType(), long.class);
    assertEquals(primitive.cast(Long.valueOf("1776")), 1776L);
    assertEquals(primitive.cast(Byte.valueOf("100")), 100L);
    assertEquals(primitive.cast(Short.valueOf("99")), 99L);
    assertEquals(primitive.cast(Integer.valueOf("1776")), 1776L);
    // Test ClassCastException
    assertEquals(primitive.cast("Freedom"), 1776);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testFloatPrimitive()
  {
    Primitive primitive = FLOAT_PRIMITIVE;
    assertEquals(primitive.valueOf("1776"), 1776.0f);
    assertEquals(primitive.getBoxedType(), Float.class);
    assertEquals(primitive.getPrimitiveType(), float.class);
    assertEquals(primitive.cast(Byte.valueOf("64")), 64.0f);
    assertEquals(primitive.cast(Short.valueOf("1776")), 1776.0f);
    assertEquals(primitive.cast(Integer.valueOf("1776")), 1776.0f);
    assertEquals(primitive.cast(Long.valueOf("1776")), 1776.0f);
    assertEquals(primitive.cast(Float.valueOf("1776")), 1776.0f);
    // Test ClassCastException
    assertEquals(primitive.cast("Freedom"), 1776);
  }

  @Test(expectedExceptions =
  {
    ClassCastException.class
  })
  public void testDoublePrimitive()
  {
    Primitive primitive = DOUBLE_PRIMITIVE;
    assertEquals(primitive.valueOf("1776"), 1776.0D);
    assertEquals(primitive.getBoxedType(), Double.class);
    assertEquals(primitive.getPrimitiveType(), double.class);
    assertEquals(primitive.cast(Byte.valueOf("64")), 64.0D);
    assertEquals(primitive.cast(Short.valueOf("1776")), 1776.0D);
    assertEquals(primitive.cast(Integer.valueOf("1776")), 1776.0D);
    assertEquals(primitive.cast(Long.valueOf("1776")), 1776.0D);
    assertEquals(primitive.cast(Float.valueOf("1776")), 1776.0D);
    assertEquals(primitive.cast(Double.valueOf("1776")), 1776.0D);
    // Test ClassCastException
    assertEquals(primitive.cast("Freedom"), 1776);
  }

  @Test
  public void testSTRING_CONVERTER()
  {
    assertEquals(STRING_CONVERTER.valueOf("echo"), "echo");
    assertEquals(STRING_CONVERTER.getObjectType(), String.class);
  }

  @Test
  public void testINSTANT_CONVERTER()
  {
    assertEquals(INSTANT_CONVERTER.valueOf("1776-08-02T00:00:00.00Z"), Instant.parse("1776-08-02T00:00:00.00Z"));
    assertEquals(INSTANT_CONVERTER.getObjectType(), Instant.class);
  }

  @Test(expectedExceptions =
  {
    IllegalArgumentException.class
  })
  public void testUUID_CONVERTER()
  {
    assertEquals(UUID_CONVERTER.valueOf("123456-7890-1234-5678-abcdef"), UUID.fromString("123456-7890-1234-5678-abcdef"));
    assertEquals(UUID_CONVERTER.getObjectType(), UUID.class);

    // Test IllegalArgumentException
    UUID_CONVERTER.valueOf("123456");
  }

  @Test
  public void testEnumConverter()
  {
    EnumConverter ec = new EnumConverter(EnumTest.class);
    assertEquals(ec.valueOf("A"), EnumTest.A);
    assertEquals(ec.getObjectType(), EnumTest.class);
  }

}
