// --- file: gov/llnl/rtk/pileup/TriggerPerfect.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.pileup;

import gov.llnl.math.random.PoissonRandom;
import gov.llnl.math.random.RandomGenerator;
import java.util.Arrays;

/**
 *
 * @author nelson85
 */
public class TriggerPerfect implements TriggerModel
{
  @Override
  public double getTau()
  {
    return 0;
  }

  @Override
  public int drawCounts(RandomGenerator generator, double rate, double time)
  {
    return PoissonRandom.draw(generator, rate * time);
  }

  @Override
  public void drawDistribution(int[] output, RandomGenerator generator, double rate, double time)
  {
    Arrays.fill(output, 0);
    output[0] = this.drawCounts(generator, rate, time);
  }

}
