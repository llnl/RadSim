// --- file: gov/llnl/rtk/physics/QuantityEncoding.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.proto.MessageEncoding;
import static gov.llnl.utility.proto.MessageEncoding.newBuilder;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class QuantityEncoding extends MessageEncoding<Quantity>
{
  public final static QuantityEncoding INSTANCE = new QuantityEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "quantity", QuantityEncoding.State::new, QuantityEncoding::convert);
    builder.field("value", 1).type(Type.Double)
            .as((o) -> o.getValue(), (o, v) -> o.value = v);
    builder.field("units", 2).type(Type.String)
            .as((o) -> o.getUnits().getSymbol(), (o, v) -> o.units = v);
    builder.field("units", 3).type(Type.Double)
            .as((o) -> o.getUncertainty(), (o, v) -> o.uncertainty = v);
    builder.field("units", 4).type(Type.Bool)
            .as((o) -> o.isSpecified(), (o, v) -> o.specified = v);
    FIELDS = builder.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

  static private Quantity convert(State state)
  {
    return Quantity.of(state.value, Units.get(state.units), state.uncertainty, state.specified);
  }

  private static class State
  {
    double value;
    String units;
    double uncertainty;
    boolean specified;
  }
}
