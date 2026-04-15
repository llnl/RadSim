// --- file: gov/llnl/rtk/calibration/RatioPeakTester.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.utility.annotation.Debug;
import gov.llnl.utility.xml.bind.Reader;

@Reader.Declaration(pkg = RtkPackage.class, name = "ratioPeakTester", referenceable = true)
public class RatioPeakTester implements PeakTester
{
  double minimumPeakRatio = -1;
  double lastRatio;

  @Override
  public PeakTesterResult testPeakFit(Spectrum spectrum, PeakFitter.PeakFitterResult fitResult)
  {
    boolean passed = true;

    if (minimumPeakRatio > 0)
    {
      double peakRatio = fitResult.getIntegratedIntensity() / (fitResult.getIntegratedIntensity() + fitResult.getIntegratedContinuum());
      lastRatio = peakRatio;
      passed = (peakRatio > minimumPeakRatio);
    }
    PeakTesterResult testResult = new PeakTesterResultImpl(passed);
    return testResult;
  }

  @Reader.Element(name = "minimumPeakRatio")
  public void setMinimumPeakRatio(double minimumPeakRatio)
  {
    this.minimumPeakRatio = minimumPeakRatio;
  }

}
