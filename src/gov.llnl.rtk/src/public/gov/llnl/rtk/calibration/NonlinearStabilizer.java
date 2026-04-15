// --- file: gov/llnl/rtk/calibration/NonlinearStabilizer.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

import gov.llnl.math.MathExceptions;
import gov.llnl.math.random.RandomGenerator;
import gov.llnl.math.spline.Spline;
import gov.llnl.rtk.EnergyScaleException;
import static gov.llnl.rtk.calibration.NonlinearStabilizerUtilities.computeIntegratedPower;
import static gov.llnl.rtk.calibration.NonlinearStabilizerUtilities.correctPairs;
import static gov.llnl.rtk.calibration.NonlinearStabilizerUtilities.scalePairs;
import gov.llnl.rtk.data.EnergyPairsScale;
import gov.llnl.rtk.data.EnergyScale;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.RadiationSensor;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.data.SpectrumAttributes;
import gov.llnl.rtk.data.EnergyPairsScaleImpl;
import gov.llnl.rtk.quality.FaultSetImpl;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.quality.FaultSet;
import gov.llnl.rtk.quality.QualityCheck;
import gov.llnl.utility.ExpandableObject;
import gov.llnl.utility.InitializeException;
import gov.llnl.utility.annotation.Debug;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Rebinning stabilizer.
 *
 * This can be used to linearize spectrum, deal with power or deal with
 * temperature dependent effects.
 *
 * @author nelson85
 */
@ReaderInfo(NonlinearStabilizerReader.class)
public class NonlinearStabilizer extends ExpandableObject implements DetectorCalibrator
{
  NonlinearityMap temperatureMap = null;
  NonlinearityMap rateMap = null;
  int channels = -1;
  double[] inputEdges; // Remove declaration after this is finalized
  StabilizerTarget target;
  PeakTracker peakTracker;
  EnergyScale targetEnergyScale;
  RadiationSensor sensor = null;
  NonlinearRebin rebin = new NonlinearRebin();

//<editor-fold desc="loader">
  @Override
  public void setSensor(RadiationSensor sensor)
  {
    this.sensor = sensor;
  }

  public void setTemperatureMap(NonlinearityMap nm)
  {
    this.temperatureMap = nm;
  }

  public void setRateMap(NonlinearityMap nm)
  {
    this.rateMap = nm;
  }

  /**
   * Sets the number of channels to produce in the output spectrum.
   *
   * @param channels is the number of channels or -1 if the input should match
   * the output.
   */
  @Reader.Attribute(name = "channels")
  public void setChannels(int channels)
  {
    this.channels = channels;
  }

//</editor-fold>
//<editor-fold desc="stabilizer" default_state="collapsed">
  @Override
  public void initialize() throws InitializeException
  {
    if (target == null)
    {
      throw new InitializeException("Target is not set");
    }
    if (this.peakTracker != null)
    {
      this.peakTracker.setTarget(target);
      this.peakTracker.initialize();
    }
  }

