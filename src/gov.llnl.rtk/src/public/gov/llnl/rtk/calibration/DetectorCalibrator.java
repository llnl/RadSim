// --- file: gov/llnl/rtk/calibration/DetectorCalibrator.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.EnergyScaleException;
import static gov.llnl.rtk.calibration.DetectorCalibratorAttributes.DEFAULT_ENERGY_SCALE;
import static gov.llnl.rtk.calibration.DetectorCalibratorAttributes.ENERGY_SCALE_MAPPER;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleMapper;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.RadiationSensor;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.impl.FaultImpl;
import gov.llnl.rtk.quality.Fault;
import gov.llnl.rtk.quality.FaultCategory;
import gov.llnl.rtk.quality.FaultLevel;
import gov.llnl.rtk.quality.QualityControlled;
import gov.llnl.utility.Expandable;
import gov.llnl.utility.InitializeInterface;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.Serializable;

/**
 *
 * @author nelson85
 */
@ReaderInfo(DetectorCalibratorReader.class)
public interface DetectorCalibrator extends InitializeInterface, QualityControlled, Serializable, Expandable
{
  final static Fault STABILIZER_OUT_OF_RANGE = new FaultImpl(FaultLevel.INFO, FaultCategory.CALIBRATION_PROBLEM, "Out of range stabilizer", null, true);

  void setSensor(RadiationSensor sensor);
  
  /**
   * Set what random number generator to use so replays can be deterministic.
   *
   * @param randomGenerator
   */
  void setRandomGenerator(RandomGenerator randomGenerator);

  /**
   * Push energy scale into calibrator which in turn will push into it's
   * components.
   *
   * @param energyScale
   * @throws EnergyScaleException
   */
  void setTargetEnergyScale(EnergyScale energyScale) throws EnergyScaleException;

  /**
   * Incorporate the rawSpectrum in to the calibration
   *
   * @param rawSpectrum
   */
  void incorporate(IntegerSpectrum rawSpectrum);

  /**
   * Reset the calibrator.
   */
  void reset();

  /**
   * Calibrate an integer spectrum
   *
   * @param spectrum
   * @return
   */
  CalibratorResult applyCalibration(Spectrum spectrum);

  /**
   * Get target energy scale.
   *
   * @return
   */
  EnergyScale getTargetEnergyScale();

  /**
   * Get target energy scale that will be used if no other scale is applied.
   * This must be pushed in with using setTargetEnergyScale to make it active.
   *
   * @return
   */
  default EnergyScale getDefaultEnergyScale()
  {
    return this.getAttribute(DEFAULT_ENERGY_SCALE, EnergyScale.class, null);
  }

  /**
   * Get the energy scale mapper
   *
   * @return
   */
  default EnergyScaleMapper getEnergyScaleMapper()
  {
    return this.getAttribute(ENERGY_SCALE_MAPPER, EnergyScaleMapper.class, null);
  }

  /** Access the peak tracker.  
   * 
   * This is for auditing purposes. Hopefully this is not needed anymore
   * as all of the data should be communicated in the CalibratorResult.
   * 
   * @return 
   */
  PeakTracker getPeakTracker();

  /**
   * The field energy scale is the scale needed to interpret the raw
   * measurement.
   *
   * Unlike the resulting calibrated spectrum, the energy scale for the measured
   * data shifts with time.
   *
   * @return
   */
  EnergyScale getFieldEnergyScale();


}
