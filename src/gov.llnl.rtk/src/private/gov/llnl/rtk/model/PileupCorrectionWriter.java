// --- file: gov/llnl/rtk/model/PileupCorrectionWriter.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.model.PileupCorrection;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class PileupCorrectionWriter extends ObjectWriter<PileupCorrection>
{
  public PileupCorrectionWriter()
  {
    super(Options.NONE, "pileupCorrection", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, PileupCorrection object) throws WriterException
  {
  }

  @Override
  public void contents(PileupCorrection object) throws WriterException
  {
    WriterBuilder wb = newBuilder();
    if (object != null)
    {
      wb.contents(PileupCorrection.class).put(object);
    }
  }

}
