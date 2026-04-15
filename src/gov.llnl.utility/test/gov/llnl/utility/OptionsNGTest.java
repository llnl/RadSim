/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import gov.llnl.utility.Options.OptionContent;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test code for Options.
 */
public class OptionsNGTest
{

  public OptionsNGTest()
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
   * Test of getError method, of class Options.
   */
  @Test
  public void testGetError()
  {
    Options instance = new Options();
    String expResult = null;
    String result = instance.getError();
    assertEquals(result, expResult);
  }

  /**
   * Test of getNumArguments method, of class Options.
   */
  @Test
  public void testGetNumArguments()
  {
    Options instance = new Options();
    String[] args = new String[]
    {
      "0", "1", "2"
    };
    instance.arguments_ = args;
    int expResult = 3;
    int result = instance.getNumArguments();
    assertEquals(result, expResult);
  }

  /**
   * Test of getArgument method, of class Options.
   */
  @Test
  public void testGetArgument()
  {
    int num = 1;
    Options instance = new Options();
    String[] args = new String[]
    {
      "0", "1", "2"
    };
    instance.arguments_ = args;
    String expResult = "1";
    String result = instance.getArgument(num);
    assertEquals(result, expResult);
  }

  /**
   * Test of getArguments method, of class Options.
   */
  @Test
  public void testGetArguments()
  {
    Options instance = new Options();
    String[] args = new String[]
    {
      "0", "1", "2"
    };
    instance.arguments_ = args;
    List expResult = Arrays.asList(args);
    List result = instance.getArguments();
    assertEquals(result, expResult);
  }

  /**
   * Test of setOptionString method, of class Options.
   */
  @Test
  public void testSetOptionString()
  {
    String getopt_string = "";
    Options instance = new Options();
    instance.setOptionString(getopt_string);
  }

  /**
   * Test of addOption method, of class Options.
   */
  @Test
  public void testAddOption_String()
  {
//     Calls a method that is already unit tested
    String key = "";
    Options instance = new Options();
    instance.addOption(key);
  }

  /**
   * Test of addOption method, of class Options.
   */
  @Test
  public void testAddOption_String_boolean()
  {
    // Calls a method that is already unit tested
    // System.out.println("addOption");
    // String key = "";
    // boolean has_argument = false;
    // Options instance = new Options();
    // instance.addOption(key, has_argument);
    // TODO review the generated test code and remove the default call to fail.
  }

  /**
   * Test of addOption method, of class Options.
   */
  @Test
  public void testAddOption_3args()
  {
    String key = "k1";
    boolean has_argument = true;
    String default_value = "v1";
    Options instance = new Options();
    instance.addOption(key, has_argument, default_value);
    assertEquals(instance.options_.get(key).name, key);
    assertEquals(instance.options_.get(key).hasArgument, has_argument);
    assertEquals(instance.options_.get(key).specified, false);
    assertEquals(instance.options_.get(key).value, default_value);
  }

  /**
   * Test of parse method, of class Options.
   */
  @Test
  public void testParse_1()
  {
    String[] argv = new String[]
    {
      "a"
    };
    Options instance = new Options();
    boolean expResult = true;
    boolean result = instance.parse(argv);
    assertEquals(result, expResult);
  }

  /**
   * Test of parse method, of class Options.
   */
  @Test
  public void testParse_2()
  {
    String[] argv = new String[]
    {
      "-"
    };
    Options instance = new Options();
    boolean expResult = true;
    boolean result = instance.parse(argv);
    assertEquals(result, expResult);
  }

  /**
   * Test of isOptionSpecified method, of class Options. option is not specified
   */
  @Test
  public void testIsOptionSpecified1()
  {
    String key = "k1";
    Options instance = new Options();
    boolean expResult = false;
    boolean result = instance.isOptionSpecified(key);
    assertEquals(result, expResult);
  }

  /**
   * Test of isOptionSpecified method, of class Options. option is specified
   */
  @Test
  public void testIsOptionSpecified2()
  {
    Options instance = new Options();
    Options.OptionContent keyValue = instance.new OptionContent();
    keyValue.specified = false;

    TreeMap<String, Options.OptionContent> tm = new TreeMap<>();
    String key = "k1";
    tm.put(key, keyValue);
    instance.options_ = tm;

    boolean expResult = false;
    boolean result = instance.isOptionSpecified(key);
    assertEquals(result, expResult);
  }

  /**
   * Test of getOptionValue method, of class Options. options_ is not
   * initialized
   */
  @Test
  public void testGetOptionValue1()
  {
    Options instance = new Options();
    String key = "k1";
    String expResult = "";
    String result = instance.getOptionValue(key);
    assertEquals(result, expResult);
  }

  /**
   * Test of getOptionValue method, of class Options. options_ is initialized
   */
  @Test
  public void testGetOptionValue2()
  {
    Options instance = new Options();
    Options.OptionContent keyValue = instance.new OptionContent();
    keyValue.value = "test";

    TreeMap<String, Options.OptionContent> tm = new TreeMap<>();
    String key = "k1";
    tm.put(key, keyValue);
    instance.options_ = tm;

    String expResult = "test";
    String result = instance.getOptionValue(key);
    assertEquals(result, expResult);
  }

}
