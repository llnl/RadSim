// --- file: gov/llnl/rtk/ml/ResidualModuleEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;
import java.util.Arrays;

public class ResidualModuleEncoding extends MessageEncoding<ResidualModule>
{
  private static final ProtoField[] FIELDS;

  static
  {
    try{
    ProtoBuilder<ResidualModule, State> proto = MessageEncoding.newBuilder(
            null, "ResidualModule", ResidualModule.class, State.class
    );
    // The main sequence of modules
    proto.field("modules", 1)
            .list(NeuralModuleEncoding.getInstance())
            .as(
                    (o) -> Arrays.asList(o.modules),
                    (o, v) -> o.modules = v.toArray(NeuralModule[]::new)
            );
    // The residual layer (may be null)
    proto.field("residualLayer", 2)
            .encoding(NeuralModuleEncoding.getInstance())
            .as(
                    (o) -> o.residualLayer,
                    (o, v) -> o.residualLayer = v
            );
    FIELDS = proto.toFields();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw ex;
    }
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
  protected ResidualModule finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    State s = (State) o;
    if (s.modules == null)
      s.modules = new NeuralModule[0];
    return new ResidualModule(s.residualLayer, s.modules);
  }

  private static class State
  {
    public NeuralModule[] modules;
    public NeuralModule residualLayer;
  }
}
