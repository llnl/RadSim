// --- file: gov/llnl/rtk/gm/GeigerMuellerFactory.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.gm;

/**
 *
 * @author nelson85
 */
public class GeigerMuellerFactory
{
  static double[] energies = new double[]
  {   0,   100,  200,   500,   700,  1000,  1500,  2000,  2500
  };
  
  static double[] efficiencyAl = new double[]
  {
    0.00,  0.00, 0.05,  0.37,  0.53,  0.73,  1.05,  1.41,  1.80
  };

  static double[] efficiencyCu = new double[]
  {
    0.00,  0.20, 0.20,  0.30,  0.45,  0.65,  0.98,  1.31,  1.62
  };

  static double[] efficiencyPb = new double[]
  {
    0.00,  1.25, 1.45,  1.05,  0.74,  0.80,  1.12,  1.42,  1.75
  };
  
  private String material;
  private double diameter;
  private double length;
  private double recoveryTime;

  /**
   * @return the material
   */
  public String getMaterial()
  {
    return material;
  }

  /**
   * @param material the material to set
   */
  public void setMaterial(String material)
  {
    this.material = material;
  }

  /**
   * @return the diameter
   */
  public double getDiameter()
  {
    return diameter;
  }

  /**
   * @param diameter the diameter to set
   */
  public void setDiameter(double diameter)
  {
    this.diameter = diameter;
  }

  /**
   * @return the length
   */
  public double getLength()
  {
    return length;
  }

  /**
   * @param length the length to set
   */
  public void setLength(double length)
  {
    this.length = length;
  }

  /**
   * @return the recoveryTime
   */
  public double getRecoveryTime()
  {
    return recoveryTime;
  }

  /**
   * @param recoveryTime the recoveryTime to set
   */
  public void setRecoveryTime(double recoveryTime)
  {
    this.recoveryTime = recoveryTime;
  }
}
