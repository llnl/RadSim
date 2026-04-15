/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

import gov.llnl.math.euclidean.Vector3;

/**
 * SolidAngleCalculator computes the solid angles for a reflected surface as a
 * function of angle.
 *
 * This is for use by the scattering calculator. It is not reentrant so is
 * serves as its own evaluator.
 *
 * @author nelson85
 */
public class ReflectionCalculator
{
  public final double STEPS = 40;

  // Inputs
  Vector3 sourcePosition;
  Vector3 detectorPosition;
  Vector3 reflectorPosition;
  Vector3 reflectorX;
  Vector3 reflectorY;
  Vector3 reflectorZ;
  double surfaceLength;
  double surfaceWidth;
  BeamProfile beamProfile = null;

  // Number of histogram bins
  public int bins = 64;

  // Tally of the cosTheta into histogram bins.
  public double[] result;

  // Track the minimum and maximum cosTheta evaluated.
  //   (used to truncate the integral calculations)
  public double min;
  public double max;

  // Workspace variables
  double[][] tmp1;
  double[][] tmp2;

  /**
   * Define the location of the source.
   *
   * @param position
   */
  public void setSourcePosition(Vector3 position)
  {
    this.sourcePosition = position;
  }

  public Vector3 getSourcePosition()
  {
    return sourcePosition;
  }

  /**
   * Define the location of the detector.
   *
   * @param position
   */
  public void setDetectorPosition(Vector3 position)
  {
    this.detectorPosition = position;
  }

  public Vector3 getDetectorPosition()
  {
    return detectorPosition;
  }

  /**
   * Define the surface for the reflector.
   *
   * @param center is the location of the center of the reflector.
   * @param length is the scalar length of the reflector in the x direction.
   * @param width is the scalar length of the reflector in the y direction.
   * @param x is the length vector.
   * @param y is the width vector (must by orthogonal to length.)
   */
  public void defineReflector(Vector3 center,
          double length, double width,
          Vector3 x,
          Vector3 y)
  {
    this.reflectorPosition = center;
    this.reflectorX = unit(x);
    this.reflectorY = unit(y);
    this.reflectorZ = unit(cross(reflectorX, reflectorY));
    this.surfaceLength = length;
    this.surfaceWidth = width;
  }

  public Vector3 getReflectorPosition()
  {
    return reflectorPosition;
  }

  public double getSurfaceLength()
  {
    return surfaceLength;
  }

  public double getSurfaceWidth()
  {
    return surfaceWidth;
  }

  public void setBeamProfile(BeamProfile beam)
  {
    this.beamProfile = beam;
  }

  public BeamProfile getBeamProfile()
  {
    return beamProfile;
  }

  /**
   * Compute the reflection off a surface.
   *
   * @return
   */
  public double[] compute()
  {
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    result = new double[bins];
    return apply(result);
  }

