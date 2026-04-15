// --- file: gov/llnl/rtk/data/EnergyScaleReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 * Polymorphic contents.
 *
 * @author nelson85
 */
@Internal

@Reader.Declaration(pkg = RtkPackage.class, name = "energyScale",
        cls = EnergyScale.class,
        document = true,
        order = Reader.Order.CHOICE, referenceable = true)
@Reader.Attribute(name = "use_db", type = String.class)
public class EnergyScaleReader extends ObjectReader<EnergyScale>
{
  static class State{
  boolean loaded = false;
  boolean use_db;
  EnergyScale obj;
  }
  
  static State getState(ReaderContext context)
  {
    return (State) context.getState();
  }

  @Override
  public EnergyScale start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    State state = new State();
    context.setState(state);
 
    // Needed for old RNAK identify.  Remove once attribute is moved or reworked.
    String value = attributes.getValue("use_db");
    if (value != null)
    {
      state.use_db = Boolean.parseBoolean(value);
    }

    return null;
  }

  @Override
  public EnergyScale end(ReaderContext context) throws ReaderException
  {
    State state = getState(context);
    if (state.use_db == true)
    {
      EnergyScale obj = new EnergyBinsImpl(new double[0]);
      obj.setAttribute("use_database", state.use_db);
      return state.obj;
    }
    return state.obj;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<EnergyScale> builder = this.newBuilder();
    builder.element("pairs")
            .reader(new EnergyPairsScaleReader())
            .callContext((c,o,v)->getState(c).obj = v);
    builder.element("sqrt")
            .reader(new BinsSqrt())
            .callContext((c,o,v)->getState(c).obj = v);
    builder.element("linear")
            .reader(new BinsLinear())
            .callContext((c,o,v)->getState(c).obj = v);
    builder.element("values")
            .callContext((c,o,v)->getState(c).obj = EnergyScaleFactory.newScale(v), double[].class);
    return builder.getHandlers();
  }

  @Reader.Declaration(pkg = RtkPackage.class, name = "linear",
          cls = EnergyScale.class,
          order = Reader.Order.ALL)
  @Reader.Attribute(name = "begin", type = Double.class, required=true)
  @Reader.Attribute(name = "end", type = Double.class, required=true)
  @Reader.Attribute(name = "steps", type = Integer.class, required=true)
  static public class BinsLinear extends ObjectReader<EnergyScale>
  {
    @Override
    public EnergyScale start(ReaderContext context, Attributes attributes) throws ReaderException
    {
      double begin = getAttribute(attributes, "begin", Double.class);
      double end = getAttribute(attributes, "end", Double.class);
      int steps = getAttribute(attributes, "steps", Integer.class);
      return EnergyScaleFactory.newLinearScale(begin, end, steps);
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      return null;
    }
  }

  @Reader.Declaration(pkg = RtkPackage.class, name = "sqrt",
          cls = EnergyScale.class,
          order = Reader.Order.ALL)
  @Reader.Attribute(name = "begin", type = Double.class)
  @Reader.Attribute(name = "end", type = Double.class)
  @Reader.Attribute(name = "steps", type = Integer.class)
  static public class BinsSqrt extends ObjectReader<EnergyScale>
  {
    @Override
    public EnergyScale start(ReaderContext context, Attributes attributes) throws ReaderException
    {
      double begin = Double.parseDouble(attributes.getValue("begin").trim());
      double end = Double.parseDouble(attributes.getValue("end").trim());
      int steps = Integer.parseInt(attributes.getValue("steps").trim());
      return EnergyScaleFactory.newSqrtScale(begin, end, steps);
    }

    @Override
    public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
    {
      return null;
    }

  }
}
