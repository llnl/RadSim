// --- file: gov/llnl/rtk/physics/LayerBuilder.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import static gov.llnl.rtk.physics.LayerSpecification.MASS;
import static gov.llnl.rtk.physics.LayerSpecification.OUTER;

/**
 *
 * @author nelson85
 */
public class LayerBuilder
{
  final LayerImpl layer;
  MaterialBuilder material;
  LayerSpecification spec = null;
  boolean finished = false;
  private final UnitSystem units;

  public LayerBuilder(LayerImpl layer, UnitSystem units)
  {
    this.layer = layer;
    this.units = units;
  }
  
  public LayerBuilder label(String str)
  {
    this.layer.label = str;
    return this;
  }
  
//<editor-fold desc="material" defaultstate="collapsed">
  /** 
   * Specify the material for the layer.
   * 
   * The density is determined by the material.
   * 
   * @param material
   * @return 
   */
  public LayerBuilder material(Material material)
  {
    layer.material = material;
    return this;
  }

  /** 
   * Create a builder for the material.
   * 
   * @return 
   */
  public MaterialBuilder material()
  {
    this.material = new MaterialBuilder(this.units);
    this.layer.material = material.material;
    return this.material;
  }

//</editor-fold>
//<editor-fold desc="contraints" defaultstate="collapsed">
  /**
   * Verify if the layer is already specified with another constraint.
   *
   * @param spec
   */
  private void checkConstraint(LayerSpecification spec)
  {
    if (this.spec != null && this.spec != spec)
      throw new IllegalStateException("Layer already specified as " + this.spec);
    this.spec = spec;
  }

  /**
   * Define the thickness of the layer.
   *
   * Only one specification can be applied to the layer.
   *
   * @param length
   * @return the layer builder.
   */
  public LayerBuilder thickness(Quantity length)
  {
    checkConstraint(LayerSpecification.THICKNESS);
    length.require(PhysicalProperty.LENGTH);
    layer.thickness.assign(length);
    return this;
  }

  /**
   * Define the layer using the outer radius.
   *
   * Only one specification can be applied to the layer.
   *
   * @param length
   * @return
   */
  public LayerBuilder outer(Quantity length)
  {
    checkConstraint(LayerSpecification.OUTER);
    length.require(PhysicalProperty.LENGTH);
    layer.outer.assign(length);
    return this;
  }

  /**
   * Define the layer using the layer mass.
   *
   * Only one specification can be applied to the layer.
   *
   * @param mass
   * @return
   */
  public LayerBuilder mass(Quantity mass)
  {
    checkConstraint(LayerSpecification.MASS);
    mass.require(PhysicalProperty.MASS);
    layer.mass.assign(mass);
    return this;
  }

  /**
   * Define the layer using the volume.
   *
   * Only one specification can be applied to the layer.
   *
   * @param volume
   * @return
   */
  public LayerBuilder volume(Quantity volume)
  {
    checkConstraint(LayerSpecification.VOLUME);
    volume.require(PhysicalProperty.VOLUME);
    layer.volume.assign(volume);
    return this;
  }
//</editor-fold>  
//<editor-fold desc="shortcuts" defaultstate="collapsed">
  public LayerBuilder thickness(double length)
  {
    return thickness(Quantity.of(length, units.getLengthUnit()));
  }

  public LayerBuilder outer(double length)
  {
    return outer(Quantity.of(length, units.getLengthUnit()));
  }

  public LayerBuilder mass(double mass)
  {
    return mass(Quantity.of(mass, units.getMassUnit()));
  }

  public LayerBuilder volume(double volume)
  {
    return this.volume(Quantity.of(volume, units.getVolumeUnit()));
  }

//</editor-fold>
//<editor-fold desc="build" defaultstate="collapsed">
  public void build()
  {
    if (finished)
      return;
    finished = true;
    if (this.layer.material == null)
      throw new IllegalStateException("material not specified");

    double volume = this.layer.volume.value;
    double inner = this.layer.inner.value;
    switch (spec)
    {
      case THICKNESS:
        break;

      case OUTER:
        layer.thickness.value = layer.outer.value - layer.inner.value;
        // Copy the units
        layer.inner.setUnits(layer.outer.getUnits());
        layer.thickness.setUnits(layer.outer.getUnits());
        break;

      case MASS:
        double density = this.layer.material.getDensity().as(PhysicalProperty.DENSITY);
        volume = this.layer.mass.value / density;

      case VOLUME:
        // FIXME this should come from the geometry        
        layer.outer.value = Math.pow(volume * 3.0 / 4.0 / Math.PI + inner * inner * inner, 1 / 3.0);
        layer.outer.setUnits(layer.inner.getUnits());
        layer.thickness.setUnits(layer.inner.getUnits());
        layer.thickness.value = layer.outer.value - inner;
        break;
    }
    if (layer.label == null)
    {
      layer.label = String.format("layer-%x", this.hashCode());
    }
    layer.update();
  }
//</editor-fold>
}
