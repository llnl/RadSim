// --- file: gov/llnl/rtk/data/Spectrum.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.data;

import gov.llnl.utility.xml.bind.WriterInfo;

/**
 *
 * @author nelson85
 * @param <Type> is the type of array holding the data. It should be an array of
 * primitives type such as int[], float[], or double[].
 */
@WriterInfo(SpectrumWriter.class)
public interface Spectrum<Type> extends RadiationData<Type>
{
  /**
   * Alters the size of the spectrum.
   *
   * The current counts in the spectrum are cleared. Clears the valid range.
   *
   * throw UnsupportedOperationException if the spectrum cannot be resized.
   *
   * @param size
   */
  default void resize(int size)
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Get the size of the spectrum.
   *
   * @return the number of channels
   */
  int size();

  /**
   * Set the energy bins associated with the spectrum.
   *
   * @param bins
   */
  void setEnergyScale(EnergyScale bins);

  /**
   * Get the energy bins associated with the spectrum.
   *
   * @return the energy bins associated with the spectrum, or null if no scale
   * is set.
   */
  EnergyScale getEnergyScale();

  /**
   * Get the data storage type for this spectrum.
   *
   * @return the type of the data storage.
   */
  Class getCountClass();

  /**
   * Get the counts that are below the valid energy scale.
   *
   * @return
   */
  double getUnderRangeCounts();

  /**
   * Get the counts that are above the valid energy scale.
   *
   * @return
   */
  double getOverRangeCounts();

  /**
   * Get the total counts in the region of interest.
   *
   * @param roi can either be in energy or channels, if null it will give the gross.
   * @return the total counts in the interval.
   */
  double getCounts(RegionOfInterest roi);

    /**
   * Get the total counts in the region of interest.
   *
   * @param roi can either be in energy or channels, if null it will give the gross.
   * @return the total counts in the interval.
   */
  double getRate(RegionOfInterest roi);

  default int getMinimumValidChannel()
  {
    return 0;
  }

  default int getMaximumValidChannel()
  {
    return size();
  }

  static SpectrumBuilder builder()
  {
    return new SpectrumBuilderImpl();
  }

  /**
   * Access the underlying data in the collection.
   *
   * The data type should be an array type such as int[] or double[].
   *
   * @return access the array.
   * @throws UnsupportedOperationException if the collection does not support
   * modification of the data.
   */
  Type toArray() throws UnsupportedOperationException;

  /**
   * Clear the cached valued. This must be called if the spectrum data is
   * altered.
   */
  public abstract void clearCache();


}
