// --- file: gov/llnl/rtk/test/SampleGeneratorImpl.java ---
/*
 * Copyright 2017, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.model.GammaPileupModel;
import java.util.ArrayList;
import java.util.List;
import gov.llnl.rtk.model.GammaSensorModel;
import gov.llnl.utility.ArrayMap;
import java.util.Map;

//<editor-fold desc="impl">
class SampleGeneratorImpl implements SampleGenerator
{
  final RandomGenerator randomGenerator;
  DoubleSpectrum accumulator;
  Map<String, Object> parameters = null;
  SampleGeneratorActionPoissonDraw draw;
  final ArrayList<SampleGenerator.Action> actions;
  private final GammaSensorModel gammaModel;

  SampleGeneratorImpl(GammaSensorModel gammaModel, RandomGenerator randomGenerator, List<Action> actions)
          throws SampleException
  {
    this.gammaModel = gammaModel;
    this.randomGenerator = randomGenerator;
    this.actions = new ArrayList<>(actions);
    this.draw = new SampleGeneratorActionPoissonDraw(randomGenerator);
  }

  @Override
  public IntegerSpectrum drawInteger() throws SampleGenerator.SampleException
  {
    GammaPileupModel pm = gammaModel.getPileupModel();
    createSample();

    // Apply any pileup to distort the spectrum here
    // FIXME
    // Convert the sample to an integer spectrum
    IntegerSpectrum sample = SampleGeneratorUtilities.convert(draw.evaluate(this));

    // Apply the post draw actions to compute the livetime
    if (pm != null)
    {
      sample.setLiveTime(pm.computeLiveTime(sample));
    }
    return sample;
  }

  @Override
  public DoubleSpectrum drawDouble() throws SampleGenerator.SampleException
  {
    createSample();
    if (this.getParameter("poisson", Boolean.class) == true)
    {
      return draw.evaluate(this);
    }
    return accumulator;
  }

  void createSample() throws SampleException
  {
    // Start a new accumulation
    this.accumulator = null;

    // Execute the actions.
    for (SampleGenerator.Action action : actions)
    {
      accumulator = action.evaluate(this);
    }
  }

  @Override
  public DoubleSpectrum getAccumulator()
  {
    return accumulator;
  }

  @Override
  public void setParameter(String key, Object value)
  {
    if (parameters == null)
      parameters = new ArrayMap<>();
    this.parameters.put(key, value);
  }

  @Override
  public Object getParameter(String key)
  {
    if (parameters == null)
      return null;
    return this.parameters.get(key);
  }

  @Override
  public <Type> Type getParameter(String key, Class<Type> cls)
  {
    return cls.cast(this.getParameter(key));
  }

  @Override
  public List<Action> getActions()
  {
    return actions;
  }

  @Override
  public RandomGenerator getRandomGenerator()
  {
    return this.randomGenerator;
  }

  @Override
  public GammaSensorModel getGammaDetectorModel()
  {
    return this.gammaModel;
  }

}
