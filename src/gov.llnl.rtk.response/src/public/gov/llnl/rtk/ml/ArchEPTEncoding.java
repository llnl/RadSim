// --- file: gov/llnl/rtk/ml/ArchEPTEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;

public class ArchEPTEncoding extends MessageEncoding<ArchEPT>
{
  final static ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<ArchEPT, State> proto = MessageEncoding.newBuilder(
            null, "ArchEPT", ArchEPT.class, State.class
    );
    proto.field("encoder", 1)
            .encoding(SequentialModuleEncoding.INSTANCE)
            .as((o) -> o.encoder, (o, v) -> o.encoder = (SequentialModule) v);

    proto.field("predictor", 2)
            .encoding(SequentialModuleEncoding.INSTANCE)
            .as((o) -> o.predictor, (o, v) -> o.predictor = (SequentialModule) v);

    proto.field("translate", 3)
            .encoding(NeuralModuleEncoding.getInstance())
            .as((o) -> o.translate, (o, v) -> o.translate = (NeuralModule) v);


    FIELDS = proto.toFields();
  }

  @Override
  public ProtoField[] getFields() { return FIELDS; }

  @Override
  protected Object allocate(ProtoContext context, ProtoField[] fields) { return new State(); }

  @Override
  protected ArchEPT finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    State s = (State) o;
    // Do not validate sizes here; allow unknown (-1) sizes for flexibility.
    return new ArchEPT(s.encoder, s.predictor, s.translate);
  }

  private static class State
  {
    SequentialModule encoder;
    SequentialModule predictor;
    NeuralModule translate;
    // Optionally: ActivationFunction activation, outputActivation;
  }
}