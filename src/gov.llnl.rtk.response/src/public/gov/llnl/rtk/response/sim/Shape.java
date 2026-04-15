// --- file: gov/llnl/rtk/response/sim/Shape.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.response.sim;

import gov.llnl.math.euclidean.MutableVector3;
import gov.llnl.math.random.UniformRandom;

/**
 * Interface for geometric shapes supporting randomization, point-inclusion
 * testing, ray-surface intersection, and feature extraction for simulation and
 * analysis.
 *
 * <p>
 * Implementations of this interface represent parameterizable 3D shapes (e.g.,
 * cuboid, cylinder) used in Monte Carlo simulations and geometric modeling.
 * Methods support random shape generation, intersection calculations, and
 * extraction of normalized geometric features for use in probabilistic modeling
 * or machine learning.
 * </p>
 *
 * @author nelson85
 */
public interface Shape
{

  /**
   * Randomizes the dimensions and/or orientation of this shape instance.
   * <p>
   * Implementations should use the provided {@link UniformRandom} generator to
   * assign new, random values to the shape's defining parameters (e.g., side
   * lengths, radii). This enables sampling over a distribution of shapes for
   * simulation purposes.
   * </p>
   *
   * @param ur uniform random number generator for parameter selection
   */
  void drawDimensions(UniformRandom ur);

  /**
   * Determines whether a given point lies inside the shape.
   * <p>
   * Used to test if a specific location (typically a source or emission point)
   * is contained within the shape's boundaries.
   * </p>
   *
   * @param v the point to test, as a 3D vector
   * @return {@code true} if the point is inside the shape; {@code false}
   * otherwise
   */
  boolean inside(MutableVector3 v);

  /**
   * Computes the distances from a source point to all intersection points with
   * the shape's surfaces, along a specified direction.
   * <p>
   * This method finds the distances at which a ray, starting from
   * {@code source} and proceeding in the {@code direction}, intersects the
   * shape's surfaces. All intersection distances are written into the provided
   * {@code distance} array. The array must be preallocated with sufficient
   * space for multiple results (e.g., for edge or corner cases).
   * </p>
   *
   * @param distance array to store intersection distances (output)
   * @param source starting point of the ray
   * @param direction normalized direction vector of the ray
   * @return the number of intersection points found (number of valid entries in
   * {@code distance})
   */
  int intercept(double[] distance, MutableVector3 source, MutableVector3 direction);


}
