/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import java.util.Iterator;
import java.util.NoSuchElementException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for StringTokenMatcher.
 */
public class StringTokenMatcherNGTest
{

  public StringTokenMatcherNGTest()
  {
  }

  /**
   * Test of next method, of class StringTokenMatcher.
   */
  @Test(expectedExceptions =
  {
    Tokenizer.TokenException.class
  })
  public void testNext()
  {
    String str = "Hello, World!";
    TokenDef[] tokenDefArray = new TokenDef[]
    {
      new TokenDef("^\\bHello\\b"),
      new TokenDef("^\\bWorld\\b"),
      new TokenDef("^[ ?!,]+")
    };
    StringTokenMatcher stringTokenMatcher = new StringTokenMatcher(str, tokenDefArray);

    assertEquals(stringTokenMatcher.next().group(), "Hello");
    assertEquals(stringTokenMatcher.next().group(), ", ");
    assertEquals(stringTokenMatcher.next().group(), "World");
    assertEquals(stringTokenMatcher.next().group(), "!");
    assertNull(stringTokenMatcher.next());

    // Test Tokenizer.TokenException
    stringTokenMatcher = new StringTokenMatcher("no match", tokenDefArray);
    stringTokenMatcher.next();
  }

  /**
   * Test of iterator method, of class StringTokenMatcher.
   */
  @Test
  public void testIterator()
  {
    String str = "Hello, World!";
    TokenDef[] tokenDefArray = new TokenDef[]
    {
      new TokenDef(".*?\\bHello\\b.*?"),
      new TokenDef(".*?\\bWorld\\b.*?"),
      new TokenDef(".*?!$")
    };
    StringTokenMatcher stringTokenMatcher = new StringTokenMatcher(str, tokenDefArray);

    Iterator<Tokenizer.Token> iterator = stringTokenMatcher.iterator();
    // Hello
    assertTrue(iterator.hasNext());
    Tokenizer.Token tokenImpl = iterator.next();
    assertNotNull(tokenImpl);
    assertEquals(tokenImpl.group(), "Hello");
    // World
    assertTrue(iterator.hasNext());
    tokenImpl = iterator.next();
    assertNotNull(tokenImpl);
    assertEquals(tokenImpl.group(), ", World");
    // !
    assertTrue(iterator.hasNext());
    tokenImpl = iterator.next();
    assertNotNull(tokenImpl);
    assertEquals(tokenImpl.group(), "!");

    assertEquals(iterator.hasNext(), false);

    try
    {
      iterator.next();
      fail("Expected NoSuchElementException");
    }
    catch (NoSuchElementException ex)
    {
    }

    // Test UnsupportedOperationException
    try
    {
      iterator.remove();
      fail("Expected UnsupportedOperationException");
    }
    catch (UnsupportedOperationException ex)
    {
    }
  }

}
