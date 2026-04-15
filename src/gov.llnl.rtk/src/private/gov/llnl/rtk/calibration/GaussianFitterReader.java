// --- file: gov/llnl/rtk/calibration/GaussianFitterReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.GaussianFitter;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "gaussianFitter",
        cls = GaussianFitter.class,
        referenceable = true, order = Reader.Order.OPTIONS)
public class GaussianFitterReader extends ObjectReader<GaussianFitter>
{

  @Override
  public GaussianFitter start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new GaussianFitter();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<GaussianFitter> builder = this.newBuilder();
    builder.element("startChannelFraction")
            .callDouble(GaussianFitter::setStartChannelFraction);
    builder.element("endChannelFraction")
            .callDouble(GaussianFitter::setEndChannelFraction);
    return builder.getHandlers();
  }
}
