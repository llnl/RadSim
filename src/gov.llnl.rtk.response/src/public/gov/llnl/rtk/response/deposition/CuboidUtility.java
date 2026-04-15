// --- file: gov/llnl/rtk/response/deposition/CuboidUtility.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

/**
 *
 * @author nelson85
 */
public class CuboidUtility
{

  static double computeSolidAngleYZ(double x, double y, double z, double l, double w, double h, double offset)
  {
    return computeSolidAngle(x, y, z, w, h, offset);
  }

  static double computeSolidAngleXZ(double x, double y, double z, double l, double w, double h, double offset)
  {
    return computeSolidAngle(y, x, z, l, h, offset);
  }

  static double computeSolidAngleXY(double x, double y, double z, double l, double w, double h, double offset)
  {
    return computeSolidAngle(z, x, y, l, w, offset);
  }

  static double f(double z, double x, double y)
  {
    return Math.atan2(x * y, z * Math.sqrt(z * z + x * x + y * y));
  }

  /**
   * Compute the solid angle subtended by a face of a cuboid, generalized for
   * any orientation.
   *
   * @param axis The coordinate (x, y, or z) perpendicular to the face.
   * @param coord1 The first in-plane coordinate (y or x or x).
   * @param coord2 The second in-plane coordinate (z or z or y).
   * @param dim1 The first in-plane dimension (width or length or length).
   * @param dim2 The second in-plane dimension (height or height or width).
   * @param offset The offset for the perpendicular axis.
   * @return The solid angle subtended by the face.
   */
  static double computeSolidAngle(double axis, double coord1, double coord2,
          double dim1, double dim2, double offset)
  {
    double depth = Math.abs(offset - axis);
    double v1 = -dim1 / 2 - coord1;
    double v2 = -dim2 / 2 - coord2;
    double v3 = dim1 + v1;
    double v4 = dim2 + v2;
    return f(depth, v2, v1) - f(depth, v4, v1) - f(depth, v2, v3) + f(depth, v4, v3);
  }

  /**
   * Compute normalized geometric and positional features for a cuboid. Fills
   * the provided feature array (xe) with 15 features.
   *
   * @param xe Output feature array (length 15)
   * @param index
   * @param x Source x position (physical units)
   * @param y Source y position
   * @param z Source z position
   * @param l Cuboid length (X)
   * @param w Cuboid width (Y)
   * @param h Cuboid height (Z)
   * @param diag Cuboid diagonal (normalization)
   */
  public static void fillCuboidFeatures(double[] xe, int index,
          double x, double y, double z,
          double l, double w, double h, 
          double diag)
  {

    // The source must always be in the negative quadrant
    if (x > 0)
      x = -x;
    if (y > 0)
      y = -y;
    if (z > 0)
      z = -z;
    
    double xN = x / diag, yN = y / diag, zN = z / diag;
    double lN = l / diag, wN = w / diag, hN = h / diag;

    // Compute solid angles
    double Bx1 = computeSolidAngleYZ(xN, yN, zN, lN, wN, hN, -lN / 2);
    double Bx2 = computeSolidAngleYZ(xN, yN, zN, lN, wN, hN, +lN / 2);
    double By1 = computeSolidAngleXZ(xN, yN, zN, lN, wN, hN, -wN / 2);
    double By2 = computeSolidAngleXZ(xN, yN, zN, lN, wN, hN, +wN / 2);
    double Bz1 = computeSolidAngleXY(xN, yN, zN, lN, wN, hN, -hN / 2);
    double Bz2 = computeSolidAngleXY(xN, yN, zN, lN, wN, hN, +hN / 2);

    // Determine which faces are forward
    double Fx = 0, Fy = 0, Fz = 0;
    if (x < -l / 2)
    {
      Fx = Bx1;
      Bx1 = 0;
    }
    if (y < -w / 2)
    {
      Fy = By1;
      By1 = 0;
    }
    if (z < -h / 2)
    {
      Fz = Bz1;
      Bz1 = 0;
    }

    // Size of faces
    xe[index++] = lN;
    xe[index++] = wN;
    xe[index++] = hN;
    
    //  direction to the center of the cube
    double T1 = Math.sqrt(x * x + y * y + z * z);
    xe[index++] = Math.abs(x / T1);
    xe[index++] = Math.abs(y / T1);
    xe[index++] = Math.abs(z / T1);
    
    // Front face fractions
    double T2 = Fx + Fy + Fz;
    xe[index++] = Fx / T2;
    xe[index++] = Fy / T2;
    xe[index++] = Fz / T2;
    
    // Rear face fractions
    double T3 = Bx1 + Bx2 + By1 + By2 + Bz1 + Bz2;
    xe[index++] = Bx1 / T3;
    xe[index++] = Bx2 / T3;
    xe[index++] = By1 / T3;
    
    xe[index++] = By2 / T3;
    xe[index++] = Bz1 / T3;
    xe[index++] = Bz2 / T3;
  }


  static void permute(double[] dims, double[] entry, double x, double y, double z, double l, double w, double h)
  {
    // Store dimensions and entries in arrays
    dims[0] = l;
    dims[1] = w;
    dims[2] = h;
    entry[0] = x;
    entry[1] = y;
    entry[2] = z;

    // Sort dims descending, and permute entry accordingly
    for (int i = 0; i < 3; ++i)
    {
      for (int j = i + 1; j < 3; ++j)
      {
        if (dims[j] > dims[i])
        {
          // Swap dims
          double tempDim = dims[i];
          dims[i] = dims[j];
          dims[j] = tempDim;
          // Swap entry to match
          double tempEntry = entry[i];
          entry[i] = entry[j];
          entry[j] = tempEntry;
        }
      }
    }
  }

}
