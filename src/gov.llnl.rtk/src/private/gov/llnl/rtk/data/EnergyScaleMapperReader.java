// --- file: gov/llnl/rtk/data/EnergyScaleMapperReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.ChannelEnergyPair;
import gov.llnl.rtk.data.EnergyRegionOfInterest;
import gov.llnl.rtk.data.EnergyScaleMapper;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "energyScaleMapper",
        cls = EnergyScaleMapper.class,
        order = Reader.Order.FREE,
        referenceable = true, document = true, autoAttributes = true)
public class EnergyScaleMapperReader extends ObjectReader<EnergyScaleMapper>
{

  @Override
  public EnergyScaleMapper start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new EnergyScaleMapper();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<EnergyScaleMapper> builder = this.newBuilder();
    builder.element("pair").call(EnergyScaleMapper::addPair, ChannelEnergyPair.class);
    builder.element("ignore").call(EnergyScaleMapper::addIgnoreRoi, EnergyRegionOfInterest.class);
    return builder.getHandlers();
  }

}
