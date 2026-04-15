// --- file: gov/llnl/rtk/response/sim/MonteSim.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

/**
 * Interface for Monte Carlo simulation strategies for geometric response
 * modeling.
 *
 * <p>
 * Implementations of this interface provide mechanisms to:
 * <ul>
 * <li>Randomly generate new simulation configurations (geometries, source
 * positions, etc.)</li>
 * <li>Draw samples (e.g., chord lengths, path lengths) according to the current
 * configuration and simulation scenario</li>
 * <li>Manage random number generator seeds for reproducibility</li>
 * <li>Expose the dimensionality of "condition" and "state" variables for
 * downstream modeling</li>
 * </ul>
 * </p>
 *
 * <p>
 * This abstraction allows for flexible swapping of simulation strategies (e.g.,
 * direct, scatter, analytic) in batch processing, machine learning, or
 * validation workflows.
 * </p>
 *
 * @author nelson85
 */
public interface MonteSim
{
  /**
   * Generates and returns a new random simulation configuration.
   * <p>
   * This method should randomize all "condition" parameters (such as shape
   * geometry, source location, and other scenario-defining variables), and
   * return a feature vector describing the current configuration. The returned
   * array may include both geometric and physical parameters, depending on the
   * simulation type.
   * </p>
   *
   * @return a feature vector representing the current simulation configuration
   */
  double[] nextConfiguration();

  /**
   * Draws a batch of samples according to the current simulation configuration.
   * <p>
   * Fills the provided {@code values} array with simulated results (e.g., chord
   * lengths, path lengths, or other observables) for the current configuration.
   * The array must be preallocated to the desired batch size.
   * </p>
   *
   * @param values array to be filled with sampled results
   */
  void draw(double[] values);

  /**
   * Sets the random number generator seed for reproducible simulations.
   * <p>
   * This method should update all relevant random number generators used by the
   * simulation so that subsequent calls produce deterministic results for the
   * same seed.
   * </p>
   *
   * @param l the seed value
   */
  void setSeed(long l);

  /**
   * Returns the number of "condition" variables describing the simulation
   * scenario.
   * <p>
   * Condition variables are those that are fixed for a large batch run (e.g.,
   * geometry, emission point, physical parameters). This value determines the
   * length of the configuration feature vector returned by
   * {@link #nextConfiguration()} that should be treated as fixed for a given
   * batch.
   * </p>
   *
   * @return the number of condition variables
   */
  int getConditionSize();

  /**
   * Returns the number of "state" (dynamics) variables that must be swept or
   * integrated over during simulation.
   * <p>
   * State variables typically represent parameters that vary within a batch,
   * such as scattering angles, attenuation coefficients, or other dynamic
   * properties. The goal is to minimize the number of state variables in favor
   * of conditions, for efficiency.
   * </p>
   *
   * @return the number of state (dynamics) variables
   */
  int getDynamicsSize();

}
