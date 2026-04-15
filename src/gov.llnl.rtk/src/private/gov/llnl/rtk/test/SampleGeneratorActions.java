// --- file: gov/llnl/rtk/test/SampleGeneratorActions.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.RebinUtilities;
import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.test.SampleGenerator.Action;
import gov.llnl.rtk.test.SampleGenerator.SampleException;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.data.SpectrumAttributes;
import java.util.List;
import gov.llnl.rtk.model.GammaSensorModel;

/**
 *
 * @author nelson85
 */
 class SampleGeneratorActions
{

  /**
   * Adds a sample in. Increases the realtime and livetime.
   */
  static public class Add implements SampleGenerator.Producer
  {
    SampleGenerator.Producer action;

    Add(SampleGenerator.Producer action)
    {
      this.action = action;
    }

    @Override
    public DoubleSpectrum evaluate(SampleGenerator generator)
            throws SampleGenerator.SampleException
    {
      try
      {
        DoubleSpectrum accumulator = generator.getAccumulator();
        DoubleSpectrum sample = action.evaluate(generator);
        if (sample.getEnergyScale() == null)
          throw new SampleException("Unable to add spectrum without energy scale");
        sample.rebinAssign(generator.getGammaDetectorModel().getEnergyScale());
        if (accumulator == null)
        {
          if (sample.getAttribute(SpectrumAttributes.DISTANCE)==null)
            sample.setAttribute(SpectrumAttributes.DISTANCE, 1.0);
          return new DoubleSpectrum(sample);
        }
        return accumulator.addAssign(sample);
      }
      catch (RebinUtilities.RebinException ex)
      {
        throw new SampleGenerator.SampleException("Unable to rebin spectrum", ex);
      }
    }
  }

//<editor-fold desc="producers"  defaultstate="collapsed">
  static public class Deferred implements SampleGenerator.Producer
  {
    String label;

    Deferred()
    {
    }

    Deferred(String label)
    {
      this.label = label;
    }

    @Override
    public DoubleSpectrum evaluate(SampleGenerator generator)
            throws SampleGenerator.SampleException
    {
      DoubleSpectrum sample = generator.getParameter(label, DoubleSpectrum.class);
      if (sample == null)
        throw new SampleGenerator.SampleException("Unable to find parameter " + label + " as DoubleSpectrum");
      return sample;
    }
  }

  static public class Fixed implements SampleGenerator.Producer
  {
    Spectrum<double[]> sample;

    Fixed(Spectrum<double[]> sample)
    {
      if (sample == null)
        throw new NullPointerException();
      this.sample = sample;
    }

    @Override
    public DoubleSpectrum evaluate(SampleGenerator generator)
            throws SampleGenerator.SampleException
    {
      return new DoubleSpectrum(sample);
    }
  }

  static public class FixedInteger implements SampleGenerator.Producer
  {
    DoubleSpectrum doubleSpectrum;

    FixedInteger(Spectrum<int[]> sample)
    {
      if (sample == null)
        throw new NullPointerException();
      double[] doubles = sample.toDoubles();
      doubleSpectrum = new DoubleSpectrum(doubles);
      doubleSpectrum.setEnergyScale(sample.getEnergyScale());
    }

    @Override
    public DoubleSpectrum evaluate(SampleGenerator generator)
            throws SampleGenerator.SampleException
    {
      return doubleSpectrum;
    }
  }

  static public class Proxy implements SampleGenerator.Producer
  {
    SampleGenerator sg;

    Proxy(SampleGenerator generator)
    {
      this.sg = generator;
    }

    @Override
    public DoubleSpectrum evaluate(SampleGenerator generator) throws SampleGenerator.SampleException
    {
      SampleGeneratorProxy sg2 = new SampleGeneratorProxy(generator, sg);
      return sg2.drawDouble();
    }
  }

//</editor-fold>
//<editor-fold desc="generator" defaultstate="collapsed">
  public static class SampleGeneratorProxy implements SampleGenerator
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
      return proxy.getRandomGenerator();
    }

    @Override
    public GammaSensorModel getGammaDetectorModel()
    {
      return proxy.getGammaDetectorModel();
    }
  }
//</editor-fold>
}
