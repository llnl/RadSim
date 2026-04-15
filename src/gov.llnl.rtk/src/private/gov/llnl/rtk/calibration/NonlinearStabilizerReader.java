// --- file: gov/llnl/rtk/calibration/NonlinearStabilizerReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.DetectorCalibratorReader;
import gov.llnl.rtk.calibration.NonlinearStabilizer;
import gov.llnl.rtk.calibration.NonlinearityMap;
import gov.llnl.rtk.calibration.PeakTracker;
import gov.llnl.rtk.calibration.StabilizerTarget;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "nonlinearStabilizer",
        cls = NonlinearStabilizer.class,
        referenceable = true, order = Reader.Order.SEQUENCE, document = true, autoAttributes = true)
public class NonlinearStabilizerReader extends ObjectReader<NonlinearStabilizer>
{

  @Override
  public NonlinearStabilizer start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new NonlinearStabilizer();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<NonlinearStabilizer> rb = newBuilder();
    rb.element("target")
            .call(NonlinearStabilizer::setTarget, StabilizerTarget.class);
    rb.element("temperatureMap")
            .call(NonlinearStabilizer::setTemperatureMap, NonlinearityMap.class);
    rb.element("rateMap")
            .call(NonlinearStabilizer::setRateMap, NonlinearityMap.class);
    PeakTrackerReader ptr = new PeakTrackerReader();
    rb.group(Order.CHOICE).readers(PeakTracker.class, ptr.getReaders())
            .call(NonlinearStabilizer::setPeakTracker);
    DetectorCalibratorReader.addHandlers(rb);
    return rb.getHandlers();
  }

}
