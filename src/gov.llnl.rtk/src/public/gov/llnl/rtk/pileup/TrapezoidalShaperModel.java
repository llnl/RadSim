// --- file: gov/llnl/rtk/pileup/TrapezoidalShaperModel.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.pileup;

import gov.llnl.math.random.RandomGenerator;

public class TrapezoidalShaperModel implements ShaperModel
{
  public double fractionAccept=1;
  public double fractionRecovery=1;
  public double fractionFlat;
  public double fractionTrap;
  
  
  
  public TrapezoidalShaperModel(double fractionFlat, double fractionTrap)
  {
    this.fractionFlat = fractionFlat;
    this.fractionTrap = fractionTrap;
  }

  @Override
  public double draw(RandomGenerator random)
  {
    double f = random.nextDouble();
    if (f>fractionRecovery)
      return 0;
    if (f>fractionAccept)
      return -1;
    if (f < fractionFlat)
      return 1;
    f -= fractionFlat;
    if (f > fractionTrap)
      return 0;
    return 1-f / fractionTrap;
  }

}
