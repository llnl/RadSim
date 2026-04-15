// --- file: gov/llnl/rtk/data/ChannelEnergyPairReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.ChannelEnergyPair;
import gov.llnl.rtk.calibration.ChannelEnergyPair.Origin;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Internal
@Reader.Declaration(pkg = RtkPackage.class, name = "channelEnergyPair",
        cls = ChannelEnergyPair.class,
        order = Reader.Order.OPTIONS, referenceable = true)
@Reader.Attribute(name = "channel", type = Double.class, required = true)
@Reader.Attribute(name = "energy", type = Double.class, required = true)
@Reader.Attribute(name = "origin", type = Origin.class)
public class ChannelEnergyPairReader extends ObjectReader<ChannelEnergyPair>
{
  final Origin origin;

  public ChannelEnergyPairReader()
  {
     origin = Origin.CENTER;
  }

  public ChannelEnergyPairReader(Origin origin)
  {
    this.origin = origin;
  }

  @Override
  public ChannelEnergyPair start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    double channel = Double.parseDouble(attributes.getValue("channel").trim());
    double energy = Double.parseDouble(attributes.getValue("energy").trim());

    Origin origin = this.origin;
    String ov = attributes.getValue("origin");
    if (ov != null)
      origin = ChannelEnergyPair.Origin.valueOf(ov);
    return new ChannelEnergyPair(channel, energy, origin);
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
