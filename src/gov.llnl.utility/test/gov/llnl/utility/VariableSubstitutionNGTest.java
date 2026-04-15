/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for VariableSubstitution.
 */
public class VariableSubstitutionNGTest
{

  public VariableSubstitutionNGTest()
  {
  }

  @BeforeClass
  public static void setUpClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownClass() throws Exception
  {
  }

  @BeforeMethod
  public void setUpMethod() throws Exception
  {
  }

  @AfterMethod
  public void tearDownMethod() throws Exception
  {
  }

  /**
   * Test of setOutput method, of class VariableSubstitution.
   */
  @Test
  public void testSetOutput_Writer()
  {
    Writer writer = new OutputStreamWriter(new ByteArrayOutputStream());
    VariableSubstitution instance = new VariableSubstitution();
    instance.setOutput(writer);
    assertSame(instance.output, writer);
  }

  /**
   * Test of setOutput method, of class VariableSubstitution.
   */
  @Test
  public void testSetOutput_OutputStream()
  {
    OutputStream writer = new ByteArrayOutputStream();
    VariableSubstitution instance = new VariableSubstitution();
    instance.setOutput(writer);
    assertNotNull(instance.output);
  }

  /**
   * Test of execute method, of class VariableSubstitution.
   */
  @Test
  public void testExecute_String() throws Exception
  {
    VariableSubstitution instance = new VariableSubstitution();
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    instance.setOutput(boas);
    instance.put("this", "bar");
    instance.execute("test ${this}");
    assertEquals(new String(boas.toByteArray()), "test bar");
  }

  /**
   * Test of execute method, of class VariableSubstitution.
   */
  @Test
  public void testExecute_InputStream() throws Exception
  {
    InputStream input = new ByteArrayInputStream("test ${this}".getBytes());
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    VariableSubstitution instance = new VariableSubstitution();
    instance.put("this", "bar");
    instance.setOutput(boas);
    instance.execute(input);
    assertEquals(new String(boas.toByteArray()), "test bar");
  }

  /**
   * Test of put method, of class VariableSubstitution.
   */
  @Test
  public void testPut_String_String()
  {
    String key = "k";
    String value = "v";
    VariableSubstitution instance = new VariableSubstitution();
    Map<String, String> expResult = new ArrayMap<>();
    expResult.put(key, value);
    VariableSubstitution result = instance.put(key, value);
    // Check the class of result and expResult
    assertEquals(result.substitutions.getClass(), expResult.getClass());
    // Check if the key exists in both maps
    assertEquals(result.substitutions.containsKey(key), expResult.containsKey(key));
    // Check if the values for the key match
    assertEquals(result.substitutions.get(key), expResult.get(key));
  }

  /**
   * Test of put method, of class VariableSubstitution.
   */
  @Test
  public void testPut_String_Object()
  {
    String key = "k";
    Object value = new Object();
    VariableSubstitution instance = new VariableSubstitution();
    Map<String, String> expResult = new ArrayMap<>();
    expResult.put(key, value.toString());
    VariableSubstitution result = instance.put(key, value);
    // Check the class of result and expResult
    assertEquals(result.substitutions.getClass(), expResult.getClass());
    // Check if the key exists in both maps
    assertEquals(result.substitutions.containsKey(key), expResult.containsKey(key));
    // Check if the values for the key match
    assertEquals(result.substitutions.get(key), expResult.get(key));
  }

  /**
   * Test of put method, of class VariableSubstitution.
   */
  @Test
  public void testPut_String_int()
  {
    String key = "k";
    int value = 1;
    VariableSubstitution instance = new VariableSubstitution();
    Map<String, String> expResult = new ArrayMap<>();
    expResult.put(key, Integer.toString(value));
    VariableSubstitution result = instance.put(key, value);
    // Check the class of result and expResult
    assertEquals(result.substitutions.getClass(), expResult.getClass());
    // Check if the key exists in both maps
    assertEquals(result.substitutions.containsKey(key), expResult.containsKey(key));
    // Check if the values for the key match
    assertEquals(result.substitutions.get(key), expResult.get(key));
  }

  /**
   * Test of substitute method, of class VariableSubstitution.
   */
  @Test
  public void testSubstitute()
  {
    String input = "test ${this}";
    VariableSubstitution instance = new VariableSubstitution();
    instance.put("this", "bar");
    String expResult = "test bar";
    String result = instance.substitute(input);
    assertEquals(result, expResult);
  }

  /**
   * Test of substituteString method, of class VariableSubstitution.
   */
  @Test
  public void testSubstituteString()
  {
    Map<String, String> substitutions = ArrayMap.of("this", "bar");
    String expResult = "test bar";
    String result = VariableSubstitution.substituteString("test ${this}", substitutions);
    assertEquals(result, expResult);
  }

}
