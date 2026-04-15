// --- file: gov/llnl/rtk/test/SampleGeneratorActionPoissonDraw.java ---
/*
 * Copyright 2018, Lawrence Livermore National Security, LLC.
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.test;

import gov.llnl.math.random.PoissonRandom;
import gov.llnl.math.random.RandomGenerator;
import gov.llnl.rtk.data.DoubleSpectrum;

/**
 *
 * @author nelson85
 */
 class SampleGeneratorActionPoissonDraw implements SampleGenerator.Action
{
  RandomGenerator generator;
  PoissonRandom pois;

  SampleGeneratorActionPoissonDraw(RandomGenerator generator)
  {
    this.generator = generator;
    this.pois = new PoissonRandom(generator);
  }

  @Override
  public DoubleSpectrum evaluate(SampleGenerator generator)
  {
    DoubleSpectrum accumulator = generator.getAccumulator();
    if (accumulator.getAttribute("poisson", Boolean.class, false))
      return accumulator;
    double[] out = accumulator.toArray();
    for (int i = 0; i < out.length; ++i)
    {
      out[i] = pois.draw(out[i]);
    }
    accumulator.setOverRange((double) pois.draw(accumulator.getOverRangeCounts()));
    accumulator.setUnderRange((double) pois.draw(accumulator.getUnderRangeCounts()));
    accumulator.setAttribute("poisson", true);
    return accumulator;
  }

  void setRandomGenerator(RandomGenerator randomGenerator)
  {
    this.pois.setGenerator(randomGenerator);
  }

}
