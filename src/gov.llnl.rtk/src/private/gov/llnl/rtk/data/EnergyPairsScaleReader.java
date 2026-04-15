// --- file: gov/llnl/rtk/data/EnergyPairsScaleReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
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
@Reader.Declaration(pkg = RtkPackage.class, name = "energyPairs",
        cls = EnergyPairsScale.class,
        order = Reader.Order.FREE, referenceable = true)
@Reader.Attribute(name = "origin", type = Origin.class)
@Reader.Attribute(name = "channels", type = Integer.class)
public class EnergyPairsScaleReader extends ObjectReader<EnergyPairsScale>
{
  final Origin origin;

  public EnergyPairsScaleReader()
  {
    origin = Origin.CENTER;
  }

  @Override
  public EnergyPairsScale start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    Origin origin = this.origin;
    String ov = attributes.getValue("origin");
    if (ov != null)
      origin = Origin.valueOf(ov);

    EnergyPairsScaleImpl obj = new EnergyPairsScaleImpl();
    int channels = Integer.parseInt(attributes.getValue("channels").trim());
    obj.setChannels(channels);
    return obj;
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<EnergyPairsScale> builder = this.newBuilder();
    builder.element("pair")
            .reader(new ChannelEnergyPairReader(origin))
            .call(EnergyPairsScale::add);
    return builder.getHandlers();
  }

}
