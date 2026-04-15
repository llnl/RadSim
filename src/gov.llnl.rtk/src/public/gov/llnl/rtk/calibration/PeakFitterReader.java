// --- file: gov/llnl/rtk/calibration/PeakFitterReader.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PolymorphicReader;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author seilhan3
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "peakFitter",
        cls = PeakFitter.class,
        referenceable = true)
public class PeakFitterReader extends PolymorphicReader<PeakFitter>
{

  @Override
  @SuppressWarnings("unchecked")
  public ObjectReader<? extends PeakFitter>[] getReaders() throws ReaderException
  {
    return group(
            new WeightedCentroidFitterReader(),
            new GaussianFitterReader()
    );
  }

}
