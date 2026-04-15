// --- file: gov/llnl/rtk/calibration/WeightedCentroidFitterReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.WeightedCentroidFitter;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "weightedCentroidFitter",
        cls = WeightedCentroidFitter.class,
        referenceable = true, order = Reader.Order.OPTIONS)
public class WeightedCentroidFitterReader extends ObjectReader<WeightedCentroidFitter>
{

  @Override
  public WeightedCentroidFitter start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new WeightedCentroidFitter();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<WeightedCentroidFitter> builder = this.newBuilder();
    builder.element("biasCoefficients")
            .contents(double[].class)
            .call(WeightedCentroidFitter::setBiasCoefficients);
    builder.element("expectedChannel")
            .callDouble(WeightedCentroidFitter::setExpectedChannel)
            .optional();
    builder.element("peakSigma")
            .callDouble(WeightedCentroidFitter::setPeakSigma);
    return builder.getHandlers();
  }

}
