// --- file: gov/llnl/rtk/model/PileupCorrectionReader.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.model.PileupCorrection;
import gov.llnl.rtk.model.PileupDeadTimeCorrection;
import gov.llnl.rtk.model.PileupRateCorrection;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.ReaderException;
import gov.llnl.utility.xml.bind.ObjectReader;
import gov.llnl.utility.xml.bind.PolymorphicReader;
import gov.llnl.utility.xml.bind.Reader;

/**
 *
 * @author nelson85
 */
@Internal

@Reader.Declaration(pkg = RtkPackage.class, name = "pileupCorrection", cls = PileupCorrection.class)
public class PileupCorrectionReader extends PolymorphicReader<PileupCorrection>
{
 
  @Override
  @SuppressWarnings("unchecked")
  public ObjectReader<? extends PileupCorrection>[] getReaders() throws ReaderException
  {
    return group(
            create(PileupRateCorrection.class),
            create(PileupDeadTimeCorrection.class));
  }
}
