/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import gov.llnl.utility.Tokenizer.TokenException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for Tokenizer.
 */
public class TokenizerNGTest
{

  public TokenizerNGTest()
  {
  }

  /**
   * Test of create method, of class Tokenizer.
   */
  @Test
  public void testCreate()
  {
    Tokenizer expResult = new TokenizerImpl("Hello");
    Tokenizer result = Tokenizer.create("Hello");
    assertEquals(result.matcher("Hello, World!").next().group(),
            expResult.matcher("Hello, World!").next().group());

  }

  /**
   * Test of matcher method, of class Tokenizer.
   */
  @Test
  public void testTokenException()
  {
    try
    {
      throw new TokenException("Test 1 of TokenException");
    }
    catch (TokenException ex)
    {
      assertEquals(ex.getClass(), TokenException.class);
      assertEquals(ex.getMessage(), "Test 1 of TokenException");
    }

    try
    {
      throw new TokenException("Test 2 of TokenException", new Exception("Unit testing"));
    }
    catch (TokenException ex)
    {
      assertEquals(ex.getClass(), TokenException.class);
      assertEquals(ex.getMessage(), "Test 2 of TokenException");
      assertEquals(ex.getCause().getMessage(), "Unit testing");
    }
  }
}
