// --- file: gov/llnl/rtk/pileup/TriggerParalyzable.java ---
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
public class TriggerParalyzable implements TriggerModel
{
  double tau;

  @Override
  public double getTau()
  {
    return tau;
  }

// Keeping this code until it gets moved over
//  @Override
//  public double[] getFractions(double rate)
//  {
//    double nt = rate * tau;
//    double ent = Math.exp(-nt);
//    double dnt = 1 / (nt + 1);
//    double p0 = ent / dnt;
//    double p1 = ent * (1 + ent) / dnt;
//    double p2 = ent * (1 + ent) * (1 + ent) / dnt;
//    double p3 = ent * (1 + ent) * (1 + ent) / dnt;
//    return new double[]
//    {
//      p0, p1, p2, p3
//    };
//  }

  @Override
  public int drawCounts(RandomGenerator generator, double rate, double time)
  {
    double P0 = rate;
    double P1 = rate * Math.exp(-rate * tau);
    double f = Math.sqrt(P1 / P0);
    return (int) (f * PoissonRandom.draw(generator, P0 * time) * P1 / P0
            + (1 - f) * PoissonRandom.draw(generator, P1 * time));
  }

  @Override
  public void drawDistribution(int[] output, RandomGenerator generator, double rate, double time)
  {
    // Currently I am not aware of paralyzable spectral sensors.  
    throw new UnsupportedOperationException("Not supported"); 
  }

}
