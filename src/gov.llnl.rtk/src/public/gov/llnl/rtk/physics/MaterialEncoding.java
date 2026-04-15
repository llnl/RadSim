// --- file: gov/llnl/rtk/physics/MaterialEncoding.java ---
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
public class MaterialEncoding extends MessageEncoding<Material>
{
  public final static MaterialEncoding INSTANCE = new MaterialEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "Material", MaterialImpl::new, MaterialEncoding::convert);
    builder.field("name", 1).type(Type.String)
            .as((o) -> o.getName(), (o, v) -> o.label = v);
    builder.field("description", 2).type(Type.String)
            .as((o) -> o.getDescription(), (o, v) -> o.description = v);
    builder.field("comment", 3).type(Type.String)
            .as((o) -> o.getComment(), (o, v) -> o.comment = v);
    builder.field("density", 4).encoding(QuantityEncoding.INSTANCE)
            .as((o) -> o.getDensity(), (o, v) -> o.density = v);
    builder.field("age", 5).encoding(QuantityEncoding.INSTANCE)
            .as((o) -> o.getAge(), (o, v) -> o.age = v);
    builder.field("components", 6).list(ComponentEncoding.INSTANCE)
            .as((o) -> o, (o, v) -> o.entries.addAll(v));
    FIELDS = builder.toFields();
  }
  
  private static Material convert(MaterialImpl material)
  {
    return material;
  }

  public static MaterialEncoding getInstance()
  {
    return INSTANCE;
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

}
