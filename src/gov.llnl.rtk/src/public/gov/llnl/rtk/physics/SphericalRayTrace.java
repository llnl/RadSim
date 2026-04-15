// --- file: gov/llnl/rtk/physics/SphericalRayTrace.java ---
/*
 * Copyright 2025, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Supplier;

/**
 * Evaluator for extracting a ray trace from a model.
 * <p>
 * <strong>Not reentrant:</strong> This object reuses internal state and segment
 * instances between trace calls. Do <strong>not</strong> retain or reuse
 * results outside the current evaluation unless explicitly copied (e.g., via
 * {@code copyInto(Collection)}).
 * </p>
 * <p>
 * <strong>Thread safety:</strong> This class is not thread-safe and should not
 * be shared between threads.
 * </p>
 *
 * @author nelson85
 */
public class SphericalRayTrace implements Iterable<RayTraceSegment>
{
  private final static double MINIMUM_LENGTH = 1e-9;
  private final static double EPS = 1e-9;

  // Resused resources for speed
  private final ArrayList<RayTraceSegment> segments = new ArrayList<>();
  private final ArrayList<RayTraceSegment> pool = new ArrayList<>();
  
  // State variables
  double radius;
  double angle;

  /**
   * Update a trace for a specific radius and angle.
   *
   * @param model is the model to trace.
   * @param radius is the starting distance. (it can be negative)
   * @param theta is the starting angle of the ray. 0 is outwards, pi is
   * inwards.
   */
  public void trace(SourceModel model, Quantity radius, double theta)
  {
    angle = theta;
    this.radius = radius.get();
    if (this.radius < 0)
    {
      this.radius = -this.radius;
      angle = Math.PI - angle;
    }

    // Return old result to the pool
    this.reset();

    // Fint the starting point 
    //   we need both the layer data and the iterator so we can walk through 
    //   the layers.
    ListIterator<? extends Layer> iter = model.getLayers().listIterator();
    Layer current = findFirst(this.radius, iter);

    // Check to make sure it has a layer.
    if (current == null)
      return;

    // We started outside the object, no matter check to see if we hit the outer layer
    double outer = current.getOuterRadius().get();

    if (this.radius >= outer && !handleOuter(current))
      return;

    // We are inside the model, work our way in 
    for (int j = 0; j < model.size() * 2; ++j)
    {
      if (!testInner(current))
        break;
      current = iter.previous();
    }

    // We are inside the model, work our way out
    for (int j = 0; j < model.size() * 2; ++j)
    {
      if (!testOuter(current))
        break;
      if (!iter.hasNext())
        break;
      current = iter.next();
    }
  }

  /**
   * Returns an {@link Iterator} over the ray trace segments.
   * <p>
   * <strong>Important:</strong> The iterator returns references to internal,
   * mutable segment objects. These objects are reused and may change after
   * subsequent trace calls. If you need to retain results, use
   * {@link #copyInto(Collection)} to create detached copies.
   * </p>
   *
   * @return an iterator over the current ray trace segments
   */
  @Override
  public Iterator<RayTraceSegment> iterator()
  {
    return this.segments.iterator();
  }

  /**
   * Copies the current segments into the provided collection as new, detached
   * instances. The destination collection will be cleared before copying.
   *
   * @param <C> is a collection type.
   * @param destination the collection to receive the copied segments
   * @return the collection that was filled.
   */
  public <C extends Collection<RayTraceSegment>> C copyInto(C destination)
  {
    if (destination == null)
      throw new IllegalArgumentException("Destination collection cannot be null");
    destination.clear();
    for (RayTraceSegment segment : segments)
      destination.add(new RayTraceSegment(segment));
    return destination;
  }

  public <C extends Collection<RayTraceSegment>> C copyInto(Supplier<C> collectionFactory)
  {
    return copyInto(collectionFactory.get());
  }

//<editor-fold desc="internal" defaultstate="collapsed">
  /**
   * Add a segment to the result.
   *
   * @param layer
   * @param length
   * @param theta
   */
  private void add(Layer layer, double length, double theta)
  {
    if (length < MINIMUM_LENGTH)
      return;
    RayTraceSegment segment;
    if (!pool.isEmpty())
      segment = pool.remove(pool.size() - 1);
    else
      segment = new RayTraceSegment();
    QuantityImpl q = (QuantityImpl) segment.length;
    segment.layer = layer;
    q.value = length;
    segment.angle = theta;
    this.segments.add(segment);
  }

  /**
   * Clears the current ray trace segments, returning them to the object pool
   * for reuse.
   *
   * <p>
   * Moves all segments from {@code this.segments} to {@code this.pool}, then
   * clears the segment list. Intended to reset the ray trace object before
   * starting a new trace.
   * </p>
   */
  private void reset()
  {
    // Clear the ray trace object of the previous result.
    this.pool.addAll(this.segments);
    this.segments.clear();
  }

