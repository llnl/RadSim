// --- file: gov/llnl/rtk/physics/SphericalModelBuilder.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * A utility for creating a spherical model.
 *
 * @author nelson85
 */
public class SphericalModelBuilder extends ObjectBuilder
{
  LayerBuilder current;
  private final SourceModelImpl model;

  public SphericalModelBuilder()
  {
    this.model = new SourceModelImpl();
  }

  /**
   * Start a new layer.
   *
   * @return
   */
  public LayerBuilder layer()
  {
    // Finish the last layer if it hasn't been built.
    LayerImpl previous = finishLayer();

    // Create a new layer
    LayerImpl layer = new LayerImpl(Geometry.newSpherical(), previous);
    
    // Pass out a fresh builder
    this.current = new LayerBuilder(layer, units);
    return current;
  }

  /** Complete the model.
   * 
   * @return 
   */
  public SourceModel build()
  {
    finishLayer();
    if (current != null)
      current.build();
    SourceModelImpl out = this.model;
    return out;
  }

  /** 
   * Complete the current layer.
   */
  private LayerImpl finishLayer()
  {
    if (current == null)
      return null;
    current.build();
    model.layers.add(current.layer);
    return current.layer;
  }


}
