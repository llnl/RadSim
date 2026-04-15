// --- file: gov/llnl/rtk/model/PileupDeadTimeCorrection.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.model;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.xml.bind.WriterInfo;
import gov.llnl.utility.xml.bind.Reader;

/**
 * This method is useful if the instrument is providing a real time and live
 * time from which we can infer the dead time.
 */
@WriterInfo(DeadTimeCorrectionWriter.class)
@Reader.Declaration(pkg = RtkPackage.class, name = "deadTimeCorrection")
public class PileupDeadTimeCorrection implements PileupCorrection
{
  @Override
  public double compute(Spectrum spectrum)
  {
    double realtime = spectrum.getRealTime();
    double livetime = spectrum.getLiveTime();
    double counts = spectrum.getCounts();
    double deadtime = realtime - livetime;
    double dt = deadtime / counts;
    return 1.0 / (1.0 - counts / realtime * dt);
  }
}
