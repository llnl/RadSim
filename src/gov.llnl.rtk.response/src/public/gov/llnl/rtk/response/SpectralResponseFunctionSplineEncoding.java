// --- file: gov/llnl/rtk/response/SpectralResponseFunctionSplineEncoding.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import static gov.llnl.rtk.response.SpectralResponseFunctionSplineEncoding.FIELDS;
import gov.llnl.utility.proto.MessageEncoding;
import static gov.llnl.utility.proto.MessageEncoding.newBuilder;
import gov.llnl.utility.proto.ProtoBuilder;
import gov.llnl.utility.proto.ProtoContext;
import gov.llnl.utility.proto.ProtoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * This is a very complicated encoder because it is interacting with the
 * builder.
 *
 * @author nelson85
 */
public class SpectralResponseFunctionSplineEncoding extends MessageEncoding<SpectralResponseFunctionSpline>
{
  final static ProtoField[] FIELDS;

  static
  {
    var proto = newBuilder(null, "SpectralResponseFunctionSpline",
            SpectralResponseFunctionSpline.class,
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

    // The list was already consumed by the builder.
    proto.field("entries", 7).list(new ResponseEntryEncoding())
            .as((o) -> new ArrayList(Arrays.asList(o.entries)), (o, v) ->
            {
            });

    // Support for lld
    proto.field("lld_energy", 8).type(Type.NetworkDoubles)
            .as((o) -> o.lld.energy, (o, v) -> o.builder.lldEnergy(v));
    proto.field("lld_attenuation", 9).type(Type.NetworkDoubles)
            .as((o) -> o.lld.attenuation, (o, v) -> o.builder.lldAttenuation(v));

    // Support for internal sources (LaBr3)
    proto.field("internal", 10).type(Type.NetworkDoubles)
            .as((o) -> o.internal, (o, v) -> o.builder.internal(v));

    // Support for CZT
    proto.field("incomplete_intensity", 11).type(Type.Double)
            .as((o) -> o.incomplete.intensity, (o, v) -> o.builder.incompleteIntensity(v));
    proto.field("incomplete_center", 12).type(Type.Double)
            .as((o) -> o.incomplete.center, (o, v) -> o.builder.incompleteCenter(v));
    proto.field("incomplete_variance", 13).type(Type.Double)
            .as((o) -> o.incomplete.variance, (o, v) -> o.builder.incompleteVariance(v));

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
    SpectralResponseFunctionSplineBuilder out = new SpectralResponseFunctionSplineBuilder();
    State state = new State();
    state.builder = out;
    context.setState(FIELDS[0], state);
    return state;
  }

  @Override
  protected SpectralResponseFunctionSpline finish(ProtoContext context, ProtoField[] fields, Object o)
  {
    return ((State) o).builder.create();
  }

  /**
   * State to hold progress during parsing.
   */
  private static class State
  {
    SpectralResponseFunctionSplineBuilder builder;
    SpectralResponseFunctionSplineBuilder.EntryBuilder entry;
    int[] continuumMesh = new int[4];
    double[] continuumValues;
    RenderItem type = RenderItem.PHOTOELECTRIC;
    double amp;
    double center;
    double width;
    double widthFactor;

    private void clear()
    {
      Arrays.fill(continuumMesh,0);
      type = RenderItem.PHOTOELECTRIC;
      amp = center = width = 0;
      continuumValues = null;
    }
  }

  private static class ResponseEntryEncoding extends MessageEncoding<SplineResponseEntry>
  {

    final static ProtoField[] INNER;

    @Override
    public ProtoField[] getFields()
    {
      return INNER;
    }

    static
    {
      gov.llnl.utility.proto.ProtoBuilder<gov.llnl.rtk.response.SplineResponseEntry, gov.llnl.rtk.response.SpectralResponseFunctionSplineEncoding.State> proto = newBuilder(null, "SpectralResponseFunctionSpline", SplineResponseEntry.class, State.class);
      proto.field("energy", 1)
              .type(Type.Double)
              .as((o) -> o.continuum.energy, (o, v) -> o.entry.energy(v));

      // The list was already consumed by the builder.
      proto.field("peaks", 2)
              .list(new SpectralResponsePeakEncoding())
              .as(ResponseEntryEncoding::getPeaks, (o, v) ->
              {
              });

      // Compatible with old formated files
      proto.field("s1", 3).type(Type.Int32)
              .as((o) -> o.continuum.meshPoints[0], (o, v) -> o.continuumMesh[0] = v)
              .optional((o)->false);  // replaced going forward
      proto.field("s2", 4).type(Type.Int32)
              .as((o) -> o.continuum.meshPoints[1], (o, v) -> o.continuumMesh[1] = v)
              .optional((o)->false);  // replaced going forward
      proto.field("s3", 5).type(Type.Int32)
              .as((o) -> o.continuum.meshPoints[2], (o, v) -> o.continuumMesh[2] = v)
              .optional((o)->false);  // replaced going forward

      // Continuum specification going forward
      proto.field("continuumValues", 6).type(Type.NetworkDoubles)
              .as((o) -> o.continuum.values, (o, v) -> o.continuumValues = v);
      proto.field("continuumMesh", 7).packed(Type.Int32)
              .as((o) -> o.continuum.meshPoints, (o, v) -> o.continuumMesh = v);
      proto.field("widthFactor", 8).type(Type.Double)
              .as((o) -> o.continuum.widthFactor, (o, v) -> o.widthFactor = v);
      INNER = proto.toFields();
    }

    @Override
    protected Object allocate(ProtoContext context, ProtoField[] fields)
    {
      State state = (State) context.getState(FIELDS[0], true);
      // Declare start of a new entry
      state.entry = state.builder.photon();
      state.clear();
      return state;
    }

    @Override
    protected SplineResponseEntry finish(ProtoContext context, ProtoField[] fields, Object o)
    {
      State state = (State) o;
      state.entry.continuum(state.continuumValues, state.continuumMesh, state.widthFactor);
      return state.entry.create();
    }

    /**
     * Collect all of the pieces so they can be serialized together.
     *
     * @param entry
     * @return
     */
    static private List<SplineResponseLine> getPeaks(SplineResponseEntry entry)
    {
      ArrayList<SplineResponseLine> out = new ArrayList<>();
      if (entry.photoelectric != null)
        out.add(entry.photoelectric);
      if (entry.singleEscape != null)
        out.add(entry.singleEscape);
      if (entry.doubleEscape != null)
        out.add(entry.doubleEscape);
      if (entry.annihilation != null)
        out.add(entry.annihilation);
      if (entry.peaks != null)
        out.addAll(Arrays.asList(entry.peaks));
      return out;
    }
  }

  /**
   * Encoding for peaks.
   */
  private static class SpectralResponsePeakEncoding extends MessageEncoding<SplineResponseLine>
  {
    final static ProtoField[] INNER;

    static
    {
      ProtoBuilder<SplineResponseLine, State> proto = newBuilder(null, "SpectralResponsePeak", SplineResponseLine.class, State.class);
      proto.field("amp", 1).type(Type.Double)
              .asDouble((o) -> o.amplitude, (o, v) -> o.amp = v);
      proto.field("center", 2).type(Type.Double)
              .asDouble((o) -> o.center, (o, v) -> o.center = v);
      proto.field("width", 3).type(Type.Double)
              .asDouble((o) -> o.width, (o, v) -> o.width = v);
      proto.field("type", 4).type(Type.Int32)
              .asInt((o) -> o.type.ordinal(),
                      (State o, int v) -> o.type = RenderItem.values()[v]);
      INNER = proto.toFields();
    }

    @Override
    protected Object allocate(ProtoContext context, ProtoField[] fields)
    {
      State state = (State) context.getState(FIELDS[0], true);
      // ProtoBuf omits 0 in int fields.   We should never use 0 for an enum value as a result!
      state.type = RenderItem.PHOTOELECTRIC;
      return state;
    }

    @Override
    protected SplineResponseLine finish(ProtoContext context, ProtoField[] fields, Object o)
    {
      State state = (State) o;
      state.entry.line(state.type, state.amp, state.center, state.width);
      return null;
    }

    @Override
    public ProtoField[] getFields()
    {
      return INNER;
    }
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

//message SpectralResponseSplineProto
//{
//  message Peak
//  {
//    double amplitude = 1;
//    double center = 2;
//    double width = 3;
//    int32 type = 4;
//  }
//
//  message Entry
//  {
//    double energy = 1;
//    repeated Peak peaks = 2;
//    int32 s1 = 3;
//    int32 s2 = 4;
//    int32 s3 = 5;
//    bytes continuumValues = 6;
//  }
//
//  string vendor = 1;
//  string model = 2;
//  EnergyScale energy_scale = 3;
//  double theta = 4;
//  double negative_tail = 5;
//  double positive_tail = 6;
//  repeated Entry entries = 7;
//  bytes lld_energy = 8;
//  bytes lld_attenuation = 9;
//  bytes internal = 10; // double array on the same energy scale
//}

