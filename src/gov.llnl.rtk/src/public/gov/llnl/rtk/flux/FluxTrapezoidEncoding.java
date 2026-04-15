// --- file: gov/llnl/rtk/flux/FluxTrapezoidEncoding.java ---
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
public class FluxTrapezoidEncoding extends MessageEncoding<FluxTrapezoid>
{
  final static FluxTrapezoidEncoding INSTANCE = new FluxTrapezoidEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "flux_trapezoid", FluxTrapezoid::new);
    builder.field("photon_lines", 1).list(FluxLineStepEncoding.getInstance())
            .as((o) -> o.getPhotonLines(), (o, v) -> v.stream().forEach(o::addPhotonLine));
    builder.field("photon_groups", 2).list(FluxGroupTrapezoidEncoding.getInstance())
            .as((o) -> o.getPhotonGroups(), (o, v) -> v.stream().forEach(o::addPhotonGroup));
    builder.field("neutron_groups", 3).list(FluxGroupTrapezoidEncoding.getInstance())
            .as((o) -> o.getNeutronGroups(), (o, v) -> v.stream().forEach(o::addNeutronGroup));
    FIELDS = builder.toFields();
  }

  public static FluxTrapezoidEncoding getInstance()
  {
    return INSTANCE;
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }
}
