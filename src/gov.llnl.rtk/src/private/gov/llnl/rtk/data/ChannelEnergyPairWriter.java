// --- file: gov/llnl/rtk/data/ChannelEnergyPairWriter.java ---
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
public class ChannelEnergyPairWriter extends ObjectWriter<ChannelEnergyPair>
{
  public ChannelEnergyPairWriter()
  {
    super(Options.NONE, "channelEnergyPair", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, ChannelEnergyPair object) throws WriterException
  {
    attributes.add("channel", object.getChannelEdge());
    attributes.add("energy", object.getEnergy());
  }

  @Override
  public void contents(ChannelEnergyPair object) throws WriterException
  {
  }
}
