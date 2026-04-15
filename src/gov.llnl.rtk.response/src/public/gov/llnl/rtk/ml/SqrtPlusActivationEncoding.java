package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;

/**
 * Encoding for SqrtPlusActivation for model serialization.
 *
 * @author nelson85
 */
public class SqrtPlusActivationEncoding extends MessageEncoding<SqrtPlusActivation>
{
  private static final ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<SqrtPlusActivation, State> proto =
        MessageEncoding.newBuilder(null, "SqrtPlusActivation", SqrtPlusActivation.class, State.class);
    proto.field("k", 1).type(Type.Double)
        .as(o -> o.k, (o, v) -> o.k = v);
    FIELDS = proto.toFields();
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

  @Override
  protected Object allocate(ProtoContext context, ProtoField[] fields)
  {
    return new State();
  }

  @Override
  protected SqrtPlusActivation finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    State s = (State) o;
    return new SqrtPlusActivation(s.k);
  }

  private static class State
  {
    double k;
  }
}