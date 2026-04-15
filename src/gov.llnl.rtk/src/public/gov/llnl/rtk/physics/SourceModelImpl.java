// --- file: gov/llnl/rtk/physics/SourceModelImpl.java ---
/*
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 *
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.ExpandableObject;
import java.util.ArrayList;
import java.util.List;

/**
 * FIXME rename this. This is a 1dm model for an object
 *
 * @author nelson85
 */
public class SourceModelImpl extends ExpandableObject implements SourceModel
{
  private String title;
  private Geometry geometry = Geometry.newSpherical();
  final ArrayList<LayerImpl> layers = new ArrayList<>();
  LayerImpl last = null;

  /**
   * Set the title for the model.
   *
   * This is used by the readers and writers.
   *
   * @param title
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * Set the geometry of the layer.
   *
   * Geometries are limited to simple 1d objects (spherical, cap, cylinder,
   * slab).
   *
   * @param geometry
   */
  public void setGeometry(Geometry geometry)
  {
    this.geometry = geometry;
  }

  /**
   * Add a layer to the outside of the object.
   *
   * @param layer is the new layer to be added.
   */
  public void addLayer(Layer layer)
  {
    LayerImpl li;
    if (layer == null)
      li = new LayerImpl(geometry, last);
    else
      li = new LayerImpl(layer);
    li.previous = last;
    li.update();
    this.layers.add(li);
    last = li;
  }

  @Override
  public String getTitle()
  {
    return this.title;
  }

  @Override
  public Geometry getGeometry()
  {
    return geometry;
  }

  /**
   * Get the list of layers.
   *
   * The layer list produced is modifiable. If the layers order or properties
   * are altered, be sure to call normalize() to make sure all the derived
   * properties are calculated.
   *
   * @return the layer list.
   */
  @Override
  public List<? extends Layer> getLayers()
  {
    return layers;
  }

  /**
   * Makes sure all the fields are consistent for a model.
   *
   * This requires all layers to be LayerImpl.
   *
   * @throws ClassCastException if one of the layers is not a LayerImpl
   */
  public void normalize()
  {
    for (LayerImpl layer : layers)
    {
      layer.update();
    }
  }

}
