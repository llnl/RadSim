/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.utility.xml.bind;

import gov.llnl.utility.PackageResource;
import gov.llnl.utility.PropertyUtilities;
import gov.llnl.utility.UtilityPackage;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.xml.bind.SaxHandler.SAXExceptionProxy;
import gov.llnl.utility.io.MD5FilterInputStream;
import gov.llnl.utility.io.ReaderException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Base class for all ObjectReaders that can load a document. DocumentReader can
 * be included or imported.
 *
 * @param <Component> is the type of object produced by this document.
 */
@Internal
@SuppressWarnings("unchecked")
public class DocumentReaderImpl<Component> implements DocumentReader<Component>
{
  ReaderContextImpl readerContext;
  ObjectReader objectReader;
  Class<Component> cls;
  Map<String, Object> properties = new HashMap<>();
  ReaderContext.ExceptionHandler exceptionHandler;
  PropertyMap propertyHandler;
  private ElementContextImpl lastContext;

  public DocumentReaderImpl(ObjectReader reader)
  {
    if (reader == null)
      throw new NullPointerException();
    this.objectReader = reader;
    setSchema(reader);
  }

  final void setSchema(Reader reader)
  {
    if (reader == null)
      return;

    PackageResource pkg = reader.getPackage();
    Reader.Declaration attr = reader.getClass().getAnnotation(Reader.Declaration.class);
    if (!attr.xslt().equals(Reader.Declaration.NULL))
    {
      this.properties.put(XSLT_SOURCE, attr.xslt());
    }
    
    if (!attr.schema().equals(Reader.Declaration.NULL))
    {
      this.properties.put(SCHEMA_SOURCE, attr.schema());
    }
    else if (pkg != null)
    {
      this.properties.put(SCHEMA_SOURCE, pkg.getSchemaURI());
    }
  }

  public DocumentReaderImpl(Class<Component> cls)
  {
    if (cls == null)
      throw new NullPointerException();
    this.objectReader = null;
    this.cls = cls;
    try
    {
      setSchema(ObjectReader.create(cls));
    }
    catch (ReaderException ex)
    {
    }
  }

//<editor-fold desc="readerContext" defaultstate="collapsed">
  @Override
  public ReaderContext getContext()
  {
    return readerContext;
  }

  @Override
  public void clearContext()
  {
    readerContext = null;
  }

  @Override
  public ReaderContextImpl createContext() throws ReaderException
  {

    ReaderBuilderImpl.HandlerList hl = new ReaderBuilderImpl.HandlerList();
    ReaderHandler rootHandler;
    String namespaceURI = null;
    BiConsumer<Object, Object> complete = (p, v) ->
    {
      lastContext = readerContext.getChildContext();
    };
    if (cls == null)
    {
      // We have a defined reader
      namespaceURI = objectReader.getPackage().getNamespaceURI();
      rootHandler = new ReaderHandler(objectReader.getHandlerKey(),
              EnumSet.of(Reader.Option.NO_REFERENCE),
              complete, objectReader);
    }
    else
    {
      // We must handle any class that matched the specified type
      rootHandler = new ReaderHandler("##any",
              EnumSet.of(Reader.Option.NO_REFERENCE, Reader.Option.ANY),
              complete, AnyReader.of(cls));
    }
    hl.add(rootHandler);
    ElementContextImpl handlerContext = new ElementContextImpl(null, null, namespaceURI, "", null);
    handlerContext.handlerMap = ElementHandlerMapImpl.newInstance("#", hl);

    handlerContext.currentHandler = new ElementHandlerImpl(null, null, null, null);
    readerContext = new ReaderContextImpl();
    readerContext.documentReader = this;
    readerContext.currentContext = handlerContext;
    readerContext.setErrorHandler(exceptionHandler);
    readerContext.setPropertyHandler(n -> this.getProperty(n));
    return readerContext;
  }

//</editor-fold>
//<editor-fold desc="load" defaultstate="collapsed">
  @Override
  public Component loadStream(InputStream stream) throws ReaderException
  {
    InputSource inputSource = new InputSource();
    inputSource.setByteStream(stream);
    inputSource.setSystemId("stream");
    return (Component) loadSource(inputSource);
  }

  @Override
  public Component loadResource(String resourceName) throws ReaderException, IOException
  {
    URL url = getClass().getClassLoader().getResource(resourceName);
    if (url == null)
      throw new RuntimeException("Unable to locate resource " + resourceName);
    return loadURL(url);
  }

