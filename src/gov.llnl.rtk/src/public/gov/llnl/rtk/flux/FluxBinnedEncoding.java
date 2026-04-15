// --- file: gov/llnl/rtk/flux/FluxBinnedEncoding.java ---
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
public class FluxBinnedEncoding extends MessageEncoding<FluxBinned>
{
  final static FluxBinnedEncoding INSTANCE = new FluxBinnedEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "flux_binned", FluxBinned::new);
    builder.field("photon_lines", 1).list(FluxLineStepEncoding.getInstance())
            .as((o) -> o.photonLines, (o, v) -> v.stream().forEach(o::addPhotonLine));
    builder.field("photon_groups", 2).list(FluxGroupBinEncoding.getInstance())
            .as((o) -> o.photonGroups, (o, v) -> v.stream().forEach(o::addPhotonGroup));
    builder.field("neutron_groups", 3).list(FluxGroupBinEncoding.getInstance())
            .as((o) -> o.neutronGroups, (o, v) -> v.stream().forEach(o::addNeutronGroup));
    FIELDS = builder.toFields();
  }

  public static FluxBinnedEncoding getInstance()
  {
    return INSTANCE;
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }
}
