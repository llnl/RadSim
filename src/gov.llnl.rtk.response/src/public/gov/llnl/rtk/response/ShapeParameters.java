// --- file: gov/llnl/rtk/response/SpectralBuffer.java ---
/*
 * Copyright 2022, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response;

/**
 * Parameters for describing line shape.
 * 
 * @author nelson85
 */
public class ShapeParameters
{
  double theta;
  double negativeTail;
  double positiveTail;

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 79 * hash + (int) (Double.doubleToLongBits(this.theta) ^ (Double.doubleToLongBits(this.theta) >>> 32));
    hash = 79 * hash + (int) (Double.doubleToLongBits(this.negativeTail) ^ (Double.doubleToLongBits(this.negativeTail) >>> 32));
    hash = 79 * hash + (int) (Double.doubleToLongBits(this.positiveTail) ^ (Double.doubleToLongBits(this.positiveTail) >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final ShapeParameters other = (ShapeParameters) obj;
    if (Double.doubleToLongBits(this.theta) != Double.doubleToLongBits(other.theta))
      return false;
    if (Double.doubleToLongBits(this.negativeTail) != Double.doubleToLongBits(other.negativeTail))
      return false;
    return Double.doubleToLongBits(this.positiveTail) == Double.doubleToLongBits(other.positiveTail);
  }

  /**
   * @return the theta
   */
  public double getTheta()
  {
    return theta;
  }

  /**
   * @return the negativeTail
   */
  public double getNegativeTail()
  {
    return negativeTail;
  }

  /**
   * @return the positiveTail
   */
  public double getPositiveTail()
  {
    return positiveTail;
  }

}
