// --- file: gov/llnl/rtk/calibration/ExternalCalibration.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.EnergyScaleException;
import gov.llnl.rtk.RtkPackage;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.RadiationSensor;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.quality.FaultSetImpl;
import gov.llnl.rtk.quality.FaultSet;
import gov.llnl.rtk.quality.QualityCheck;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.InitializeException;
import gov.llnl.utility.xml.bind.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author seilhan3
 */
@Reader.Declaration(pkg = RtkPackage.class, name = "externalCalibration", referenceable = true)
public class ExternalCalibration extends ExpandableObject implements DetectorCalibrator
{
  private RadiationSensor sensor = null;
  private EnergyScale fieldEnergyScale;
  private EnergyScale targetEnergyScale;

  private final List<QualityCheck> qualityChecks = new ArrayList<>();

    @Override
  public void setSensor(RadiationSensor sensor)
  {
    this.sensor = sensor;
  }
  
  @Override
  public void setRandomGenerator(RandomGenerator randomGenerator)
  {

  }

  @Override
  public void setTargetEnergyScale(EnergyScale energyScale) throws EnergyScaleException
  {
    targetEnergyScale = energyScale;
    this.fieldEnergyScale = energyScale;
  }

  @Override
  public void incorporate(IntegerSpectrum rawSpectrum)
  {
    this.fieldEnergyScale = rawSpectrum.getEnergyScale();
  }

  
  @Override
  public void initialize() throws InitializeException
  {
  }


  @Override
  public CalibratorResult applyCalibration(Spectrum input)
  {
    // Set up a result
    IntegerSpectrum outSpectrum = new IntegerSpectrum(input);
    outSpectrum.setStartTime(input.getStartTime());
    outSpectrum.setEnergyScale(input.getEnergyScale());
    return new CalibratorResult(this.sensor, outSpectrum, true);
  }
  
  @Override
  public void reset()
  {
  }

  @Override
  public EnergyScale getTargetEnergyScale()
  {
    return targetEnergyScale;
  }

  @Override
  public PeakTracker getPeakTracker()
  {
    return null;
  }

  @Override
  public EnergyScale getFieldEnergyScale()
  {
    return fieldEnergyScale;
  }

  @Override
  public FaultSet getFaultSet()
  {
    return new FaultSetImpl();
  }

  @Override
  public List<QualityCheck> getQualityChecks()
  {
    return qualityChecks;
  }


}
