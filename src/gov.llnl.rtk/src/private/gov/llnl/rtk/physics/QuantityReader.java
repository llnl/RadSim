// --- file: gov/llnl/rtk/physics/QuantityReader.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "quantity",
        contents = Reader.Contents.TEXT, cls = Quantity.class)
@Reader.Attribute(name = "units", type = String.class)
@Reader.Attribute(name = "unc", type = Double.class, required = false)
public class QuantityReader extends ObjectReader<Quantity>
{
  public final String target;
  private final PhysicalProperty requires;

  public QuantityReader()
  {
    target = null;
    requires = null;
  }

  public QuantityReader(String target)
  {
    this.target = target;
    requires = null;
  }

  public QuantityReader(PhysicalProperty require)
  {
    this.target = null;
    this.requires = require;
  }

  @Override
  public Quantity start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    State s = new State();
    s.units = attributes.getValue("units");
    if (s.units == null)
      s.units = target;
    String unc = attributes.getValue("unc");
    if (unc != null)
      s.uncertainty = Double.parseDouble(unc.strip());
    context.setState(s);
    return null;
  }

  @Override
  public Quantity contents(ReaderContext context, String textContents) throws ReaderException
  {
    State s = (State) context.getState();
    Quantity out = Quantity.of(Double.parseDouble(textContents.strip()), Units.get(s.units), s.uncertainty, true);
    out.require(requires);
    return out;
  }

  static class State
  {
    String units;
    double uncertainty;
  }
}
