// --- file: gov/llnl/rtk/data/AttributesReader.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "attributes",
        cls = Map.class,
        document = false,
        order = Reader.Order.FREE)
public class AttributesReader extends ObjectReader<Map>
{
  
  @Override
  public Map start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new HashMap();
  }
  
  @Override
  public Reader.ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    Reader.ReaderBuilder<Map> builder = this.newBuilder();
    builder.any(Object.class)
            .callContext(AttributesReader::setAttribute).optional()
            .options(Reader.Option.ANY_ALL);
    return builder.getHandlers();
  }

  private static void setAttribute(ReaderContext context, Map map, Object object)
  {
    ReaderContext.ElementContext hc = context.getChildContext();
    String namespace = hc.getNamespaceURI();
    String localname = hc.getLocalName();
    if (namespace == null)
      namespace = "";
    map.put(String.format("%s#%s", namespace, localname), (Serializable) object);
  }
}
