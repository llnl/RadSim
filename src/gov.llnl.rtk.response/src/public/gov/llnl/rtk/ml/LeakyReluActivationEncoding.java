// --- file: gov/llnl/rtk/ml/LeakyReluActivationEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class LeakyReluActivationEncoding extends MessageEncoding<LeakyReluActivation>
{
  private static final ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<LeakyReluActivation, State> proto = MessageEncoding.newBuilder(null, "LeakyReluActivation", LeakyReluActivation.class, State.class);
    proto.field("slope", 1).type(Type.Double).as(o -> o.slope, (o, v) -> o.slope = v);
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
  protected LeakyReluActivation finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    State s = (State) o;
    return new LeakyReluActivation(s.slope);
  }

  private static class State
  {
    double slope;
  }

}