  /**
   * Finds the first layer in the iterator whose outer radius is greater than or
   * equal to the specified value.
   *
   * <p>
   * Iterates through the provided {@code ListIterator} of layers, returning the
   * first {@link Layer} whose outer radius is at least {@code r} (within a
   * small epsilon tolerance).
   * </p>
   *
   * @param r the radius value to compare against each layer's outer boundary
   * @param iter a {@code ListIterator} over layers, ordered by increasing outer
   * radius
   * @return the first {@link Layer} whose outer radius is greater than or equal
   * to {@code r}; or the last checked layer if none match
   */
  private Layer findFirst(double r, ListIterator<? extends Layer> iter)
  {
    Layer current = null;
    while (iter.hasNext())
    {
      current = iter.next();
      if (r - EPS <= current.getOuterRadius().get())
        break;
    }
    return current;
  }

  /**
   * Adds a segment representing the ray entering the object from outside at the
   * outer boundary of the specified layer.
   *
   * <p>
   * Called when the ray starts outside the object and first intersects the
   * outer surface of {@code current}. Adds a {@link RayTraceSegment} with
   * {@code null} as the layer to represent entry from vacuum or air. Updates
   * the ray's position and direction for further tracing inside the object.
   * </p>
   *
   * @param current the {@link Layer} whose outer boundary is tested for entry
   * @return {@code true} if the ray intersects the outer boundary and the
   * segment is added; {@code false} otherwise
   *
   * @implNote Updates {@code radius} and {@code angle} to the entry point. The
   * added segment has {@code null} for the layer.
   */
  private boolean handleOuter(Layer current)
  {
    double outer = current.getOuterRadius().get();
    double u = -radius * Math.cos(angle);
    double sT = Math.sin(angle);
    double q = outer * outer - radius * radius * sT * sT;
    if (u > 0 && q > 0)
    {
      // Compute the distance along the ray to the intersection with the outer boundary
      double d0 = u - Math.sqrt(q);

      // Add a segment with null layer (entry from outside)
      add(null, d0, Math.PI - angle);

      // Update the ray's angle and radius for continued tracing inside the object
      // - angle: new direction after entry (reflected across π)
      // - radius: set to negative outer boundary's radius (now inside the object)
      angle = Math.asin(radius / outer * Math.sin(angle));
      radius = -outer;
      return true;
    }
    return false;
  }

  /**
   * Advances the ray inward through the inner boundary of the specified layer.
   *
   * <p>
   * Computes the intersection of the current ray with the inner boundary of
   * {@code current}. If successful, adds a {@link RayTraceSegment} to the
   * trace, and updates the ray's position and direction for continued tracing.
   * </p>
   *
   * @param current the current {@link Layer} through which the ray is
   * propagating
   * @return {@code true} if the ray crosses the inner boundary and is updated;
   * {@code false} if no intersection occurs
   *
   * @implNote Updates {@code this.radius} and {@code angle} to the new position
   * and direction.
   */
  private boolean testInner(Layer current)
  {
    // Get the inner radius of the current layer
    double inner = current.getInnerRadius().get();

    // Compute quadratic terms for intersection
    double u = -this.radius * Math.cos(angle);
    double sT = Math.sin(angle);
    double q = inner * inner - this.radius * this.radius * sT * sT;

    // If both u > 0 and q > 0, the ray will intersect the inner boundary while moving inward
    if (u > 0 && q > 0) // and radius != layer.inner:
    {
      // Compute the distance along the ray to the intersection with the inner boundary
      double d0 = u - Math.sqrt(q);

      // Add a segment representing the path through the current layer to the inner boundary
      add(current, d0, Math.PI - angle);

      // Update the ray's angle and radius for continued tracing
      // - angle: new direction after reaching the inner boundary (reflected across π)
      // - radius: set to negative inner boundary's radius (deeper into the sphere)
      angle = Math.asin(this.radius / inner * sT);
      this.radius = -inner;
      return true;
    }
    return false;
  }

  /**
   * Advances the ray outward through the outer boundary of the specified layer.
   *
   * <p>
   * Computes the intersection of the current ray with the outer boundary of
   * {@code current}. If successful, adds a segment representing the path to the
   * outer boundary and updates the ray's position and direction.
   * </p>
   *
   * @param current the {@link Layer} whose outer boundary is being tested
   * @return {@code true} if the ray crosses the outer boundary and the segment
   * is added; {@code false} otherwise
   *
   * @implNote Updates {@code this.radius} and {@code angle} to the new position
   * and direction at the boundary.
   */
  private boolean testOuter(Layer current)
  {
    // Get the outer radius of the current layer
    double outer = current.getOuterRadius().get();

    // Compute quadratic terms for intersection
    double u = -this.radius * Math.cos(angle);
    double sT = Math.sin(angle);
    double q = outer * outer - this.radius * this.radius * sT * sT;

    // If q <= 0, the ray does not intersect the outer boundary (stop tracing)
    if (q <= 0)
      return false;

    // Compute the distance along the ray to the intersection with the outer boundary
    double d0 = u + Math.sqrt(q);

    // Add a segment representing the path through the current layer to the outer boundary
    add(current, d0, angle);

    // Update the ray's angle and radius for continued tracing
    // - angle: new direction after reaching the outer boundary
    // - radius: set to the outer boundary's radius
    angle = Math.asin(this.radius / outer * sT);
    this.radius = outer;

    return true;
  }
//</editor-fold>
}
