// --- file: gov/llnl/rtk/ml/IdentityActivationEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class IdentityActivationEncoding extends MessageEncoding<IdentityActivation>
{
  private static final ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<IdentityActivation, IdentityActivation> proto
            = MessageEncoding.newBuilder(null, "IdentityActivation", IdentityActivation::new);
    // No fields to add
    FIELDS = proto.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

}
