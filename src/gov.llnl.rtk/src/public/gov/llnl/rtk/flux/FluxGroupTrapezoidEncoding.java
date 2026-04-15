// --- file: gov/llnl/rtk/flux/FluxGroupTrapezoidEncoding.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class FluxGroupTrapezoidEncoding extends MessageEncoding<FluxGroupTrapezoid>
{
  static final FluxGroupTrapezoidEncoding INSTANCE = new FluxGroupTrapezoidEncoding();
  static final ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "flux_group_bin",
            Temp::new,
            FluxGroupTrapezoidEncoding::convert);
    builder.field("e1", 1).type(Type.Double).as((o) -> o.energyLower, (o, v) -> o.e1 = v);
    builder.field("e2", 2).type(Type.Double).as((o) -> o.energyUpper, (o, v) -> o.e2 = v);
    builder.field("d1", 3).type(Type.Double).as((o) -> o.densityLower, (o, v) -> o.d1 = v);
    builder.field("d2", 4).type(Type.Double).as((o) -> o.densityUpper, (o, v) -> o.d2 = v);
    FIELDS = builder.toFields();
  }

  public static FluxGroupTrapezoidEncoding getInstance()
  {
    return INSTANCE;
  }

  private static FluxGroupTrapezoid convert(Temp o)
  {
    return new FluxGroupTrapezoid(o.e1, o.e2, o.d1, o.d2);
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

  private static class Temp
  {
    double e1, e2, d1, d2;
  }

}
