// --- file: gov/llnl/rtk/test/SampleGenerator.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.IntegerSpectrum;
import gov.llnl.rtk.data.Spectrum;
import gov.llnl.rtk.model.GammaSensorModel;
import java.io.Serializable;
import java.util.List;

/**
 * A sample generator is a factory for creating a random draw of a spectrum. It
 * is created using a builder to define a sequence of actions used to build up a
 * sample from a set of noise free expected samples.
 *
 * FIXME add scattering and pileup to the sample generator.
 *
 * @author nelson85
 */
public interface SampleGenerator extends Serializable
{
  /**
   * Create a new sample generator builder. This requires a gamma detector model
   * which hold parameters for how to create the sample such as the energy scale
   * and how to apply scattering and pileup.
   *
   * @param gdm is the gamma detector model for the sample.
   * @return a new builder.
   * @throws SampleGenerator.SampleException if the detector is not properly set
   * up. At minimum the detector must specify an energy scale.
   */
  static SampleGenerator.Builder newBuilder(GammaSensorModel gdm) throws SampleException
  {
    return new SampleGeneratorBuilderImpl(gdm);
  }

  /**
   * Create a random draw with Poisson statistics from this generator.
   *
   * @return
   * @throws SampleException
   */
  IntegerSpectrum drawInteger() throws SampleException;

  /**
   * Get the expected output of this generator without Poisson statistics.
   *
   * @return
   * @throws SampleException
   */
  DoubleSpectrum drawDouble() throws SampleException;

//<editor-fold desc="parameters" defaultstate="collapsed">
  /**
   * Set a parameter for the generator. Parameters are used for creating a
   * parameterized generator in which the source spectrum can be altered each
   * draw.
   *
   * @param key
   * @param value
   */
  void setParameter(String key, Object value);

  /**
   * Get a parameter for the generator.
   *
   * @param key is the parameter name.
   * @return the parameter or null if not set.
   */
  Object getParameter(String key);

  /**
   * Get a parameter from the generator with a specified type.
   *
   * @param <Type>
   * @param key is the parameter name.
   * @param cls is the class of the parameter requested.
   * @return the parameter or null if the parameter is not set.
   * @throws ClassCastException if the parameter type is different than
   * requested.
   */
  <Type> Type getParameter(String key, Class<Type> cls);
//</editor-fold>
//<editor-fold desc="audit" defaultstate="collapsed">

  /**
   * Get the list of actions that will be preformed. This is mainly for
   * auditing.
   *
   * @return
   */
  List<Action> getActions();

  DoubleSpectrum getAccumulator();

  /**
   * Get the random number generator used by this sample generator. Random
   * number generator is set by the builder.
   *
   * @return
   */
  RandomGenerator getRandomGenerator();

  public GammaSensorModel getGammaDetectorModel();

//</editor-fold>
//<editor-fold desc="interfaces" defaultstate="collapsed">
  /**
   * An action is an operation on the accumulated spectrum.
   *
   */
  public interface Action
  {
    DoubleSpectrum evaluate(SampleGenerator sampleGenerator) throws SampleException;
  }

  /**
   * A special action that produces a new spectrum. Not all actions produce a
   * spectrum, so we will specialize the interface.
   */
  public interface Producer extends Action
  {
    @Override
    DoubleSpectrum evaluate(SampleGenerator sampleGenerator) throws SampleException;
  }
//</editor-fold>
//<editor-fold desc="builder" defaultstate="collapsed">

  public interface BuilderCreate
  {
    SampleGenerator create() throws SampleException;
  }

  public interface BuilderTerminal extends BuilderCreate
  {
    BuilderTerminal miscalibrate(double offsetStd, double gainStd);
  }

  /**
   * Factory for producing a sample generator. Builds up a sequence of actions
   * to create a sample generator. The order of the operations is important in
   * determine the final outcome.
   *
   * @author nelson85
   */
  public interface Builder extends BuilderTerminal
  {
//<editor-fold desc="add/include" defaultstate="collapsed">

    /**
     * Add a spectrum to the sample. The total setTime is increased. Does not
     * adjust the counts.
     *
     * @param <T> is one of String, DoubleSpectrum, or SampleGenerator.
     * @param producer
     * @return the builder for chaining.
     */
    <T> Builder add(T producer);

    /**
     * Include a spectrum in the sample. The spectrum is scaled to match the
     * targets setTime. Does not increase the total setTime in the sample.
     * Adjusts the energy bins as needed.
     *
     * @param <T> is one of String, DoubleSpectrum, or SampleGenerator.
     * @param producer
     * @return the builder for chaining.
     */
    <T> Builder include(T producer);

