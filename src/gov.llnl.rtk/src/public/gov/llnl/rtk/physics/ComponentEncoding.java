// --- file: gov/llnl/rtk/physics/ComponentEncoding.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class ComponentEncoding extends MessageEncoding<MaterialComponent>
{
  final static ComponentEncoding INSTANCE = new ComponentEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "component", MaterialComponentImpl::new);
    builder.field("nuclide", 1).encoding(NuclideEncoding.INSTANCE)
            .as((o) -> o.getNuclide(), (o, v) -> o.nuclide = v);
    builder.field("dose_fraction", 2).type(Type.Double).as((o) -> o.getDoseFraction(), (o, v) -> o.doseFraction = v);
    builder.field("atom_fraction", 3).type(Type.Double).as((o) -> o.getAtomFraction(), (o, v) -> o.atomFraction = v);
    builder.field("mass_fraction", 4).type(Type.Double).as((o) -> o.getMassFraction(), (o, v) -> o.massFraction = v);
    builder.field("activity", 5).encoding(QuantityEncoding.INSTANCE)
            .as((o) -> o.getActivity(), (o, v) -> o.activity = v);
    FIELDS = builder.toFields();
  }

  public static ComponentEncoding getInstance()
  {
    return INSTANCE;
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

}
