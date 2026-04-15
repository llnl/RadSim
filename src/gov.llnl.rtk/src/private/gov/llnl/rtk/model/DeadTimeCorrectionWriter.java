// --- file: gov/llnl/rtk/model/DeadTimeCorrectionWriter.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.model.PileupDeadTimeCorrection;
import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.io.WriterException;
import gov.llnl.utility.xml.bind.ObjectWriter;

/**
 *
 * @author nelson85
 */
@Internal
public class DeadTimeCorrectionWriter extends ObjectWriter<PileupDeadTimeCorrection>
{
  public DeadTimeCorrectionWriter()
  {
    super(Options.NONE, "deadTimeCorrection", RtkPackage.getInstance());
  }

  @Override
  public void attributes(WriterAttributes attributes, PileupDeadTimeCorrection object) throws WriterException
  {
  }

  @Override
  public void contents(PileupDeadTimeCorrection object) throws WriterException
  {
  }

}
