// --- file: gov/llnl/rtk/response/GeigerMuellerResponseFunctionEncoding.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class GeigerMuellerResponseFunctionEncoding extends MessageEncoding<GeigerMuellerResponseFunction>
{
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "GeigerMuellerResponseFunction",
            GeigerMuellerResponseFunction.Builder::new,
            (o) -> o.create());
    builder.field("vendor", 1).string().as(o -> o.vendor, (o, v) -> o.vendor(v));
    builder.field("model", 2).string().as(o -> o.model, (o, v) -> o.model(v));
    builder.field("efficiency_energies", 3)
            .type(Type.NetworkDoubles)
            .as(o -> o.efficiency.getX(), (o, v) -> o.efficiencyX = v);
    builder.field("efficiency_values", 4)
            .type(Type.NetworkDoubles)
            .as(o -> o.efficiency.getY(), (o, v) -> o.efficiencyY = v);
    builder.field("dose_factor", 5)
            .type(Type.Double).as(o -> o.getDoseFactor(), (o, v) -> o.doseFactor(v));
    builder.field("end_factor", 6)
            .type(Type.Double).as(o -> o.getEndFactor(), (o, v) -> o.endFactor(v));
    builder.field("side_factor", 7)
            .type(Type.Double).as(o -> o.getSideFactor(), (o, v) -> o.sideFactor(v));

    FIELDS = builder.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

}
