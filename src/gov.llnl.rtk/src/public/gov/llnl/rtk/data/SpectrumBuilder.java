// --- file: gov/llnl/rtk/data/SpectrumBuilder.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC.
 * All rights reserved
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import java.time.Instant;

/**
 * Helper class to construct a spectrum with specified fields.
 * @author nelson85
 */
public interface SpectrumBuilder
{
 /**
  * Set spectrum counts to a double array
  * @param d
  * @return
  * @throws IllegalStateException 
  */
  SpectrumBuilder counts(double[] d) throws IllegalStateException;

 /**
 * Set spectrum counts to an integer array
 * @param d
 * @return
 * @throws IllegalStateException 
 */
  SpectrumBuilder counts(int[] d) throws IllegalStateException;

  /**
   * Set the energy scale of the spectrum
   * @param scale
   * @return 
   */
  SpectrumBuilder scale(EnergyScale scale);

 /**
 * Set both live time and real time to the given time length.
 * @param time
 * @return 
 */
  SpectrumBuilder time(double time);

  /**
   * Set the live and real time of the spectrum
   * @param livetime
   * @param realtime
   * @return 
   */
  SpectrumBuilder time(double livetime, double realtime);

  /**
   * Set the timestamp when the measurement was taken
   * @param timestamp
   * @return 
   */
  SpectrumBuilder timestamp(Instant timestamp);

  /**
   * Set the title of the spectrum
   * @param title
   * @return 
   */
  SpectrumBuilder title(String title);
  
  /**
   * Set the number of over range (overflow) channels in the spectrum.
   * @param channels number of channels in overRange
   * @return 
   */
  SpectrumBuilder overRange(int channels);
  
  /**
   * Set the number of under range (underflow) channels in the spectrum.
   * @param channels number of channels in underRange
   * @return 
   */
  SpectrumBuilder underRange(int channels);

  /**
   * @return the spectrum as a double spectrum
   * @throws IllegalStateException 
   */
  DoubleSpectrum asDouble() throws IllegalStateException;

  /**
   * 
   * @return the spectrum as an integer spectrum
   * @throws IllegalStateException 
   */
  IntegerSpectrum asInteger() throws IllegalStateException;

}
