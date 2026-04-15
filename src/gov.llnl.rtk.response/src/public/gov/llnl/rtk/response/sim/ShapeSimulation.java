// --- file: gov/llnl/rtk/response/sim/ShapeSimulation.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

/**
 * Interface for geometric simulation engines supporting random shape
 * generation, source placement, and sampling of chord/path distributions for
 * Monte Carlo analysis.
 *
 * <p>
 * Implementations of this interface provide methods to:
 * <ul>
 * <li>Randomize the geometric shape under study</li>
 * <li>Randomize the source (emission) location</li>
 * <li>Simulate random chords, direct rays, and scattered rays through the
 * shape</li>
 * </ul>
 * </p>
 *
 * @author nelson85
 */
public interface ShapeSimulation
{
  /**
   * Randomizes the dimensions and orientation of the current shape.
   * <p>
   * This method should update the internal state to represent a new, randomly
   * parameterized instance of the shape (e.g., cuboid, cylinder). Used to
   * generate diverse simulation scenarios.
   * </p>
   */
  void drawShape();

  /**
   * Randomizes the source (emission) location for the simulation.
   * <p>
   * This method should select a new source point, typically outside the shape,
   * for use in subsequent ray tracing or chord simulations. The source should
   * be chosen according to the statistical requirements of the simulation.
   * </p>
   */
  void drawSourceLocation();

  /**
   * Fills the provided array with random chord lengths sampled through the
   * current shape.
   * <p>
   * For each entry in the {@code chords} array, this method should generate a
   * random chord (line segment) through the shape using unbiased sampling. This
   * is typically used for statistical analysis or validation of analytic
   * models.
   * </p>
   *
   * @param chords array to be filled with sampled chord lengths; must be
   * preallocated
   */
  void simulateChord(double[] chords);

  /**
   * Fills the provided array with samples of direct (unscattered) ray path
   * lengths through the current shape from the current source location.
   * <p>
   * For each entry in the {@code chords} array, this method should simulate a
   * straight-line (direct) ray from the source toward the shape and record the
   * length of the segment passing through the shape.
   * </p>
   *
   * @param chords array to be filled with sampled direct path lengths; must be
   * preallocated
   */
  void simulateDirect(double[] chords);

  /**
   * Fills the provided array with samples of scattered ray path lengths through
   * the shape.
   * <p>
   * For each entry in the {@code chords} array, this method should simulate a
   * ray that enters the shape from the source, interacts (scatters) at a random
   * point along its path (possibly with attenuation), and then exits the shape
   * in a new direction determined by the specified scattering angle.
   * </p>
   *
   * @param chords array to be filled with sampled scattered path lengths; must
   * be preallocated
   * @param cosAngle cosine of the scattering angle for outgoing rays
   * @param attenuation attenuation coefficient controlling interaction
   * probability
   */
  void simulateScatter(double[] chords, double cosAngle, double attenuation);

}
