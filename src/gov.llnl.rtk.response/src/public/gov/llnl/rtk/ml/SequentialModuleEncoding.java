// --- file: gov/llnl/rtk/ml/SequentialModuleEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;
import java.util.Arrays;

public class SequentialModuleEncoding extends MessageEncoding<SequentialModule>
{
  public static final MessageEncoding<SequentialModule> INSTANCE = new SequentialModuleEncoding();
  private static final ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<SequentialModule, State> proto = MessageEncoding.newBuilder(
            null, "SequentialModule", SequentialModule.class, State.class
    );
    // Field: repeated submodules (use NeuralModuleEncoding for each)
    proto.field("modules", 1)
            .list(NeuralModuleEncoding.getInstance())
            .as(
                    (o) -> Arrays.asList(o.modules), // getter: array of modules
                    (o, v) -> o.modules = v.toArray(NeuralModule[]::new) // setter: assign array
            );
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
  protected SequentialModule finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    State s = (State) o;
    // Defensive: null check or empty array if needed
    if (s.modules == null)
      s.modules = new NeuralModule[0];
    return new SequentialModule(s.modules);
  }

  // Inner state class for deserialization
  private static class State
  {
    public NeuralModule[] modules;
  }
}
