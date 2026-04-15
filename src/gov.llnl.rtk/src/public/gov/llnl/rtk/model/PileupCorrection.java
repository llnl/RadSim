// --- file: gov/llnl/rtk/model/PileupCorrection.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;

/**
 * Mathematical model to compute what is the factor to correct a spectrum to its
 * proper count rate. Implementations of this include correction based on the
 * difference between livetime and deadtime.
 *
 * @author nelson85
 */
@ReaderInfo(PileupCorrectionReader.class)
@WriterInfo(PileupCorrectionWriter.class)
public interface PileupCorrection
{
  double compute(Spectrum spectrum);
}
