// --- file: gov/llnl/rtk/response/deposition/AngularUtility.java ---
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
public class AngularUtility
{
    /**
   * Fill the predictor (xp) array with angular and cross-section features for
   * the ML model.
   *
   * @param xp Output array (must have at least 12 elements)
   * @param index fill location in array.
   * @param cosAngle Cosine of scattering angle
   * @param attenuation Cross-section (already normalized to unit distance)
   */
  public static void fillAngularFeatures(double[] xp, int index, double cosAngle, double attenuation)
  {
    double a0 = Math.sqrt(1 - cosAngle*cosAngle);
    double a1 = (cosAngle - 1) * (cosAngle - 1) / 4;
    double a2 = (cosAngle + 1) * (cosAngle + 1) / 4;
    double a3 = Math.acos(cosAngle) / Math.PI;
    
    xp[index++] = cosAngle;
    xp[index++] = a0;
    xp[index++] = a1;
    xp[index++] = a2;
    xp[index++] = a3;
    xp[index++] = 1 - 0.01 / (attenuation + 0.01);
    xp[index++] = 1 - 0.1 / (attenuation + 0.1);
    xp[index++] = 1 - 1 / (attenuation + 1);
    xp[index++] = 1 - 10 / (attenuation + 10);
    xp[index++] = 1 - 100 / (attenuation + 100);
  }
  
}
