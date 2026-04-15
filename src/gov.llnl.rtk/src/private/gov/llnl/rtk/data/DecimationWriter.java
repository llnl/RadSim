// --- file: gov/llnl/rtk/data/DecimationWriter.java ---
/* 
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class DecimationWriter extends ObjectWriter<Decimation>
{
  public DecimationWriter()
  {
    super(Options.NONE, "decimation", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, Decimation object) throws WriterException
  {
    if (object.targetEnergyEdges != null)
    {
      attributes.add("type", "energy");
    }
    else if (object.channelEdges != null)
    {
      attributes.add("type", "bins");
    }
    else
      throw new WriterException("invalid decimation object");
  }

  @Override
  public void contents(Decimation object) throws WriterException
  {
    if (object.targetEnergyEdges != null)
    {
      this.addContents(object.targetEnergyEdges);
    }
    else if (object.channelEdges != null)
    {
      this.addContents(object.channelEdges);
    }
  }

}
