// --- file: gov/llnl/rtk/calibration/CalibrationSensor.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.data.RadiationSensor;

/**
 *
 * @author seilhan3
 */
public interface CalibrationSensor extends RadiationSensor
{
  /**
   * Define the energy scale for the measurements from this sensor.
   *
   * @param energyScale
   */
  void applyEnergyScale(EnergyScale energyScale);

  /**
   * Get the current energy scale for the sensor.
   *
   * @return
   */
  EnergyScale getEnergyScale();

  /**
   * Get the routine that will be used to calibrate raw data.
   *
   * This routine is responsible for adding the energy scale to the measurement.
   *
   * @return
   */
  DetectorCalibrator getCalibrator();

  /**
   * Calibrate the working.
   *
   * @param spectrum
   * @return true on success, false otherwise.
   */
  CalibratorResult calibrate(Spectrum spectrum);

  /**
   * Get how many channels are defined as overrange for the energy scale.
   *
   * @return
   */
  int getOverRangeChannels();

  /**
   * Get how many channels are defined as underrange for the energy scale.
   *
   * @return
   */
  int getUnderRangeChannels();
}
