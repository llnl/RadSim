/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for ReflectionUtilities.
 */
public class ReflectionUtilitiesNGTest
{

  public ReflectionUtilitiesNGTest()
  {
  }

  /**
   * Test of convertMethod method, of class ReflectionUtilities.
   */
  @Test
  public void testConvertMethod() throws NoSuchMethodException
  {
    Method method = this.getClass().getMethod("reference", Object.class);
    BiConsumer result = ReflectionUtilities.convertMethod(method);
    assertNotNull(result);
  }

  Object object;

  public void reference(Object a)
  {
    this.object = a;
  }

  /**
   * Test of getMethod method, of class ReflectionUtilities.
   */
  @Test
  public void testGetMethod()
  {
    BiConsumer result = ReflectionUtilities.getMethod("reference", this.getClass(), Object.class);
    result.accept(this, 2);
    assertEquals(this.object, 2);
  }

  /**
   * Test of getConstructor method, of class ReflectionUtilities.
   */
  @Test
  public void testGetConstructor_String_Class()
  {
    String clsName = A.class.getName();
    Class<A> resultType = A.class;
    Supplier<A> result = ReflectionUtilities.getConstructor(clsName, resultType);
    assertNotNull(result);
  }

  static public class A
  {
    public A()
    {

    }

    public A(String s)
    {

    }
  }

  /**
   * Test of getConstructor method, of class ReflectionUtilities.
   */
  @Test
  public void testGetConstructor_3args()
  {
    String clsName = A.class.getName();
    Class<A> resultType = A.class;
    Class<String> argument = String.class;
    Function<String, A> result = ReflectionUtilities.getConstructor(clsName, resultType, argument);
    assertNotNull(result);
  }

  /**
   * Test of newInstance method, of class ReflectionUtilities.
   */
  @Test
  public void testNewInstance()
  {
    String clsName = A.class.getName();
    Object result = ReflectionUtilities.newInstance(clsName, "Test");
    assertNotNull(result);
  }

  /**
   * Test of argumentsMatch method, of class ReflectionUtilities.
   */
  @Test
  public void testArgumentsMatch() throws NoSuchMethodException
  {
    Method method = ReflectionUtilities.class.getMethod("argumentsMatch", Parameter[].class, Class[].class);
    Parameter[] parameters = method.getParameters();
    assertTrue(ReflectionUtilities.argumentsMatch(parameters, Parameter[].class, Class[].class));
    assertFalse(ReflectionUtilities.argumentsMatch(parameters, Parameter[].class));
  }

}
