// --- file: gov/llnl/rtk/flux/FluxGroupBinEncoding.java ---
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
public class FluxGroupBinEncoding extends MessageEncoding<FluxGroupBin>
{
  static final FluxGroupBinEncoding INSTANCE = new FluxGroupBinEncoding();
  static final ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "flux_group_bin", Temp::new, (o) -> new FluxGroupBin(o.e1, o.e2, o.counts, o.uncertainty));
    builder.field("e1", 1).type(Type.Double).as((o) -> o.energyLower, (o, v) -> o.e1 = v);
    builder.field("e2", 2).type(Type.Double).as((o) -> o.energyUpper, (o, v) -> o.e2 = v);
    builder.field("counts", 3).type(Type.Double).as((o) -> o.counts, (o, v) -> o.counts = v);
    builder.field("uncertainty", 4).type(Type.Double).as((o) -> o.uncertainty, (o, v) -> o.uncertainty = v);
    FIELDS = builder.toFields();
  }

  public static FluxGroupBinEncoding getInstance()
  {
    return INSTANCE;
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

  private static class Temp
  {
    double e1, e2, counts, uncertainty;
  }

}
