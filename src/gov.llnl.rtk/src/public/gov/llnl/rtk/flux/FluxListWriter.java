// --- file: gov/llnl/rtk/flux/FluxListWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
public class FluxListWriter
        extends ObjectWriter<FluxList>
{

  public FluxListWriter()
  {
    super(Options.NONE, "fluxList", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, FluxList object) throws WriterException
  {
  }

  @Override
  @SuppressWarnings("unchecked")
  public void contents(FluxList object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    WriteObject wos = wb.element("flux").writer(new FluxWriter());
    for (Flux flux : object)
    {
      wos.put(flux);
    }
  }
}