  /**
   * Add a reflector to the buffer.
   *
   * @param result
   * @return
   */
  public double[] apply(double[] result)
  {
    // Gather the coordinate points for speed

    // Source
    double sx = sourcePosition.getX();
    double sy = sourcePosition.getY();
    double sz = sourcePosition.getZ();

    // Detector
    double dx = detectorPosition.getX();
    double dy = detectorPosition.getY();
    double dz = detectorPosition.getZ();

    // Reflector
    double rx = reflectorPosition.getX();
    double ry = reflectorPosition.getY();
    double rz = reflectorPosition.getZ();

    // Reflector X
    double ux = reflectorX.getX();
    double uy = reflectorX.getY();
    double uz = reflectorX.getZ();

    // Reflector Y
    double vx = reflectorY.getX();
    double vy = reflectorY.getY();
    double vz = reflectorY.getZ();

    // Reflector norm
    double nx = reflectorZ.getX();
    double ny = reflectorZ.getY();
    double nz = reflectorZ.getZ();

    double[] mn;
    double[] mx;

    // Determine the number of steps required for the integration
    // FIXME this assumes that each direction needs the same number of steps
    double R = norm(sx - rx, sy - ry, sz - rz);
    double cosS = dot(sx - rx, sy - ry, sz - rz,
            nx, ny, nz) / R;

    // If we behind the surface the we don't need to calculate it.
    if (cosS < 0)
      return result;

    double area = surfaceLength * surfaceWidth;
    double sa = area * cosS / R / R;
    int steps = (int) Math.max(Math.sqrt(sa) * STEPS, 5);
    double sarea = area / steps / steps;

    // Allocate memory
    if (tmp1 == null || tmp2.length < steps + 1)
    {
      tmp1 = new double[steps + 1][2];
      tmp2 = new double[steps + 1][2];
    }

    // Integrate accross the surface
    for (int iu = 0; iu < steps + 1; ++iu)
    {
      double f1 = ((double) iu / steps - 0.5);
      for (int iv = 0; iv < steps + 1; ++iv)
      {
        double f2 = ((double) iv / steps - 0.5);

        // Point on surface
        double px = rx + surfaceLength * ux * f1 + surfaceWidth * vx * f2;
        double py = ry + surfaceLength * uy * f1 + surfaceWidth * vy * f2;
        double pz = rz + surfaceLength * uz * f1 + surfaceWidth * vz * f2;

        double k = 1.0;
        if (beamProfile != null)
          k = beamProfile.getFactor(ux, uy, uz,
                  vx, vy, vz,
                  surfaceLength * f1,
                  surfaceWidth * f2,
                  surfaceLength / steps,
                  surfaceWidth / steps,
                  sx, sy, sz,
                  rx, ry, rz);

        // Lengths of incident and reflection
        double distI = norm(sx - px, sy - py, sz - pz);
        double distE = norm(dx - px, dy - py, dz - pz);

        double cosD = dot(
                px - sx, py - sy, pz - sz,
                dx - px, dy - py, dz - pz) / distI / distE;

        // Deflection
        tmp1[iv][0] = cosD;

        if (k == 0)
        {
          tmp1[iv][1] = 0;
          continue;
        }

        // Incident angle
        double cosI = dot(sx - px, sy - py, sz - pz,
                nx, ny, nz) / distI;

        // FIXME figure out how to tally the emitted angle
        // double cosE=dot(dx-px, dy-py, dz-pz, nx, ny, nz)/RE;
        // Deflection angle
        double sinD = Math.sqrt(1 - cosD * cosD);

        // Solid angle at deflection
        tmp1[iv][1] = k * sarea * cosI / 2 / Math.PI / sinD;
      }

      if (iu > 0)
      {
        for (int iv = 0; iv < steps; ++iv)
        {
          // Find the minumum angle
          mn = tmp1[iv];
          if (mn[0] > tmp1[iv + 1][0])
            mn = tmp1[iv + 1];
          if (mn[0] > tmp2[iv][0])
            mn = tmp2[iv];
          if (mn[0] > tmp2[iv + 1][0])
            mn = tmp2[iv + 1];

          // Find the maximum angle
          mx = tmp1[iv];
          if (mx[0] < tmp1[iv + 1][0])
            mx = tmp1[iv + 1];
          if (mx[0] < tmp2[iv][0])
            mx = tmp2[iv];
          if (mx[0] < tmp2[iv + 1][0])
            mx = tmp2[iv + 1];

          // Tally over the interval
          tally(result, mn, mx);
        }
      }

      // Swap
      double[][] tmp3 = tmp2;
      tmp2 = tmp1;
      tmp1 = tmp3;
    }

    return result;
  }

