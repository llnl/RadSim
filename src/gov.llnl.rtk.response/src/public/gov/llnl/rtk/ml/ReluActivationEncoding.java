// --- file: gov/llnl/rtk/ml/ReluActivationEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class ReluActivationEncoding extends MessageEncoding<ReluActivation>
{
  private static final ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<ReluActivation, ReluActivation> proto
            = MessageEncoding.newBuilder(null, "ReluActivation", ReluActivation::new);
    // No fields to add
    FIELDS = proto.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

}
