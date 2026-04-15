// --- file: gov/llnl/rtk/calibration/PeakTesterReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.calibration.PeakTester;
import gov.llnl.rtk.calibration.RatioPeakTester;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PolymorphicReader;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author nelson85
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "peakTester",
        cls = PeakTester.class,
        referenceable = true)
public class PeakTesterReader extends PolymorphicReader<PeakTester>
{

  @Override
  @SuppressWarnings("unchecked")
  public ObjectReader<? extends PeakTester>[] getReaders() throws ReaderException
  {
    return group(ObjectReader.create(RatioPeakTester.class));
  }
}
