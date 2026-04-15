// --- file: gov/llnl/rtk/calibration/ExternalPeakTrackerReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.ExternalPeakTracker;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.Reader;
import gov.llnl.utility.xml.bind.ReaderContext;
import org.xml.sax.Attributes;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "externalPeakTracker",
        cls = ExternalPeakTracker.class,
        referenceable = true, order = Reader.Order.ALL)
public class ExternalPeakTrackerReader extends ObjectReader<ExternalPeakTracker>
{

  @Override
  public ExternalPeakTracker start(ReaderContext context, Attributes attributes) throws ReaderException
  {
    return new ExternalPeakTracker();
  }

  @Override
  public ElementHandlerMap getHandlers(ReaderContext context) throws ReaderException
  {
    return null;
  }
}
