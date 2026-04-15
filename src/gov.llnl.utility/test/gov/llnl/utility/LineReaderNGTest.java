/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility;

import gov.llnl.utility.LineReader.LineIterator;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for LineReader.
 */
public class LineReaderNGTest
{

  /**
   * Test of readLine method, of class LineReader.
   */
  @Test
  public void testReadLine() throws Exception
  {
    LineReader instance = new LineReader(new StringReader("test\nlines\n"));
    assertEquals(instance.readLine(), "test\n");
    assertEquals(instance.readLine(), "lines\n");
    instance = new LineReader(new StringReader("hello\r\n"));
    assertEquals(instance.readLine(), "hello\r\n");
    assertEquals(instance.readLine(), "");
    instance = new LineReader(new StringReader("hello\rw"));
    assertEquals(instance.readLine(), "hello\r");
    assertEquals(instance.readLine(), "w");
  }

  /**
   * Test of close method, of class LineReader.
   */
  @Test
  public void testClose() throws Exception
  {
    StringReader base = new StringReader("test\nlines\n");
    LineReader instance = new LineReader(base);
    assertTrue(base.ready());
    instance.close();
    try
    {
      base.ready();
      fail("File is not closed");
    }
    catch (IOException ex)
    {
    }
  }

  /**
   * Test of iterator method, of class LineReader.
   */
  @Test
  public void testIterator()
  {
    StringReader base = new StringReader("test\nlines\n");
    LineReader instance = new LineReader(base);
    Iterator<String> result = instance.iterator();
    List<String> contents = Iterators.stream(result)
            .collect(Collectors.toList());
    // The extra empty string seems like an error.   This should 
    // be reviewed in the project that is using this code.
    assertEquals(contents, Arrays.asList("test\n", "lines\n", ""));
  }

  @Test(expectedExceptions =
  {
    UnsupportedOperationException.class
  })
  public void testRemove()
  {
    StringReader base = new StringReader("test\nlines\n");
    LineReader instance = new LineReader(base);
    LineIterator iter = instance.new LineIterator();
    iter.remove();
  }
}
