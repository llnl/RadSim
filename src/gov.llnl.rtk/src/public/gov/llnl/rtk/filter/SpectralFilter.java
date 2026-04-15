// --- file: gov/llnl/rtk/filter/SpectralFilter.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.filter;

import gov.llnl.math.MathExceptions.SizeException;
import gov.llnl.math.matrix.Matrix;
import gov.llnl.rtk.data.DoubleSpectrum;
import gov.llnl.rtk.data.Spectrum;

/**
 * SpectralFilter is used to smooth out Poisson statistical noise to improve the
 * training quality for some algorithms. It can either be applied to multiple
 * samples at once, or a single vector.
 *
 * @author nelson85
 */
public interface SpectralFilter
{
  /**
   * Apply the filter to each column of a DoubleMatrix.
   *
   * @param in is the input matrix to be operated on.
   * @return the filtered copy of the input.
   * @throws SizeException when the size of the input does not match the size of
   * the filter.
   */
  Matrix apply(Matrix in) throws SizeException;

  /**
   * Apply the filter to a double array.
   *
   * @param in is the double array to be operated on.
   * @return the filtered copy of the input.
   * @throws SizeException when the size of the input does not match the size of
   * the filter.
   */
  double[] apply(double[] in) throws SizeException;

  default DoubleSpectrum apply(Spectrum<?> spectrum)
  {
    DoubleSpectrum outSpectrum = new DoubleSpectrum(spectrum);
    outSpectrum.setGammaData(this.apply(spectrum.toDoubles()));
    return outSpectrum;
  }
}
