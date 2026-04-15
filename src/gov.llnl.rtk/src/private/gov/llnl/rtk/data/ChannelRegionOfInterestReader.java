// --- file: gov/llnl/rtk/data/ChannelRegionOfInterestReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "channelRegionOfInterest",
        cls = ChannelRegionOfInterest.class,
        order = Reader.Order.ALL)
@Reader.Attribute(name = "lower", type = Double.class, required = true)
@Reader.Attribute(name = "upper", type = Double.class, required = true)
public class ChannelRegionOfInterestReader extends ObjectReader<ChannelRegionOfInterest>
{
  public ChannelRegionOfInterestReader()
  {
  }

  @Override
  public ChannelRegionOfInterest start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    double lower = Double.parseDouble(attributes.getValue("lower").trim());
    double upper = Double.parseDouble(attributes.getValue("upper").trim());
    return new ChannelRegionOfInterestImpl(lower, upper);
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }

}
