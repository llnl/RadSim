// --- file: gov/llnl/rtk/flux/FluxListReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "fluxList",
        cls = FluxList.class,
        referenceable = true, document = true,
        order = Reader.Order.FREE)
public class FluxListReader extends ObjectReader<Collection>
{
  final Class<? extends Collection> listClass;
  final Class<? extends Flux> objectClass;
  final Collection list;

  public FluxListReader()
  {
    listClass = FluxList.class;
    objectClass = FluxBinned.class;
    list = null;
  }

  public FluxListReader(Collection<? extends Flux> collection)
  {
    this.list = collection;
    objectClass = FluxBinned.class;
    listClass = collection.getClass();
  }

  public FluxListReader(Class<? extends List> listClass)
  {

    this.listClass = listClass;
    objectClass = FluxBinned.class;
    list = null;
  }

  @Override
  public Collection start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    if (list != null)
      return list;
    try
    {
      return listClass.getDeclaredConstructor().newInstance();
    }
    catch (IllegalArgumentException
            | SecurityException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException ex)
    {
      throw new ReaderException(ex);
    }
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<Collection> builder = this.newBuilder();
    builder.element("flux").reader(new FluxReader())
            .call((p, v) -> p.add(v));
    return builder.getHandlers();
  }
}
