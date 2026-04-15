// --- file: gov/llnl/rtk/physics/DoseTableImpl.java ---
/*
 * Copyright 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.annotation.Internal;
import gov.llnl.utility.xml.bind.ReaderInfo;
import java.util.TreeMap;

/**
 *
 * @author nelson85
 */
@Internal
@ReaderInfo(DoseTableReader.class)
public final class DoseTableImpl implements DoseTable
{
  public int version;
  int index_;
  int num_records_;
  TreeMap<String, InterpolationTable> tables_ = new TreeMap<>();

  /**
   * Creates a dose conversion object. Must be loaded with data before it will
   * function.
   */
  DoseTableImpl()
  {
    version = -1;
    index_ = 0;
    num_records_ = 0;
  }

  /**
   * Get the version number for the dose conversion file.
   *
   * @return the version number.
   */
  @Override
  public int getVersion()
  {
    return version;
  }

  /**
   * Compute the dose conversion (Dose/Activity) in units of Sv/hr/Ci @ 1m
   *
   * @param nuclide is the name of the nuclide.
   * @param Z is the atomic number of the shielding.
   * @param AD is the areal density of the shielding.
   * @return the dose conversion or -1 if not available.
   */
  @Override
  public double getDoseToActivityConversion(Nuclide nuclide, double Z, double AD)
  {
    double factor = 1e-4;
    InterpolationTable ir = tables_.get(nuclide.getName());
    if (ir == null)
      return -1;

    if (Z < ir.Z_list[0])
    {
      return factor * Math.exp(interpCS((float) AD, ir.AD_list, ir.AD_list.length, ir.values[0], ir.slopes[0]));
    }

    if (Z >= ir.Z_list[ir.Z_list.length - 1])
    {
      return factor * Math.exp(interpCS((float) AD, ir.AD_list, ir.AD_list.length,
              ir.values[(ir.Z_list.length - 1)], ir.slopes[(ir.Z_list.length - 1)]));
    }

    int zi = 0;
    for (zi = 1; zi < ir.Z_list.length; zi++)
    {
      if (Z < ir.Z_list[zi])
        break;
    }

    double f = (Z - ir.Z_list[zi]) / (ir.Z_list[zi - 1] - ir.Z_list[zi]);
    float c1 = interpCS((float) AD, ir.AD_list, ir.AD_list.length,
            ir.values[(zi - 1)], ir.slopes[(zi - 1)]);
    float c2 = interpCS((float) AD, ir.AD_list, ir.AD_list.length,
            ir.values[(zi)], ir.slopes[(zi)]);
    return factor * Math.exp(f * c1 + (1 - f) * c2);
  }

  /**
   * Internal function for interpolating the spline tables.
   *
   * @param X
   * @param X0
   * @param len
   * @param Y0
   * @param Z0
   * @return the interpolated value
   */
  private float interpCS(float X, float[] X0, int len,
          float[] Y0, float[] Z0)
  {
    if (X <= X0[0])
      return Y0[0];
    if (X >= X0[len - 1])
      return Y0[len - 1];

    int i0;
    for (i0 = 1; i0 < len; ++i0)
    {
      if (X < X0[i0])
        break;
    }
    i0--;
    float h = X0[i0 + 1] - X0[i0];
    float out = (Z0[i0 + 1] * (X - X0[i0]) * (X - X0[i0]) * (X - X0[i0])
            + Z0[i0] * (X0[i0 + 1] - X) * (X0[i0 + 1] - X) * (X0[i0 + 1] - X)) / 6 / h
            + (Y0[i0 + 1] / h - h / 6 * Z0[i0 + 1]) * (X - X0[i0])
            + (Y0[i0] / h - h / 6 * Z0[i0]) * (X0[i0 + 1] - X);
    return out;
  }

  public void setVersion(int version)
  {
    this.version = version;
  }

  public void add(InterpolationTable table)
  {
    this.tables_.put(table.nuclide, table);
  }

  /**
   *
   * @author nelson85
   */
  public static class InterpolationTable
  {
    public String nuclide;
    // short atomicNumber;
    // short mass1, mass2;
    // short meta;
    int ptr;
    public float[] Z_list;
    public float[] AD_list;
    public float[][] values;
    public float[][] slopes;

    public void setZ_list(float[] Z_list)
    {
      this.Z_list = Z_list;
    }

    public void setAD_list(float[] AD_list)
    {
      this.AD_list = AD_list;
    }

    public void setValues(float[][] values)
    {
      this.values = values;
    }

    public void setSlopes(float[][] slopes)
    {
      this.slopes = slopes;
    }

    public void setNuclide(String nuclide)
    {
      this.nuclide = nuclide;
    }
  }

}
