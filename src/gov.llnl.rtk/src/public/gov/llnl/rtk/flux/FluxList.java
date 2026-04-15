// --- file: gov/llnl/rtk/flux/FluxList.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.flux;

import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of SpectraList for DoubleSpectrum.
 *
 * @author nelson85
 */
@ReaderInfo(FluxListReader.class)
@WriterInfo(FluxListWriter.class)
public class FluxList
        extends ArrayList<Flux>
{
  public final static long serialVersionUID = -152046824508101937L;

   public FluxList()
  {
    super();
  }

  public FluxList(Collection<? extends Flux> items)
  {
    super(items);
  }



}
