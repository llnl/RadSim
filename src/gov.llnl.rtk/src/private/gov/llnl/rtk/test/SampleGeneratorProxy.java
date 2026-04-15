// --- file: gov/llnl/rtk/test/SampleGeneratorProxy.java ---
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
import java.util.List;
import gov.llnl.rtk.model.GammaSensorModel;

/**
 * Proxy class for executing the actions defined by another generator in a
 * different context.
 */
 class SampleGeneratorProxy implements SampleGenerator
{
  SampleGenerator parent;
  SampleGenerator proxy;
  DoubleSpectrum accumulator;

  SampleGeneratorProxy(SampleGenerator parent, SampleGenerator proxy)
  {
    this.parent = parent;
    this.proxy = proxy;
  }

  void addAction(SampleGenerator.Action action)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IntegerSpectrum drawInteger() throws SampleGenerator.SampleException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public DoubleSpectrum drawDouble() throws SampleGenerator.SampleException
  {
    this.accumulator = null;
    for (SampleGenerator.Action action : proxy.getActions())
    {
      accumulator = action.evaluate(this);
    }
    return accumulator;
  }

  @Override
  public DoubleSpectrum getAccumulator()
  {
    return accumulator;
  }

  @Override
  public void setParameter(String key, Object value)
  {
    parent.setParameter(key, value);
  }

  @Override
  public Object getParameter(String key)
  {
    Object out = parent.getParameter(key);
    if (out == null)
      out = proxy.getParameter(key);
    return out;
  }

  @Override
  public <Type> Type getParameter(String key, Class<Type> cls)
  {
    return cls.cast(this.getParameter(key));
  }

  @Override
  public List<Action> getActions()
  {
    return proxy.getActions();
  }

  @Override
  public RandomGenerator getRandomGenerator()
  {
    return parent.getRandomGenerator();
  }

  @Override
  public GammaSensorModel getGammaDetectorModel()
  {
    return parent.getGammaDetectorModel();
  }

}