  @Override
  public CalibratorResult applyCalibration(Spectrum measurement)
  {
    if (measurement == null)
    {
      return null;
    }

    // Copy the data so that we do not mess up the original.
    IntegerSpectrum is = new IntegerSpectrum(measurement);

//    CalibrationSensor det = detector;
//    int overrangeChannels = det.getOverRangeChannels();
//    int underrangeChannels = det.getUnderRangeChannels();
//        // Clear the out of range channels
//    if (overrangeChannels > 0)
//      is.clearOverRange(overrangeChannels);
//    if (underrangeChannels > 0)
//      is.clearUnderRange(underrangeChannels);
    double location = peakTracker.getPeakLocation();
    boolean locked = peakTracker.isLocked();
    if (locked)
    {
      is = this.correct(is, target.getChannel() / location);
    }

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

  @Override
  public void setTargetEnergyScale(EnergyScale scale) throws EnergyScaleException
  {
    target.setTargetEnergyScale(scale);
    this.targetEnergyScale = scale;
  }

  @Override
  public EnergyScale getTargetEnergyScale()
  {
    return targetEnergyScale;
  }

  public void setTarget(StabilizerTarget target)
  {
    this.target = target;
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
  public void setRandomGenerator(RandomGenerator rg)
  {
    this.rebin.setGenerator(rg);
  }
//<editor-fold desc="internal">  

  @Override
  public EnergyScale getFieldEnergyScale()
  {
    // Unknown at this time
    return null;
  }

  @Override
  public FaultSet getFaultSet()
  {
    return new FaultSetImpl();
  }

  @Override
  public List<QualityCheck> getQualityChecks()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

//</editor-fold>
//</editor-fold>
//<editor-fold desc="correction" defaultstate="collapsed">  
  /**
   *
   * @param spectrum
   * @param gain is the gain computed by the peak tracking during periods of
   * background. It will be used to correct of the gross gain change.
   * @return the corrected integer spectrum
   */
  public IntegerSpectrum correct(IntegerSpectrum spectrum, double gain)
  {
    try
    {
      // number of edges in the input 
      int N = spectrum.size() + 1;

      inputEdges = new double[N];
      for (int i = 0; i < N; ++i)
      {
        inputEdges[i] = i;
      }

      // Construct a channel to channel correction for the temperature
      if (temperatureMap != null && spectrum.hasAttribute(SpectrumAttributes.TEMPERATURE))
      {
        Quantity temperature = spectrum.getAttribute(SpectrumAttributes.TEMPERATURE, Quantity.class);
        Spline correction = temperatureMap.getCorrectionSpline(temperature.get());
        inputEdges = correction.evaluateRangeOrdered(inputEdges, 0, N);
      }

      // Construct a channel to channel correction for the rate
      if (rateMap != null)
      {
        // Compute the observable power=(channel counts * channel)/livetime
        double power = computeIntegratedPower(spectrum);
        Spline correction = rateMap.getCorrectionSpline(power);
        inputEdges = correction.evaluateRangeOrdered(inputEdges, 0, N);
      }

      // Apply the gain factor computed externally based on background data
      for (int i = 0; i < inputEdges.length; ++i)
      {
        inputEdges[i] *= gain;
      }
      IntegerSpectrum output = this.rebin.rebin(spectrum, inputEdges);

      // Deal with computing the valid range in the resulting spectrum
      return output;
    }
    catch (ClassCastException ex)
    {
      throw new RuntimeException("Temperature attribute was incorrect type, expected Double", ex);
    }
    catch (MathExceptions.DomainException ex)
    {
      throw new RuntimeException("Exceeded spline domain in interpolation", ex);
    }
  }

  public EnergyPairsScale correctEnergyScale(EnergyPairsScale scale, double temperature, double integratedPower)
  {
    // Copy the scale to a new list
    List<ChannelEnergyPair> pairs = new ArrayList<>(scale);

    if (scale.getChannels() != channels)
      pairs = scalePairs(pairs, ((double) channels) / scale.getChannels());

    if (temperatureMap != null)
    {
      // Apply the temperature correction
      Spline correction = temperatureMap.getCorrectionSpline(temperature);
      pairs = correctPairs(pairs, correction);
    }
    if (rateMap != null)
    {
      Spline correction = rateMap.getCorrectionSpline(integratedPower);
      pairs = correctPairs(pairs, correction);
    }

    if (scale.getChannels() != channels)
      pairs = scalePairs(pairs, scale.getChannels() / ((double) channels));

    EnergyPairsScaleImpl out = new EnergyPairsScaleImpl(scale.getChannels(), pairs);
    out.copyAttributes(scale);
    return out;
  }

//</editor-fold>
//<editor-fold desc="inner classes" defaultstate="collapsed">  
  public static class Result extends CalibratorResult
  {
    private double peakLocation;

    public Result(RadiationSensor sensor, IntegerSpectrum spectrum, boolean locked, double peakLocation)
    {
      super(sensor, spectrum, locked);
      this.peakLocation = peakLocation;
    }

    /**
     * @return the peakLocation
     */
    public double getPeakLocation()
    {
      return peakLocation;
    }
  }
//</editor-fold>  
}
