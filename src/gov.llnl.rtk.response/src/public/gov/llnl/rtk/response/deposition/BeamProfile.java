/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

/**
 *
 * @author nelson85
 */
public interface BeamProfile
{
  /**
   * Get the fill fraction for the beam in the current integration interval.
   *
   * @param ux is the x component of the surface u vector.
   * @param uy is the y component of the surface u vector.
   * @param uz is the z component of the surface u vector.
   * @param vx is the x component of the surface v vector.
   * @param vy is the y component of the surface v vector.
   * @param vz is the z component of the surface v vector.
   * @param pu is the length of u for the patch.
   * @param pv is the length of v for the patch.
   * @param lu is the patch dimension in u.
   * @param lv is the patch dimension in v.
   * @param sx is the x component of the source.
   * @param sy is the y component of the source.
   * @param sz is the z component of the source.
   * @param dx is the x component of the surface.
   * @param dy is the x component of the surface.
   * @param dz is the x component of the surface.
   * @return
   */
  public double getFactor(double ux, double uy, double uz,
          double vx, double vy, double vz,
          double pu, double pv,
          double lu, double lv,
          double sx, double sy, double sz,
          double dx, double dy, double dz);

}
