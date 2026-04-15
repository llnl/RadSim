// --- file: gov/llnl/rtk/flux/FluxEncoding.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.utility.proto.OneOfEncoding;

/**
 *
 * @author nelson85
 */
public class FluxEncoding extends OneOfEncoding<Flux>
{
  final static FluxEncoding INSTANCE = new FluxEncoding();

  private FluxEncoding()
  {
    super("flux", Flux.class);
    this.add(1, FluxSpectrum.class, FluxSpectrumEncoding.getInstance());
    this.add(2, FluxBinned.class, FluxBinnedEncoding.getInstance());
    this.add(3, FluxTrapezoid.class, FluxTrapezoidEncoding.getInstance());
  }

  public static FluxEncoding getInstance()
  {
    return INSTANCE;
  }
}
