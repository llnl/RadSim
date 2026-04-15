/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import gov.llnl.utility.StreamTokenMatcher.TokenIterator;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for StreamTokenMatcher.
 */
public class StreamTokenMatcherNGTest
{

  /**
   * Test of fetchLine method, of class StreamTokenMatcher.
   */
  @Test
  public void testFetchLine() throws Exception
  {
    StreamTokenMatcher instance = getInstance();
    instance.fetchLine();
    assertEquals(instance.target, "test this  token matcher\n");
    assertEquals(instance.start, 0);
    assertEquals(instance.end, 25);
    assertEquals(instance.offset, 25);
    assertEquals(instance.tokens[0].index, 0);
    assertEquals(instance.tokens[1].index, 1);
    assertEquals(instance.tokens[2].index, 2);
  }

  /**
   * Test of next method, of class StreamTokenMatcher.
   */
  @Test(expectedExceptions =
  {
    Tokenizer.TokenException.class
  })
  public void testNext()
  {
    StreamTokenMatcher instance = getInstance();
    assertEquals(instance.next().group(), "test");
    assertEquals(instance.next().group(), " ");
    assertEquals(instance.next().group(), "this");
    assertEquals(instance.next().group(), "  ");
    assertEquals(instance.next().group(), "token");
    assertEquals(instance.next().group(), " ");
    assertEquals(instance.next().group(), "matcher\n");
    assertEquals(instance.next().group(), "test");

    TokenDef[] tokens = new TokenDef[]
    {
    };
    instance = new StreamTokenMatcher(
            new ByteArrayInputStream("hello".getBytes()), tokens);
    assertEquals(instance.next().group(), null);
  }

  /**
   * Test of iterator method, of class StreamTokenMatcher.
   */
  @Test
  public void testIterator()
  {
    StreamTokenMatcher instance = getInstance();
    Iterator result = instance.iterator();
    assertNotNull(result);
  }

  private StreamTokenMatcher getInstance()
  {
    TokenDef[] tokens = new TokenDef[]
    {
      new TokenDef("^t[^ ]*"),
      new TokenDef("^[^t ][^ ]*"),
      new TokenDef("^[\n ]+"),
    };
    return new StreamTokenMatcher(
            new ByteArrayInputStream("test this  token matcher\ntest".getBytes()), tokens);
  }

  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testRemove()
  {
    StreamTokenMatcher instance = getInstance();
    TokenIterator iter = instance.new TokenIterator();
    iter.remove();
  }

}
