// --- file: gov/llnl/rtk/quality/QualityCheck.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.quality;

import java.io.Serializable;

/**
 *
 * @author seilhan3
 * @param <Type>
 */
public interface QualityCheck<Type> extends Serializable
{
  /**
   * Execute the check.
   *
   * @param fault
   * @param measurement
   * @return false if the check fails and should not proceed.
   * @throws QualityControlException
   */
  boolean execute(FaultSet fault, Type measurement) throws QualityControlException;

}
