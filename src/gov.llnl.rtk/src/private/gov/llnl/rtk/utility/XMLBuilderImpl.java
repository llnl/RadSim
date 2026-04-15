// --- file: gov/llnl/rtk/utility/XMLBuilderImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.utility;

import gov.llnl.rtk.utility.XMLBuilder;
import gov.llnl.utility.annotation.Internal;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class XMLBuilderImpl implements XMLBuilder, XMLBuilder.ElementStart, XMLBuilder.ElementContents, XMLBuilder.DocumentContents
{
  XMLBuilderImpl parent;
  XMLBuilderImpl child;
  XMLStreamWriter writer;
  boolean hasContents = false;

  private XMLBuilderImpl()
  {
  }

  public XMLBuilderImpl(XMLStreamWriter writer)
  {
    this.writer = writer;
  }

  @Override
  public XMLBuilderImpl setDefaultNamespace(String prefix) throws XMLStreamException
  {
    if (writer == null)
      throw new XMLStreamException("Write to closed element");
    writer.setDefaultNamespace(prefix);
    return this;
  }

  @Override
  public XMLBuilderImpl setPrefix(String prefix, String namespace) throws XMLStreamException
  {
    assertOpen();

    writer.setDefaultNamespace(prefix);
    return this;
  }

  @Override
  public XMLBuilderImpl document() throws XMLStreamException
  {
    if (parent != null && child != null)
      throw new XMLStreamException("Document already started");
    return newContext();
  }

  @Override
  public XMLBuilderImpl element(String localName) throws XMLStreamException
  {
    assertOpen();
    writer.writeStartElement(localName);
    return newContext();
  }

  @Override
  public XMLBuilderImpl element(String namespaceUri, String localName) throws XMLStreamException
  {
    assertOpen();
    writer.writeStartElement(namespaceUri, localName);
    return newContext();
  }

  @Override
  public XMLBuilderImpl element(String localName, String prefix, String namespace) throws XMLStreamException
  {
    assertOpen();
    writer.writeStartElement(localName, prefix, namespace);
    return newContext();
  }

  private XMLBuilderImpl newContext() throws XMLStreamException
  {
    hasContents = true;

    // Create the new context
    XMLBuilderImpl context = new XMLBuilderImpl();
    context.writer = writer;
    context.parent = this;

    // This is the root builder
    if (parent == null)
    {
      if (child != null)
        throw new XMLStreamException("Second root");
      child = context;
      return context;
    }

    // First child nothing to do
    if (child == null)
    {
      child = context;
      return context;
    }

    // make sure we are still active
    if (writer == null || parent.child != this)
      throw new XMLStreamException("Write to a closed element");

    // Close the existing context
    child.end();
    child = context;
    return context;
  }

  @Override
  public XMLBuilderImpl end() throws XMLStreamException
  {
    if (child != null)
      child.end();
    writer.writeEndElement();
    writer = null;
    if (parent != null)
      parent.child = null;
    return parent;
  }

  @Override
  public XMLBuilderImpl attribute(String localName, String value) throws XMLStreamException
  {
    assertOpen();
    if (hasContents)
      throw new XMLStreamException("Attribute after contents");
    writer.writeAttribute(localName, value);
    return this;
  }

  @Override
  public XMLBuilderImpl characters(String contents) throws XMLStreamException
  {
    assertOpen();
    hasContents = true;
    writer.writeCharacters(contents);
    return this;
  }

  private void assertOpen() throws XMLStreamException
  {
    if (writer == null)
      throw new XMLStreamException("Write to closed element");
  }
}
