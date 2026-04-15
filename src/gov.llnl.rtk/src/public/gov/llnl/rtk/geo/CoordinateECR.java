// --- file: gov/llnl/rtk/geo/CoordinateECR.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

/**
 *
 * @author nelson85
 */
public interface CoordinateECR
{
  double getX();

  double getY();

  double getZ();

  public static class RTheta
  {
    public double R, Theta;

    public RTheta(double r, double theta)
    {
      R = r;
      Theta = theta;
    }
  }

}
