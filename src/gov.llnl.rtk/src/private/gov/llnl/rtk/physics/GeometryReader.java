// --- file: gov/llnl/rtk/physics/GeometryReader.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
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

@Reader.Declaration(pkg = RtkPackage.class, name = "geometry", cls = Geometry.class,
        order = Reader.Order.OPTIONS, referenceable = true)
@Reader.Attribute(name = "type", type = String.class)
public class GeometryReader extends ObjectReader<Geometry>
{
  @Override
  public Geometry start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    String typeString = attributes.getValue("type");
    Geometry.Type type = Geometry.Type.valueOf(typeString.toUpperCase());
    State state = new State();
    state.type = type;
    context.setState(state);
    return null;
  }
  
  @Override
  public Geometry end(ReaderContext context) throws ReaderException
  {
    State s = (State) context.getState();
    return Geometry.of(s.type, s.extent1, s.extent2);
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<Geometry> builder = this.newBuilder();
    builder.element("extent1").reader(new QuantityReader(PhysicalProperty.LENGTH))
            .callContext((c,p,v)->((State)c.getState()).extent1 = v);
    builder.element("extent2").reader(new QuantityReader(PhysicalProperty.LENGTH))
            .callContext((c,p,v)->((State)c.getState()).extent2 = v);
    return builder.getHandlers();
  }
  
  private static class State
  {
    Geometry.Type type;
    Quantity extent1;
    Quantity extent2;
  }

}
