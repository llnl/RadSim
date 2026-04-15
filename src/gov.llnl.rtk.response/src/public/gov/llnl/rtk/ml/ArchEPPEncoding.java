// --- file: gov/llnl/rtk/ml/ArchEPPEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;

public class ArchEPPEncoding extends MessageEncoding<ArchEPP>
{
  final static ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<ArchEPP, State> proto = MessageEncoding.newBuilder(
            null, "ArchEPP", ArchEPP.class, State.class
    );
    // Updated: Use SequentialModuleEncoding for encoder and predictor chains
    proto.field("encoders", 1)
            .encoding(SequentialModuleEncoding.INSTANCE)
            .as((o) -> o.encoder, (o, v) -> o.encoders = (SequentialModule) v);

    proto.field("predictors", 2)
            .encoding(SequentialModuleEncoding.INSTANCE)
            .as((o) -> o.predictor, (o, v) -> o.predictors = (SequentialModule) v);

    // Reducers: array of modules, use list encoding
    proto.field("reducers", 3)
            .list(NeuralModuleEncoding.getInstance())
            .as((o) -> java.util.Arrays.asList(o.reducers),
                    (o, v) -> o.reducers = v.toArray(NeuralModule[]::new));

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
  protected ArchEPP finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    State s = (State) o;
    return new ArchEPP(s.encoders, s.predictors, s.reducers);
  }

  private static class State
  {
    SequentialModule encoders;
    SequentialModule predictors;
    NeuralModule[] reducers;
  }
}
