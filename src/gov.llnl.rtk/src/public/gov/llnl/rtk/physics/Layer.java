// --- file: gov/llnl/rtk/physics/Layer.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Represents a physical layer in a model with associated properties such as
 * material, geometry, thickness, and mass. Layers can be stacked, with each
 * layer referencing the previous layer to calculate inner and outer dimensions.
 *
 * @author nelson85
 */
public interface Layer
{

  /**
   * Get the label for this layer.
   *
   * @return the label as a String.
   */
  String getLabel();

  /**
   * Get the previous layer in the stack.
   *
   * @return the previous Layer, or null if this is the first layer.
   */
  Layer getPrevious();

  /**
   * Get the material associated with this layer.
   *
   * @return the Material object representing the composition of this layer.
   */
  Material getMaterial();

  /**
   * Get the geometry of this layer.
   *
   * @return the Geometry object describing the layer's shape and spatial
   * properties.
   */
  Geometry getGeometry();

  /**
   * Get the mass of this layer.
   *
   * @return the mass as a Quantity object.
   */
  Quantity getMass();

  /**
   * Get the thickness of this layer.
   *
   * @return the thickness as a Quantity object.
   */
  Quantity getThickness();

  /**
   * Calculate the outer dimension of this layer. The outer dimension is the
   * cumulative thickness of this layer and all previous layers.
   *
   * @return the outer dimension as a Quantity object.
   */
  default Quantity getOuterRadius()
  {
    Layer previous = this.getPrevious();
    if (previous != null)
      return this.getThickness().plus(previous.getOuterRadius());
    return this.getThickness();
  }

  /**
   * Calculate the inner dimension of this layer. The inner dimension is the
   * outer dimension of the previous layer.
   *
   * @return the inner dimension as a Quantity object.
   */
  default Quantity getInnerRadius()
  {
    Layer previous = this.getPrevious();
    if (previous != null)
      return previous.getOuterRadius();
    return Quantity.of(0, this.getThickness().getUnits());
  }

  /**
   * Compute the volume of this layer based on its geometry, inner dimension,
   * and thickness.
   *
   * @return the volume as a Quantity object.
   */
  default Quantity getVolume()
  {
    return this.getGeometry().computeVolume(this.getInnerRadius(), this.getThickness());
  }

}
