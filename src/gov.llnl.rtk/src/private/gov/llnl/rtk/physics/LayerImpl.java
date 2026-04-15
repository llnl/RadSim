// --- file: gov/llnl/rtk/physics/LayerImpl.java ---
/* 
 * Copyright (c) 2016, Lawrence Livermore National Security, LLC.
 * All rights reserved.
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

/**
 * Default implementation for Layer.  
 * 
 * This uses caching for speed.
 * 
 * @author nelson85
 */
public class LayerImpl implements Layer
{

  Layer previous;
  String label;
  Material material;
  Geometry geometry;
  
  boolean uptodate = false;

  // Store all values in Quantities for easy access.
  QuantityImpl inner = new QuantityImpl(0, PhysicalProperty.LENGTH, 0, true);
  QuantityImpl outer = new QuantityImpl(0, PhysicalProperty.LENGTH, 0, true);
  QuantityImpl thickness = new QuantityImpl(0, PhysicalProperty.LENGTH, 0, true);
  QuantityImpl volume = new QuantityImpl(0, PhysicalProperty.VOLUME, 0, true);
  QuantityImpl mass = new QuantityImpl(0, PhysicalProperty.MASS, 0, true);

  public LayerImpl(Geometry geometry, LayerImpl previous)
  {
    this.geometry = geometry;
    this.previous = previous;
  }

  public LayerImpl(Layer layer)
  {
    label = layer.getLabel();
    this.geometry = layer.getGeometry();
    material = layer.getMaterial();
    
    // Copy over the values assuming that they are correct in the layer we are copying
    inner.assign(layer.getInnerRadius());
    thickness.assign(layer.getThickness());
    outer.assign(layer.getOuterRadius());
    volume.assign(layer.getVolume());
    mass.assign(layer.getMass());
  }

  /** 
   * Update the derived values.
   * 
   * Inner comes from previous.
   * Outer comes from inner and thickness.
   * Volume comes from geometry inner and thickness.
   * Mass comes from volume and material.
   * 
   */
  public final void update()
  {
    if (uptodate)
      return;
    if (this.previous != null)
      this.inner.value = this.previous.getOuterRadius().get();
    this.outer.value = this.inner.value + this.thickness.value;
    if (this.geometry != null)
      this.volume.value = this.geometry.computeVolume(inner, thickness).get();
    else
      this.volume.value = 0;
    if (this.material != null)
      this.mass.value = this.volume.value * this.material.getDensity().get();
    else
      this.mass.value = 0;
    uptodate = true;
  }

//<editor-fold desc="getters" defaultstate="collapsed">
  @Override
  public String getLabel()
  {
    return label;
  }

  @Override
  public Geometry getGeometry()
  {
    return geometry;
  }

  @Override
  public Quantity getMass()
  {
    return mass;
  }

  @Override
  public Material getMaterial()
  {
    return material;
  }

  @Override
  public Quantity getThickness()
  {
    return thickness;
  }
  
  @Override
  public Layer getPrevious()
  {
    return this.previous;
  }

  @Override
  public Quantity getInnerRadius()
  {
    return this.inner;
  }

  @Override
  public Quantity getOuterRadius()
  {
    return this.outer;
  }

  @Override
  public Quantity getVolume()
  {
    return volume;
  }
//</editor-fold>
//<editor-fold desc="setters" defaultstate="collapsed">
  public void setLabel(String label)
  {
    this.label = label;
  }

  public void setGeometry(Geometry geometry)
  {
    this.geometry = geometry;
    uptodate = false;
  }
  
  public void setPrevious(Layer layer)
  {
    this.previous = layer;
    uptodate = false;
  }

  public void setMass(Quantity mass)
  {
    this.mass.assign(mass);
    uptodate = false;
  }

  public void setMaterial(Material material)
  {
    this.material = material;
    uptodate = false;
  }

  public void setThickness(Quantity value)
  {
    thickness.assign(value);
    uptodate = false;
  }

  @Override
  public String toString()
  {
    return String.format("Layer(%s, %s, %s)", this.label, this.material.toString(), this.thickness.toString());
  }
//</editor-fold>
}
