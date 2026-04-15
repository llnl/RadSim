// --- file: gov/llnl/rtk/physics/CompositionReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Internal

@Reader.Declaration(pkg = RtkPackage.class, name = "composition", cls = Composition.class,
        order = Reader.Order.FREE, referenceable = true)
@Reader.Attribute(name = "name", type = String.class)
public class CompositionReader extends ObjectReader<Composition>
{

  @Override
  public Composition start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    String name = attributes.getValue("name");
    Composition composition = new CompositionImpl(name);
    return composition;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<Composition> builder = this.newBuilder();
    builder.element("entry").call(Composition::add, MaterialComponent.class).optional();
    return builder.getHandlers();
  }

}
