// --- file: gov/llnl/rtk/calibration/PeakTrackerReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.calibration.PredictivePeakTrackerReader;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.PeakTracker;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PolymorphicReader;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "peakTracker",
        cls = PeakTracker.class,
        referenceable = true)
public class PeakTrackerReader extends PolymorphicReader<PeakTracker>
{

  @Override
  @SuppressWarnings("unchecked")
  public ObjectReader<? extends PeakTracker>[] getReaders() throws ReaderException
  {
    return group(new ExternalPeakTrackerReader(),
            new PredictivePeakTrackerReader()
    );
  }
}
