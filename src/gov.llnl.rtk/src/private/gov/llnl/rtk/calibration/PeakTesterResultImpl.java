// --- file: gov/llnl/rtk/calibration/PeakTesterResultImpl.java ---
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.calibration.PeakTester;

/**
 *
 * @author seilhan3
 */
public class PeakTesterResultImpl implements PeakTester.PeakTesterResult
{
  boolean passed;

  public PeakTesterResultImpl(boolean passed)
  {
    this.passed = passed;
  }

  @Override
  public boolean isPassed()
  {
    return passed;
  }

}
