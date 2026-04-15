// --- file: gov/llnl/rtk/utility/XMLBuilder.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.utility;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Wrapper for XMLStreamWriter to create compact and correct documents.
 *
 * @author nelson85
 */
public interface XMLBuilder
{
  public static XMLBuilder create(XMLStreamWriter writer)
  {
    return new XMLBuilderImpl(writer);
  }

  XMLBuilder setDefaultNamespace(String prefix) throws XMLStreamException;

  XMLBuilder setPrefix(String prefix, String namespace) throws XMLStreamException;

  DocumentContents document() throws XMLStreamException;

  public interface ElementStart
  {
    XMLBuilder setDefaultNamespace(String prefix) throws XMLStreamException;

    XMLBuilder setPrefix(String prefix, String namespace) throws XMLStreamException;

    ElementContents element(String localName) throws XMLStreamException;

    ElementContents element(String prefix, String localName) throws XMLStreamException;

    ElementContents element(String localName, String prefix, String namespace) throws XMLStreamException;
  }

  public interface DocumentContents extends ElementStart
  {
  }

  public interface ElementContents extends ElementStart
  {
    ElementContents attribute(String key, String value) throws XMLStreamException;

    ElementContents characters(String contents) throws XMLStreamException;

    ElementContents end() throws XMLStreamException;
  }
}
