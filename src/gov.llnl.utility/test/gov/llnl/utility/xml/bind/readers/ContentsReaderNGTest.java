/*
 * Copyright 2016, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind.readers;

import static gov.llnl.utility.TestSupport.newElement;
import gov.llnl.utility.UtilityPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.DomBuilder;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import gov.llnl.utility.xml.bind.readers.ContentsReader;
import java.lang.annotation.Annotation;
import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test code for ContentsReader.
 */
public class ContentsReaderNGTest
{

  /**
   * Test of start method, of class ContentsReader.
   */
  @Test
  public void testStart() throws Exception
  {
    assertNull(new TestContentsReaderImpl().start(null, null));
  }

  /**
   * Test of contents method, of class ContentsReader.
   */
  @Test
  public void testContents() throws ReaderException
  {
    assertNull(new TestContentsReaderImpl().contents(null, ""));
  }

  /**
   * Test of createSchemaType method, of class ContentsReader.
   */
  @Test
  public void testCreateSchemaType() throws Exception
  {
    new TestContentsReaderImpl().createSchemaType(null);
  }

  /**
   * Test of createSchemaElement method, of class ContentsReader.
   */
  @Test
  public void testCreateSchemaElement()
  {
    Element first = newElement("First");
    DomBuilder domBuilder = new DomBuilder(first);

    ContentsReader instance = new TestContentsReaderImpl();
    DomBuilder tooDomTooBuildrious = instance.createSchemaElement(
            null, "Toretto&O'Conner", domBuilder, false);

    Element second = tooDomTooBuildrious.toElement();

    assertEquals(tooDomTooBuildrious.toElement(), domBuilder.toElement().getFirstChild());

    assertEquals(second.getTagName(), "xs:element");
    assertEquals(second.getAttribute("name"), "Toretto&O'Conner");
    assertEquals(second.getAttribute("type"), "xs:testcontentsreader");
  }

  @Reader.Declaration(
          pkg = UtilityPackage.class,
          name = "TestContentsReader",
          referenceable = true,
          contents = Reader.Contents.TEXT)
  public class TestContentsReaderImpl extends ContentsReader
  {
    @Override
    public Object contents(ReaderContext context, String textContents) throws ReaderException
    {
      return null;
    }

    public Reader.TextContents getTextContents()
    {
      Reader.TextContents tc
              = this.getClass().getDeclaredAnnotation(Reader.TextContents.class);

      if (tc != null)
        return tc;

      return new Reader.TextContents()
      {
        @Override
        public String base()
        {
          return "xs:testcontentsreader";
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
          return Reader.TextContents.class;
        }
      };
    }
  }

}
