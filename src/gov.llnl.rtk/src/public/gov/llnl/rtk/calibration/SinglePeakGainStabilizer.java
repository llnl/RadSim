// --- file: gov/llnl/rtk/calibration/SinglePeakGainStabilizer.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.IntegerRebin;
import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.EnergyScaleException;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.EnergyScaleFactory;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.RadiationSensor;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.quality.FaultSetImpl;
import gov.llnl.rtk.quality.FaultSet;
import gov.llnl.rtk.quality.QualityCheck;
import gov.llnl.utility.Copyable;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.InitializeException;
import gov.llnl.utility.Serializer;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author nelson85
 */
@ReaderInfo(SinglePeakGainStabilizerReader.class)
public class SinglePeakGainStabilizer extends ExpandableObject implements DetectorCalibrator, Copyable
{
  // Parameters
  private RadiationSensor sensor = null;
  private StabilizerTarget stabilizerTarget;
  private PeakTracker peakTracker;
  private IntegerRebin integerRebin = new IntegerRebin();
  private EnergyScale targetEnergyScale;
  private FaultSet faultSet = new FaultSetImpl();

  @Override
  public void setSensor(RadiationSensor sensor)
  {
    this.sensor = sensor;
  }

  @Override
  public void initialize() throws InitializeException
  {
    if (this.stabilizerTarget == null)
      throw new InitializeException("Target is not set");

    if (this.peakTracker != null)
    {
      this.peakTracker.setTarget(getStabilizerTarget());
      this.peakTracker.initialize();
    }

    if (targetEnergyScale == null)
    {
      throw new InitializeException("Target energy scale not set");
    }
  }

  @Override
  public void setRandomGenerator(RandomGenerator rg)
  {
    integerRebin.setRandomGenerator(rg);
  }

  @Override
  public EnergyScale getFieldEnergyScale()
  {
    //FIXME: Does this need to be the other direction?
    double ratio = getStabilizerTarget().getChannel() / peakTracker.getPeakLocation();
    return EnergyScaleFactory.newScaledScale(targetEnergyScale, ratio);
  }

  @Override
  public EnergyScale getTargetEnergyScale()
  {
    return targetEnergyScale;
  }

  @Override
  public FaultSet getFaultSet()
  {
    return this.faultSet;
  }

  @Override
  public List<QualityCheck> getQualityChecks()
  {
    return Collections.emptyList();
  }

  @Override
  public Result applyCalibration(Spectrum input)
  {
    // Fail, if the result is null
    if (input == null)
      return null;

    IntegerSpectrum is = new IntegerSpectrum(input);

    // Get the peak location from the tracker
    double location = peakTracker.getPeakLocation();
    boolean locked = peakTracker.isLocked();

    // If the peak location is valid
    if (locked)
    {
      // Perform a gain adjustment
      is = new IntegerSpectrum(
              integerRebin.scale(is.toArray(), this.stabilizerTarget.getChannel() / location),
              is.getLiveTime(),
              is.getRealTime()
      );
      is.setValidRange(input.getMinimumValidChannel(), input.getMaximumValidChannel());
      is.copyAttributes(input);
    }
    // FIXME figure out how to get attributes to pass through
    is.setEnergyScale(targetEnergyScale);
    return new Result(this.sensor, is, locked, location);
  }

  @Override
  public void incorporate(IntegerSpectrum input)
  {
    peakTracker.incorporate(input);
  }

  @Override
  public void reset()
  {
    peakTracker.clearHistory();
  }

  /**
   * Set target energy or channel to stabilize to.
   *
   * @param target
   */
  public void setStabilizerTarget(StabilizerTarget target)
  {
    this.stabilizerTarget = target;
  }

  @Override
  public void setTargetEnergyScale(EnergyScale scale) throws EnergyScaleException
  {
    stabilizerTarget.setTargetEnergyScale(scale);
    this.targetEnergyScale = scale;
  }

  /**
   * @return the stabilizerTarget
   */
  public StabilizerTarget getStabilizerTarget()
  {
    return stabilizerTarget;
  }

  public void setPeakTracker(PeakTracker pt)
  {
    this.peakTracker = pt;
  }

  @Override
  public PeakTracker getPeakTracker()
  {
    return peakTracker;
  }

  @Override
  public Object copyOf()
  {
    try
    {
      return Serializer.copy(this);
    }
    catch (IOException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public static class Result extends CalibratorResult implements Serializable
  {
    private final double peakLocation;

    public Result(RadiationSensor sensor, IntegerSpectrum is, boolean locked, double location)
    {
      super(sensor, is, locked);
      this.peakLocation = location;
    }

    /**
     * @return the peakLocation
     */
    public double getPeakLocation()
    {
      return peakLocation;
    }
  }

}
