// --- file: gov/llnl/rtk/physics/RayTraceSegment.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Result of the ray trace.
 * <p>
 * <b>Ownership and Pooling:</b>
 * <ul>
 * <li>This object is owned and managed by the {@code RayTracer} that produced
 * it.</li>
 * <li>Instances are pooled and reused for performance; fields (including
 * {@code length}) are mutable and will be reset between uses.</li>
 * <li><b>Do not retain references</b> to this object or its fields outside the
 * context of the owning {@code RayTracer} unless you explicitly clone or copy
 * it.</li>
 * <li>To keep a result, use {@link #clone()} or the copy constructor.</li>
 * </ul>
 * <b>Thread Safety:</b> Not thread-safe; do not share between threads.
 * </p>
 */
public class RayTraceSegment implements Cloneable
{
  /**
   * The layer that was hit.
   */
  Layer layer;

  /**
   * The length of the chord.
   * <p>
   * This is a mutable {@link Quantity} object reused per run for efficiency. Do
   * not retain a reference to this object outside the context of the owning
   * RayTracer unless you make a defensive copy (e.g., via
   * {@code Quantity.of(...)}).
   * </p>
   */
  final Quantity length;

  /**
   * The angle of the ray relative to the layer's local outward normal (in
   * radians).
   * <p>
   * An angle of 0 indicates the ray is directed straight outward (along the
   * normal, exiting the layer). An angle of π (pi) indicates the ray is
   * directed straight inward (opposite the normal, entering deeper into the
   * object). Intermediate values represent oblique paths relative to the
   * normal.
   * </p>
   */
  double angle;

  /**
   * Default constructor.
   * <p>
   * Initializes this segment with null layer, zero length, and zero angle.
   * </p>
   */
  public RayTraceSegment()
  {
    layer = null;
    length = new QuantityImpl(0, PhysicalProperty.LENGTH, 0, true);
    angle = 0;
  }

  /**
   * Primary constructor.
   *
   * @param layer the layer traversed by this segment
   * @param length the length of the chord (a defensive copy is made)
   * @param angle the angle relative to the layer
   */
  public RayTraceSegment(Layer layer, Quantity length, double angle)
  {
    this.layer = layer;
    // Defensive copy for Quantity (assume Quantity.of() creates a new instance)
    this.length = Quantity.of(length.getValue(), length.getUnits());
    this.angle = angle;
  }

  /**
   * Copy constructor.
   * <p>
   * Creates a deep copy of the given segment, including a defensive copy of the
   * length.
   * </p>
   *
   * @param other the segment to copy
   */
  public RayTraceSegment(RayTraceSegment other)
  {
    this(other.layer, other.length, other.angle);
  }

  @Override
  public String toString()
  {
    return String.format("Seg(%s,%s,%.2f)", layer, length, angle);
  }

  /**
   * Gets the layer traversed by this segment.
   *
   * @return the {@link Layer} instance
   */
  public Layer getLayer()
  {
    return layer;
  }

  /**
   * Gets the length of the chord for this segment.
   * <p>
   * Note: This is a mutable object; see class-level documentation for usage
   * guidelines.
   * </p>
   *
   * @return the {@link Quantity} representing the chord length
   */
  public Quantity getLength()
  {
    return length;
  }

  /**
   * Gets the angle relative to the layer for this segment.
   *
   * @return the angle in radians
   */
  public double getAngle()
  {
    return angle;
  }

}
