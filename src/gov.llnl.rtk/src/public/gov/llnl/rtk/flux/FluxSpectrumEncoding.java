// --- file: gov/llnl/rtk/flux/FluxSpectrumEncoding.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.utility.proto.MessageEncoding;
import gov.llnl.utility.proto.ProtoField;

/**
 *
 * @author nelson85
 */
public class FluxSpectrumEncoding extends MessageEncoding<FluxSpectrum>
{
  final static FluxSpectrumEncoding INSTANCE = new FluxSpectrumEncoding();
  final static ProtoField[] FIELDS;

  static
  {
    var builder = newBuilder(null, "flux_spectrum", Temp::new, FluxSpectrumEncoding::convert);
    builder.field("photon_scale", 1).encoding(new EnergyScaleEncoding())
            .as((o) -> o.photonScale, (o, v) -> o.pscale = v);
    builder.field("photon_groups", 2).type(Type.NetworkDoubles)
            .as((o) -> o.photonCounts, (o, v) -> o.pcounts = v);
    builder.field("neutron_scale", 3).encoding(new EnergyScaleEncoding())
            .as((o) -> o.neutronScale, (o, v) -> o.nscale = v);
    builder.field("neutron_counts", 4).type(Type.NetworkDoubles)
            .as((o) -> o.neutronCounts, (o, v) -> o.ncounts = v);
    FIELDS = builder.toFields();
  }

  public static FluxSpectrumEncoding getInstance()
  {
    return INSTANCE;
  }

  @Override
  public ProtoField[] getFields()
  {
    return FIELDS;
  }

  private static FluxSpectrum convert(Temp o)
  {
    return new FluxSpectrum(o.pscale, o.pcounts, o.nscale, o.ncounts);
  }

  private static class Temp
  {
    EnergyScale pscale;
    double[] pcounts;
    EnergyScale nscale;
    double[] ncounts;
  }

  private static class EnergyScaleEncoding extends MessageEncoding<EnergyScale>
  {
    final static ProtoField[] FIELDS;

    static
    {
      var builder = newBuilder(null, "es",
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