  public double[][] testBeam()
  {
    // Gather the coordinate points for speed

    // Source
    double sx = sourcePosition.getX();
    double sy = sourcePosition.getY();
    double sz = sourcePosition.getZ();

    // Reflector
    double rx = reflectorPosition.getX();
    double ry = reflectorPosition.getY();
    double rz = reflectorPosition.getZ();

    // Reflector X
    double ux = reflectorX.getX();
    double uy = reflectorX.getY();
    double uz = reflectorX.getZ();

    // Reflector Y
    double vx = reflectorY.getX();
    double vy = reflectorY.getY();
    double vz = reflectorY.getZ();

    // Reflector norm
    double nx = reflectorZ.getX();
    double ny = reflectorZ.getY();
    double nz = reflectorZ.getZ();

    // Determine the number of steps required for the integration
    // FIXME this assumes that each direction needs the same number of steps
    double R = norm(sx - rx, sy - ry, sz - rz);
    double cosS = dot(sx - rx, sy - ry, sz - rz,
            nx, ny, nz) / R;

    // If we behind the surface the we don't need to calculate it.
    if (cosS < 0)
      return null;

    double area = surfaceLength * surfaceWidth;
    double sa = area * cosS / R / R;
    int steps = (int) Math.max(Math.sqrt(sa) * STEPS, 5);
    double[][] out = new double[steps][steps];

    // Integrate accross the surface
    for (int iu = 0; iu < steps; ++iu)
    {
      double f1 = ((double) iu / steps - 0.5);
      for (int iv = 0; iv < steps; ++iv)
      {
        double f2 = ((double) iv / steps - 0.5);
        out[iu][iv] = beamProfile.getFactor(ux, uy, uz,
                vx, vy, vz,
                surfaceLength * f1,
                surfaceWidth * f2,
                surfaceLength / steps,
                surfaceWidth / steps,
                sx, sy, sz,
                rx, ry, rz);
      }
    }

    return out;
  }

//<editor-fold desc="internal">
  public void tally(double[] result, double[] p1, double[] p2)
  {
    if (p1[0] < min)
      min = p1[0];
    if (p2[0] > max)
      max = p2[0];

    // Transform into result coordinates
    double c1 = 0.5 * (p1[0] + 1) * bins;
    double c2 = 0.5 * (p2[0] + 1) * bins;

    // Compute the length of this integration
    double S = c2 - c1;

    // If it is zero length, then just add to the result directly
    if (S <= 0)
    {
      int i = (int) (c1);
      result[i] += (p1[1] + p2[1]) / 2;
      return;
    }

    // Add it over the region bound by the min and max linearly
    double lower = c1;
    double v0 = p1[1];
    int upper = (int) c1 + 1;
    while (upper < c2)
    {
      double v1 = (c2 - upper) / S * p1[1] + (upper - c1) / S * p2[1];
      result[upper - 1] += (v0 + v1) / 2 * (upper - lower) / S;
      v0 = v1;
      lower = upper;
      upper++;
    }
    result[upper - 1] += (p2[1] + v0) / 2 * (c2 - lower) / S;
  }

  /**
   * Create a unit vector.
   *
   * @param p
   * @return
   */
  static Vector3 unit(Vector3 p)
  {
    double x = p.getX();
    double y = p.getY();
    double z = p.getZ();
    double n = Math.sqrt(x * x + y * y + z * z);
    return Vector3.of(x / n, y / n, z / n);
  }

  static Vector3 cross(Vector3 p1, Vector3 p2)
  {
    double x1 = p1.getX();
    double y1 = p1.getY();
    double z1 = p1.getZ();
    double x2 = p2.getX();
    double y2 = p2.getY();
    double z2 = p2.getZ();
    return Vector3.of(y1 * z2 - y2 * z1, x2 * z1 - x1 * z2, x1 * y2 - x2 * y1);
  }

  /**
   * Compute the norm for a vector.
   *
   * @param x1
   * @param y1
   * @param z1
   * @return
   */
  static double norm(double x1, double y1, double z1)
  {
    return Math.sqrt((x1 * x1 + y1 * y1 + z1 * z1));
  }

  /**
   * Compute the dot product for vector.
   *
   * cos(A,B) = dot(A,B)/norm(A)/norm(B)
   *
   * @param x1
   * @param y1
   * @param z1
   * @param x2
   * @param y2
   * @param z2
   * @return
   */
  static double dot(double x1, double y1, double z1,
          double x2, double y2, double z2
  )
  {
    return (x1 * x2 + y1 * y2 + z1 * z2);
  }
//</editor-fold>
}