    /**
     * Include a spectrum in the sample. The spectrum is scaled to match the
     * targets setTime. Does not increase the total setTime in the sample.
     * Adjusts the energy bins as needed.
     *
     * @param <T> is one of String, DoubleSpectrum, or SampleGenerator.
     * @param producer
     * @return the builder for chaining.
     */
    <T> Builder include(T producer, ScalingRule sr);

//</editor-fold>
//<editor-fold desc="scaling" defaultstate="collapsed">
    /**
     * ScaleCounts the spectrum by a factor. Does not adjust the times.
     *
     * @param factor is the factor to multiply counts.
     * @return the builder for chaining.
     */
    Builder scaleCounts(Number factor);

    /**
     * Scale the spectrum by a factor. Time and counts are scaled.
     *
     * @param factor is the factor to multiply time and counts.
     * @return the builder for chaining.
     */
    Builder scaleTime(Number factor);

    /**
     * Adjust the rate of a sample. The total counts will be the rate times the
     * setTime. Time is preserved, counts are adjusted.
     *
     * @param rate
     * @return the builder for chaining.
     */
    Builder scaleToRate(Number rate);

    /**
     * Adjust the counts in a sample without changing the realtime or livetime.
     *
     * @param count
     * @return the builder for chaining.
     */
    Builder scaleToCounts(Number count);
//</editor-fold>
//<editor-fold desc="time" defaultstate="collapsed">

    /**
     * Adjust the time of a sample to match.
     *
     * Counts are scaled to maintain the count rate.  This is a scale to
     * a fixed time.  ScaleTime is a different concept that directly multiplies
     * a sample time by a factor, rather than relative to the existing time.
     *
     * @param time
     * @return
     */
    Builder adjustTime(Number time);

    /**
     * Change the time of the sample. This does not adjust the counts. The
     * realtime and livetime will be adjusted accordingly. This should always be
     * called prior to calling scaleToRate.
     *
     * @param time
     * @return the builder for chaining.
     */
    Builder setTime(Number time);

//</editor-fold>
//<editor-fold desc="distance" defaultstate="collapsed">
    /**
     * For the usual 1/r^2 behavior with distance.
     * @param key
     * @return
     */
    Builder distance(Number key);

    /**
     * For unusual (1/r)^power behavior with distance
     * @param key
     * @param power "-2" would be the usual 1/r^2
     * @return
     */
    Builder distance(Number key, Number power);
//</editor-fold>

//<editor-fold desc="post" defaultstate="collapsed">
    /**
     * Apply a random calibration error to the sample. This should be applied
     * last after all spectrum have been added so that every spectrum in the
     * sample has the same miscalibration.
     *
     * @param offsetStd is the standard deviation of the offset (keV).
     * @param gainStd in the standard deviation of the gain (unitless).
     * @return the builder for chaining.
     */
    @Override
    BuilderTerminal miscalibrate(double offsetStd, double gainStd);

//</editor-fold>
    /**
     * Create sample generator. Clears the existing sample generator builder for
     * reuse.
     *
     * @return the sample generator produced by the builder.
     * @throws SampleException
     */
    @Override
    SampleGenerator create() throws SampleException;

    /**
     * Set the random number generator for creating samples. This is used to
     * create the same sequence of samples.
     *
     * @param rng
     */
    void setRandomGenerator(RandomGenerator rng);

    /**
     * Sets the output to have Poisson statistics even it drawn as a double.
     *
     * @param usePoisson is true if a Poisson sample must be created.
     * @return
     */
    Builder poisson(boolean usePoisson);
  }

//</editor-fold>
//<editor-fold desc="scaling rule" defaultstate="collapsed">
  public interface ScalingRule
  {
    double computeScale(Spectrum sample, Spectrum accumulator) throws SampleGenerator.SampleException;
  }

  @SuppressWarnings("Convert2Lambda")
  static ScalingRule byWeigthedSNR(final double target)
  {
    return new ScalingRule()
    {
      @Override
      public double computeScale(Spectrum sample, Spectrum accumulator)
      {
        double current = gov.llnl.rtk.test.SampleGeneratorUtilities.computeWeightedSNR(sample, accumulator);
        return target / current;
      }
    };

  }

  @SuppressWarnings("Convert2Lambda")
  static ScalingRule byTime()
  {
    return new ScalingRule()
    {
      @Override
      public double computeScale(Spectrum sample, Spectrum accumulator) throws SampleGenerator.SampleException
      {
        double lt = accumulator.getLiveTime();
        double lt2 = sample.getLiveTime();
        if (lt == 0)
          throw new SampleGenerator.SampleException("Livetime must be set on accumulator");
        return lt / lt2;
      }
    };
  }

//</editor-fold>
//<editor-fold desc="exceptions" defaultstate="collapsed">
  public class SampleException extends Exception
  {
    public SampleException(String msg)
    {
      super(msg);
    }

    public SampleException(String msg, Throwable ex)
    {
      super(msg, ex);
    }

    public SampleException(Throwable ex)
    {
      super(ex);
    }
  }
//</editor-fold>
}
