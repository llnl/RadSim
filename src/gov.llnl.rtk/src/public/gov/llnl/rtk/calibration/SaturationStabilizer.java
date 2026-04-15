// --- file: gov/llnl/rtk/calibration/SaturationStabilizer.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.calibration;

/**
 *
 * @author nelson85
 */
public class SaturationStabilizer
{
  // Implemented later  
}

/*

This will be used to correct both gain and saturation effects.  Multiple peaks
need to be specified for this to work.

Example:
  <calibration>
    <saturationStabilizer>
      <peak> 
        <target units="channels">540</target>
        <predictivePeakTracker2>
          <decimation>20</decimation>
          <expectedChannel>535</expectedChannel>
          <meanTimeConstant>60</meanTimeConstant>
          <biasTimeConstant>300</biasTimeConstant>
          <spectralTimeConstant1>60</spectralTimeConstant1>
          <spectralTimeConstant2>300</spectralTimeConstant2>
          <peakFitter>
            <weightedCentroidFitter>
              <peakSigma>15</peakSigma>
              <biasCoefficients>-17.67211619 2.08267191 -0.06418428</biasCoefficients>
            </weightedCentroidFitter>
          </peakFitter>
        </predictivePeakTracker2>
      </peak>
      <peak>
        <target units="channels">1020</target>
        <predictivePeakTracker2>
          <decimation>20</decimation>
          <expectedChannel>1020</expectedChannel>
          <meanTimeConstant>60</meanTimeConstant>
          <biasTimeConstant>300</biasTimeConstant>
          <spectralTimeConstant1>60</spectralTimeConstant1>
          <spectralTimeConstant2>300</spectralTimeConstant2>
          <peakFitter>
            <weightedCentroidFitter>
              <peakSigma>15</peakSigma>
              <biasCoefficients>-17.67211619 2.08267191 -0.06418428</biasCoefficients>
            </weightedCentroidFitter>
          </peakFitter>
        </predictivePeakTracker2>
      </peak>
    </singlePeakGainStabilizer>
  </calibration>

 */
