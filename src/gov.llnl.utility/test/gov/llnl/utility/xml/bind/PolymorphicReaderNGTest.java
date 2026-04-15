/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.TestSupport;
import gov.llnl.utility.TestSupport.TestPolyReader;
import gov.llnl.utility.TestSupport.TestReader;
import gov.llnl.utility.TestSupport.TestReaderContext;
import gov.llnl.utility.xml.bind.readers.DoubleContents;
import gov.llnl.utility.xml.bind.readers.IntegerContents;
import gov.llnl.utility.xml.bind.readers.LongContents;
import gov.llnl.utility.io.ReaderException;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Test code for PolymorphicReader.
 */
public class PolymorphicReaderNGTest
{

  public PolymorphicReaderNGTest()
  {
  }

  /**
   * Test of getDeclaration method, of class PolymorphicReader.
   */
  @Test
  public void testGetDeclaration()
  {
    PolymorphicReader instance = new PolymorphicReaderImpl();
    Reader.Declaration result = instance.getDeclaration();
    assertEquals(result.pkg().getName(), "gov.llnl.utility.TestSupport$TestPackage");
    assertEquals(result.name(), "PolymorphicReaderImpl");
    assertEquals(result.order(), Reader.Order.CHOICE);
  }

  /**
   * Test of getReaders method, of class PolymorphicReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetReaders() throws Exception
  {
    PolymorphicReader instance = new PolymorphicReaderImpl();
    ObjectReader[] readers = instance.getReaders();
    assertNotNull(readers);
    assertTrue(readers.length == 1);
    assertEquals(readers[0].getClass(), LongContents.class);
  }

  /**
   * Test of group method, of class PolymorphicReader.
   */
  @Test
  public void testGroup()
  {
    ObjectReader[] result = PolymorphicReader.group(
            new PolymorphicReaderImpl(),
            TestReader.of(String.class),
            new TestPolyReader()
    );
    assertEquals(result.length, 3);
    assertEquals(result[0].getClass(), PolymorphicReaderImpl.class);
    assertEquals(result[1].getClass(), TestReader.class);
    assertEquals(result[2].getClass(), TestPolyReader.class);
  }

  /**
   * Test of of method, of class PolymorphicReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testOf() throws Exception
  {
    ObjectReader[] result = PolymorphicReader.of(Integer.class, Double.class, Long.class);
    assertTrue(result.length == 3);
    assertEquals(result[0].getClass(), IntegerContents.class);
    assertEquals(result[1].getClass(), DoubleContents.class);
    assertEquals(result[2].getClass(), LongContents.class);
    assertEquals(result[0].getObjectClass(), Integer.class);
    assertEquals(result[1].getObjectClass(), Double.class);
    assertEquals(result[2].getObjectClass(), Long.class);
  }

  /**
   * Test of start method, of class PolymorphicReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testStart() throws Exception
  {
    PolymorphicReader instance = new PolymorphicReaderImpl();
    assertNull(instance.start(null, null));
  }

  /**
   * Test of end method, of class PolymorphicReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testEnd() throws Exception
  {
    ReaderContext context = TestReaderContext.create();
    PolymorphicReader instance = new PolymorphicReaderImpl();
    assertNull(instance.end(context));

    IntPolyReaderImpl ipri = new IntPolyReaderImpl();
    Integer obj = 6;
    context.setState(obj);
    assertSame(ipri.end(context), obj);
  }

  /**
   * Test of getHandlers method, of class PolymorphicReader.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetHandlers() throws Exception
  {
    PolymorphicReader instance = new PolymorphicReaderImpl();
    assertNotNull(instance.getHandlers(null));
  }

  @Reader.Declaration(pkg = TestSupport.TestPackage.class,
          name = "PolymorphicReaderImpl", cls = Long.class)
  public class PolymorphicReaderImpl extends PolymorphicReader
  {
    @Override
    public ObjectReader[] getReaders() throws ReaderException
    {
      return new ObjectReader[]
      {
        new LongContents()
      };
    }
  }

  public class IntPolyReaderImpl<Integer> extends PolymorphicReader<Integer>
  {
    @Override
    public ObjectReader[] getReaders() throws ReaderException
    {
      return null;
    }
  }

}
