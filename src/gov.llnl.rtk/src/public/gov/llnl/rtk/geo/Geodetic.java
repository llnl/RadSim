// --- file: gov/llnl/rtk/geo/Geodetic.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.geo;

/**
 * Convert used for world coordinates.
 * 
 * @author nelson85
 */
public interface Geodetic
{
  static public class Ellipsoid
  {
    public final double A;
    public final double B;
    public final double INV_F;
    public final double E2;
    public final double EP2;

    public Ellipsoid(double a, double iF)
    {
      A = a;
      B = a * (1 - 1 / iF);
      INV_F = iF;
      E2 = (2 * iF - 1) / iF / iF;
      EP2 = (2 * iF - 1) / (iF - 1) / (iF - 1);
    }

    @Override
    public String toString()
    {
      return String.format("a=%.12e\nb=%.12e\nif=%.12e\ne^2=%.12e\ne'^2=%.12e",
              A, B, INV_F, E2, EP2);
    }
  }

  /** 
   * Convert geodetic coordinate to Earth centered frame.
   * 
   * FIXME rename to something more specific.
   * 
   * @param geo
   * @return 
   */
  CoordinateECR convert(CoordinateGeo geo);

  /** 
   * Convert Earth centered frame to geodetic coordinate.
   * 
   * FIXME rename to something more specific.
   * 
   * @param geo
   * @return 
   */
  CoordinateGeo convert(CoordinateECR geo);

  /** 
   * Compute the distance between to geodetic coordinates.
   * 
   * This will use this Geodetic to convert to Earth centered coordinates then
   * compute distance.
   * 
   * @param pos1
   * @param pos2
   * @return 
   */
  double computeDistance(CoordinateGeo pos1, CoordinateGeo pos2);

  /** 
   * Compute the angular distance between two points.
   * 
   * This is used by utilities when compute the great circle between two
   * points on the Earth's surface.
   * 
   * @param c1
   * @param c2
   * @return 
   */
  double computeAngularDistance(CoordinateGeo c1, CoordinateGeo c2);

}
