// --- file: gov/llnl/rtk/EnergyScaleException.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk;

/**
 *
 * @author seilhan3
 */
public class EnergyScaleException extends Exception
{
  public EnergyScaleException(String reason)
  {
    super(reason);
  }

  public EnergyScaleException(Exception ex)
  {
    super(ex);
  }

  public EnergyScaleException(String reason, Exception ex)
  {
    super(reason, ex);
  }
}
