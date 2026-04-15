// --- file: gov/llnl/rtk/test/SampleGeneratorBuilderImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.DoubleUtilities;
import gov.llnl.math.random.RandomFactory;
import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.SpectrumAttributes;
import gov.llnl.rtk.test.SampleGeneratorActions.Add;
import gov.llnl.rtk.test.SampleGeneratorActions.Deferred;
import gov.llnl.rtk.test.SampleGeneratorActions.Fixed;
import gov.llnl.rtk.test.SampleGeneratorActions.FixedInteger;
import gov.llnl.rtk.test.SampleGeneratorActions.Proxy;
import gov.llnl.rtk.model.GammaSensorModel;
import gov.llnl.rtk.physics.Quantity;
import gov.llnl.rtk.test.SampleGenerator.Producer;
import gov.llnl.rtk.test.SampleGenerator.ScalingRule;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nelson85
 */
 class SampleGeneratorBuilderImpl implements SampleGenerator.Builder
{
  final GammaSensorModel gammaModel;
  RandomGenerator randomGenerator = RandomFactory.getDefaultGenerator();
  final List<SampleGenerator.Action> actions = new LinkedList<>();
  boolean poisson = false;

  public SampleGeneratorBuilderImpl(GammaSensorModel gammaModel) throws SampleGenerator.SampleException
  {
    this.gammaModel = gammaModel;

    if (this.gammaModel.getEnergyScale() == null)
      throw new SampleGenerator.SampleException("Detector energy scale is not set");
  }

  protected Producer createProducer(Object obj)
  {
    if (obj instanceof String)
      return new Deferred((String) obj);
    if (obj instanceof Producer)
      return (Producer) obj;
    if (obj instanceof DoubleSpectrum)
      return new Fixed((DoubleSpectrum) obj);
    if (obj instanceof IntegerSpectrum)
      return new FixedInteger((IntegerSpectrum) obj);
    if (obj instanceof SampleGenerator)
      return new Proxy((SampleGenerator) obj);
    throw new UnsupportedOperationException("Unknown type "+obj.getClass());
  }

  @Override
  public <T> SampleGeneratorBuilderImpl add(T producer)
  {
    actions.add(new Add(this.createProducer(producer)));
    return this;
  }

  @Override
  public <T> SampleGeneratorBuilderImpl include(T producer)
  {
    actions.add(new SampleGeneratorActionInclude(this.createProducer(producer), SampleGenerator.byTime()));
    return this;
  }

  @Override
  public <T> SampleGeneratorBuilderImpl include(T producer, ScalingRule sr)
  {
    actions.add(new SampleGeneratorActionInclude(this.createProducer(producer), sr));
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl adjustTime(Number time)
  {
    actions.add(new SampleGenerator.Action()
    {
      @Override
      public DoubleSpectrum evaluate(SampleGenerator sampleGenerator) throws SampleGenerator.SampleException
      {
        double time0 = time.doubleValue();
        DoubleSpectrum accumulator = sampleGenerator.getAccumulator();
        double factor = time0 / accumulator.getLiveTime();
        accumulator.multiplyAssign(factor);
        accumulator.setRealTime(time0);
        accumulator.setLiveTime(time0);
        return accumulator;
      }
    });
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl setTime(Number time)
  {
    actions.add(new SampleGeneratorActionSetTimes(time, time));
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl scaleCounts(Number factor)
  {
    actions.add(new SampleGeneratorActionScaleCounts(factor));
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl scaleTime(Number factor)
  {
    actions.add(new SampleGeneratorActionScaleAll(factor));
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl scaleToRate(Number rate)
  {
    actions.add(new SampleGeneratorActionScaleRate(rate));
    return this;
  }

//  /**
//   * Set the binning structure of the drawn spectrum.
//   *
//   * @param bins are the energy bins for the resulting spectrum.
//   * @return the builder for chaining.
//   */
//  @Override
//  public SampleGeneratorBuilder bins(EnergyScale bins)
//  {
//    actions.add(new ApplyBins(bins));
//    return this;
//  }
  /**
   * Apply a random calibration error to the sample. This should be applied last
   * after all spectrum have been added so that every spectrum in the sample has
   * the same miscalibration.
   *
   * @param offsetStd is the standard deviation of the offset (keV).
   * @param gainStd in the standard deviation of the gain (unitless).
   * @return the builder for chaining.
   */
  @Override
  public SampleGeneratorBuilderImpl miscalibrate(double offsetStd, double gainStd)
  {
    actions.add(new SampleGeneratorActionMiscalibrate(offsetStd, gainStd));
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl scaleToCounts(Number counts)
  {
    actions.add(new SampleGeneratorActionSetCounts(counts));
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl distance(Number distance, Number power)
  {
    if (distance == null)
      throw new NullPointerException("distance is zero");

    actions.add(new SampleGenerator.Action()
    {
      @Override
      public DoubleSpectrum evaluate(SampleGenerator sampleGenerator) throws SampleGenerator.SampleException
      {
        DoubleSpectrum accumulator = sampleGenerator.getAccumulator();
        Number distance0 = accumulator.getAttribute(SpectrumAttributes.DISTANCE, Quantity.class).get();
        if (distance0 ==null)
          throw new NullPointerException("Accumulator distance is not set");

        double factor = 1.;
        double ratio = distance0.doubleValue() / distance.doubleValue();
        if (power.equals(-2))
          factor = DoubleUtilities.sqr(ratio);
        else
          factor = Math.pow(ratio, -power.doubleValue());

        double liveTime = accumulator.getLiveTime();
        double realTime = accumulator.getRealTime();
        accumulator.multiplyAssign(factor);
        accumulator.setLiveTime(liveTime);
        accumulator.setRealTime(realTime);

        return accumulator;
      }
    });
    return this;
  }

  @Override
  public SampleGeneratorBuilderImpl distance(Number distance)
  {
    return distance(distance, -2);
  }

  public SampleGeneratorBuilderImpl poisson(boolean use)
  {
    this.poisson = use;
    return this;
  }

  @Override
  public SampleGenerator create() throws SampleGenerator.SampleException
  {
    SampleGeneratorImpl sg = new SampleGeneratorImpl(this.gammaModel, this.randomGenerator, this.actions);
    actions.clear();
    sg.setParameter("poisson", poisson);
    poisson = false;
    return sg;
  }

  @Override
  public void setRandomGenerator(RandomGenerator rng)
  {
    this.randomGenerator = rng;
  }

}