  @Override
  public synchronized Component loadFile(Path file) throws ReaderException, FileNotFoundException, IOException
  {
    ReaderContextImpl newContext = (ReaderContextImpl) createContext();
    newContext.setFile(file.toUri());
    try (InputStream fs = Files.newInputStream(file))
    {
      InputStream fs2 = fs;
      UtilityPackage.LOGGER.log(Level.FINE, "File {0}", file);
      if (file.getFileName().toString().endsWith(".gz"))
      {
        fs2 = new GZIPInputStream(fs);
      }
      InputSource inputSource = new InputSource();
      inputSource.setByteStream(new BufferedInputStream(fs2));
      inputSource.setSystemId(file.toString());
      return (Component) loadSourceInternal(inputSource);
    }
  }

  @Override
  public synchronized Component loadURL(URL url) throws IOException, ReaderException
  {
    try
    {
      ReaderContextImpl newContext = (ReaderContextImpl) createContext();
      newContext.setFile(url.toURI());
      URLConnection connection = url.openConnection();
      connection.setUseCaches(false);
      try (InputStream is = connection.getInputStream())
      {
        InputStream is2 = is;
        if (url.getFile().endsWith(".gz"))
          is2 = new GZIPInputStream(is);
        InputSource source = new InputSource();
        source.setByteStream(is2);
        source.setSystemId(url.toString());
        return (Component) loadSourceInternal(source);
      }
    }
    catch (URISyntaxException ex)
    {
      throw new ReaderException(ex);
    }
  }

//</editor-fold>
//<editor-fold desc="internal" defaultstate="collapsed">
  @Override
  public synchronized Component loadSource(InputSource inputSource) throws ReaderException
  {
    // Allow for pre-configuring of reader context
    if (this.readerContext == null)
      this.readerContext = (ReaderContextImpl) createContext();
    this.readerContext.setFile(URI.create(inputSource.getSystemId()));
    return loadSourceInternal(inputSource);
  }

  private synchronized Component loadSourceInternal(InputSource inputSource) throws ReaderException
  {
    boolean computeChecksum = false;
    Map<String, String> checksums = null;
    Class contentClass = this.cls;
    if (contentClass == null)
    {
      contentClass = this.objectReader.getObjectClass();
    }

    Class<Hook> hookClass;
    Hook hook = null;
    Hooks annotation = (Hooks) contentClass.getAnnotation(Hooks.class);
    if (annotation != null)
    {
      hookClass = (Class<Hook>) annotation.value();

      try
      {
        hook = hookClass.getConstructor().newInstance();
      }
      catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex)
      {
        // Reflection failed not use.
      }
    }

    // If hooks are set then call them before processing
    if (hook != null)
    {
      hook.startDocument(this);
    }

    // Handle case where MD5sum is request
    Object propComputeChecksum = properties.get(DocumentReader.COMPUTE_MD5SUM);
    if (propComputeChecksum != null && (Boolean) propComputeChecksum == true)
    {
      if (!properties.containsKey(DocumentReader.RESULT_MD5SUM))
        properties.put(DocumentReader.RESULT_MD5SUM, new TreeMap<>());
      computeChecksum = true;
      checksums = (Map<String, String>) properties.get(DocumentReader.RESULT_MD5SUM);
      inputSource.setByteStream(new MD5FilterInputStream(inputSource.getByteStream()));
    }

