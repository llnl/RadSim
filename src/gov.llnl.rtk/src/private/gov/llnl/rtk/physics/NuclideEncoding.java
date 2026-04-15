// --- file: gov/llnl/rtk/physics/NuclideEncoding.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.proto.MessageEncoding;
import static gov.llnl.utility.proto.MessageEncoding.newBuilder;
import gov.llnl.utility.proto.ProtoEncoding;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
class NuclideEncoding extends MessageEncoding<Nuclide>
{
  final static NuclideEncoding INSTANCE = new NuclideEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "nuclide", NuclideEncoding.State::new, NuclideEncoding::convert);
    builder.field("z", 1).type(ProtoEncoding.Type.Int32)
            .as((o) -> o.getAtomicNumber(), (o, v) -> o.z = v);
    builder.field("m", 2).type(ProtoEncoding.Type.Int32)
            .as((o) -> o.getMassNumber(), (o, v) -> o.m = v);
    builder.field("i", 3).type(ProtoEncoding.Type.Int32)
            .as((o) -> o.getIsomerNumber(), (o, v) -> o.i = v);
    FIELDS = builder.toFields();
  }
  
  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

  static private Nuclide convert(State state)
  {
    return Nuclides.get(state.z, state.m, state.i);
  }

  private static class State
  {
    int z, m, i;
  }
}
