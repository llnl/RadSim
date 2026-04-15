// --- file: gov/llnl/rtk/response/ElectronShellEncoding.java ---
/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.physics.ElectronShell;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.physics.QuantityEncoding;
import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoField;

import static gov.llnl.utility.proto.MessageEncoding.newBuilder;

public class ElectronShellEncoding extends MessageEncoding<ElectronShell>
{
  public static final ElectronShellEncoding INSTANCE = new ElectronShellEncoding();

  private static final ProtoField[] FIELDS;

  static
  {
    var proto = newBuilder(
            null,
            "ElectronShell",
            State::new,
            ElectronShellEncoding::newElectronShell);

    proto.field("count", 1).type(Type.Int32)
            .as(
                    (ElectronShell es) -> es.count,
                    (State o, Integer v) -> o.count = v);

    proto.field("energy", 2).encoding(QuantityEncoding.INSTANCE)
            .as((ElectronShell es) -> es.energy, (State o, Quantity v) -> o.energy = v)
            .optional(es -> es.energy != null && es.energy.isSpecified());

    FIELDS = proto.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

  static class State
  {
    int count = 0;
    Quantity energy = Quantity.UNSPECIFIED;
  }

  static ElectronShell newElectronShell(State state)
  {
    return new ElectronShell(state.count, state.energy);
  }
}
