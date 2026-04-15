// --- file: gov/llnl/rtk/physics/SourceModel.java ---
/*
 * Copyright 2019, Lawrence Livermore National Security, LLC. 
 * All rights reserved
 * 
 * Terms and conditions are given in "Notice" file.
 */
package gov.llnl.rtk.physics;

import gov.llnl.utility.Expandable;
import gov.llnl.utility.xml.bind.ReaderInfo;
import gov.llnl.utility.xml.bind.WriterInfo;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a source model containing layers and geometry. Source models are
 * expandable and serializable, and they can be used to calculate properties
 * such as radius and size.
 *
 * @author nelson85
 */
@ReaderInfo(SourceModelReader.class)
@WriterInfo(SourceModelWriter.class)
public interface SourceModel extends Expandable, Serializable
{

  /**
   * Get the geometry of the source model.
   *
   * @return the Geometry object representing the shape and spatial properties.
   */
  Geometry getGeometry();

  /**
   * Get the list of layers in the source model.
   *
   * @return a List of Layer objects.
   */
  List<? extends Layer> getLayers();

  /**
   * Get the title of the source model.
   *
   * @return the title as a String.
   */
  String getTitle();

  /**
   * Get the number of layers in the source model.
   *
   * @return the number of layers as an integer.
   */
  default int size()
  {
    return this.getLayers().size();
  }

  /**
   * Calculate the radius of the source model. The radius is the cumulative
   * thickness of all layers.
   *
   * @return the radius as a Quantity object.
   */
  default Quantity getRadius()
  {
    double out = 0;
    for (Layer l : this.getLayers())
    {
      out += l.getThickness().as(PhysicalProperty.LENGTH);
    }
    return Quantity.of(out, PhysicalProperty.LENGTH);
  }
}
