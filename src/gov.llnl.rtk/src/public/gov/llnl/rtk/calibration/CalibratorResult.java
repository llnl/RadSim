// --- file: gov/llnl/rtk/calibration/CalibratorResult.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.GammaMeasurement;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.RadiationSensor;
import gov.llnl.rtk.data.Spectrum;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
public class CalibratorResult implements Serializable, GammaMeasurement
{
  private final RadiationSensor sensor;
  private final IntegerSpectrum spectrum;
  private final boolean calibrated;
  
  public CalibratorResult(RadiationSensor sensor, IntegerSpectrum spectrum, boolean calibrated)
  {
    this.sensor = sensor;
    this.spectrum = spectrum;
    this.calibrated = calibrated;
  }

  /**
   * @return the calibrated
   */
  public boolean isCalibrated()
  {
    return calibrated;
  }

  @Override
  public RadiationSensor getSensor()
  {
    return sensor;
  }

  @Override
  public Spectrum getSample()
  {
    return spectrum;
  }

}
