// --- file: gov/llnl/rtk/calibration/SinglePeakGainStabilizerReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.DetectorCalibratorReader;
import gov.llnl.rtk.calibration.PeakTracker;
import gov.llnl.rtk.calibration.SinglePeakGainStabilizer;
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
@Reader.Declaration(pkg = RtkPackage.class, name = "singlePeakGainStabilizer",
        cls = SinglePeakGainStabilizer.class,
        referenceable = true, order = Reader.Order.SEQUENCE, document = true)
public class SinglePeakGainStabilizerReader extends ObjectReader<SinglePeakGainStabilizer>
{

  @Override
  public SinglePeakGainStabilizer start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new SinglePeakGainStabilizer();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    ReaderBuilder<SinglePeakGainStabilizer> builder = this.newBuilder();
    builder.element("target")
            .call(SinglePeakGainStabilizer::setStabilizerTarget, StabilizerTarget.class);
    PeakTrackerReader ptr = new PeakTrackerReader();
    builder.group(Order.CHOICE).readers(PeakTracker.class, ptr.getReaders())
            .call(SinglePeakGainStabilizer::setPeakTracker);
    DetectorCalibratorReader.addHandlers(builder);
    return builder.getHandlers();
  }

}
