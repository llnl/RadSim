// --- file: gov/llnl/rtk/response/SpectralResponseFunctionCalculatedEncoding.java ---
/*
 * Copyright 2026, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.rtk.physics.MaterialEncoding;
import gov.llnl.rtk.physics.QuantityEncoding;
import static gov.llnl.rtk.response.SpectralResponseFunctionCalculatedEncoding.FIELDS;
import gov.llnl.utility.proto.MessageEncoding;
import static gov.llnl.utility.proto.MessageEncoding.newBuilder;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 *
 * This is a very complicated encoder because it is interacting with the
 * builder.
 *
 * @author nelson85
 */
public class SpectralResponseFunctionCalculatedEncoding extends MessageEncoding<SpectralResponseFunctionCalculated>
{
  final static ProtoField[] FIELDS;

  static
  {
    var proto = newBuilder(null, "SpectralResponseFunctionCalculated",
            SpectralResponseFunctionCalculated.class,
            State.class);
    proto.field("vendor", 1).string().as((o) -> o.vendor, (o, v) -> o.builder.vendor(v));
    proto.field("model", 2).string().as((o) -> o.model, (o, v) -> o.builder.model(v));
    proto.field("energy_scale", 3).encoding(new EnergyScaleEncoding())
            .as((o) -> o.energyScale, (o, v) -> o.builder.energyScale(v));

    // line shape
    proto.field("theta", 4).type(Type.Double)
            .as((o) -> o.peakShapeParameters.theta, (o, v) -> o.builder.theta(v));
    proto.field("negative_tail", 5).type(Type.Double)
            .as((o) -> o.peakShapeParameters.negativeTail, (o, v) -> o.builder.negativeTail(v));
    proto.field("positive_tail", 6).type(Type.Double)
            .as((o) -> o.peakShapeParameters.positiveTail, (o, v) -> o.builder.positiveTail(v));

    // Support for lld
    proto.field("lld_energy", 8).type(Type.NetworkDoubles)
            .as((o) -> o.lld.energy, (o, v) -> o.builder.lldEnergy(v));
    proto.field("lld_attenuation", 9).type(Type.NetworkDoubles)
            .as((o) -> o.lld.attenuation, (o, v) -> o.builder.lldAttenuation(v));

    proto.field("shells", 20).list(ElectronShellEncoding.INSTANCE)
            .as(o -> o.shells, (o, v) -> o.builder.shells(v));

    proto.field("material", 21).type(MaterialEncoding.INSTANCE)
            .as(o -> o.material, (o, v) -> o.builder.material(v));

    proto.field("length", 22).type(QuantityEncoding.INSTANCE)
            .as(o -> o.length, (o, v) -> o.builder.length(v));

    proto.field("width", 23).type(QuantityEncoding.INSTANCE)
            .as(o -> o.width, (o, v) -> o.builder.width(v));

    proto.field("height", 24).type(QuantityEncoding.INSTANCE)
            .as(o -> o.height, (o, v) -> o.builder.height(v));

    proto.field("resolution_energy", 25).type(Type.NetworkDoubles)
            .as((o) -> o.resolutionEnergy,
                    (o, v) -> o.builder.resolutionEnergy(v));
    proto.field("resolution_fwhm", 26).type(Type.NetworkDoubles)
            .as((o) -> DoubleStream.of(o.resolutionWidth2).map(x -> Math.sqrt(Math.max(x, 0.0))).toArray(),
                    (o, v) -> o.builder.resolutionFwhm(v));

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
    SpectralResponseFunctionCalculatedBuilder out = new SpectralResponseFunctionCalculatedBuilder();
    State state = new State();
    state.builder = out;
    context.setState(fields[0], state);
    return state;
  }

  @Override
  protected SpectralResponseFunctionCalculated finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    return ((State) o).builder.create();
  }

  /**
   * State to hold progress during parsing.
   */
  private static class State
  {
    SpectralResponseFunctionCalculatedBuilder builder;
  }

  private static class EnergyScaleEncoding extends MessageEncoding<EnergyScale>
  {
    final static ProtoField[] FIELDS;

    static
    {
      var builder = newBuilder(null, "EnergyScale",
              () -> new Object[1],
              (o) -> EnergyScaleFactory.newScale((double[]) ((Object[]) o)[0]));
      builder.field("scale", 1).type(Type.NetworkDoubles)
              .as(
                      (o) -> o.getEdges(),
                      (o, v) -> ((Object[]) o)[0] = v);
      FIELDS = builder.toFields();
    }

    @Override
    public ProtoField[] getFields()
    {
      return FIELDS;
    }
  }

}