    try
    {
      // Only turn validate off if the system property is set to false
      boolean validate = PropertyUtilities.get("gov.llnl.utility.xml.validation", true);

      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setValidating(validate);
      spf.setNamespaceAware(true);
      SAXParser saxParser = spf.newSAXParser();

      // Path for validation
      SchemaManagerImpl schemaMgr = (SchemaManagerImpl) SchemaManager.getInstance();
      if (validate)
      {
        saxParser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");

        // Install the user requested schema
        String schema = this.getProperty(SCHEMA_SOURCE, String.class);
        if (schema != null)
        {
          saxParser.setProperty(SCHEMA_SOURCE, schema);
        }
      }
      else
      {
        if (this.getProperty(SCHEMA_SOURCE) != null)
        {
          try
          {
            URL i = schemaMgr.mangleURI(new URI((String) this.getProperty(SCHEMA_SOURCE)));
            schemaMgr.scanSchema(i);
          }
          catch (URISyntaxException ex)
          {
            throw new ReaderException("Unable to handle SCHEMA_SOURCE", ex);
          }
        }
      }

      XMLReader xmlReader = saxParser.getXMLReader();

      // Set up a SAX handler
      SaxHandler sh = new SaxHandler(readerContext);
      sh.setValidate(validate);
      xmlReader.setErrorHandler(sh);
      xmlReader.setEntityResolver(schemaMgr);

      // Apply an xslt if requested
      if (this.getProperty(XSLT_SOURCE) != null)
      {
        try
        {
          // Parse the xslt into a Path
          URI xsltPath = null;
          Object xslt = this.getProperty(XSLT_SOURCE);
          if (xslt instanceof String)
          {
            if (((String) xslt).matches("[A-z]+://.*"))
              xsltPath = new URI((String) xslt);
            else
              xsltPath = Paths.get((String) xslt).toUri();
          }
          else if (xslt instanceof Path)
          {
            xsltPath = ((Path) xslt).toUri();
          }
          else if (xslt instanceof URI)
          {
            xsltPath = ((URI) xslt);
          }
          else
          {
            throw new ReaderException("Unknown xslt type");
          }

          // Special target for "java" schema which needs to use the class loader
          if ("java".equals(xsltPath.getScheme()))
          {
            String s = ClassLoader.getSystemClassLoader()
                    .getResource(xsltPath.getRawPath().substring(1))
                    .toExternalForm();
            xsltPath = new URI(s);
          }

          // Use the reader to parse the input into a source for the xslt transform
          SAXSource source = new SAXSource(inputSource);

          // Create an XSLT transform using the xslt document
          StreamSource styleSource = new StreamSource(xsltPath.toURL().openStream());
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          Transformer transformer = transformerFactory.newTransformer(styleSource);

          // The result will be pushed to our SAX handler
          ByteArrayOutputStream bs = new ByteArrayOutputStream();
          StreamResult result = new StreamResult(bs);  // Create a SAXResult with our contentHandler
          transformer.transform(source, result);

          // Parse the remaining output
          ByteArrayInputStream bis = new ByteArrayInputStream(bs.toByteArray());
          inputSource.setByteStream(bis);
          xmlReader.setContentHandler(sh);
          xmlReader.parse(inputSource);
        }
        catch (TransformerException ex)
        {
          throw new ReaderException(ex);
        }
        catch (URISyntaxException ex)
        {
          throw new ReaderException("Bad uri");
        }
      }
      else
      {
        xmlReader.setContentHandler(sh);
        xmlReader.parse(inputSource);
      }

      // If this the final scope then we must resolve all deferred actions.
      if (readerContext.hasDeferred())
      {
        String deferred = readerContext.getDeferredElements();
        throw new ReaderException("unresolved deferred elements " + deferred);
      }

      // Compute the checksum if requested
      if (computeChecksum)
      {
        String ns = lastContext.namespaceURI;
        StringBuilder sb = new StringBuilder();
        if (ns != null)
        {
          sb.append(ns);
          sb.append("#");
        }
        sb.append(lastContext.localName);
        checksums.put(sb.toString(), ((MD5FilterInputStream) inputSource.getByteStream()).getChecksum());
      }

      // Resync the context with the end tag for the root object
      readerContext.currentContext = lastContext;
      if (hook != null)
      {
        hook.endDocument(this);
      }

      return (Component) lastContext.targetObject;
    }
    catch (SAXExceptionProxy ex)
    {
      Throwable cause = ex.exception;
      if (cause instanceof ReaderException)
        throw ((ReaderException) cause).addPathLocation(readerContext.getLocation());
      throw new ReaderException("Error in sax parsing " + cause.getMessage() + " <<", cause)
              .addPathLocation(readerContext.getLocation());
    }
    catch (SAXParseException ex)
    {
      throw new ReaderException("Error in sax parsing " + ex.getLineNumber(), ex)
              .addPathLocation(readerContext.getLocation());
    }
    catch (SAXException ex)
    {
      throw new ReaderException("Error in sax parsing", ex)
              .addPathLocation(readerContext.getLocation());
    }
    catch (IOException ex)
    {
      throw new ReaderException("IO Error in sax parsing", ex)
              .addPathLocation(readerContext.getLocation());
    }
    catch (ParserConfigurationException ex)
    {
      throw new RuntimeException(ex);
    }

  }

//</editor-fold>
  @Override
  public ObjectReader getObjectReader()
  {
    return this.objectReader;
  }

  @Override
  public void setErrorHandler(ReaderContext.ExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }

  @Override
  public void setProperty(String key, Object value)
  {
    this.properties.put(key, value);
  }

  static Object NOT_DEFINED = new Object();

  @Override
  public Object getProperty(String key)
  {
    // This is a hack to allow us to define null values in the
    // property map if needed.
    Object obj = this.properties.getOrDefault(key, NOT_DEFINED);
    if (obj != NOT_DEFINED)
      return obj;
    if (this.propertyHandler == null)
      return null;
    return this.propertyHandler.get(key);
  }

  @Override
  public Map<String, Object> getProperties()
  {
    return Collections.unmodifiableMap(properties);
  }

  @Override
  public void setPropertyHandler(PropertyMap handler)
  {
    this.propertyHandler = handler;
  }
}
