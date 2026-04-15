// --- file: gov/llnl/rtk/calibration/PeakFinderReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.CumulativePeakFinder;
import gov.llnl.rtk.calibration.PeakFinder;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PolymorphicReader;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author seilhan3
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "peakFinder",
        cls = PeakFinder.class,
        referenceable = true)
public class PeakFinderReader extends PolymorphicReader<PeakFinder>
{

  @Override
  @SuppressWarnings("unchecked")
  public ObjectReader<? extends PeakFinder>[] getReaders() throws ReaderException
  {
    return group(ObjectReader.create(CumulativePeakFinder.class));
  }
//
//  @Override
//  public Class<PeakFinder> getObjectClass()
//  {
//    return PeakFinder.class;
//  }

}
