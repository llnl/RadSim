// --- file: gov/llnl/rtk/response/deposition/IsotropicChordCDF.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.deposition;

/**
 * Interface for cumulative distribution functions (CDFs) describing the
 * probability distribution of chord lengths through a geometric shape.
 * <p>
 * <b>Assumptions:</b>
 * <ul>
 * <li>The underlying shape is a physically realizable, convex volume (e.g.,
 * cuboid, cylinder).</li>
 * <li>The IsotropicChordCDF is strictly increasing and continuous over the interval
 [0, maxChord].</li>
 * <li>For each probability value in (0,1), there is a unique corresponding
 * chord length.</li>
 * <li>No plateaus or discontinuities exist in the IsotropicChordCDF.</li>
 * </ul>
 * <p>
 * Implementations must override {@link #getMaxChord()} to provide the correct
 * maximum chord length for the shape.
 *
 * @author nelson85
 */
public interface IsotropicChordCDF
{
  
    /**
     * Evaluates the cumulative distribution function (IsotropicChordCDF) for the
 chord length distribution of a shape at the specified chord length
 {@code x}.
     * <p>
     * This method returns the probability that a randomly chosen chord through
     * the shape has length less than or equal to {@code x}.
     *
     * @param x the chord length at which to evaluate the cumulative probability
     * @return the value of the IsotropicChordCDF at chord length {@code x}, in the
     * range [0, 1]
     */
    public double eval(double x);

    /**
     * Return the maximum possible chord length for the shape.
     * <p>
     * Concrete implementations must override this method to return the correct
     * value.
     *
     * @return the maximum chord length for the shape
     */
    public double getMaxChord();

    /**
     * Return an approximate inverse function of this CDF, mapping cumulative
     * probability values in [0, 1] to corresponding chord lengths.
     * <p>
 The returned operator uses recursive subdivision and quadratic
 interpolation to ensure accuracy and monotonicity, assuming the
 IsotropicChordCDF is strictly increasing and continuous.
     *
     * @return a DoubleUnaryOperator representing the inverse IsotropicChordCDF
     */
    public default IsotropicChordQF inverse()
    {
      return new IsotropicChordQF(this, 101, this.getMaxChord());
    }

}
