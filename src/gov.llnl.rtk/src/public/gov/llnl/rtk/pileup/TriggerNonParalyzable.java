// --- file: gov/llnl/rtk/pileup/TriggerNonParalyzable.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.pileup;

import gov.llnl.math.random.PoissonRandom;
import gov.llnl.math.random.RandomGenerator;

/**
 *
 * @author nelson85
 */
public class TriggerNonParalyzable implements TriggerModel
{
  double tau;

  public TriggerNonParalyzable(double tau)
  {
    this.tau = tau;
  }

  @Override
  public double getTau()
  {
    return tau;
  }

  @Override
  public int drawCounts(RandomGenerator generator, double rate, double time)
  {
    // This computes the total counts observed
    double P0 = rate * (1 + rate * tau);
    double P1 = 1 / (1 + rate * tau);
    return (int) (PoissonRandom.draw(generator, time * P0) * P1 * P1 + generator.nextDouble());
  }

  @Override
  public void drawDistribution(int[] output, RandomGenerator generator, double rate, double time)
  {
    double counts = rate * time;
    double nt = rate * tau;
    double P = Math.exp(-nt);
    double k = (1 + rate * tau);
    double rest = 1;
    for (int i = 0; i < output.length - 1; ++i)
    {
      output[i] = (int) (PoissonRandom.draw(generator, P * counts * k) / k / k + generator.nextDouble());
      rest -= P;
      P *= nt / (i + 1);
    }
    output[output.length - 1] = (int) (PoissonRandom.draw(generator, rest * counts * k) / k / k + generator.nextDouble());
  }

}
