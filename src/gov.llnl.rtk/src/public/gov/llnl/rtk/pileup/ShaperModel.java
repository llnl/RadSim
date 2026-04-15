// --- file: gov/llnl/rtk/pileup/ShaperModel.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.pileup;

import gov.llnl.math.random.RandomGenerator;

/**
 *
 * @author nelson85
 */
public interface ShaperModel
{

  /**
   * Return the fraction of energy deposited for a pileup event.
   *
   * @param random
   * @return
   */
  public double draw(RandomGenerator random);

}
