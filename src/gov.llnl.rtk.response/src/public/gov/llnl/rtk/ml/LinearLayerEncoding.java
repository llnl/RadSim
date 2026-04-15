// --- file: gov/llnl/rtk/ml/LinearLayerEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;

public class LinearLayerEncoding extends MessageEncoding<LinearLayer>
{
  final static ProtoField[] FIELDS;

  static
  {
    ProtoBuilder<LinearLayer, State> proto = MessageEncoding.newBuilder(
            null, "LinearLayer", LinearLayer.class, State.class
    );
    proto.field("weights", 1).type(Type.NetworkDoubles2)
            .as(
                    (o) -> o.getWeights(), // get weights as double[][]
                    (o, v) -> o.weights = v // set in parsing state
            );
    proto.field("bias", 2).type(Type.NetworkDoubles)
            .as(
                    (o) -> o.getBias(),
                    (o, v) -> o.bias = v
            );
    proto.field("in_size", 3).type(Type.Int32)
            .as(
                    (o) -> o.getInputSize(),
                    (o, v) -> o.inSize = v
            );
    proto.field("out_size", 4).type(Type.Int32)
            .as(
                    (o) -> o.getOutputSize(),
                    (o, v) -> o.outSize = v
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
  protected LinearLayer finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    State state = (State) o;
    // Validate shapes if needed (optional)
    if (state.weights.length != state.outSize || state.weights[0].length != state.inSize)
      throw new IllegalArgumentException("Weights array shape does not match out/in size");
    if (state.bias.length != state.outSize)
      throw new IllegalArgumentException("Bias array length does not match out size");
    // Construct the layer
    LinearLayer layer = new LinearLayer(state.inSize, state.outSize);
    layer.setWeights(state.weights);
    layer.setBias(state.bias);
    return layer;
  }

  private static class State
  {
    double[][] weights;
    double[] bias;
    int inSize;
    int outSize;
  }
}
