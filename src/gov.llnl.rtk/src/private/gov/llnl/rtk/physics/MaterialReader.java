// --- file: gov/llnl/rtk/physics/MaterialReader.java ---
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

@Reader.Declaration(pkg = RtkPackage.class, name = "material", cls = Material.class,
        order = Reader.Order.SEQUENCE, referenceable = true)
public class MaterialReader extends ObjectReader<Material>
{

  @Override
  public Material start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    context.setState(new MaterialBuilder(UnitSystem.SI));
    return null;
  }
  
  @Override
  public Material end(ReaderContext context) throws ReaderException
  {
    return state(context).build();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<Material> builder = this.newBuilder();
    builder.element("title")
            .callContext((s, p, v) -> state(s).label(v), String.class).optional();
    builder.element("description")
            .callContext((s, p, v) -> state(s).description(v), String.class).optional();
    builder.element("comment")
            .callContext((s, p, v) -> state(s).comment(v), String.class).optional();
    builder.element("age")
            .reader(new QuantityReader(PhysicalProperty.TIME))
            .callContext((s,p,v)->state(s).age(v)).optional();
    builder.element("density")
            .reader(new QuantityReader(PhysicalProperty.DENSITY))
            .callContext((s,p,v)->state(s).density(v));
    builder.element("component")
            .reader(new MaterialComponentReader())
            .callContext((s,p,v)->state(s).add(v)).unbounded().optional();
    return builder.getHandlers();
  }
  
  MaterialBuilder state(ReaderContext context)
  {
    return (MaterialBuilder) context.getState();
  }

}
