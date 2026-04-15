// --- file: gov/llnl/rtk/calibration/PredictivePeakTrackerReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.PeakFitterReader;
import gov.llnl.rtk.calibration.PredictivePeakTracker;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "predictivePeakTracker",
        cls = PredictivePeakTracker.class,
        referenceable = true, order = Reader.Order.ALL, document = true)
public class PredictivePeakTrackerReader extends ObjectReader<PredictivePeakTracker>
{

  @Override
  public PredictivePeakTracker start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new PredictivePeakTracker();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<PredictivePeakTracker> builder = this.newBuilder();

    builder.element("decimation")
            .callInteger(PredictivePeakTracker::setDecimation);
    builder.element("meanTimeConstant")
            .callDouble(PredictivePeakTracker::setMeanTimeConstant);
    builder.element("biasTimeConstant")
            .callDouble(PredictivePeakTracker::setBiasTimeConstant);
    builder.element("spectralTimeConstant1")
            .callDouble(PredictivePeakTracker::setSpectralTimeConstant1);
    builder.element("spectralTimeConstant2")
            .callDouble(PredictivePeakTracker::setSpectralTimeConstant2);
    builder.element("peakFitter")
            .reader(new PeakFitterReader())
            .call(PredictivePeakTracker::setFitter);
    builder.element("peakFinder")
            .reader(new PeakFinderReader())
            .call(PredictivePeakTracker::setFinder)
            .optional();
    builder.element("peakTester")
            .reader(new PeakTesterReader())
            .call(PredictivePeakTracker::setTester);
    return builder.getHandlers();
  }

}
