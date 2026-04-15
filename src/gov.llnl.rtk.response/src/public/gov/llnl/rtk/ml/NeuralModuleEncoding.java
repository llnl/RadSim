// --- file: gov/llnl/rtk/ml/ModuleEncoding.java ---
package gov.llnl.rtk.ml;

import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.OneOfEncoding;

/**
 * Registry and encoding manager for serializing/deserializing NeuralModule types with Protobuf.
 * 
 * Uses a singleton pattern and lazy initialization.
 */
public class NeuralModuleEncoding extends OneOfEncoding<NeuralModule>
{
  final static OneOfEncoding<NeuralModule> INSTANCE = new NeuralModuleEncoding();
  static boolean initialized = false;

  private NeuralModuleEncoding()
  {
    super("NeuralModule", NeuralModule.class);

    // ... add more as needed
  }

  public static MessageEncoding<NeuralModule> getInstance()
  {
    if (!initialized)
    {
      // Due to race conditions with modules include each other, we need to use
      // a deferred loading pattern.
      synchronized (NeuralModuleEncoding.class)
      {
        initialized = true;
        // Add all module types
        INSTANCE.add(1, IdentityActivation.class, new IdentityActivationEncoding());
        INSTANCE.add(2, ReluActivation.class, new ReluActivationEncoding());
        INSTANCE.add(3, LeakyReluActivation.class, new LeakyReluActivationEncoding());
        INSTANCE.add(4, SigmoidActivation.class, new SigmoidActivationEncoding());
        INSTANCE.add(5, SqrtPlusActivation.class, new SqrtPlusActivationEncoding());

        INSTANCE.add(10, LinearLayer.class, new LinearLayerEncoding());

        INSTANCE.add(20, SequentialModule.class, new SequentialModuleEncoding());
        INSTANCE.add(21, ResidualModule.class, new ResidualModuleEncoding());
      }
    }
    return INSTANCE;
  }
}
