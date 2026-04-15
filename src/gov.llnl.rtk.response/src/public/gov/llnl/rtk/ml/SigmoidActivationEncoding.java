package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoField;

/**
 * Encoding for SigmoidActivation for model serialization.
 *
 * @author nelson85
 */
public class SigmoidActivationEncoding extends MessageEncoding<SigmoidActivation>
{
  private static final ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<SigmoidActivation, SigmoidActivation> proto =
        MessageEncoding.newBuilder(null, "SigmoidActivation", SigmoidActivation::new);
    // No fields required, as SigmoidActivation has no parameters.
    FIELDS = proto.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }
}