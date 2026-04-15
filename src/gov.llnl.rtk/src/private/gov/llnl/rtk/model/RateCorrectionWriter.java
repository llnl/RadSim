// --- file: gov/llnl/rtk/model/RateCorrectionWriter.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.model.PileupRateCorrection;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class RateCorrectionWriter extends ObjectWriter<PileupRateCorrection>
{
  public RateCorrectionWriter()
  {
    super(Options.NONE, "rateCorrection", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, PileupRateCorrection object) throws WriterException
  {
    attributes.add("rate", object.getMaxRate());
  }

  @Override
  public void contents(PileupRateCorrection object) throws WriterException
  {
  }

}
