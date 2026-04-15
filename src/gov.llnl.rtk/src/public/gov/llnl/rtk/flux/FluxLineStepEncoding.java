// --- file: gov/llnl/rtk/flux/FluxLineStepEncoding.java ---
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
public class FluxLineStepEncoding extends MessageEncoding<FluxLineStep>
{
  static final FluxLineStepEncoding INSTANCE = new FluxLineStepEncoding();
  static final ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "flux_line_step", Temp::new, (o) -> new FluxLineStep(o.energy, o.intensity, o.step));
    builder.field("energy", 1).type(Type.Double).as((o) -> o.energy, (o, v) -> o.energy = v);
    builder.field("intensity", 2).type(Type.Double).as((o) -> o.intensity, (o, v) -> o.intensity = v);
    builder.field("step", 3).type(Type.Double).as((o) -> o.step, (o, v) -> o.step = v);
    FIELDS = builder.toFields();
  }

  public static FluxLineStepEncoding getInstance()
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
    double energy, intensity, step;
  }

}
