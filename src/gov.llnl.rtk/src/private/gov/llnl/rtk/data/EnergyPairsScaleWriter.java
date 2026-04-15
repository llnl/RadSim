// --- file: gov/llnl/rtk/data/EnergyPairsScaleWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.ChannelEnergyPair;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class EnergyPairsScaleWriter extends ObjectWriter<EnergyPairsScale>
{
  public EnergyPairsScaleWriter()
  {
    super(Options.REFERENCEABLE, "energyPairs", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, EnergyPairsScale object) throws WriterException
  {
    attributes.add("channels", object.getChannels());
  }

  @Override
  public void contents(EnergyPairsScale object) throws WriterException
  {
    WriterBuilder builder = newBuilder();
    WriteObject<ChannelEnergyPair> p1 = builder.element("pair").writer(new ChannelEnergyPairWriter());
    for (ChannelEnergyPair pair : object)
    {
      p1.put(pair);
    }
  }
}
